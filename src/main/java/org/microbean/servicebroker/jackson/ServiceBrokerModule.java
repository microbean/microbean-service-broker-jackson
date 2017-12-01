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

import org.microbean.servicebroker.api.query.state.Catalog.Service.DashboardClient;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.InputParameters;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.ServiceBinding;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.ServiceInstance;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan;
import org.microbean.servicebroker.api.query.state.Catalog.Service;
import org.microbean.servicebroker.api.query.state.Catalog;

import org.microbean.servicebroker.jackson.query.state.Catalog.Service.DashboardClientMixin;
import org.microbean.servicebroker.jackson.query.state.Catalog.Service.Plan.Schema.InputParametersMixin;
import org.microbean.servicebroker.jackson.query.state.Catalog.Service.Plan.Schema.ServiceBindingMixin;
import org.microbean.servicebroker.jackson.query.state.Catalog.Service.Plan.Schema.ServiceInstanceMixin;
import org.microbean.servicebroker.jackson.query.state.Catalog.Service.Plan.SchemaMixin;
import org.microbean.servicebroker.jackson.query.state.Catalog.Service.PlanMixin;
import org.microbean.servicebroker.jackson.query.state.Catalog.ServiceMixin;
import org.microbean.servicebroker.jackson.query.state.CatalogMixin;

public class ServiceBrokerModule extends SimpleModule {

  private static final long serialVersionUID = 1L;
  
  public ServiceBrokerModule() {
    super(new PackageVersion().version());
    
    this.setMixInAnnotation(Catalog.class, CatalogMixin.class);
    this.setMixInAnnotation(DashboardClient.class, DashboardClientMixin.class);
    this.setMixInAnnotation(InputParameters.class, InputParametersMixin.class);
    this.setMixInAnnotation(Plan.class, PlanMixin.class);
    this.setMixInAnnotation(Schema.class, SchemaMixin.class);
    this.setMixInAnnotation(Service.class, ServiceMixin.class);
    this.setMixInAnnotation(ServiceBinding.class, ServiceBindingMixin.class);
    this.setMixInAnnotation(ServiceInstance.class, ServiceInstanceMixin.class);

    this.setMixInAnnotation(org.microbean.servicebroker.api.AbstractStatefulObject.class, org.microbean.servicebroker.jackson.AbstractStatefulObjectMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.command.AbstractProvisioningResponse.class, org.microbean.servicebroker.jackson.command.AbstractProvisioningResponseMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.command.DeleteBindingCommand.class, org.microbean.servicebroker.jackson.command.DeleteBindingCommandMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.command.DeleteServiceInstanceCommand.class, org.microbean.servicebroker.jackson.command.DeleteServiceInstanceCommandMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.command.ProvisionBindingCommand.BindResource.class, org.microbean.servicebroker.jackson.command.ProvisionBindingCommand.BindResourceMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.command.ProvisionBindingCommand.Response.class, org.microbean.servicebroker.jackson.command.ProvisionBindingCommand.ResponseMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.command.ProvisionBindingCommand.class, org.microbean.servicebroker.jackson.command.ProvisionBindingCommandMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.command.ProvisionServiceInstanceCommand.Response.class, org.microbean.servicebroker.jackson.command.ProvisionServiceInstanceCommand.ResponseMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.command.ProvisionServiceInstanceCommand.class, org.microbean.servicebroker.jackson.command.ProvisionServiceInstanceCommandMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.command.UpdateServiceInstanceCommand.PreviousValues.class, org.microbean.servicebroker.jackson.command.UpdateServiceInstanceCommand.PreviousValuesMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.command.UpdateServiceInstanceCommand.Response.class, org.microbean.servicebroker.jackson.command.UpdateServiceInstanceCommand.ResponseMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.command.UpdateServiceInstanceCommand.class, org.microbean.servicebroker.jackson.command.UpdateServiceInstanceCommandMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.query.state.LastOperation.State.class, org.microbean.servicebroker.jackson.query.state.LastOperation.StateMixin.class);
    this.setMixInAnnotation(org.microbean.servicebroker.api.query.state.LastOperation.class, org.microbean.servicebroker.jackson.query.state.LastOperationMixin.class);
  }
  
}
