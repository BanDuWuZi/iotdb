/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.db.metadata.schemaRegion;

import org.apache.iotdb.commons.path.PartialPath;
import org.apache.iotdb.consensus.ConsensusFactory;
import org.apache.iotdb.db.conf.IoTDBDescriptor;
import org.apache.iotdb.db.queryengine.common.schematree.ClusterSchemaTree;
import org.apache.iotdb.db.schemaengine.schemaregion.ISchemaRegion;
import org.apache.iotdb.db.schemaengine.schemaregion.write.req.SchemaRegionWritePlanFactory;
import org.apache.iotdb.db.schemaengine.template.Template;

import org.apache.tsfile.enums.TSDataType;
import org.apache.tsfile.file.metadata.enums.CompressionType;
import org.apache.tsfile.file.metadata.enums.TSEncoding;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.apache.iotdb.commons.schema.SchemaConstant.ALL_MATCH_SCOPE;

public class SchemaRegionSimpleRecoverTest extends AbstractSchemaRegionTest {

  private String schemaRegionConsensusProtocolClass;

  public SchemaRegionSimpleRecoverTest(SchemaRegionTestParams testParams) {
    super(testParams);
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();
    schemaRegionConsensusProtocolClass =
        IoTDBDescriptor.getInstance().getConfig().getSchemaRegionConsensusProtocolClass();
    IoTDBDescriptor.getInstance()
        .getConfig()
        .setSchemaRegionConsensusProtocolClass(ConsensusFactory.SIMPLE_CONSENSUS);
  }

  @After
  public void tearDown() throws Exception {
    super.tearDown();
    IoTDBDescriptor.getInstance()
        .getConfig()
        .setSchemaRegionConsensusProtocolClass(schemaRegionConsensusProtocolClass);
  }

  @Test
  public void testRecoverWithAlignedTemplate() throws Exception {
    ISchemaRegion schemaRegion = getSchemaRegion("root.sg", 0);
    int templateId = 1;
    Template template =
        new Template(
            "t1",
            Arrays.asList("s1", "s2"),
            Arrays.asList(TSDataType.DOUBLE, TSDataType.INT32),
            Arrays.asList(TSEncoding.RLE, TSEncoding.RLE),
            Arrays.asList(CompressionType.SNAPPY, CompressionType.SNAPPY),
            true);
    template.setId(templateId);
    schemaRegion.activateSchemaTemplate(
        SchemaRegionWritePlanFactory.getActivateTemplateInClusterPlan(
            new PartialPath("root.sg.d1"), 2, templateId),
        template);
    ClusterSchemaTree schemaTree =
        schemaRegion.fetchSchema(
            ALL_MATCH_SCOPE, Collections.singletonMap(templateId, template), true, true);
    Assert.assertTrue(schemaTree.getAllDevices().get(0).isAligned());

    simulateRestart();
    schemaRegion = getSchemaRegion("root.sg", 0);
    schemaTree =
        schemaRegion.fetchSchema(
            ALL_MATCH_SCOPE, Collections.singletonMap(templateId, template), true, true);
    Assert.assertTrue(schemaTree.getAllDevices().get(0).isAligned());
  }
}
