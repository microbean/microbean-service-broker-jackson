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

import java.net.URI;
import java.net.URISyntaxException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.Before;
import org.junit.Test;

import org.microbean.servicebroker.api.command.DeleteBindingCommand;
import org.microbean.servicebroker.api.command.DeleteServiceInstanceCommand;
import org.microbean.servicebroker.api.command.ProvisionBindingCommand;
import org.microbean.servicebroker.api.command.ProvisionBindingCommand.BindResource;
import org.microbean.servicebroker.api.command.ProvisionServiceInstanceCommand;
import org.microbean.servicebroker.api.command.UpdateServiceInstanceCommand;
import org.microbean.servicebroker.api.command.UpdateServiceInstanceCommand.PreviousValues;
import org.microbean.servicebroker.api.command.UpdateServiceInstanceCommand.Response;

import org.microbean.servicebroker.api.query.state.Catalog.Service.DashboardClient;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.InputParameters;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.ServiceBinding;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.ServiceInstance;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema;
import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan;
import org.microbean.servicebroker.api.query.state.Catalog.Service;
import org.microbean.servicebroker.api.query.state.Catalog;
import org.microbean.servicebroker.api.query.state.LastOperation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    this.objectMapper.registerModule(new ServiceBrokerModule());
    this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
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

  @Test
  public void testReadService() throws IOException {
    final Path servicePath = this.referenceFiles.resolve("service.json");
    assertNotNull(servicePath);
    final Service service = objectMapper.readValue(servicePath.toUri().toURL(), Service.class);
    assertNotNull(service);
    testService(service);
  }

  private static final void testService(final Service service) {
    assertNotNull(service);
    assertEquals("fake-service", service.getName());
    assertEquals("acb56d7c-XXXX-XXXX-XXXX-feb140a59a66", service.getId());
    assertEquals("fake service", service.getDescription());
    assertEquals(new LinkedHashSet<>(Arrays.asList("no-sql", "relational")), service.getTags());
    assertEquals(new LinkedHashSet<>(Arrays.asList("route_forwarding")), service.getRequires());
    assertTrue(service.isBindable());
    final Map<? extends String, ?> metadata = service.getMetadata();
    assertNotNull(metadata);
    @SuppressWarnings("unchecked")
    final Map<? extends String, ?> provider = (Map<? extends String, ?>)metadata.get("provider");
    assertNotNull(provider);
    assertEquals("The name", provider.get("name"));
    @SuppressWarnings("unchecked")
    final Map<? extends String, ?> listing = (Map<? extends String, ?>)metadata.get("listing");
    assertNotNull(listing);
    assertEquals("http://example.com/cat.gif", listing.get("imageUrl"));
    assertEquals("Add a blurb here", listing.get("blurb"));
    assertEquals("A long time ago, in a galaxy far far away...", listing.get("longDescription"));
    assertEquals("The Fake Broker", metadata.get("displayName"));
    assertEquals(metadata, service.getMetadata());
    final DashboardClient dashboardClient = service.getDashboardClient();
    assertNotNull(dashboardClient);
    assertEquals("398e2f8e-XXXX-XXXX-XXXX-19a71ecbcf64", dashboardClient.getOAuthClientId());
    assertEquals("277cabb0-XXXX-XXXX-XXXX-7822c0a90e5d", dashboardClient.getSecret());
    assertEquals("http://localhost:1234", dashboardClient.getRedirectUri().toString());
    assertTrue(service.isPlanUpdatable());
    final Set<? extends Plan> plans = service.getPlans();
    assertNotNull(plans);
    assertEquals(1, plans.size());
    final Plan plan0 = plans.iterator().next();
    assertNotNull(plan0);
    testPlan(plan0);
  }

  @Test
  public void testWriteService() throws IOException {
    final Service service = createService();
    assertNotNull(service);
    
    final String json = objectMapper.writeValueAsString(service);
    assertNotNull(json);
    
    final Path servicePath = this.referenceFiles.resolve("service.json");
    assertNotNull(servicePath);
    
    final String expectedJson = new String(Files.readAllBytes(servicePath), "UTF-8").trim();
    assertEquals(expectedJson, json);    
  }

  private static final Service createService() {
    final Plan plan0 = createPlan();
    assertNotNull(plan0);
    final Map<String, Object> metadata = new LinkedHashMap<>();
    final Map<String, Object> provider = new LinkedHashMap<>();
    provider.put("name", "The name");
    metadata.put("provider", provider);
    final Map<String, Object> listing = new LinkedHashMap<>();
    listing.put("imageUrl", "http://example.com/cat.gif");
    listing.put("blurb", "Add a blurb here");
    listing.put("longDescription", "A long time ago, in a galaxy far far away...");
    metadata.put("listing", listing);
    metadata.put("displayName", "The Fake Broker");
    final Set<String> tags = new LinkedHashSet<>();
    tags.add("no-sql");
    tags.add("relational");
    final Set<String> requires = new LinkedHashSet<>();
    requires.add("route_forwarding");
    final DashboardClient dashboardClient =
      new DashboardClient("398e2f8e-XXXX-XXXX-XXXX-19a71ecbcf64",
                          "277cabb0-XXXX-XXXX-XXXX-7822c0a90e5d",
                          URI.create("http://localhost:1234"));
    return new Service("acb56d7c-XXXX-XXXX-XXXX-feb140a59a66",
                       "fake-service",
                       "fake service",
                       tags,
                       requires,
                       true,
                       metadata,
                       dashboardClient,
                       true,
                       Collections.singleton(plan0));
  }

  @Test
  public void testReadCatalog() throws IOException {
    final Path catalogPath = this.referenceFiles.resolve("catalog.json");
    assertNotNull(catalogPath);
    final Catalog catalog = objectMapper.readValue(catalogPath.toUri().toURL(), Catalog.class);
    assertNotNull(catalog);
    testCatalog(catalog);
  }

  private static final void testCatalog(final Catalog catalog) {
    assertNotNull(catalog);
    final Set<? extends Service> services = catalog.getServices();
    assertNotNull(services);
    assertEquals(1, services.size());
    final Service service0 = services.iterator().next();
    assertNotNull(service0);
    testService(service0);
  }

  @Test
  public void testWriteCatalog() throws IOException {
    final Catalog catalog = createCatalog();
    assertNotNull(catalog);

    final String json = objectMapper.writeValueAsString(catalog);
    assertNotNull(json);
    
    final Path catalogPath = this.referenceFiles.resolve("catalog.json");
    assertNotNull(catalogPath);
    
    final String expectedJson = new String(Files.readAllBytes(catalogPath), "UTF-8").trim();
    assertEquals(expectedJson, json);
  }

  private static final Catalog createCatalog() {
    final Service service = createService();
    assertNotNull(service);
    final Catalog catalog = new Catalog(Collections.singleton(service));
    catalog.setProperty("argle", "bargle");
    return catalog;
  }

  @Test
  public void testReadDeleteBindingCommand() throws IOException {
    final Path deleteBindingCommandPath = this.referenceFiles.resolve("deleteBindingCommand.json");
    assertNotNull(deleteBindingCommandPath);
    final DeleteBindingCommand deleteBindingCommand = objectMapper.readValue(deleteBindingCommandPath.toUri().toURL(), DeleteBindingCommand.class);
    assertNotNull(deleteBindingCommand);
    testDeleteBindingCommand(deleteBindingCommand);
  }

  private static final void testDeleteBindingCommand(final DeleteBindingCommand deleteBindingCommand) {
    assertNotNull(deleteBindingCommand);
    assertNull(deleteBindingCommand.getBindingId());
    assertNull(deleteBindingCommand.getInstanceId());
    assertEquals("xyz", deleteBindingCommand.getServiceId());
    assertEquals("basic", deleteBindingCommand.getPlanId());
    assertEquals("bargle", deleteBindingCommand.getProperty("argle_bargle"));
  }

  @Test
  public void testWriteDeleteBindingCommand() throws IOException {
    final DeleteBindingCommand deleteBindingCommand = createDeleteBindingCommand();
    assertNotNull(deleteBindingCommand);

    final String json = objectMapper.writeValueAsString(deleteBindingCommand);
    assertNotNull(json);
    
    final Path deleteBindingCommandPath = this.referenceFiles.resolve("deleteBindingCommand.json");
    assertNotNull(deleteBindingCommandPath);
    
    final String expectedJson = new String(Files.readAllBytes(deleteBindingCommandPath), "UTF-8").trim();
    assertEquals(expectedJson, json);
  }

  private static final DeleteBindingCommand createDeleteBindingCommand() {
    final DeleteBindingCommand returnValue = new DeleteBindingCommand(null, null, "xyz", "basic");
    returnValue.setProperty("argle_bargle", "bargle");
    return returnValue;
  }
  
  @Test
  public void testReadDeleteBindingCommandResponse() throws IOException {
    final Path deleteBindingCommandResponsePath = this.referenceFiles.resolve("deleteBindingCommand.response.json");
    assertNotNull(deleteBindingCommandResponsePath);
    final DeleteBindingCommand.Response deleteBindingCommandResponse = objectMapper.readValue(deleteBindingCommandResponsePath.toUri().toURL(), DeleteBindingCommand.Response.class);
    assertNotNull(deleteBindingCommandResponse);
    testDeleteBindingCommandResponse(deleteBindingCommandResponse);
  }

  private static final void testDeleteBindingCommandResponse(final DeleteBindingCommand.Response deleteBindingCommandResponse) {
    assertNotNull(deleteBindingCommandResponse);
    assertEquals("bargle", deleteBindingCommandResponse.getProperty("argle_bargle"));
  }

  @Test
  public void testWriteDeleteBindingCommandResponse() throws IOException {
    final DeleteBindingCommand.Response deleteBindingCommandResponse = createDeleteBindingCommandResponse();
    assertNotNull(deleteBindingCommandResponse);
    
    final String json = objectMapper.writeValueAsString(deleteBindingCommandResponse);
    assertNotNull(json);
    
    final Path deleteBindingCommandResponsePath = this.referenceFiles.resolve("deleteBindingCommand.response.json");
    assertNotNull(deleteBindingCommandResponsePath);
    
    final String expectedJson = new String(Files.readAllBytes(deleteBindingCommandResponsePath), "UTF-8").trim();
    assertEquals(expectedJson, json);    
  }

  private static final DeleteBindingCommand.Response createDeleteBindingCommandResponse() {
    final DeleteBindingCommand.Response deleteBindingCommandResponse = new DeleteBindingCommand.Response();
    deleteBindingCommandResponse.setProperty("argle_bargle", "bargle");
    return deleteBindingCommandResponse;
  }

  @Test
  public void testReadDeleteServiceInstanceCommand() throws IOException {
    final Path deleteServiceInstanceCommandPath = this.referenceFiles.resolve("deleteServiceInstanceCommand.json");
    assertNotNull(deleteServiceInstanceCommandPath);
    final DeleteServiceInstanceCommand deleteServiceInstanceCommand = objectMapper.readValue(deleteServiceInstanceCommandPath.toUri().toURL(), DeleteServiceInstanceCommand.class);
    assertNotNull(deleteServiceInstanceCommand);
    testDeleteServiceInstanceCommand(deleteServiceInstanceCommand);
  }

  private static final void testDeleteServiceInstanceCommand(final DeleteServiceInstanceCommand deleteServiceInstanceCommand) {
    assertNotNull(deleteServiceInstanceCommand);
    assertEquals("abc", deleteServiceInstanceCommand.getInstanceId());
    assertEquals("xyz", deleteServiceInstanceCommand.getServiceId());
    assertEquals("basic", deleteServiceInstanceCommand.getPlanId());
    assertFalse(deleteServiceInstanceCommand.getAcceptsIncomplete());
    assertEquals("bargle", deleteServiceInstanceCommand.getProperty("argle_bargle"));
  }

  @Test
  public void testWriteDeleteServiceInstanceCommand() throws IOException {
    final DeleteServiceInstanceCommand deleteServiceInstanceCommand = createDeleteServiceInstanceCommand();
    assertNotNull(deleteServiceInstanceCommand);
    
    final String json = objectMapper.writeValueAsString(deleteServiceInstanceCommand);
    assertNotNull(json);
    
    final Path deleteServiceInstanceCommandPath = this.referenceFiles.resolve("deleteServiceInstanceCommand.json");
    assertNotNull(deleteServiceInstanceCommandPath);
    
    final String expectedJson = new String(Files.readAllBytes(deleteServiceInstanceCommandPath), "UTF-8").trim();
    assertEquals(expectedJson, json);    
  }

  private static final DeleteServiceInstanceCommand createDeleteServiceInstanceCommand() {
    final DeleteServiceInstanceCommand returnValue = new DeleteServiceInstanceCommand("abc", "xyz", "basic", false);
    returnValue.setProperty("argle_bargle", "bargle");
    return returnValue;
  }

  @Test
  public void testReadDeleteServiceInstanceCommandResponse() throws IOException {
    final Path deleteServiceInstanceCommandResponsePath = this.referenceFiles.resolve("deleteServiceInstanceCommand.response.json");
    assertNotNull(deleteServiceInstanceCommandResponsePath);
    final DeleteServiceInstanceCommand.Response deleteServiceInstanceCommandResponse = objectMapper.readValue(deleteServiceInstanceCommandResponsePath.toUri().toURL(), DeleteServiceInstanceCommand.Response.class);
    assertNotNull(deleteServiceInstanceCommandResponse);
    testDeleteServiceInstanceCommandResponse(deleteServiceInstanceCommandResponse);
  }

  private static final void testDeleteServiceInstanceCommandResponse(final DeleteServiceInstanceCommand.Response deleteServiceInstanceCommandResponse) {
    assertNotNull(deleteServiceInstanceCommandResponse);
    assertEquals("bargle", deleteServiceInstanceCommandResponse.getProperty("argle_bargle"));
  }

  @Test
  public void testWriteDeleteServiceInstanceCommandResponse() throws IOException {
    final DeleteServiceInstanceCommand.Response deleteServiceInstanceCommandResponse = createDeleteServiceInstanceCommandResponse();
    assertNotNull(deleteServiceInstanceCommandResponse);
    
    final String json = objectMapper.writeValueAsString(deleteServiceInstanceCommandResponse);
    assertNotNull(json);
    
    final Path deleteServiceInstanceCommandResponsePath = this.referenceFiles.resolve("deleteServiceInstanceCommand.response.json");
    assertNotNull(deleteServiceInstanceCommandResponsePath);
    
    final String expectedJson = new String(Files.readAllBytes(deleteServiceInstanceCommandResponsePath), "UTF-8").trim();
    assertEquals(expectedJson, json);    
  }

  private static final DeleteServiceInstanceCommand.Response createDeleteServiceInstanceCommandResponse() {
    final DeleteServiceInstanceCommand.Response deleteServiceInstanceCommandResponse = new DeleteServiceInstanceCommand.Response();
    deleteServiceInstanceCommandResponse.setProperty("argle_bargle", "bargle");
    return deleteServiceInstanceCommandResponse;
  }

  @Test
  public void testReadProvisionBindingCommand() throws IOException {
    final Path provisionBindingCommandPath = this.referenceFiles.resolve("provisionBindingCommand.json");
    assertNotNull(provisionBindingCommandPath);
    final ProvisionBindingCommand provisionBindingCommand = objectMapper.readValue(provisionBindingCommandPath.toUri().toURL(), ProvisionBindingCommand.class);
    assertNotNull(provisionBindingCommand);
    testProvisionBindingCommand(provisionBindingCommand);
  }

  private static final void testProvisionBindingCommand(final ProvisionBindingCommand provisionBindingCommand) {
    assertNotNull(provisionBindingCommand);
    assertEquals("123", provisionBindingCommand.getBindingId());
    assertEquals("abc", provisionBindingCommand.getInstanceId());
    assertEquals("xyz", provisionBindingCommand.getServiceId());
    assertEquals("basic", provisionBindingCommand.getPlanId());
    assertEquals("bargle", provisionBindingCommand.getProperty("argle_bargle"));
    final BindResource bindResource = provisionBindingCommand.getBindResource();
    assertNotNull(bindResource);
    assertEquals("abcxyz", bindResource.getAppGuid());
    assertEquals("bargle", bindResource.getProperty("argle_bargle"));
    assertEquals("http://example.com:8080", bindResource.getRoute().toString());
  }

  @Test
  public void testWriteProvisionBindingCommand() throws IOException {
    final ProvisionBindingCommand provisionBindingCommand = createProvisionBindingCommand();
    assertNotNull(provisionBindingCommand);

    final String json = objectMapper.writeValueAsString(provisionBindingCommand);
    assertNotNull(json);
    
    final Path provisionBindingCommandPath = this.referenceFiles.resolve("provisionBindingCommand.json");
    assertNotNull(provisionBindingCommandPath);
    
    final String expectedJson = new String(Files.readAllBytes(provisionBindingCommandPath), "UTF-8").trim();
    assertEquals(expectedJson, json);
  }

  private static final ProvisionBindingCommand createProvisionBindingCommand() {
    final BindResource bindResource = new BindResource("abcxyz", URI.create("http://example.com:8080"));
    bindResource.setProperty("argle_bargle", "bargle");
    final ProvisionBindingCommand returnValue = new ProvisionBindingCommand("123", "abc", "xyz", "basic", bindResource, null);
    returnValue.setProperty("argle_bargle", "bargle");
    return returnValue;
  }
  
  @Test
  public void testReadProvisionBindingCommandResponse() throws IOException {
    final Path provisionBindingCommandResponsePath = this.referenceFiles.resolve("provisionBindingCommand.response.json");
    assertNotNull(provisionBindingCommandResponsePath);
    final ProvisionBindingCommand.Response provisionBindingCommandResponse = objectMapper.readValue(provisionBindingCommandResponsePath.toUri().toURL(), ProvisionBindingCommand.Response.class);
    assertNotNull(provisionBindingCommandResponse);
    testProvisionBindingCommandResponse(provisionBindingCommandResponse);
  }

  private static final void testProvisionBindingCommandResponse(final ProvisionBindingCommand.Response provisionBindingCommandResponse) {
    assertNotNull(provisionBindingCommandResponse);
    final Map<? extends String, ?> credentials = provisionBindingCommandResponse.getCredentials();
    assertNotNull(credentials);
    assertEquals("bar", credentials.get("foo"));
    assertEquals("bargle", provisionBindingCommandResponse.getProperty("argle_bargle"));
  }

  @Test
  public void testWriteProvisionBindingCommandResponse() throws IOException {
    final ProvisionBindingCommand.Response provisionBindingCommandResponse = createProvisionBindingCommandResponse();
    assertNotNull(provisionBindingCommandResponse);
    
    final String json = objectMapper.writeValueAsString(provisionBindingCommandResponse);
    assertNotNull(json);
    
    final Path provisionBindingCommandResponsePath = this.referenceFiles.resolve("provisionBindingCommand.response.json");
    assertNotNull(provisionBindingCommandResponsePath);
    
    final String expectedJson = new String(Files.readAllBytes(provisionBindingCommandResponsePath), "UTF-8").trim();
    assertEquals(expectedJson, json);    
  }

  private static final ProvisionBindingCommand.Response createProvisionBindingCommandResponse() {
    final Map<String, Object> credentials = new LinkedHashMap<>();
    credentials.put("foo", "bar");
    final ProvisionBindingCommand.Response provisionBindingCommandResponse = new ProvisionBindingCommand.Response(credentials);
    provisionBindingCommandResponse.setProperty("argle_bargle", "bargle");
    return provisionBindingCommandResponse;
  }

  @Test
  public void testReadProvisionServiceInstanceCommand() throws IOException {
    final Path provisionServiceInstanceCommandPath = this.referenceFiles.resolve("provisionServiceInstanceCommand.json");
    assertNotNull(provisionServiceInstanceCommandPath);
    final ProvisionServiceInstanceCommand provisionServiceInstanceCommand = objectMapper.readValue(provisionServiceInstanceCommandPath.toUri().toURL(), ProvisionServiceInstanceCommand.class);
    assertNotNull(provisionServiceInstanceCommand);
    testProvisionServiceInstanceCommand(provisionServiceInstanceCommand);
  }

  private static final void testProvisionServiceInstanceCommand(final ProvisionServiceInstanceCommand provisionServiceInstanceCommand) {
    assertNotNull(provisionServiceInstanceCommand);
    assertEquals("abc", provisionServiceInstanceCommand.getInstanceId());
    assertEquals("xyz", provisionServiceInstanceCommand.getServiceId());
    assertEquals("basic", provisionServiceInstanceCommand.getPlanId());
    assertEquals("abcxyz", provisionServiceInstanceCommand.getOrganizationGuid());
    assertEquals("abcxyz", provisionServiceInstanceCommand.getSpaceGuid());
    assertTrue(provisionServiceInstanceCommand.getAcceptsIncomplete());
    assertEquals("bargle", provisionServiceInstanceCommand.getProperty("argle_bargle"));
    final Map<? extends String, ?> parameters = provisionServiceInstanceCommand.getParameters();
    assertNotNull(parameters);
    assertEquals("bar", parameters.get("foo"));
  }
  
  @Test
  public void testWriteProvisionServiceInstanceCommand() throws IOException {
    final ProvisionServiceInstanceCommand provisionServiceInstanceCommand = createProvisionServiceInstanceCommand();
    assertNotNull(provisionServiceInstanceCommand);

    final String json = objectMapper.writeValueAsString(provisionServiceInstanceCommand);
    assertNotNull(json);
    
    final Path provisionServiceInstanceCommandPath = this.referenceFiles.resolve("provisionServiceInstanceCommand.json");
    assertNotNull(provisionServiceInstanceCommandPath);
    
    final String expectedJson = new String(Files.readAllBytes(provisionServiceInstanceCommandPath), "UTF-8").trim();
    assertEquals(expectedJson, json);
  }

  private static final ProvisionServiceInstanceCommand createProvisionServiceInstanceCommand() {
    final Map<String, Object> parameters = new LinkedHashMap<>();
    parameters.put("foo", "bar");
    final ProvisionServiceInstanceCommand returnValue = new ProvisionServiceInstanceCommand("abc", "xyz", "basic", null /* no context */, true, "abcxyz", "abcxyz", parameters);
    returnValue.setProperty("argle_bargle", "bargle");
    return returnValue;
  }
  
  @Test
  public void testReadProvisionServiceInstanceCommandResponse() throws IOException {
    final Path provisionServiceInstanceCommandResponsePath = this.referenceFiles.resolve("provisionServiceInstanceCommand.response.json");
    assertNotNull(provisionServiceInstanceCommandResponsePath);
    final ProvisionServiceInstanceCommand.Response provisionServiceInstanceCommandResponse = objectMapper.readValue(provisionServiceInstanceCommandResponsePath.toUri().toURL(), ProvisionServiceInstanceCommand.Response.class);
    assertNotNull(provisionServiceInstanceCommandResponse);
    testProvisionServiceInstanceCommandResponse(provisionServiceInstanceCommandResponse);
  }

  private static final void testProvisionServiceInstanceCommandResponse(final ProvisionServiceInstanceCommand.Response provisionServiceInstanceCommandResponse) {
    assertNotNull(provisionServiceInstanceCommandResponse);
    final String operation = provisionServiceInstanceCommandResponse.getOperation();
    assertEquals("task_10", operation);
    assertEquals("bargle", provisionServiceInstanceCommandResponse.getProperty("argle_bargle"));
  }

  @Test
  public void testWriteProvisionServiceInstanceCommandResponse() throws IOException {
    final ProvisionServiceInstanceCommand.Response provisionServiceInstanceCommandResponse = createProvisionServiceInstanceCommandResponse();
    assertNotNull(provisionServiceInstanceCommandResponse);
    
    final String json = objectMapper.writeValueAsString(provisionServiceInstanceCommandResponse);
    assertNotNull(json);
    
    final Path provisionServiceInstanceCommandResponsePath = this.referenceFiles.resolve("provisionServiceInstanceCommand.response.json");
    assertNotNull(provisionServiceInstanceCommandResponsePath);
    
    final String expectedJson = new String(Files.readAllBytes(provisionServiceInstanceCommandResponsePath), "UTF-8").trim();
    assertEquals(expectedJson, json);    
  }

  private static final ProvisionServiceInstanceCommand.Response createProvisionServiceInstanceCommandResponse() {
    final ProvisionServiceInstanceCommand.Response provisionServiceInstanceCommandResponse = new ProvisionServiceInstanceCommand.Response("task_10");
    provisionServiceInstanceCommandResponse.setProperty("argle_bargle", "bargle");
    return provisionServiceInstanceCommandResponse;
  }

  @Test
  public void testReadUpdateServiceInstanceCommand() throws IOException {
    final Path updateServiceInstanceCommandPath = this.referenceFiles.resolve("updateServiceInstanceCommand.json");
    assertNotNull(updateServiceInstanceCommandPath);
    final UpdateServiceInstanceCommand updateServiceInstanceCommand = objectMapper.readValue(updateServiceInstanceCommandPath.toUri().toURL(), UpdateServiceInstanceCommand.class);
    assertNotNull(updateServiceInstanceCommand);
    testUpdateServiceInstanceCommand(updateServiceInstanceCommand);
  }

  private static final void testUpdateServiceInstanceCommand(final UpdateServiceInstanceCommand updateServiceInstanceCommand) {
    assertNotNull(updateServiceInstanceCommand);
    assertEquals("abc", updateServiceInstanceCommand.getInstanceId());
    assertEquals("xyz", updateServiceInstanceCommand.getServiceId());
    assertEquals("basic", updateServiceInstanceCommand.getPlanId());
    final Map<?, ?> context = updateServiceInstanceCommand.getContext();
    assertNotNull(context);
    assertTrue(context.isEmpty());
    final Map<?, ?> parameters = updateServiceInstanceCommand.getParameters();
    assertNotNull(parameters);
    assertTrue(parameters.isEmpty());
    assertTrue(updateServiceInstanceCommand.getAcceptsIncomplete());
    final PreviousValues previousValues = updateServiceInstanceCommand.getPreviousValues();
    assertNull(previousValues);
    assertEquals("bargle", updateServiceInstanceCommand.getProperty("argle_bargle"));
  }

  @Test
  public void testWriteUpdateServiceInstanceCommand() throws IOException {
    final UpdateServiceInstanceCommand updateServiceInstanceCommand = createUpdateServiceInstanceCommand();
    assertNotNull(updateServiceInstanceCommand);

    final String json = objectMapper.writeValueAsString(updateServiceInstanceCommand);
    assertNotNull(json);
    
    final Path updateServiceInstanceCommandPath = this.referenceFiles.resolve("updateServiceInstanceCommand.json");
    assertNotNull(updateServiceInstanceCommandPath);
    
    final String expectedJson = new String(Files.readAllBytes(updateServiceInstanceCommandPath), "UTF-8").trim();
    assertEquals(expectedJson, json);
  }

  private static final UpdateServiceInstanceCommand createUpdateServiceInstanceCommand() {
    final UpdateServiceInstanceCommand returnValue = new UpdateServiceInstanceCommand("abc", null, "xyz", "basic", null /* no parameters */, true, null);
    returnValue.setProperty("argle_bargle", "bargle");
    return returnValue;
  }
  
  @Test
  public void testReadUpdateServiceInstanceCommandResponse() throws IOException {
    final Path updateServiceInstanceCommandResponsePath = this.referenceFiles.resolve("updateServiceInstanceCommand.response.json");
    assertNotNull(updateServiceInstanceCommandResponsePath);
    final UpdateServiceInstanceCommand.Response updateServiceInstanceCommandResponse = objectMapper.readValue(updateServiceInstanceCommandResponsePath.toUri().toURL(), UpdateServiceInstanceCommand.Response.class);
    assertNotNull(updateServiceInstanceCommandResponse);
    testUpdateServiceInstanceCommandResponse(updateServiceInstanceCommandResponse);
  }

  private static final void testUpdateServiceInstanceCommandResponse(final UpdateServiceInstanceCommand.Response updateServiceInstanceCommandResponse) {
    assertNotNull(updateServiceInstanceCommandResponse);
    final String operation = updateServiceInstanceCommandResponse.getOperation();
    assertEquals("task_10", operation);
    assertEquals("bargle", updateServiceInstanceCommandResponse.getProperty("argle_bargle"));
  }

  @Test
  public void testWriteUpdateServiceInstanceCommandResponse() throws IOException {
    final UpdateServiceInstanceCommand.Response updateServiceInstanceCommandResponse = createUpdateServiceInstanceCommandResponse();
    assertNotNull(updateServiceInstanceCommandResponse);
    
    final String json = objectMapper.writeValueAsString(updateServiceInstanceCommandResponse);
    assertNotNull(json);
    
    final Path updateServiceInstanceCommandResponsePath = this.referenceFiles.resolve("updateServiceInstanceCommand.response.json");
    assertNotNull(updateServiceInstanceCommandResponsePath);
    
    final String expectedJson = new String(Files.readAllBytes(updateServiceInstanceCommandResponsePath), "UTF-8").trim();
    assertEquals(expectedJson, json);    
  }

  private static final UpdateServiceInstanceCommand.Response createUpdateServiceInstanceCommandResponse() {
    final UpdateServiceInstanceCommand.Response updateServiceInstanceCommandResponse = new UpdateServiceInstanceCommand.Response("task_10");
    updateServiceInstanceCommandResponse.setProperty("argle_bargle", "bargle");
    return updateServiceInstanceCommandResponse;
  }
  
}
