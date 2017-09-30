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

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.databind.introspect.ClassIntrospector;

import org.junit.Before;
import org.junit.Test;

import org.microbean.servicebroker.api.query.state.Catalog.Service.Plan.Schema.InputParameters;

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
    final Map<String, String> billingAccount = new LinkedHashMap<>();
    billingAccount.put("description", "Billing account number used to charge use of shared fake server.");
    billingAccount.put("type", "string");

    final Map<String, Object> properties = new LinkedHashMap<>();
    properties.put("billing-account", billingAccount);

    final Map<String, Object> parameters = new LinkedHashMap<>();
    parameters.put("$schema", "http://json-schema.org/draft-04/schema#");
    parameters.put("type", "object");
    parameters.put("properties", properties);

    final InputParameters inputParameters = new InputParameters(parameters);

    final String json = objectMapper.writeValueAsString(inputParameters);
    assertNotNull(json);

    final Path inputParametersPath = this.referenceFiles.resolve("inputParameters.json");
    assertNotNull(inputParametersPath);
    
    final String expectedJson = new String(Files.readAllBytes(inputParametersPath), "UTF-8").trim();
    assertEquals(expectedJson, json);
    
  }

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
          System.out.println("*** using " + returnValue + " for " + c);
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
