/*
 * Copyright 2017 StreamSets Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.stage.origin.ipctokafka;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.streamsets.pipeline.api.Config;
import com.streamsets.pipeline.api.StageException;
import com.streamsets.pipeline.api.StageUpgrader;
import com.streamsets.pipeline.config.upgrade.UpgraderTestUtils;
import com.streamsets.pipeline.stage.destination.kafka.KafkaTargetUpgrader;
import com.streamsets.pipeline.stage.util.tls.TlsConfigBeanUpgraderTestUtil;
import com.streamsets.pipeline.upgrader.SelectorStageUpgrader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TestSdcIpcToKafkaUpgrader {

  private StageUpgrader upgrader;
  private List<Config> configs;
  private StageUpgrader.Context context;

  @Before
  public void setUp() {
    URL yamlResource = ClassLoader.getSystemClassLoader().getResource("upgrader/SdcIpcToKafkaDSource.yaml");
    upgrader = new SelectorStageUpgrader("stage", new SdcIpcToKafkaUpgrader(), yamlResource);
    configs = new ArrayList<>();
    context = Mockito.mock(StageUpgrader.Context.class);
  }

  @Test
  public void testV2toV3() throws Exception {
    List<Config> configs = new ArrayList<>();
    configs.add(new Config("configs.maxRpcRequestSize", 10));
    new SdcIpcToKafkaUpgrader().upgrade("l", "s", "i", 2, 3, configs);
    Assert.assertEquals(1, configs.size());
    Assert.assertEquals("configs.maxRpcRequestSize", configs.get(0).getName());
    Assert.assertEquals(10 * 1000, configs.get(0).getValue());
  }

  @Test
  public void testV3toV4() throws StageException {
    TlsConfigBeanUpgraderTestUtil.testRawKeyStoreConfigsToTlsConfigBeanUpgrade(
        "configs.",
        new SdcIpcToKafkaUpgrader(),
        4
    );
  }

  @Test
  public void testV4ToV5() {
    Mockito.doReturn(4).when(context).getFromVersion();
    Mockito.doReturn(5).when(context).getToVersion();

    String configPrefix = "configs.tlsConfigBean.";
    configs = upgrader.upgrade(configs, context);

    UpgraderTestUtils.assertExists(configs, configPrefix + "useRemoteKeyStore", false);
    UpgraderTestUtils.assertExists(configs, configPrefix + "privateKey", "");
    UpgraderTestUtils.assertExists(configs, configPrefix + "certificateChain", new ArrayList<>());
    UpgraderTestUtils.assertExists(configs, configPrefix + "trustedCertificates", new ArrayList<>());
  }

  @Test
  public void testV5toV6() {
    Mockito.doReturn(5).when(context).getFromVersion();
    Mockito.doReturn(6).when(context).getToVersion();

    configs.add(new Config("conf.kafkaProducerConfigs", ImmutableList.of(
        ImmutableMap.of("key", "security.protocol", "value", "SASL_PLAINTEXT"),
        ImmutableMap.of("key", "sasl.kerberos.service.name", "value", "kafka"),
        ImmutableMap.of("key", "ssl.truststore.type", "value", "JKS"),
        ImmutableMap.of("key", "ssl.truststore.location", "value", "/tmp/truststore"),
        ImmutableMap.of("key", "ssl.truststore.password", "value", "trustpwd"),
        ImmutableMap.of("key", "ssl.keystore.type", "value", "PKCS12"),
        ImmutableMap.of("key", "ssl.keystore.location", "value", "/tmp/keystore"),
        ImmutableMap.of("key", "ssl.keystore.password", "value", "keystpwd"),
        ImmutableMap.of("key", "ssl.key.password", "value", "keypwd"),
        ImmutableMap.of("key", "ssl.enabled.protocols", "value", "TlSv1.2, TLSv1.3")
    )));

    configs.add(new Config("conf.provideKeytab", true));
    configs.add(new Config("conf.userKeytab", "userKeytab"));
    configs.add(new Config("conf.userPrincipal", "sdc/sdc@CLUSTER"));

    configs = upgrader.upgrade(configs, context);

    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.securityOption", "SASL_PLAINTEXT");
    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.kerberosServiceName", "kafka");
    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.provideKeytab", true);
    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.userKeytab", "userKeytab");
    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.userPrincipal", "sdc/sdc@CLUSTER");
    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.truststoreType", "JKS");
    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.truststoreFile", "/tmp/truststore");
    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.truststorePassword", "trustpwd");
    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.keystoreType", "PKCS12");
    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.keystoreFile", "/tmp/keystore");
    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.keystorePassword", "keystpwd");
    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.keyPassword", "keypwd");
    UpgraderTestUtils.assertExists(configs, "conf.securityConfig.enabledProtocols", "TlSv1.2, TLSv1.3");

    UpgraderTestUtils.assertExists(configs, "conf.kafkaProducerConfigs", ImmutableList.of());
  }
}
