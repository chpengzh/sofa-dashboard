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
package com.alipay.sofa.dashboard.app.dao.impl;

import com.alipay.sofa.dashboard.app.dao.api.ActuatorClient;
import com.alipay.sofa.dashboard.app.model.AppHost;
import com.alipay.sofa.dashboard.app.model.actuator.env.EnvironmentDescriptor;
import com.alipay.sofa.dashboard.app.model.actuator.health.Health;
import com.alipay.sofa.dashboard.app.model.actuator.mappings.ApplicationMappings;
import com.alipay.sofa.dashboard.app.model.actuator.metrics.MetricResponse;
import com.alipay.sofa.dashboard.app.model.actuator.thread.ThreadDumpResponse;
import com.alipay.sofa.dashboard.enums.ActuatorPathEnum;
import com.alipay.sofa.dashboard.model.monitor.*;
import com.alipay.sofa.dashboard.utils.DashboardUtil;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Application ActuatorClient implement
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Repository
public class ActuatorClientImpl implements ActuatorClient {

    private static final String SPIT     = ", ";

    /**
     * 调用客户端实例
     */
    private final RestTemplate  template = new RestTemplate();

    @Override
    @NonNull
    public EnvironmentInfo getEnvironmentInfo(@NonNull AppHost source) throws RestClientException {
        EnvironmentDescriptor call = template.getForObject(
            source.toURL(ActuatorPathEnum.ENV.getPath()), EnvironmentDescriptor.class);
        assert call != null;
        return call.toEnvironmentInfo();
    }

    @Override
    @NonNull
    public List<MetricsInfo> getMetricsInfo(@NonNull AppHost source) throws RestClientException {
        // TODO: [#21] Integration SOFALookout Server!!
        throw new UnsupportedOperationException("Not implement yet");
    }

    @Override
    @NonNull
    public HealthInfo getHealthInfo(@NonNull AppHost source) throws RestClientException {
        Health call = template.getForObject(source.toURL(ActuatorPathEnum.HEALTH.getPath()),
            Health.class);
        assert call != null;
        return call.toHealthInfo();
    }

    @Override
    @NonNull
    public Map<String, Object> getAppInfo(@NonNull AppHost source) throws RestClientException {
        //noinspection unchecked
        Map<String, Object> infoResult = (Map<String, Object>) template.getForObject(
            source.toURL(ActuatorPathEnum.INFO.getPath()), Map.class);
        assert infoResult != null;
        return toFlatMap("", infoResult);
    }

    @Override
    public DetailThreadInfo getDetailsThread(@NonNull AppHost source) throws RestClientException {
        MetricResponse jvmThreadsLiveResp = template.getForObject(
                source.toURL(ActuatorPathEnum.METRICS.getPath(), "jvm.threads.live"),
                MetricResponse.class);
        MetricResponse jvmThreadsDaemonResp = template.getForObject(
                source.toURL(ActuatorPathEnum.METRICS.getPath(), "jvm.threads.daemon"),
                MetricResponse.class);
        MetricResponse jvmThreadsPeakResp = template.getForObject(
                source.toURL(ActuatorPathEnum.METRICS.getPath(), "jvm.threads.peak"),
                MetricResponse.class);
        assert jvmThreadsLiveResp != null;
        assert jvmThreadsDaemonResp != null;
        assert jvmThreadsPeakResp != null;

        DetailThreadInfo threadInfo = new DetailThreadInfo();
        parallelDoWithDetail(new Object[][] {
                { "LIVE",
                  jvmThreadsLiveResp.readDefaultVal().intValue(),
                  (Consumer<DetailsItem>) (threadInfo::setLive) },
                { "DAEMON",
                  jvmThreadsDaemonResp.readDefaultVal().intValue(),
                  (Consumer<DetailsItem>) (threadInfo::setDaemon) },
                { "PEAK",
                  jvmThreadsPeakResp.readDefaultVal().intValue(),
                  (Consumer<DetailsItem>) (threadInfo::setPeak) }
        });
        return threadInfo;
    }

    @Override
    @NonNull
    public MemoryHeapInfo getHeapMemory(@NonNull AppHost source) throws RestClientException {
        MetricResponse jvmMemoryCommitted = template.getForObject(
                source.toURL(ActuatorPathEnum.METRICS.getPath(),
                        "jvm.memory.committed?tag=area:heap"),
                MetricResponse.class);
        MetricResponse jvmMemoryUsed = template.getForObject(
                source.toURL(ActuatorPathEnum.METRICS.getPath(),
                        "jvm.memory.used?tag=area:heap"),
                MetricResponse.class);
        assert jvmMemoryCommitted != null;
        assert jvmMemoryUsed != null;

        MemoryHeapInfo heap = new MemoryHeapInfo();
        parallelDoWithDetail(new Object[][] {
                { "used",
                  convertBitToMB(jvmMemoryCommitted.readDefaultVal()),
                  (Consumer<DetailsItem>) (heap::setUsed) },
                { "size",
                  convertBitToMB(jvmMemoryUsed.readDefaultVal()),
                  (Consumer<DetailsItem>) (heap::setSize) }
        });
        return heap;
    }

    @Override
    @NonNull
    public MemoryNonHeapInfo getNonHeapMemory(@NonNull AppHost source) throws RestClientException {
        MetricResponse jvmMemoryCommitted = template.getForObject(
                source.toURL(ActuatorPathEnum.METRICS.getPath(),
                        "jvm.memory.committed?tag=area:nonheap"),
                MetricResponse.class);
        MetricResponse jvmMemoryUsed = template.getForObject(
                source.toURL(ActuatorPathEnum.METRICS.getPath(),
                        "jvm.memory.used?tag=area:nonheap"),
                MetricResponse.class);
        MetricResponse jvmMemoryUsedInMetaSpace = template.getForObject(
                source.toURL(ActuatorPathEnum.METRICS.getPath(),
                        "jvm.memory.used?tag=area:nonheap&tag=id:Metaspace"),
                MetricResponse.class);
        assert jvmMemoryCommitted != null;
        assert jvmMemoryUsed != null;
        assert jvmMemoryUsedInMetaSpace != null;

        MemoryNonHeapInfo nonHeap = new MemoryNonHeapInfo();
        parallelDoWithDetail(new Object[][] {
                { "used",
                  convertBitToMB(jvmMemoryCommitted.readDefaultVal()),
                  (Consumer<DetailsItem>) (nonHeap::setUsed) },
                { "size",
                  convertBitToMB(jvmMemoryUsed.readDefaultVal()),
                  (Consumer<DetailsItem>) (nonHeap::setSize) },
                { "metaspace",
                  convertBitToMB(jvmMemoryUsed.readDefaultVal()),
                  (Consumer<DetailsItem>) (nonHeap::setMetaspace) }
        });
        return nonHeap;
    }

    @Override
    @NonNull
    public LoggersInfo getLoggers(@NonNull AppHost source) throws RestClientException {
        LoggersInfo loggersInfo = template.getForObject(
            source.toURL(ActuatorPathEnum.LOGGERS.getPath()), LoggersInfo.class);
        assert loggersInfo != null;
        return loggersInfo;
    }

    @Override
    @NonNull
    public Map<String, MappingsInfo> getMappings(@NonNull AppHost source)
            throws RestClientException {
        ApplicationMappings resp = template.getForObject(
                source.toURL(ActuatorPathEnum.MAPPINGS.getPath()),
                ApplicationMappings.class);
        assert resp != null;

        final Map<String, MappingsInfo> result = new HashMap<>();
        resp.getContexts().forEach((key, value) -> {
            MappingsInfo info = new MappingsInfo();

            Map<String, Object> mappings = value.getMappings();
            List<Map<String, Object>> dispatchServlets = readDict(mappings,
                    "dispatcherServlets", "dispatcherServlet");
            info.setDispatcherServlet(parseDispatchServlet(
                    Optional.ofNullable(dispatchServlets).orElse(new ArrayList<>())));

            List<Map<String, Object>> servletFilters = readDict(mappings,
                    "servletFilters");
            info.setServletFilters(parseServletFilter(
                    Optional.ofNullable(servletFilters).orElse(new ArrayList<>())));

            List<Map<String, Object>> servlets = readDict(mappings,
                    "servlets");
            info.setServlets(parseServletInfo(
                    Optional.ofNullable(servlets).orElse(new ArrayList<>())));

            result.put(key, info);
        });
        return result;
    }

    @Override
    @NonNull
    public List<ThreadDumpInfo> getThreadDumps(@NonNull AppHost source) throws RestClientException {
        ThreadDumpResponse resp = template.getForObject(
            source.toURL(ActuatorPathEnum.THREADDUMP.getPath()), ThreadDumpResponse.class);
        assert resp != null;
        return resp.getThreads();
    }

    /**
     * Parse dispatch servlet from data list
     *
     * @param data data list
     * @return Handler Mapping Info list
     */
    private List<MappingsInfo.HandlerMappingInfo> parseDispatchServlet(
            @NonNull List<Map<String, Object>> data
    ) {
        return data.stream().map(map -> {
            // Read meta info from map
            String handler = readDict(map, "handler");
            String predicate = readDict(map, "predicate");
            List<String> methods = readDict(map,
                    "details", "requestMappingConditions", "methods");
            List<Map<String, Object>> paramsType = readDict(map,
                    "details", "requestMappingConditions", "consumes");
            List<Map<String, Object>> returnType = readDict(map,
                    "details", "requestMappingConditions", "produces");

            // Map to description text
            String methodsDesc = mapToDesc(methods);
            String paramTypeDesc = Optional.ofNullable(paramsType).orElse(new ArrayList<>())
                    .stream()
                    .map(it -> (String) it.getOrDefault("mediaType", ""))
                    .reduce((a, b) -> a + SPIT + b)
                    .orElse("");
            String returnTypeDesc = Optional.ofNullable(returnType).orElse(new ArrayList<>())
                    .stream()
                    .map(it -> (String) it.getOrDefault("mediaType", ""))
                    .reduce((a, b) -> a + SPIT + b)
                    .orElse("");

            // Generate mapping info
            MappingsInfo.HandlerMappingInfo info = new MappingsInfo.HandlerMappingInfo();
            info.setHandler(Optional.ofNullable(handler).orElse(""));
            info.setPredicate(Optional.ofNullable(predicate).orElse(""));
            info.setMethods(methodsDesc);
            info.setParamsType(paramTypeDesc);
            info.setResponseType(returnTypeDesc);
            return info;

        }).collect(Collectors.toList());
    }

    /**
     * Parse servlet filter from data list
     *
     * @param data data list
     * @return Handler Filter info list
     */
    private List<MappingsInfo.HandlerFilterInfo> parseServletFilter(
            @NonNull List<Map<String, Object>> data
    ) {
        return data.stream().map(map -> {
            // Read meta info from map
            String name = readDict(map, "name");
            String className = readDict(map, "className");
            List<String> servletNameMappings = readDict(map, "servletNameMappings");
            List<String> urlPatternMappings = readDict(map, "urlPatternMappings");

            // Generate mapping info
            MappingsInfo.HandlerFilterInfo info = new MappingsInfo.HandlerFilterInfo();
            info.setName(Optional.ofNullable(name).orElse(""));
            info.setClassName(Optional.ofNullable(className).orElse(""));
            info.setServletNameMappings(mapToDesc(servletNameMappings));
            info.setUrlPatternMappings(mapToDesc(urlPatternMappings));
            return info;

        }).collect(Collectors.toList());
    }

    /**
     * Parse servlet from data list
     *
     * @param data data list
     * @return Servlet info list
     */
    private List<MappingsInfo.ServletInfo> parseServletInfo(
            @NonNull List<Map<String, Object>> data
    ) {
        return data.stream().map(map -> {
            // Read meta info from map
            List<String> mappings = readDict(map, "mappings");
            String name = readDict(map, "name");
            String className = readDict(map, "className");

            // Generate mapping info
            MappingsInfo.ServletInfo info = new MappingsInfo.ServletInfo();
            info.setMappings(mapToDesc(mappings));
            info.setName(Optional.ofNullable(name).orElse(""));
            info.setClassName(Optional.ofNullable(className).orElse(""));
            return info;

        }).collect(Collectors.toList());
    }

    /**
     * Transform a complex dictionary into flat map
     * Sample input:
     * <pre>
     * {
     *     "a": {
     *         "b": {
     *             "c": "d"
     *         },
     *         "e": "f"
     *     }
     * }
     * </pre>
     * Sample output:
     * <pre>
     * {
     *     "a.b.c": "d",
     *     "a.e": "f"
     * }
     * </pre>
     *
     * @param origin origin map
     * @return flat map
     */
    private Map<String, Object> toFlatMap(String prefix, Map<String, Object> origin) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : origin.entrySet()) {
            String nextKey = StringUtils.isEmpty(prefix) ? entry.getKey() : prefix + "."
                                                                            + entry.getKey();
            if (entry.getValue() instanceof Map) {
                //noinspection unchecked
                Map<String, Object> innerResult = toFlatMap(nextKey,
                    (Map<String, Object>) entry.getValue());
                result.putAll(innerResult);
            } else {
                result.put(nextKey, entry.getValue());
            }
        }
        return result;
    }

    /**
     * Inject detail entity by action parameter
     *
     * @param actionParam action param, see usage for more details
     */
    private void parallelDoWithDetail(Object[]... actionParam) {
        String currentDataKey = DashboardUtil.getCurrentDataKey();
        for (Object[] parallel : actionParam) {
            DetailsItem next = new DetailsItem();
            next.setTime(currentDataKey);
            next.setTags((String) parallel[0]);
            next.setNums((Integer) parallel[1]);
            //noinspection unchecked
            ((Consumer<DetailsItem>) parallel[2]).accept(next);
        }
    }

    /**
     * Convert bit size to MB
     *
     * @param size origin size
     * @return wrap bit size
     */
    private int convertBitToMB(@Nullable Number size) {
        if (size == null) {
            return 0;
        }
        return Math.round(size.floatValue() / (Byte.SIZE * 1024 * 1024));
    }

    /**
     * A util function to read dictionary type
     *
     * @param instance map instance
     * @param path     dict path
     * @return path value
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private <T> T readDict(@Nullable Map<String, Object> instance, String... path) {
        if (instance == null) {
            return null;
        } else if (path.length == 1) {
            return (T) instance.get(path[0]);
        }

        Object nextInstance = instance.get(path[0]);
        String[] nextPath = Arrays.copyOfRange(path, 1, path.length);
        return nextInstance instanceof Map ? readDict((Map<String, Object>) nextInstance, nextPath)
            : null;
    }

    /**
     * A util function to map list into text
     *
     * @param listStr text list
     * @return mapped text
     */
    private String mapToDesc(@Nullable List<String> listStr) {
        return Optional.ofNullable(listStr).orElse(new ArrayList<>())
                .stream()
                .reduce((a, b) -> a + SPIT + b)
                .orElse("");
    }
}
