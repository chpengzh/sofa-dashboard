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

import com.alipay.sofa.dashboard.account.dao.api.SessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.net.HttpCookie;
import java.net.URI;
import java.util.Objects;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Repository
public class SessionRepositoryImpl extends ApolloRepositoryBase implements SessionRepository {

    private final ThreadLocal<String> localSessionRef = new ThreadLocal<>();

    @NonNull
    @Override
    public String createSession(@NonNull String username, @NonNull String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);

        ResponseEntity<String> loginResp = template.exchange(RequestEntity
            .post(URI.create(portalURL + "/signin"))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(map), String.class);
        if (loginResp.getStatusCode() != HttpStatus.FOUND) {
            throw new IllegalArgumentException(
                "Illegal signIn http status, expected " + HttpStatus.FOUND.value() +
                " found" + loginResp.getStatusCodeValue());
        }

        String resultLocation = loginResp.getHeaders().getFirst("Location");
        String setCookie = loginResp.getHeaders().getFirst("Set-Cookie");
        if (!StringUtils.isEmpty(setCookie) &&
            !StringUtils.isEmpty(resultLocation) &&
            !resultLocation.endsWith("#/error")) {
            HttpCookie sessionId = HttpCookie.parse(setCookie).stream()
                .filter(it -> Objects.equals(it.getName(), COOKIE_SESSION_KEY))
                .findFirst()
                .orElse(null);
            if (sessionId != null) {
                return sessionId.getValue();
            }
        }
        throw new IllegalArgumentException("Invalid authorization.");
    }

    @Override
    public void removeSession(String sessionId) {

    }

    @Override
    public String currentSessionId() {
        return localSessionRef.get();
    }

    @Override
    public void setCurrentSessionId(String sessionId) {
        if (sessionId == null) {
            localSessionRef.remove();
        } else {
            localSessionRef.set(sessionId);
        }
    }
}
