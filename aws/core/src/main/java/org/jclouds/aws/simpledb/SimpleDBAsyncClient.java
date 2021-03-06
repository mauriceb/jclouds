/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.aws.simpledb;

import static org.jclouds.aws.simpledb.reference.SimpleDBParameters.ACTION;
import static org.jclouds.aws.simpledb.reference.SimpleDBParameters.VERSION;

import javax.annotation.Nullable;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.functions.RegionToEndpoint;
import org.jclouds.aws.simpledb.domain.ListDomainsResponse;
import org.jclouds.aws.simpledb.options.ListDomainsOptions;
import org.jclouds.aws.simpledb.xml.ListDomainsResponseHandler;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to SimpleDB via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = SimpleDBAsyncClient.VERSION)
@VirtualHost
public interface SimpleDBAsyncClient {
   public static final String VERSION = "2009-04-15";

   /**
    * @see SimpleDBClient#listDomainsInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ListDomains")
   @XMLResponseParser(ListDomainsResponseHandler.class)
   ListenableFuture<? extends ListDomainsResponse> listDomainsInRegion(
         @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region, ListDomainsOptions... options);

   /**
    * @see SimpleDBClient#createDomainInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateDomain")
   ListenableFuture<Void> createDomainInRegion(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
         @FormParam("DomainName") String domainName);

   /**
    * @see SimpleDBClient#deleteDomain
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteDomain")
   ListenableFuture<Void> deleteDomainInRegion(@EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
         @FormParam("DomainName") String domainName);

}
