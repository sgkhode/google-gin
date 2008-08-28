/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.inject.rebind.binding;

import com.google.gwt.inject.rebind.NameGenerator;
import com.google.inject.Key;
import com.google.inject.Provider;

import java.util.Collections;
import java.util.Set;

/**
 * A binding to call the requested {@link com.google.inject.Provider}.
 */
public class BindProviderBinding implements Binding {
  private final Key<?> providerKey;

  public BindProviderBinding(Key<? extends Provider<?>> providerKey) {
    this.providerKey = providerKey;
  }

  public String getCreatorMethodBody(NameGenerator nameGenerator) {
    return "return " + nameGenerator.getGetterMethodName(providerKey) + "().get();";
  }

  public Set<Key<?>> getRequiredKeys() {
    return Collections.<Key<?>>singleton(providerKey);
  }
}
