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
package com.alipay.sofa.dashboard.app.model.actuator.metrics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A model copy of {@link org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricResponse}
 * Response payload for a metric name selector.
 */
public class MetricResponse implements Serializable {

    private static final int         serialVersionUID = 0x11;

    private String                   name;

    private String                   description;

    private String                   baseUnit;

    private final List<Sample>       measurements     = new ArrayList<>();

    private final List<AvailableTag> availableTags    = new ArrayList<>();

    public Number readDefaultVal() {
        if (!measurements.isEmpty()) {
            Sample defaultSample = measurements.get(0);
            return defaultSample.getValue();
        }
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public List<Sample> getMeasurements() {
        return measurements;
    }

    public List<AvailableTag> getAvailableTags() {
        return availableTags;
    }
}