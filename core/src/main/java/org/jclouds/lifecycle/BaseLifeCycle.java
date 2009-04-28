/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.lifecycle;

import org.jclouds.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public abstract class BaseLifeCycle implements Runnable, LifeCycle {
    protected final Logger logger;
    protected final ExecutorService executor;
    protected final BaseLifeCycle[] dependencies;
    protected final Object statusLock;
    protected volatile Status status;
    protected Exception exception;

    public BaseLifeCycle(Logger logger, ExecutorService executor, BaseLifeCycle... dependencies) {
        this.logger = logger;
        this.executor = executor;
        this.dependencies = dependencies;
        this.statusLock = new Object();
        this.status = Status.INACTIVE;
    }

    public Status getStatus() {
        return status;
    }

    public void run() {
        try {
            while (shouldDoWork()) {
                doWork();
            }
        } catch (Exception e) {
            logger.error(e, "Exception doing work");
            this.exception = e;
        }
        this.status = Status.SHUTTING_DOWN;
        doShutdown();
        this.status = Status.SHUT_DOWN;
        logger.info("%1s", this);
    }

    protected abstract void doWork() throws Exception;

    protected abstract void doShutdown();

    protected boolean shouldDoWork() {
        try {
            exceptionIfDepedenciesNotActive();
        } catch (IllegalStateException e) {
            return false;
        }
        return status.equals(Status.ACTIVE) && exception == null;
    }

    @PostConstruct
    public void start() {
        logger.info("starting %1s", this);
        synchronized (this.statusLock) {
            if (this.status.compareTo(Status.SHUTDOWN_REQUEST) >= 0) {
                doShutdown();
                this.status = Status.SHUT_DOWN;
                this.statusLock.notifyAll();
                return;
            }
            if (this.status.compareTo(Status.ACTIVE) == 0) {
                this.statusLock.notifyAll();
                return;
            }

            if (this.status.compareTo(Status.INACTIVE) != 0) {
                throw new IllegalStateException("Illegal state: " + this.status);
            }

            exceptionIfDepedenciesNotActive();

            this.status = Status.ACTIVE;
        }
        executor.execute(this);
    }

    protected void exceptionIfDepedenciesNotActive() {
        for (BaseLifeCycle dependency : dependencies) {
            if (dependency.status.compareTo(Status.ACTIVE) != 0) {
                throw new IllegalStateException(String.format("Illegal state: %1s for component: %2s", dependency.status, dependency));
            }
        }
    }

    public Exception getException() {
        return this.exception;
    }

    protected void awaitShutdown(long timeout) throws InterruptedException {
        awaitStatus(Status.SHUT_DOWN, timeout);
    }

    protected void awaitStatus(Status intended, long timeout) throws InterruptedException {
        synchronized (this.statusLock) {
            long deadline = System.currentTimeMillis() + timeout;
            long remaining = timeout;
            while (this.status != intended) {
                this.statusLock.wait(remaining);
                if (timeout > 0) {
                    remaining = deadline - System.currentTimeMillis();
                    if (remaining <= 0) {
                        break;
                    }
                }
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        shutdown(2000);
    }

    public void shutdown(long waitMs) {
        synchronized (this.statusLock) {
            if (this.status.compareTo(Status.ACTIVE) > 0) {
                return;
            }
            this.status = Status.SHUTDOWN_REQUEST;
            try {
                awaitShutdown(waitMs);
            } catch (InterruptedException ignore) {
            }
        }
    }

    protected void exceptionIfNotActive() {
        if (!status.equals(Status.ACTIVE))
            throw new IllegalStateException(String.format("not active: %1s", this));
    }


}