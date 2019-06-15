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
package com.alipay.sofa.dashboard.account.dao.admin;

import org.springframework.lang.NonNull;
import org.springframework.web.client.RestClientException;

/**
 * Session repository
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface SessionRepository {

    /**
     * Get login cookie from apollo-portal, a sessionId will be return
     * only when username and password is validated by apollo-portal processor
     * <br>
     * Here's the common apollo response from apollo's <b>/signin</b> controller
     * <pre>
     * Success:
     * - Status: 302
     * - Location: /
     * - Set-Cookie: JSESSIONID=XXX
     *
     * Fail:
     * - Status: 302
     * - Location: /signin#/error
     * - Set-Cookie: JSESSIONID=XXX
     * </pre>
     *
     * @param username login username
     * @param password login password
     * @return login cookie
     * @throws RestClientException invoke error
     */
    @NonNull
    String createSession(@NonNull String username, @NonNull String password);

    /**
     * Notice apollo-portal to recycle session info
     *
     * @param sessionId sessionId, {@link SessionRepository#createSession(String, String)}
     */
    void removeSession(String sessionId);

}
