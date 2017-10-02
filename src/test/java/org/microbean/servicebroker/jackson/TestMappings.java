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

import java.io.IOException;

import java.net.URISyntaxException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.databind.introspect.ClassIntrospector;

import org.junit.Before;
import org.junit.Test;

import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.InputParameters;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.ServiceBinding;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.ServiceInstance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestMappings {

  private ObjectMapper objectMapper;

  private final Path referenceFiles;
  
  public TestMappings() throws URISyntaxException {
    super();
    this.referenceFiles = Paths.get(Thread.currentThread().getContextClassLoader().getResource("TestMappings/").toURI());
    assert this.referenceFiles != null;
  }

  @Before
  public void createObjectMapper() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    this.objectMapper.setMixInResolver(new MixinResolver());
  }

  @Test
  public void testReadInputParameters() throws IOException {
    final Path inputParametersPath = this.referenceFiles.resolve("inputParameters.json");
    assertNotNull(inputParametersPath);
    final InputParameters inputParameters = objectMapper.readValue(inputParametersPath.toUri().toURL(), InputParameters.class);
    assertNotNull(inputParameters);
    testInputParameters(inputParameters);
  }

  private static final void testInputParameters(final InputParameters inputParameters) {
    assertNotNull(inputParameters);
    final Map<? extends String, ?> parameters = inputParameters.getParameters();
    assertNotNull(parameters);
    assertFalse(parameters.isEmpty());
    assertEquals(parameters.toString(), 3, parameters.size());
    assertEquals("http://json-schema.org/draft-04/schema#", parameters.get("$schema"));
    assertEquals("object", parameters.get("type"));
    final Object propertiesObject = parameters.get("properties");
    assertTrue(propertiesObject instanceof Map);
    @SuppressWarnings("unchecked")
    final Map<? extends String, ?> properties = (Map<? extends String, ?>)propertiesObject;
    assertFalse(properties.isEmpty());
    assertEquals(1, properties.size());
    final Object billingAccountObject = properties.get("billing-account");
    assertTrue(billingAccountObject instanceof Map);
    @SuppressWarnings("unchecked")
    final Map<? extends String, ?> billingAccount = (Map<? extends String, ?>)billingAccountObject;
    assertFalse(billingAccount.isEmpty());
    assertEquals(2, billingAccount.size());
    assertEquals("Billing account number used to charge use of shared fake server.", billingAccount.get("description"));
    assertEquals("string", billingAccount.get("type"));
  }

  @Test
  public void testWriteInputParameters() throws IOException {
    final InputParameters inputParameters = createInputParameters();
    assertNotNull(inputParameters);

    final String json = objectMapper.writeValueAsString(inputParameters);
    assertNotNull(json);

    final Path inputParametersPath = this.referenceFiles.resolve("inputParameters.json");
    assertNotNull(inputParametersPath);
    
    final String expectedJson = new String(Files.readAllBytes(inputParametersPath), "UTF-8").trim();
    assertEquals(expectedJson, json);    
  }

  private static final InputParameters createInputParameters() {
    final Map<String, String> billingAccount = new LinkedHashMap<>();
    billingAccount.put("description", "Billing account number used to charge use of shared fake server.");
    billingAccount.put("type", "string");

    final Map<String, Object> properties = new LinkedHashMap<>();
    properties.put("billing-account", billingAccount);

    final Map<String, Object> parameters = new LinkedHashMap<>();
    parameters.put("$schema", "http://json-schema.org/draft-04/schema#");
    parameters.put("type", "object");
    parameters.put("properties", properties);

    return new InputParameters(parameters);
  }

  @Test
  public void testReadServiceInstance() throws IOException {
    final Path serviceInstancePath = this.referenceFiles.resolve("serviceInstance.json");
    assertNotNull(serviceInstancePath);
    final ServiceInstance serviceInstance = objectMapper.readValue(serviceInstancePath.toUri().toURL(), ServiceInstance.class);
    assertNotNull(serviceInstance);
    testServiceInstance(serviceInstance);
  }

  private static final void testServiceInstance(final ServiceInstance serviceInstance) {
    assertNotNull(serviceInstance);
    final InputParameters create = serviceInstance.getCreate();
    assertNotNull(create);
    testInputParameters(create);
    final InputParameters update = serviceInstance.getUpdate();
    if (update != null) {
      testInputParameters(update);
    }
  }

  @Test
  public void testWriteServiceInstance() throws IOException {
    final ServiceInstance serviceInstance = createServiceInstance();
    assertNotNull(serviceInstance);

    final String json = objectMapper.writeValueAsString(serviceInstance);
    assertNotNull(json);

    final Path serviceInstancePath = this.referenceFiles.resolve("serviceInstance.json");
    assertNotNull(serviceInstancePath);
    
    final String expectedJson = new String(Files.readAllBytes(serviceInstancePath), "UTF-8").trim();
    assertEquals(expectedJson, json);    
  }

  private static final ServiceInstance createServiceInstance() {
    final InputParameters inputParameters = createInputParameters();
    assertNotNull(inputParameters);
    return new ServiceInstance(inputParameters, null);
  }

  @Test
  public void testReadSchema() throws IOException {
    final Path schemaPath = this.referenceFiles.resolve("schema.json");
    assertNotNull(schemaPath);
    final Schema schema = objectMapper.readValue(schemaPath.toUri().toURL(), Schema.class);
    assertNotNull(schema);
    testSchema(schema);
  }

  private static final void testSchema(final Schema schema) {
    assertNotNull(schema);
    final ServiceInstance serviceInstance = schema.getServiceInstance();
    assertNotNull(serviceInstance);
    testServiceInstance(serviceInstance);
    final ServiceBinding serviceBinding = schema.getServiceBinding();
    assertNotNull(serviceBinding);
    testServiceBinding(serviceBinding);
  }

  private static final void testServiceBinding(final ServiceBinding serviceBinding) {
    assertNotNull(serviceBinding);
    final InputParameters create = serviceBinding.getCreate();
    assertNotNull(create);
    testInputParameters(create);
  }

  @Test
  public void testWriteSchema() throws IOException {
    final Schema schema = createSchema();
    assertNotNull(schema);

    final String json = objectMapper.writeValueAsString(schema);
    assertNotNull(json);

    final Path schemaPath = this.referenceFiles.resolve("schema.json");
    assertNotNull(schemaPath);
    
    final String expectedJson = new String(Files.readAllBytes(schemaPath), "UTF-8").trim();
    assertEquals(expectedJson, json);    
  }

  private static final Schema createSchema() {
    final InputParameters serviceInstanceCreate = createInputParameters();
    assertNotNull(serviceInstanceCreate);
    final InputParameters serviceInstanceUpdate = createInputParameters();
    assertNotNull(serviceInstanceUpdate);
    final ServiceInstance serviceInstance = new ServiceInstance(serviceInstanceCreate, serviceInstanceUpdate);
    final ServiceBinding serviceBinding = new ServiceBinding(createInputParameters());
    assertNotNull(serviceBinding);
    return new Schema(serviceInstance, serviceBinding);
  }

  @Test
  public void testReadPlan() throws IOException {
    final Path planPath = this.referenceFiles.resolve("plan.json");
    assertNotNull(planPath);
    final Plan plan = objectMapper.readValue(planPath.toUri().toURL(), Plan.class);
    assertNotNull(plan);
    testPlan(plan);
  }

  private static final void testPlan(final Plan plan) {
    assertNotNull(plan);
    assertEquals("fake-plan-1", plan.getName());
    assertEquals("d3031751-XXXX-XXXX-XXXX-a42377d3320e", plan.getId());
    assertEquals("Shared fake Server, 5tb persistent disk, 40 max concurrent connections", plan.getDescription());
    assertFalse(plan.isFree());
    final Map<? extends String, ?> metadata = plan.getMetadata();
    assertNotNull(metadata);
    assertEquals(Integer.valueOf(5), metadata.get("max_storage_tb"));
    @SuppressWarnings("unchecked")
    final List<? extends Map<? extends String, ?>> costs = (List<? extends Map<? extends String, ?>>)metadata.get("costs");
    assertNotNull(costs);
    assertEquals(2, costs.size());

    @SuppressWarnings("unchecked")
    final Map<? extends String, ?> cost0 = (Map<? extends String, ?>)costs.get(0);
    assertNotNull(cost0);
    @SuppressWarnings("unchecked")
    final Map<? extends String, ?> amount0 = (Map<? extends String, ?>)cost0.get("amount");
    assertNotNull(amount0);
    assertEquals(Double.valueOf(99.0d), amount0.get("usd"));
    assertEquals("MONTHLY", cost0.get("unit"));

    @SuppressWarnings("unchecked")
    final Map<? extends String, ?> cost1 = (Map<? extends String, ?>)costs.get(1);
    assertNotNull(cost1);
    @SuppressWarnings("unchecked")
    final Map<? extends String, ?> amount1 = (Map<? extends String, ?>)cost1.get("amount");
    assertNotNull(amount1);
    assertEquals(Double.valueOf(0.99d), amount1.get("usd"));
    assertEquals("1GB of messages over 20GB", cost1.get("unit"));

    @SuppressWarnings("unchecked")
    final List<? extends String> bullets = (List<? extends String>)metadata.get("bullets");
    assertNotNull(bullets);
    assertEquals(3, bullets.size());
    assertEquals("Shared fake server", bullets.get(0));
    assertEquals("5 TB storage", bullets.get(1));
    assertEquals("40 concurrent connections", bullets.get(2));

    final Schema schema = plan.getSchemas();
    assertNotNull(schema);
    testSchema(schema);
  }

  @Test
  public void testWritePlan() throws IOException {
    final Plan plan = createPlan();
    assertNotNull(plan);
    
    final String json = objectMapper.writeValueAsString(plan);
    assertNotNull(json);
    
    final Path planPath = this.referenceFiles.resolve("plan.json");
    assertNotNull(planPath);
    
    final String expectedJson = new String(Files.readAllBytes(planPath), "UTF-8").trim();
    assertEquals(expectedJson, json);    
  }

  private static final Plan createPlan() {    
    final List<Map<? extends String, ?>> costs = new ArrayList<>();

    final Map<String, Float> ninetyNineDollars = new HashMap<>();
    ninetyNineDollars.put("usd", 99.0f);
    final Map<String, Object> cost0 = new LinkedHashMap<>();
    cost0.put("amount", ninetyNineDollars);
    cost0.put("unit", "MONTHLY");

    costs.add(cost0);

    final Map<String, Float> ninetyNineCents = new HashMap<>();
    ninetyNineCents.put("usd", 0.99f);
    final Map<String, Object> cost1 = new LinkedHashMap<>();
    cost1.put("amount", ninetyNineCents);
    cost1.put("unit", "1GB of messages over 20GB");

    costs.add(cost1);

    final List<String> bullets = new ArrayList<>();
    bullets.add("Shared fake server");
    bullets.add("5 TB storage");
    bullets.add("40 concurrent connections");
    
    final Map<String, Object> metadata = new LinkedHashMap<>();
    metadata.put("max_storage_tb", Integer.valueOf(5));
    metadata.put("costs", costs);
    metadata.put("bullets", bullets);

    final Schema schema = createSchema();
    assertNotNull(schema);

    return new Plan("d3031751-XXXX-XXXX-XXXX-a42377d3320e",
                    "fake-plan-1",
                    "Shared fake Server, 5tb persistent disk, 40 max concurrent connections",
                    metadata,
                    false,
                    null,
                    schema);
  }

  /*
   * Inner and nested classes.
   */


  private static final class MixinResolver implements ClassIntrospector.MixInResolver {
    
    private MixinResolver() {
      super();
    }

    @Override
    public final Class<?> findMixInClassFor(final Class<?> c) {
      Class<?> returnValue = null;
      if (c != null) {
        String className = c.getName();
        if (className.startsWith("org.microbean.servicebroker.api.")) {
          assert className.length() > "org.microbean.servicebroker.api.".length();
          final StringBuilder newClassName = new StringBuilder("org.microbean.servicebroker.jackson.");
          newClassName.append(className.substring("org.microbean.servicebroker.api.".length()));
          newClassName.append("Mixin");
          try {
            returnValue = Class.forName(newClassName.toString(), true, Thread.currentThread().getContextClassLoader());
          } catch (final ClassNotFoundException cnfe) {
            returnValue = null;
          }
        }
      }
      return returnValue;
    }

    @Override
    public final ClassIntrospector.MixInResolver copy() {
      return this; // we're immutable
    }
    
  }
  
}
