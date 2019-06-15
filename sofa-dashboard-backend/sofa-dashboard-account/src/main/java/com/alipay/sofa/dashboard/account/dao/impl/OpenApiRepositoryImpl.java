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

import com.alipay.sofa.dashboard.account.dao.admin.OpenApiRepository;
import com.alipay.sofa.dashboard.account.model.request.CreateSessionReq;
import com.alipay.sofa.dashboard.account.model.response.AuthTokenResp;
import com.alipay.sofa.dashboard.utils.Dict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriTemplate;

import java.net.HttpCookie;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
@Repository
public class OpenApiRepositoryImpl extends ApolloRepositoryBase implements OpenApiRepository {

    @Nullable
    @Override
    public String getAuthTokenByAppId(@NonNull String sessionId, @NonNull String appId) {
        URI uri = new UriTemplate(portalURL + "/consumers/by-appId?appId={appId}").expand(Dict.set(
            "appId", appId));
        ResponseEntity<AuthTokenResp> resp = template
            .exchange(
                RequestEntity.get(uri)
                    .header("Cookie", new HttpCookie(COOKIE_SESSION_KEY, sessionId).toString())
                    .build(), AuthTokenResp.class);
        return Optional.ofNullable(resp.getBody()).orElse(new AuthTokenResp()).getToken();
    }

    @NonNull
    @Override
    public String createAuthToken(@NonNull String sessionId, @NonNull CreateSessionReq req) {
        ResponseEntity<AuthTokenResp> resp = template
            .exchange(
                RequestEntity.post(URI.create(portalURL + "/consumers"))
                    .header("Cookie", new HttpCookie(COOKIE_SESSION_KEY, sessionId).toString())
                    .build(), AuthTokenResp.class);
        String token = Objects.requireNonNull(resp.getBody()).getToken();
        return Objects.requireNonNull(token);
    }
}
