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
package com.alipay.sofa.dashboard.app.actuator;

import com.alipay.sofa.dashboard.app.dao.api.ActuatorClient;
import com.alipay.sofa.dashboard.app.model.AppHost;
import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.model.monitor.*;
import com.alipay.sofa.dashboard.spi.MonitorManager;
import com.alipay.sofa.dashboard.utils.DashboardUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 应用监控管理器，基于 Spring Boot Actuator 实现
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/5/7 10:34 PM
 **/
@Component
public class ActuatorMonitorManager implements MonitorManager {

    @Autowired
    private AppDynamicCacheManager cacheManager;

    @Autowired
    private ActuatorClient         actuatorClient;

    @Override
    public EnvironmentInfo fetchEnvironment(Object source) {
        return actuatorClient.getEnvironmentInfo(getHost(source));
    }

    @Override
    public List<MetricsInfo> fetchMetrics(Object source) {
        // do not support
        return null;
    }

    @Override
    public HealthInfo fetchHealth(Object source) {
        return actuatorClient.getHealthInfo(getHost(source));
    }

    @Override
    public Map fetchInfo(Object source) {
        return actuatorClient.getAppInfo(getHost(source));
    }

    @Override
    public List<DetailsItem> fetchDetailsThread(Object source) {
        List<DetailThreadInfo> data = cacheManager
                .getCachedDetailThreadsByAppId(getAppId(source))
                .getReadOnlyQueue();
        List<DetailsItem> result = new ArrayList<>();
        data.forEach((item) -> {
            result.add(item.getLive());
            result.add(item.getDaemon());
            result.add(item.getPeak());
        });
        return result;
    }

    @Override
    public List<DetailsItem> fetchHeapMemory(Object source) {
        List<MemoryHeapInfo> data = cacheManager
                .getCachedMemoryHeapInfoByAppId(getAppId(source))
                .getReadOnlyQueue();
        List<DetailsItem> result = new ArrayList<>();
        data.forEach((item) -> {
            result.add(item.getSize());
            result.add(item.getUsed());
        });
        return result;
    }

    @Override
    public List<DetailsItem> fetchNonHeapMemory(Object source) {
        List<MemoryNonHeapInfo> data = cacheManager
                .getCachedMemoryNonHeapInfoByAppId(getAppId(source))
                .getReadOnlyQueue();
        List<DetailsItem> result = new ArrayList<>();
        data.forEach((item) -> {
            result.add(item.getMetaspace());
            result.add(item.getSize());
            result.add(item.getUsed());
        });
        return result;
    }

    @Override
    public LoggersInfo fetchLoggers(Object source) {
        return actuatorClient.getLoggers(getHost(source));
    }

    @Override
    public Map<String, MappingsInfo> fetchMappings(Object source) {
        return actuatorClient.getMappings(getHost(source));
    }

    @Override
    public List<ThreadDumpInfo> fetchThreadDump(Object source) {
        return actuatorClient.getThreadDumps(getHost(source));
    }

    private AppHost getHost(Object source) {
        String hostAndPort = String.valueOf(source);
        URI uri = URI.create(hostAndPort.startsWith("http://") ? hostAndPort : "http://"
                                                                               + hostAndPort);
        AppHost host = new AppHost();
        host.setHost(uri.getHost());
        host.setRestPort(uri.getPort() <= -1 ? 80 : uri.getPort());
        return host;
    }

    private String getAppId(Object source) {
        String targetHost = String.valueOf(source);
        String[] address = targetHost.split(SofaDashboardConstants.COLON);
        return DashboardUtil.simpleEncode(address[0], Integer.valueOf(address[1]));
    }
}
