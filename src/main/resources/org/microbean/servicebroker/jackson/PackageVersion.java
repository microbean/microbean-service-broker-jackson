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

import com.fasterxml.jackson.core.Version;

import com.fasterxml.jackson.core.util.VersionUtil;

/**
 * A class that can {@linkplain #version() supply a
 * <code>Version</code>} for this general project in a way that
 * satisfies the requirements of the {@link
 * VersionUtil#packageVersionFor(Class)} method.
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see #version()
 *
 * @see VersionUtil#packageVersionFor(Class)
 *
 * @version ${project.version}
 */
public final class PackageVersion {


  /*
   * Instance fields.
   */

  
  /**
   * The {@link Version} returned by the {@link #version()} method.
   *
   * <p>This field is never {@code null}.</p>
   */
  private final Version version;


  /*
   * Constructors.
   */
  

  /**
   * Creates a new {@link PackageVersion}.
   */
  public PackageVersion() {
    super();
    this.version = VersionUtil.parseVersion("${project.version}", "${project.groupId}", "${project.artifactId}");
    assert this.version != null;
  }


  /*
   * Instance methods.
   */
  

  /**
   * Returns a {@link Version} for this general project.
   *
   * <p>This method never returns {@code null}.</p>
   *
   * @return a non-{@code null} {@link Version}
   *
   * @see VersionUtil#packageVersionFor(Class)
   */
  public final Version version() {
    return this.version;
  }

}
