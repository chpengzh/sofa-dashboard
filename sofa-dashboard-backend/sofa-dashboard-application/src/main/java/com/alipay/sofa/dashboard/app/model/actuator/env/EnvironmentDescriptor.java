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
package com.alipay.sofa.dashboard.app.model.actuator.env;

import com.alipay.sofa.dashboard.model.monitor.EnvironmentInfo;
import org.springframework.core.env.Environment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A model copy of {@link org.springframework.boot.actuate.env.EnvironmentEndpoint.EnvironmentDescriptor}
 * A description of an {@link Environment}.
 */
public class EnvironmentDescriptor implements Serializable {

    private static final int                     serialVersionUID = 0x11;

    private final List<String>                   activeProfiles   = new ArrayList<>();

    private final List<PropertySourceDescriptor> propertySources  = new ArrayList<>();

    public EnvironmentInfo toEnvironmentInfo() {
        EnvironmentInfo result = new EnvironmentInfo();
        result.setActiveProfiles(activeProfiles);
        result.setPropertySources(propertySources.stream().map(it -> {
            EnvironmentInfo.PropertySourceDescriptor next =
                    new EnvironmentInfo.PropertySourceDescriptor();

            next.setName(it.getName());

            next.setProperties(it.getProperties().entrySet().stream().map(entry -> {
                Map<String, Object> valItem = new HashMap<>();
                String key = entry.getKey();
                Object value = entry.getValue().getValue();
                valItem.put(key, value);
                return valItem;
            }).collect(Collectors.toList()));

            return next;
        }).collect(Collectors.toList()));

        return result;
    }

    public List<String> getActiveProfiles() {
        return this.activeProfiles;
    }

    public List<PropertySourceDescriptor> getPropertySources() {
        return this.propertySources;
    }

}