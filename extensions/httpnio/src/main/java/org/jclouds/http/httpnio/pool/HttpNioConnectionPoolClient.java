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
package org.jclouds.http.httpnio.pool;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.http.nio.NHttpConnection;
import org.jclouds.command.FutureCommand;
import org.jclouds.command.pool.FutureCommandConnectionPoolClient;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpFutureCommandClient;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
@Singleton
public class HttpNioConnectionPoolClient extends FutureCommandConnectionPoolClient<NHttpConnection> implements HttpFutureCommandClient {
    private List<HttpRequestFilter> requestFilters = Collections.emptyList();

    public List<HttpRequestFilter> getRequestFilters() {
        return requestFilters;
    }

    @Inject(optional = true)
    public void setRequestFilters(List<HttpRequestFilter> requestFilters) {
        this.requestFilters = requestFilters;
    }

    @Override
    protected <O extends FutureCommand> void invoke(O operation) {
        HttpRequest request = (HttpRequest) operation.getRequest();
        try {
            for (HttpRequestFilter filter : getRequestFilters()) {
                filter.filter(request);
            }
            super.invoke(operation);
        } catch (HttpException e) {
            operation.setException(e);
        }
    }

    @Inject
    public HttpNioConnectionPoolClient(Logger logger, ExecutorService executor, HttpNioFutureCommandConnectionPool httpFutureCommandConnectionHandleNHttpConnectionNioFutureCommandConnectionPool, BlockingQueue<FutureCommand> commandQueue) {
        super(logger, executor, httpFutureCommandConnectionHandleNHttpConnectionNioFutureCommandConnectionPool, commandQueue);    // TODO: Adrian: Customise this generated block
    }
}
