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

import com.fasterxml.jackson.databind.introspect.ClassIntrospector;

public final class ServiceBrokerMixInResolver implements ClassIntrospector.MixInResolver {
    
  public ServiceBrokerMixInResolver() {
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
  
