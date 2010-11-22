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

package org.jclouds.libvirt.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "libvirt.LibvirtExperimentLiveTest")
public class LibvirtExperimentLiveTest {
   protected String provider = "libvirt";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   @Test
   public void testAndExperiment() {
      ComputeServiceContext context = null;
      try {
         context = new ComputeServiceContextFactory().createContext(new LibvirtComputeServiceContextSpec(
               "test:///default", "identity", "credential"));
         /*
          * 
          * /* System.out.println("images " + context.getComputeService().listImages());
          * System.out.println("nodes " + context.getComputeService().listNodes());
          * System.out.println("hardware profiles " +
          * context.getComputeService().listHardwareProfiles());
          */

         /*
          * Template defaultTemplate = context.getComputeService().templateBuilder()
          * .hardwareId("c7ff2039-a9f1-a659-7f91-e0f82f59d52e").imageId("1") //.locationId("")
          * .build();
          */

         /*
          * We will probably make a default template out of properties at some point You can control
          * the default template via overriding a method in standalonecomputeservicexontextmodule
          */

         Set<? extends NodeMetadata> nodeMetadataSet = context.getComputeService().runNodesWithTag("tty", 1);
         for (NodeMetadata nodeMetadata : nodeMetadataSet) {
            /*
             * context.getComputeService().suspendNode(nodeMetadata.getId());
             * context.getComputeService().resumeNode(nodeMetadata.getId());
             */
            context.getComputeService().destroyNode(nodeMetadata.getId());
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if (context != null)
            context.close();
      }
   }

}