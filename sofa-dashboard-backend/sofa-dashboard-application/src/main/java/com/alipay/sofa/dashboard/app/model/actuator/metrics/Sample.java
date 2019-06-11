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

/**
 * A model copy of {@link org.springframework.boot.actuate.metrics.MetricsEndpoint.Sample}
 * A measurement sample combining a {@link io.micrometer.core.instrument.Statistic statistic} and a value.
 */
public class Sample {

    private String statistic;

    private Double value;

    public String getStatistic() {
        return statistic;
    }

    public void setStatistic(String statistic) {
        this.statistic = statistic;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MeasurementSample{" + "statistic=" + this.statistic + ", value=" + this.value + '}';
    }

}