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

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.dashboard.SofaAdminServerApplication;
import com.alipay.sofa.dashboard.base.AbstractTestBase;
import com.alipay.sofa.dashboard.model.monitor.*;
import com.alipay.sofa.dashboard.spi.MonitorManager;
import org.apache.curator.test.TestingServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Test cases for monitor manager implement
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class MonitorManagerTest extends AbstractTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorManagerTest.class);

    @LocalServerPort
    private int                 port;

    @Autowired
    private MonitorManager      manager;

    @Test
    public void fetchEnvironmentTest() {
        EnvironmentInfo info = manager.fetchEnvironment(getLocalSource());
        LOGGER.info("Fetch environment => {}", JSON.toJSONString(info));
    }

    @Test
    public void fetchMetricsTest() {
        List<MetricsInfo> metricsInfo = manager.fetchMetrics(getLocalSource());
        LOGGER.info("Fetch metrics => {}", JSON.toJSONString(metricsInfo));
    }

    @Test
    public void fetchInfoTest() {
        Map info = manager.fetchInfo(getLocalSource());
        LOGGER.info("Fetch info => {}", JSON.toJSONString(info));
    }

    @Test
    public void fetchEmptyDetailsThreadTest() {
        List<DetailsItem> detailsThread = manager.fetchDetailsThread(getLocalSource());
        LOGGER.info("Fetch empty details thread => {}", JSON.toJSONString(detailsThread));
    }

    @Test
    public void fetchEmptyHeapMemoryTest() {
        List<DetailsItem> heapMemory = manager.fetchHeapMemory(getLocalSource());
        LOGGER.info("Fetch empty heap memory => {}", JSON.toJSONString(heapMemory));
    }

    @Test
    public void fetchEmptyNonHeapMemoryTest() {
        List<DetailsItem> nonHeapMemory = manager.fetchNonHeapMemory(getLocalSource());
        LOGGER.info("Fetch empty non-heap memory => {}", JSON.toJSONString(nonHeapMemory));
    }

    @Test
    public void fetchLoggersTest() {
        LoggersInfo loggersInfo = manager.fetchLoggers(getLocalSource());
        LOGGER.info("Fetch loggers => {}", JSON.toJSONString(loggersInfo));
    }

    @Test
    public void fetchMappingsTest() {
        Map<String, MappingsInfo> mappings = manager.fetchMappings(getLocalSource());
        LOGGER.info("Fetch mappings => {}", JSON.toJSONString(mappings));
    }

    @Test
    public void fetchThreadDumpTest() {
        List<ThreadDumpInfo> threadDump = manager.fetchThreadDump(getLocalSource());
        LOGGER.info("Fetch thread-dump => {}", JSON.toJSONString(threadDump));
    }

    private Object getLocalSource() {
        return "127.0.0.1:" + port;
    }
}
