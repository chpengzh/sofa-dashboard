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

import com.alipay.sofa.dashboard.account.model.UserRole;
import com.alipay.sofa.dashboard.account.model.request.UserInfoReq;
import com.alipay.sofa.dashboard.model.account.UserInfo;
import org.springframework.lang.NonNull;

import javax.annotation.security.RolesAllowed;
import java.util.List;

/**
 * User repository
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public interface UserRepository {

    /**
     * Query user info of current session id
     *
     * @return current user's info
     */
    @NonNull
    @RolesAllowed({ UserRole.ADMIN, UserRole.NORMAL })
    String myUserId();

    /**
     * Query users keyword and page size
     *
     * @param keyword query keyword
     * @param limit   query limit
     * @return user info list
     */
    @NonNull
    @RolesAllowed({ UserRole.ADMIN })
    List<UserInfo> getUsers(String keyword, int limit);

    /**
     * Create or update a user's information
     *
     * @param req user parameter
     */
    @RolesAllowed({ UserRole.ADMIN })
    void createOrUpdateUser(@NonNull UserInfoReq req);

}
