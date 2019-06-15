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
import com.alipay.sofa.dashboard.app.zookeeper.ZookeeperApplicationManager;
import com.alipay.sofa.dashboard.model.Application;
import com.alipay.sofa.dashboard.model.monitor.DetailThreadInfo;
import com.alipay.sofa.dashboard.model.monitor.MemoryHeapInfo;
import com.alipay.sofa.dashboard.model.monitor.MemoryNonHeapInfo;
import com.alipay.sofa.dashboard.utils.DashboardUtil;
import com.alipay.sofa.dashboard.utils.FixedQueue;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A cache manager to manager dynamic application information
 * The cache pool will be update
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/5/9 5:26 PM
 **/
@Component
@EnableScheduling
public class AppDynamicCacheManager {

    @Autowired
    private ZookeeperApplicationManager                      zookeeperApplicationManager;

    @Autowired
    private ActuatorClient                                   client;

    private final Map<String, FixedQueue<DetailThreadInfo>>  cacheDetailThreads = new ConcurrentHashMap<>();

    private final Map<String, FixedQueue<MemoryHeapInfo>>    cacheHeapMemory    = new ConcurrentHashMap<>();

    private final Map<String, FixedQueue<MemoryNonHeapInfo>> cacheNonHeapMemory = new ConcurrentHashMap<>();

    /**
     * Update cache in fixed delay
     */
    @Scheduled(fixedRate = 15_000)
    public void fetchAppDynamicInfo() {
        Map<String, Set<Application>> applications = zookeeperApplicationManager.getApplications();
        Set<String> appNames = applications.keySet();
        appNames.forEach((appName) -> {
            Set<Application> appInstances = applications.get(appName);
            appInstances.forEach((app) -> {
                fetchDetailsThread(app);
                fetchMemoryHeap(app);
                fetchMemoryNonHeap(app);
            });
        });
    }

    /**
     * Get cached DetailThreads information
     *
     * @param appId appId definition, {@link DashboardUtil#simpleEncode(String, int)}
     * @return cached queue instance, or empty queue if
     */
    @NonNull
    @VisibleForTesting
    FixedQueue<DetailThreadInfo> getCachedDetailThreadsByAppId(String appId) {
        return cacheDetailThreads.getOrDefault(appId, new FixedQueue<>(4));
    }

    /**
     * Get cached DetailThreads information
     *
     * @param appId appId definition, {@link DashboardUtil#simpleEncode(String, int)}
     * @return cached queue instance, or empty queue if
     */
    @NonNull
    @VisibleForTesting
    FixedQueue<MemoryHeapInfo> getCachedMemoryHeapInfoByAppId(String appId) {
        return cacheHeapMemory.getOrDefault(appId, new FixedQueue<>(4));
    }

    /**
     * Get cached DetailThreads information
     *
     * @param appId appId definition, {@link DashboardUtil#simpleEncode(String, int)}
     * @return cached queue instance, or empty queue if
     */
    @NonNull
    @VisibleForTesting
    FixedQueue<MemoryNonHeapInfo> getCachedMemoryNonHeapInfoByAppId(String appId) {
        return cacheNonHeapMemory.getOrDefault(appId, new FixedQueue<>(4));
    }

    /**
     * Fetch detail thread info and update cache
     *
     * @param app application instance
     */
    private void fetchDetailsThread(Application app) {
        String appId = DashboardUtil.simpleEncode(app.getHostName(), app.getPort());
        DetailThreadInfo info = client.getDetailsThread(convertToHost(app));
        cacheDetailThreads.compute(appId, (key, value) -> {
            FixedQueue<DetailThreadInfo> queueIns = Optional.ofNullable(value)
                    .orElse(new FixedQueue<>(4));
            queueIns.offer(info);
            return queueIns;
        });
    }

    /**
     * Fetch heap memory info and update cache
     *
     * @param app application instance
     */
    private void fetchMemoryHeap(Application app) {
        String appId = DashboardUtil.simpleEncode(app.getHostName(), app.getPort());
        MemoryHeapInfo info = client.getHeapMemory(convertToHost(app));
        cacheHeapMemory.compute(appId, (key, value) -> {
            FixedQueue<MemoryHeapInfo> queueIns = Optional.ofNullable(value)
                    .orElse(new FixedQueue<>(4));
            queueIns.offer(info);
            return queueIns;
        });
    }

    /**
     * Fetch non-heap memory info and update cache
     *
     * @param app application instance
     */
    private void fetchMemoryNonHeap(Application app) {
        String appId = DashboardUtil.simpleEncode(app.getHostName(), app.getPort());
        MemoryNonHeapInfo info = client.getNonHeapMemory(convertToHost(app));
        cacheNonHeapMemory.compute(appId, (key, value) -> {
            FixedQueue<MemoryNonHeapInfo> queueIns = Optional.ofNullable(value)
                    .orElse(new FixedQueue<>(4));
            queueIns.offer(info);
            return queueIns;
        });
    }

    /**
     * Convert application instance to app host
     *
     * @param app application instance by zookeeper session
     * @return app host instance
     */
    @NonNull
    private AppHost convertToHost(@NonNull Application app) {
        AppHost host = new AppHost();
        host.setHost(app.getHostName());
        host.setRestPort(app.getPort());
        return host;
    }
}
