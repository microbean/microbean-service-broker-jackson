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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonIgnoreProperties(ignoreUnknown = false)
@JsonInclude(content = JsonInclude.Include.NON_EMPTY, value = JsonInclude.Include.NON_EMPTY)
@JsonNaming(SnakeCaseStrategy.class)
@JsonPropertyOrder(value = { "services" }, alphabetic = true)
public abstract class CatalogMixin {

  private CatalogMixin() {
    super();
  }

  @JsonAnyGetter
  abstract Map<? extends String, ?> getProperties();

  @JsonAnySetter
  abstract void setProperty(final String name, final Object property);
  
}
