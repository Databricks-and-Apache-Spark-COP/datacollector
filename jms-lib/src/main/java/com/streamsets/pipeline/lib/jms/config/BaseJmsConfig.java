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
package com.streamsets.pipeline.lib.jms.config;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ValueChooserModel;

import java.util.HashMap;
import java.util.Map;

public class BaseJmsConfig {
  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "JMS Initial Context Factory",
      description = "ActiveMQ example: org.apache.activemq.jndi.ActiveMQInitialContextFactory",
      displayPosition = 10,
      displayMode = ConfigDef.DisplayMode.BASIC,
      group = "JMS"
  )
  public String initialContextFactory;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "JNDI Connection Factory",
      description = "ActiveMQ example: ConnectionFactory",
      displayPosition = 20,
      displayMode = ConfigDef.DisplayMode.BASIC,
      group = "JMS"
  )
  public String connectionFactory;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "JMS Provider URL",
      description = "ActiveMQ example: tcp://mqserver:61616",
      displayPosition = 30,
      displayMode = ConfigDef.DisplayMode.BASIC,
      group = "JMS"
  )
  public String providerURL;

  @ConfigDef(
      required = false,
      type = ConfigDef.Type.MODEL,
      defaultValue = "UNKNOWN",
      label = "JMS Destination Type",
      description = "Specify the JMS destination type when validation fails with NamingException, destination not found",
      displayPosition = 70,
      displayMode = ConfigDef.DisplayMode.BASIC,
      group = "JMS"
  )
  @ValueChooserModel(DestinationTypeChooserValues.class)
  public DestinationType destinationType = DestinationType.UNKNOWN; // NOTE: same as above

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.BOOLEAN,
      defaultValue = "false",
      label = "Use ClientID",
      description = "Check to specify a clientID for this JMS connection. e.g. Required for Durable Topic Subscriptions",
      displayPosition = 75,
      displayMode = ConfigDef.DisplayMode.BASIC,
      group = "JMS"
)
  public Boolean useClientID = false;
  
  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "Client ID",
      description = "If specified, must be a unique string used by a single JMS connection instance. Required for Durable Topic Subscriptions",
      displayPosition = 77,
      displayMode = ConfigDef.DisplayMode.BASIC,
      group = "JMS",
      dependsOn = "useClientID",
      triggeredByValue = "true"
  )
  public String clientID = null;

  @ConfigDef(
      required = false,
      type = ConfigDef.Type.MAP,
      defaultValue = "",
      label = "Additional JMS Configuration Properties",
      description = "Additional properties to pass to the underlying JMS context.",
      displayPosition = 999,
      displayMode = ConfigDef.DisplayMode.ADVANCED,
      group = "JMS"
  )
  public Map<String, String> contextProperties = new HashMap<>();
}
