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
package com.alipay.sofa.dashboard.account.service;

import com.alipay.sofa.dashboard.account.dao.api.SessionRepository;
import com.alipay.sofa.dashboard.account.dao.api.UserRepository;
import com.alipay.sofa.dashboard.model.account.LoginReq;
import com.alipay.sofa.dashboard.model.account.LoginResp;
import com.alipay.sofa.dashboard.model.account.UserInfo;
import com.alipay.sofa.dashboard.spi.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Service
public class SessionManagerImpl implements SessionManager {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @NonNull
    public LoginResp login(@NonNull LoginReq loginForm) {
        String sessionId = sessionRepository.createSession(loginForm.getUsername(),
            loginForm.getPassword());

        sessionRepository.setCurrentSessionId(sessionId);
        try {
            String userId = userRepository.myUserId();
            List<UserInfo> userInfo = userRepository.getUsers(userId, 1);
            assert !userInfo.isEmpty();
            UserInfo myself = userInfo.get(0);

            LoginResp resp = new LoginResp();
            resp.setSessionId(sessionId);
            resp.setInfo(myself);
            return resp;
        } finally {
            sessionRepository.setCurrentSessionId(null);
        }
    }
}
