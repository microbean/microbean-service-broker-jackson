/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright © 2017 MicroBean.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.microbean.servicebroker.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class ServiceBrokerModule extends SimpleModule {

  private static final long serialVersionUID = 1L;
  
  public ServiceBrokerModule() {
    super();
    this.setMixInAnnotation(org.microbean.servicebroker.api.query.state.Catalog.class,
                            org.microbean.servicebroker.jackson.query.state.CatalogMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.query.state.Catalog.Service.class,
                            org.microbean.servicebroker.jackson.query.state.Catalog.ServiceMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.class,
                            org.microbean.servicebroker.jackson.query.state.Catalog.Service.PlanMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.query.state.Catalog.Service.DashboardClient.class,
                            org.microbean.servicebroker.jackson.query.state.Catalog.Service.DashboardClientMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.class,
                            org.microbean.servicebroker.jackson.query.state.Catalog.Service.Plan.SchemaMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.ServiceBinding.class,
                            org.microbean.servicebroker.jackson.query.state.Catalog.Service.Plan.Schema.ServiceBindingMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.ServiceInstance.class,
                            org.microbean.servicebroker.jackson.query.state.Catalog.Service.Plan.Schema.ServiceInstanceMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.InputParameters.class,
                            org.microbean.servicebroker.jackson.query.state.Catalog.Service.Plan.Schema.InputParametersMixin.class);
  }

  
  
}
