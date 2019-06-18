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

import com.alipay.sofa.dashboard.account.model.request.CreateSessionReq;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientException;

import javax.annotation.security.RolesAllowed;

/**
 * OpenApi token repository
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface OpenApiRepository {

    /**
     * Query the token by a given appId.
     * See <b>/open/manage.html</b> in apollo-portal for more details
     *
     * @param appId appId, which is the unique identity of application, can be given by application.properties
     * @return openApi token, return {@code null} if it's not exists
     * @throws RestClientException invoke error
     */
    @Nullable
    @RolesAllowed("ADMIN")
    String getAuthTokenByAppId(@NonNull String appId);

    /**
     * Create a token by a given appId.
     * See <b>/open/manage.html</b> in apollo-portal for more details
     *
     * @param req create session request
     * @return openApi token, should not be {@code null}
     * @throws RestClientException invoke error
     */
    @NonNull
    @RolesAllowed("ADMIN")
    String createAuthToken(@NonNull CreateSessionReq req);

}
