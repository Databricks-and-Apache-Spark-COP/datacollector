/**
 * Copyright 2016 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.datacollector.restapi.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MultiStatusResponseJson<T> {

  private final List<T> successEntities;
  private final List<String> errorMessages;

  @JsonCreator
  public MultiStatusResponseJson(
      @JsonProperty("successEntities") List<T> successEntities,
      @JsonProperty("errorMessages") List<String> errorMessages
  ) {
    this.successEntities = successEntities;
    this.errorMessages = errorMessages;
  }

  public final List<T> getSuccessEntities() {
    return successEntities;
  }

  public final List<String> getErrorMessages() {
    return errorMessages;
  }
}
