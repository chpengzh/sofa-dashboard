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
package com.alipay.sofa.dashboard.app.dao.api;

import com.alipay.sofa.dashboard.app.model.AppHost;
import com.alipay.sofa.dashboard.model.monitor.*;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

/**
 * Data access layer for actuator
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface ActuatorClient {

    /**
     * Query environment info from application
     *
     * @param source application source definition
     * @return environment information
     * @throws RestClientException request error
     */
    @NonNull
    EnvironmentInfo getEnvironmentInfo(@NonNull AppHost source) throws RestClientException;

    /**
     * Query metrics info from application
     *
     * @param source application source definition
     * @return Metrics info from remote
     * @throws RestClientException request error
     */
    @NonNull
    List<MetricsInfo> getMetricsInfo(@NonNull AppHost source) throws RestClientException;

    /**
     * Query health info from application
     *
     * @param source application source definition
     * @return Health info from remote
     * @throws RestClientException request error
     */
    @NonNull
    HealthInfo getHealthInfo(@NonNull AppHost source) throws RestClientException;

    /**
     * Query basic info from application
     *
     * @param source application source definition
     * @return Application basic info
     * @throws RestClientException request error
     */
    @NonNull
    Map<String, Object> getAppInfo(@NonNull AppHost source) throws RestClientException;

    /**
     * Query thread info details
     *
     * @param source application source definition
     * @return Thread info details
     * @throws RestClientException request error
     */
    DetailThreadInfo getDetailsThread(@NonNull AppHost source) throws RestClientException;

    /**
     * Query heap memory state from application
     *
     * @param source application source definition
     * @return Heap memory info
     * @throws RestClientException request error
     */
    @NonNull
    MemoryHeapInfo getHeapMemory(@NonNull AppHost source) throws RestClientException;

    /**
     * Query non-heap memory state from application
     *
     * @param source application source definition
     * @return Non-heap memory info
     * @throws RestClientException request error
     */
    @NonNull
    MemoryNonHeapInfo getNonHeapMemory(@NonNull AppHost source) throws RestClientException;

    /**
     * Query runtime logger definition from application
     *
     * @param source application source definition
     * @return Loggers info
     * @throws RestClientException request error
     */
    @NonNull
    LoggersInfo getLoggers(@NonNull AppHost source) throws RestClientException;

    /**
     * Query mappings definition from application
     *
     * @param source application source definition
     * @return Mappings info
     * @throws RestClientException request error
     */
    @NonNull
    Map<String, MappingsInfo> getMappings(@NonNull AppHost source) throws RestClientException;

    /**
     * Query thread dumps details from application
     *
     * @param source application source definition
     * @return Thread dumps info from remote
     * @throws RestClientException request error
     */
    @NonNull
    List<ThreadDumpInfo> getThreadDumps(@NonNull AppHost source) throws RestClientException;
}
