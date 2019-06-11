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
package com.alipay.sofa.dashboard.app.dao;

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.dashboard.SofaAdminServerApplication;
import com.alipay.sofa.dashboard.app.dao.api.ActuatorClient;
import com.alipay.sofa.dashboard.app.model.AppHost;
import com.alipay.sofa.dashboard.base.AbstractTestBase;
import com.alipay.sofa.dashboard.model.monitor.*;
import org.apache.curator.test.TestingServer;
import org.junit.AfterClass;
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
 * Test cases for actuator client
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class ActuatorClientTest extends AbstractTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActuatorClientTest.class);

    @LocalServerPort
    private int                 port;

    @Autowired
    private ActuatorClient      client;

    @Test
    public void getEnvironmentInfoTest() {
        EnvironmentInfo envInfo = client.getEnvironmentInfo(getLocalHost());
        LOGGER.info("Get localhost env info => {}", JSON.toJSONString(envInfo));
    }

    @Test
    public void getAppInfoTest() {
        Map<String, Object> appInfo = client.getAppInfo(getLocalHost());
        LOGGER.info("Get app info => {}", JSON.toJSONString(appInfo));
    }

    @Test
    public void getDetailsThreadTest() {
        DetailThreadInfo detailsThread = client.getDetailsThread(getLocalHost());
        LOGGER.info("Get details thread info => {}", JSON.toJSONString(detailsThread));
    }

    @Test
    public void getHeapMemoryTest() {
        MemoryHeapInfo memoryHeapInfo = client.getHeapMemory(getLocalHost());
        LOGGER.info("Get heap memory info => {}", JSON.toJSONString(memoryHeapInfo));
    }

    @Test
    public void getNonHeapMemoryTest() {
        MemoryNonHeapInfo memoryNonHeapInfo = client.getNonHeapMemory(getLocalHost());
        LOGGER.info("Get non-heap memory info => {}", JSON.toJSONString(memoryNonHeapInfo));
    }

    @Test
    public void getLoggersTest() {
        LoggersInfo loggersInfo = client.getLoggers(getLocalHost());
        LOGGER.info("Get loggers info => {}", JSON.toJSONString(loggersInfo));
    }

    @Test
    public void getThreadDumpsInfoTest() {
        List<ThreadDumpInfo> threadDumpInfos = client.getThreadDumps(getLocalHost());
        LOGGER.info("Get thread dump info => {}", JSON.toJSONString(threadDumpInfos));
    }

    private AppHost getLocalHost() {
        AppHost host = new AppHost();
        host.setHost("127.0.0.1");
        host.setRestPort(port);
        return host;
    }

}
