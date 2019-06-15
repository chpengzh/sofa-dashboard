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
package com.alipay.sofa.dashboard.controller;

import com.alipay.sofa.dashboard.model.account.LoginReq;
import com.alipay.sofa.dashboard.model.account.LoginResp;
import com.alipay.sofa.dashboard.spi.SessionManager;
import com.alipay.sofa.dashboard.utils.Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@RestController
@RequestMapping
public class AccountController {

    @Autowired
    private SessionManager sessionManager;

    @PostMapping("/api/signin")
    public Dict login(@RequestBody LoginReq form, HttpServletResponse resp) {
        LoginResp loginResp = sessionManager.login(form);

        Cookie cookie = new Cookie("JSESSIONID", loginResp.getSessionId());
        resp.addCookie(cookie);
        return Dict.set("info", loginResp.getInfo());
    }

}
