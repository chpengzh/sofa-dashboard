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
package com.alipay.sofa.dashboard.account.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.dashboard.account.dao.api.SessionRepository;
import com.alipay.sofa.dashboard.account.dao.api.UserRepository;
import com.alipay.sofa.dashboard.account.model.request.UserInfoReq;
import com.alipay.sofa.dashboard.model.account.UserInfo;
import com.alipay.sofa.dashboard.utils.Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Repository
public class UserRepositoryImpl extends ApolloRepositoryBase implements UserRepository {

    /**
     * Rest client instance
     */
    private final RestTemplate template = new RestTemplate();

    @Value("${apollo.portal.url:http://127.0.0.1:8087}")
    private String portalURL;

    @Autowired
    private SessionRepository sessionRepo;

    @NonNull
    @Override
    public String myUserId() {
        ResponseEntity<UserInfo> resp = template
            .exchange(
                RequestEntity.get(URI.create(portalURL + "/user"))
                    .header("Cookie",
                        new HttpCookie(COOKIE_SESSION_KEY, sessionRepo.currentSessionId())
                            .toString())
                    .build(), UserInfo.class);
        UserInfo info = Objects.requireNonNull(resp).getBody();
        return Objects.requireNonNull(info).getUserId();
    }

    @Override
    public List<UserInfo> getUsers(String keyword, int limit) {
        URI uri = new UriTemplate(portalURL + "/users?keyword={keyword}&limit={limit}").expand(Dict
            .set("keyword", Optional.ofNullable(keyword).orElse("")).and("limit", limit));
        ResponseEntity<List> resp = template
            .exchange(
                RequestEntity.get(uri)
                    .header("Cookie",
                        new HttpCookie(COOKIE_SESSION_KEY, sessionRepo.currentSessionId())
                            .toString())
                    .build(), List.class);
        return JSON.parseArray(JSON.toJSONString(resp.getBody()), UserInfo.class);
    }

    @Override
    public void createOrUpdateUser(@NonNull UserInfoReq req) {
        template.exchange(
            RequestEntity.post(URI.create(portalURL + "/users"))
                .header("Cookie",
                    new HttpCookie(COOKIE_SESSION_KEY, sessionRepo.currentSessionId()).toString())
                .body(req), Void.class);
    }
}
