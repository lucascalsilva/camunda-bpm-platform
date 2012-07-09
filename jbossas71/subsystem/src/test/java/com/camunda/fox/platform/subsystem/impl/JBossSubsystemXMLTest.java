/**
 * Copyright (C) 2011, 2012 camunda services GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.camunda.fox.platform.subsystem.impl;

import java.util.List;

import junit.framework.Assert;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.junit.Test;

import com.arjuna.ats.jbossatx.logging.jbossatxI18NLogger;
import com.camunda.fox.platform.FoxPlatformException;
import com.camunda.fox.platform.subsystem.impl.extension.Attribute;
import com.camunda.fox.platform.subsystem.impl.extension.Element;
import com.camunda.fox.platform.subsystem.impl.extension.FoxPlatformExtension;

/**
 *
 * @author nico.rehwaldt@camunda.com
 * @author christian.lipphardt@camunda.com
 */
public class JBossSubsystemXMLTest extends AbstractSubsystemTest {

  public static final String SUBSYSTEM_WITH_SINGLE_ENGINE = "subsystemWithSingleEngine.xml";
  public static final String SUBSYSTEM_WITH_ENGINES = "subsystemWithEngines.xml";
  public static final String SUBSYSTEM_WITH_PROCESS_ENGINES_ELEMENT_ONLY = "subsystemWithProcessEnginesElementOnly.xml";
  public static final String SUBSYSTEM_WITH_ENGINES_AND_PROPERTIES = "subsystemWithEnginesAndProperties.xml";
  public static final String SUBSYSTEM_WITH_DUPLICATE_ENGINE_NAMES = "subsystemWithDuplicateEngineNames.xml";
  public static final String SUBSYSTEM_WITH_JOB_EXECUTOR = "subsystemWithJobExecutor.xml";
  public static final String SUBSYSTEM_WITH_PROCESS_ENGINES_AND_JOB_EXECUTOR = "subsystemWithProcessEnginesAndJobExecutor.xml";
  public static final String SUBSYSTEM_WITH_JOB_EXECUTOR_AND_PROPERTIES = "subsystemWithJobExecutorAndProperties.xml";

  public JBossSubsystemXMLTest() {
    super(FoxPlatformExtension.SUBSYSTEM_NAME, new FoxPlatformExtension());
  }

  @Test
  public void testParseSubsystemXml() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_PROCESS_ENGINES_ELEMENT_ONLY);
    System.out.println(normalizeXML(subsystemXml));
    
    List<ModelNode> operations = parse(subsystemXml);
    System.out.println(operations);
    Assert.assertEquals(1, operations.size());
  }
  
  @Test
  public void testParseSubsystemXmlWithEngines() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_ENGINES);
    System.out.println(normalizeXML(subsystemXml));
    
    List<ModelNode> operations = parse(subsystemXml);
    System.out.println(operations);
    Assert.assertEquals(3, operations.size());
  }
  
  @Test
  public void testParseSubsystemXmlWithEnginesAndProperties() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_ENGINES_AND_PROPERTIES);
    System.out.println(normalizeXML(subsystemXml));
    
    List<ModelNode> operations = parse(subsystemXml);
    System.out.println(operations);
    Assert.assertEquals(5, operations.size());
  }
  
  @Test
  public void testInstallSubsystemXml() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_PROCESS_ENGINES_ELEMENT_ONLY);
    System.out.println(normalizeXML(subsystemXml));
    KernelServices services = installInController(subsystemXml);
//    services.getContainer().dumpServices();
    Assert.assertEquals(4, services.getContainer().getServiceNames().size());
  }
  
  @Test
  public void testInstallSubsystemWithEnginesXml() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_ENGINES);
    System.out.println(normalizeXML(subsystemXml));
    KernelServices services = installInController(subsystemXml);
//    services.getContainer().dumpServices();
    Assert.assertEquals(6, services.getContainer().getServiceNames().size());
  }
  
  @Test
  public void testInstallSubsystemWithEnginesAndPropertiesXml() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_ENGINES_AND_PROPERTIES);
    System.out.println(normalizeXML(subsystemXml));
    KernelServices services = installInController(subsystemXml);
//    services.getContainer().dumpServices();
    Assert.assertEquals(8, services.getContainer().getServiceNames().size());
  }
  
  @Test
  public void testInstallSubsystemWithDupliacteEngineNamesXml() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_DUPLICATE_ENGINE_NAMES);
    System.out.println(normalizeXML(subsystemXml));
    try {
      installInController(subsystemXml);
//    services.getContainer().dumpServices();
    } catch (FoxPlatformException fpe) {
      Assert.assertTrue("Duplicate process engine detected!", fpe.getMessage().contains("A process engine with name '__test' already exists."));
    }
  }

  @Test
  public void testInstallSubsystemWithSingleEngineXml() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_SINGLE_ENGINE);
    System.out.println(normalizeXML(subsystemXml));
    KernelServices services = installInController(subsystemXml);
//    services.getContainer().dumpServices();
    Assert.assertEquals(5, services.getContainer().getServiceNames().size());
    String persistedSubsystemXml = services.getPersistedSubsystemXml();
    compareXml(null, subsystemXml, persistedSubsystemXml);
  }
  
  @Test
  public void testParseSubsystemWithJobExecutorXml() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_JOB_EXECUTOR);
    System.out.println(normalizeXML(subsystemXml));
    
    List<ModelNode> operations = parse(subsystemXml);
    System.out.println(operations);
    Assert.assertEquals(4, operations.size());
    
    ModelNode jobExecutor = operations.get(1);
    PathAddress pathAddress = PathAddress.pathAddress(jobExecutor.get(ModelDescriptionConstants.OP_ADDR));
    Assert.assertEquals(2, pathAddress.size());

    PathElement element = pathAddress.getElement(0);
    Assert.assertEquals(ModelDescriptionConstants.SUBSYSTEM, element.getKey());
    Assert.assertEquals(FoxPlatformExtension.SUBSYSTEM_NAME, element.getValue());
    element = pathAddress.getElement(1);
    Assert.assertEquals(Element.JOB_EXECUTOR.getLocalName(), element.getKey());
    Assert.assertEquals(Attribute.DEFAULT.getLocalName(), element.getValue());
    
    Assert.assertEquals("job-executor-tp", jobExecutor.get(Element.THREAD_POOL_NAME.getLocalName()).asString());
    
    ModelNode jobAcquisition = operations.get(2);
    Assert.assertEquals("default", jobAcquisition.get(Attribute.NAME.getLocalName()).asString());
    Assert.assertEquals("SEQUENTIAL", jobAcquisition.get(Element.ACQUISITION_STRATEGY.getLocalName()).asString());
    Assert.assertTrue(jobAcquisition.has(Element.PROPERTIES.getLocalName()));
    Assert.assertTrue(!jobAcquisition.hasDefined(Element.PROPERTIES.getLocalName()));
    
    jobAcquisition = operations.get(3);
    Assert.assertEquals("anders", jobAcquisition.get(Attribute.NAME.getLocalName()).asString());
    Assert.assertEquals("SEQUENTIAL", jobAcquisition.get(Element.ACQUISITION_STRATEGY.getLocalName()).asString());
  }
  
  @Test
  public void testInstallSubsystemWithJobExecutorXml() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_JOB_EXECUTOR);
    System.out.println(normalizeXML(subsystemXml));
    KernelServices services = installInController(subsystemXml);
    //services.getContainer().dumpServices();
    Assert.assertEquals(5, services.getContainer().getServiceNames().size());
  }
  
  @Test
  public void testParseSubsystemWithJobExecutorAndPropertiesXml() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_JOB_EXECUTOR_AND_PROPERTIES);
    System.out.println(normalizeXML(subsystemXml));
    
    List<ModelNode> operations = parse(subsystemXml);
    Assert.assertEquals(4, operations.size());
  }
  
  @Test
  public void testInstallSubsystemWithJobExecutorAndPropertiesXml() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_JOB_EXECUTOR_AND_PROPERTIES);
    System.out.println(normalizeXML(subsystemXml));
    KernelServices services = installInController(subsystemXml);
    //services.getContainer().dumpServices();
    Assert.assertEquals(5, services.getContainer().getServiceNames().size());
  }
  
  
  @Test
  public void testParseSubsystemXmlWithEnginesAndJobExecutor() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_PROCESS_ENGINES_AND_JOB_EXECUTOR);
    System.out.println(normalizeXML(subsystemXml));
    
    List<ModelNode> operations = parse(subsystemXml);
    Assert.assertEquals(6, operations.size());
  }
  
  @Test
  public void testInstallSubsystemXmlWithEnginesAndJobExecutor() throws Exception {
    String subsystemXml = FileUtils.readFile(SUBSYSTEM_WITH_PROCESS_ENGINES_AND_JOB_EXECUTOR);
    System.out.println(normalizeXML(subsystemXml));
    KernelServices services = installInController(subsystemXml);
    //services.getContainer().dumpServices();
    Assert.assertEquals(7, services.getContainer().getServiceNames().size());
    
    String persistedSubsystemXml = services.getPersistedSubsystemXml();
    System.out.println(persistedSubsystemXml);
    compareXml(null, subsystemXml, persistedSubsystemXml);
  }
  
}
