/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.s3.commands;

import org.jclouds.aws.s3.S3IntegrationTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * Tests integrated functionality of all deleteBucket commands.
 * <p/>
 * Each test uses a different bucket name, so it should be perfectly fine to run
 * in parallel.
 *
 * @author Adrian Cole
 */
@Test(groups = {"integration", "live"}, testName = "s3.DeleteBucketIntegrationTest")
public class DeleteBucketIntegrationTest extends S3IntegrationTest {

    @Test
    /**
     * this method overrides bucketName to ensure it isn't found
     */
    void deleteBucketIfEmptyNotFound() throws Exception {
        String bucketName = bucketPrefix + "dbienf";
        assert client.deleteBucketIfEmpty(bucketName).get(10, TimeUnit.SECONDS);
    }

    @Test()
    void deleteBucketIfEmptyButHasContents() throws Exception {
        addObjectToBucket(bucketName, "test");
        assert !client.deleteBucketIfEmpty(bucketName).get(10, TimeUnit.SECONDS);
    }

    @Test()
    void deleteBucketIfEmpty() throws Exception {
        assert client.deleteBucketIfEmpty(bucketName).get(10, TimeUnit.SECONDS);
        assert !client.bucketExists(bucketName).get(10, TimeUnit.SECONDS);
    }
}