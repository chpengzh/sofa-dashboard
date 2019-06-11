/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.dashboard.app.model.actuator.mappings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A model copy of {@link org.springframework.boot.actuate.web.mappings.MappingsEndpoint.ContextMappings}
 * A description of an application context's request mappings. Primarily intended for
 * serialization to JSON.
 */
public class ContextMappings implements Serializable {

    private static final int          serialVersionUID = 0x11;

    private String                    parentId;

    private final Map<String, Object> mappings         = new HashMap<>();

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Map<String, Object> getMappings() {
        return mappings;
    }
}