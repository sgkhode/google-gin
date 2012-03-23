/*
 * Copyright 2011 Google Inc.
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
package com.google.gwt.inject.rebind;

import static org.easymock.classextension.EasyMock.anyObject;
import static org.easymock.classextension.EasyMock.capture;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.isNull;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.inject.rebind.reflect.FieldLiteral;
import com.google.gwt.inject.rebind.reflect.MethodLiteral;
import com.google.gwt.inject.rebind.util.InjectorMethod;
import com.google.gwt.inject.rebind.util.InjectorWriteContext;
import com.google.gwt.inject.rebind.util.MemberCollector;
import com.google.gwt.inject.rebind.util.MethodCallUtil;
import com.google.gwt.inject.rebind.util.NameGenerator;
import com.google.gwt.inject.rebind.util.SourceSnippets;
import com.google.gwt.inject.rebind.util.SourceWriteUtil;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;
import junit.framework.TestCase;
import org.easymock.Capture;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class GinjectorOutputterTest extends TestCase {

  public void testOutputStaticInjections_superClassMembers() throws Exception {
    GinjectorBindings bindings = createMock(GinjectorBindings.class);
    expect(bindings.getStaticInjectionRequests()).andStubReturn(
        Collections.<Class<?>>singleton(SubClass.class));
    expect(bindings.getNameGenerator()).andStubReturn(createNiceMock(NameGenerator.class));

    Provider<MemberCollector> collectorProvider =
        Providers.of(createNiceMock(MemberCollector.class));

    TypeLiteral<SuperClass> superClass = TypeLiteral.get(SuperClass.class);

    Capture<FieldLiteral<SuperClass>> fieldCapture = new Capture<FieldLiteral<SuperClass>>();
    Capture<MethodLiteral<SuperClass, Method>> methodCapture =
        new Capture<MethodLiteral<SuperClass, Method>>();

    MethodCallUtil methodCallUtil = createMock(MethodCallUtil.class);
    SourceWriteUtil sourceWriteUtil = createMock(SourceWriteUtil.class);
    expect(methodCallUtil.createMethodCallWithInjection(capture(methodCapture),
        (String) anyObject(), (NameGenerator) anyObject(), (List<InjectorMethod>) anyObject()))
        .andReturn(SourceSnippets.forText(""));
    expect(sourceWriteUtil.createFieldInjection(capture(fieldCapture), (String) anyObject(),
        (NameGenerator) anyObject(), (List<InjectorMethod>) anyObject()))
        .andReturn(SourceSnippets.forText(""));

    sourceWriteUtil.writeMethod((SourceWriter) isNull(), (String) anyObject(),
        (String) anyObject());
    expectLastCall().anyTimes();

    sourceWriteUtil.writeMethods((Iterable<InjectorMethod>) anyObject(), (SourceWriter) isNull(),
        (InjectorWriteContext) anyObject());
    expectLastCall().anyTimes();

    InjectorWriteContext writeContext = createMock(InjectorWriteContext.class);

    replay(bindings, methodCallUtil, sourceWriteUtil, writeContext);

    GinjectorOutputter ginjectorOutputter = new GinjectorOutputter(TreeLogger.NULL,
        collectorProvider, null, null, methodCallUtil, null, null, FakeGinjector.class, null, null,
        null);
    ginjectorOutputter.outputStaticInjections(bindings, new StringBuilder(), sourceWriteUtil,
        writeContext);

    verify(sourceWriteUtil, methodCallUtil, writeContext);

    assertEquals(superClass, methodCapture.getValue().getDeclaringType());
    assertEquals(superClass, fieldCapture.getValue().getDeclaringType());
  }

  public static class SuperClass {
    @Inject static String foo;

    @Inject
    static void setBar(String ignored) {}
  }

  public static class SubClass extends SuperClass {}

  public static interface FakeGinjector extends Ginjector {}
}
