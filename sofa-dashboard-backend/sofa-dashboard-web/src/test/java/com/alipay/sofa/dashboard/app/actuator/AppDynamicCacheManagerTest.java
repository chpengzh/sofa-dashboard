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
import com.alipay.sofa.dashboard.app.zookeeper.ZookeeperApplicationManager;
import com.alipay.sofa.dashboard.base.AbstractTestBase;
import com.alipay.sofa.dashboard.constants.SofaDashboardConstants;
import com.alipay.sofa.dashboard.model.Application;
import com.alipay.sofa.dashboard.model.monitor.DetailThreadInfo;
import com.alipay.sofa.dashboard.model.monitor.MemoryHeapInfo;
import com.alipay.sofa.dashboard.model.monitor.MemoryNonHeapInfo;
import com.alipay.sofa.dashboard.utils.DashboardUtil;
import com.alipay.sofa.dashboard.utils.FixedQueue;
import org.apache.curator.test.TestingServer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Test cases for application dynamic cache manager.
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class AppDynamicCacheManagerTest extends AbstractTestBase {

    private static final Logger         LOGGER = LoggerFactory
                                                   .getLogger(AppDynamicCacheManagerTest.class);

    @LocalServerPort
    private int                         port;

    @Autowired
    private AppDynamicCacheManager      cacheManager;

    @Autowired
    private ZookeeperApplicationManager zkManager;

    @Before
    public void beforeTest() {
        Application localApp = new Application();
        localApp.setHostName("127.0.0.1");
        localApp.setPort(port);
        localApp.setAppState("NORMAL");
        localApp.setAppName("test");
        zkManager.getApplications().clear();
        zkManager.getApplications().put(getLocalAppId(), Sets.newSet(localApp));
        cacheManager.fetchAppDynamicInfo();
    }

    @After
    public void afterTest() {
        zkManager.getApplications().clear();
    }

    @Test
    public void fetchDetailsThreadTest() {
        FixedQueue<DetailThreadInfo> detailsThreadInfo = cacheManager
            .getCachedDetailThreadsByAppId(getLocalAppId());
        Assert.assertTrue(detailsThreadInfo.size() > 0);
        LOGGER.info("Fetch details thread => {}",
            JSON.toJSONString(detailsThreadInfo.getReadOnlyQueue()));
    }

    @Test
    public void fetchMemoryHeapInfoTest() {
        FixedQueue<MemoryHeapInfo> memoryHeapInfo = cacheManager
            .getCachedMemoryHeapInfoByAppId(getLocalAppId());
        Assert.assertTrue(memoryHeapInfo.size() > 0);
        LOGGER.info("Fetch memory heap info => {}",
            JSON.toJSONString(memoryHeapInfo.getReadOnlyQueue()));
    }

    @Test
    public void fetchMemoryNonHeapInfoTest() {
        FixedQueue<MemoryNonHeapInfo> memoryNonHeapInfo = cacheManager
            .getCachedMemoryNonHeapInfoByAppId(getLocalAppId());
        Assert.assertTrue(memoryNonHeapInfo.size() > 0);
        LOGGER.info("Fetch memory non-heap info => {}",
            JSON.toJSONString(memoryNonHeapInfo.getReadOnlyQueue()));
    }

    private String getLocalAppId() {
        String targetHost = "127.0.0.1:" + port;
        String[] address = targetHost.split(SofaDashboardConstants.COLON);
        return DashboardUtil.simpleEncode(address[0], Integer.valueOf(address[1]));
    }

}
