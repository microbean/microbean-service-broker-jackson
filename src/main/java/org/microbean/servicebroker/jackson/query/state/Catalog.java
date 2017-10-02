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
package org.microbean.servicebroker.jackson.query.state;

import java.net.URI;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.microbean.servicebroker.api.query.state.Catalog.Service.DashboardClient;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.ServiceBinding;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.ServiceInstance;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.InputParameters;

abstract class Catalog {

  @JsonInclude(content = JsonInclude.Include.NON_EMPTY, value = JsonInclude.Include.NON_EMPTY)
  @JsonNaming(SnakeCaseStrategy.class)
  @JsonPropertyOrder({ "name", "id", "description", "tags", "requires", "bindable", "metadata", "dashboard_client", "plan_updateable", "plans" })
  static abstract class ServiceMixin {

    @JsonCreator
    ServiceMixin(@JsonProperty("id") final String id,
                 @JsonProperty("name") final String name,
                 @JsonProperty("description") final String description,
                 @JsonProperty("tags") final Set<? extends String> tags,
                 @JsonProperty("requires") final Set<? extends String> requires,
                 @JsonProperty("bindable") final boolean bindable,
                 @JsonProperty("metadata") final Map<? extends String, ?> metadata,
                 @JsonProperty("dashboard_client") final DashboardClient dashboardClient,
                 @JsonProperty("plan_updateable") final boolean planUpdatable,
                 @JsonProperty("plans") final Set<? extends Plan> plans) {
      super();
    }
    
    @JsonProperty("plan_updateable")
    public abstract boolean isPlanUpdatable();

  }

  static abstract class Service {
    
    @JsonInclude(content = JsonInclude.Include.NON_EMPTY, value = JsonInclude.Include.NON_EMPTY)
    @JsonNaming(SnakeCaseStrategy.class)
    @JsonPropertyOrder({ "id", "secret", "redirect_uri" })
    static abstract class DashboardClientMixin {

      @JsonCreator
      DashboardClientMixin(@JsonProperty("id") final String oAuthClientId,
                           @JsonProperty("secret") final String secret,
                           @JsonProperty("redirect_uri") final URI redirectUri) {
        super();
      }

      @JsonProperty("id")
      public abstract String getOAuthClientId();

      @JsonProperty("redirect_uri")
      public abstract URI getRedirectUri();
      
    }

    static abstract class Plan {

      static abstract class Schema {

        @JsonInclude(content = JsonInclude.Include.NON_EMPTY, value = JsonInclude.Include.NON_EMPTY)
        @JsonNaming(SnakeCaseStrategy.class)
        @JsonPropertyOrder({ "create", "update" })
        static abstract class ServiceInstanceMixin {

          @JsonCreator
          ServiceInstanceMixin(@JsonProperty("create") final InputParameters create,
                               @JsonProperty("update") final InputParameters update)
          {
            super();
          }
          
        }

        @JsonInclude(content = JsonInclude.Include.NON_EMPTY, value = JsonInclude.Include.NON_EMPTY)
        @JsonNaming(SnakeCaseStrategy.class)
        @JsonPropertyOrder({ "create" })
        static abstract class ServiceBindingMixin {

          @JsonCreator
          ServiceBindingMixin(@JsonProperty("create") final InputParameters create) {
            super();
          }
          
        }
        
        @JsonInclude(content = JsonInclude.Include.NON_EMPTY, value = JsonInclude.Include.NON_EMPTY)
        @JsonNaming(SnakeCaseStrategy.class)
        @JsonPropertyOrder({ "parameters" })
        static abstract class InputParametersMixin {

          @JsonCreator
          InputParametersMixin(@JsonProperty("parameters") final Map<? extends String, ?> parameters) {
            super();
          }
          
        }
        
      }

      @JsonInclude(content = JsonInclude.Include.NON_EMPTY, value = JsonInclude.Include.NON_EMPTY)
      @JsonNaming(SnakeCaseStrategy.class)
      @JsonPropertyOrder({ "service_instance", "service_binding" })
      static abstract class SchemaMixin {

        @JsonCreator
        SchemaMixin(@JsonProperty("service_instance") final ServiceInstance serviceInstance,
                    @JsonProperty("service_binding") final ServiceBinding serviceBinding) {
          super();
        }
        
      }
      
    }
    
    @JsonInclude(content = JsonInclude.Include.NON_EMPTY, value = JsonInclude.Include.NON_EMPTY)
    @JsonNaming(SnakeCaseStrategy.class)
    @JsonPropertyOrder({ "name", "id", "description", "free", "metadata", "bindable", "schemas" })
    static abstract class PlanMixin {

      @JsonCreator
      PlanMixin(@JsonProperty("id") final String id,
                @JsonProperty("name") final String name,
                @JsonProperty("description") final String description,
                @JsonProperty("metadata") final Map<? extends String, String> metadata,
                @JsonProperty("free") final boolean free,
                @JsonProperty("bindable") final Boolean bindable,
                @JsonProperty("schemas") final Schema schemas) {
        super();
      }
      
    }
    
  }
  
}
