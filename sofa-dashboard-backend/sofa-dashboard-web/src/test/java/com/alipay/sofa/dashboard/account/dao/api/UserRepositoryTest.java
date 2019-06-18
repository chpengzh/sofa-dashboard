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
package com.alipay.sofa.dashboard.account.dao.api;

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.dashboard.account.model.request.UserInfoReq;
import com.alipay.sofa.dashboard.model.account.UserInfo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class UserRepositoryTest extends ApolloRepositoryTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository userRepo;

    @Test
    public void getMyInfoTest() {
        String id = userRepo.myUserId();
        LOGGER.info("Fetch my id => {}", id);
    }

    @Test
    public void getAllUsersInfo() {
        List<UserInfo> myself = userRepo.getUsers("apollo", 100);
        LOGGER.info("Fetch my info => {}", JSON.toJSONString(myself));
    }

    @Test
    public void createUser() {
        UserInfoReq req = new UserInfoReq();
        req.setEmail("chpengzh@foxmail.com");
        req.setPassword("123456");
        req.setUsername("chpengzh");
        userRepo.createOrUpdateUser(req);
    }
}
