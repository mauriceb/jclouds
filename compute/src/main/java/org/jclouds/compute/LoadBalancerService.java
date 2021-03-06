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

package org.jclouds.compute;

import java.util.Set;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseLoadBalancerService;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;

/**
 * Provides portable access to load balancer services.
 * 
 * @author Lili Nadar
 */
@Beta
@ImplementedBy(BaseLoadBalancerService.class)
public interface LoadBalancerService {

   /**
    * @return a reference to the context that created this LoadBalancerService.
    */
   ComputeServiceContext getContext();

   /**
    * @param filter
    *           Predicate-based filter to define which nodes to loadbalance
    * @param loadBalancerName
    *           Load balancer name
    * @param protocol
    *           LoadBalancer transport protocol to use for routing - TCP or HTTP. This property
    *           cannot be modified for the life of the LoadBalancer.
    * @param loadBalancerPort
    *           The external TCP port of the LoadBalancer. Valid LoadBalancer ports are - 80, 443
    *           and 1024 through 65535. This property cannot be modified for the life of the
    *           LoadBalancer.
    * @param instancePort
    *           The InstancePort data type is simple type of type: integer. It is the TCP port on
    *           which the server on the instance is listening. Valid instance ports are one (1)
    *           through 65535. This property cannot be modified for the life of the LoadBalancer.
    * 
    * @return DNS Name of the load balancer; note we don't use String, as it is incompatible
    *         with google appengine.
    */
   @Beta
   Set<String> loadBalanceNodesMatching(Predicate<NodeMetadata> filter, String loadBalancerName,
            String protocol, int loadBalancerPort, int instancePort);

   @Beta
   void destroyLoadBalancer(String handle);
   
   @Beta
   Set<String> listLoadBalancers();

}
