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
package com.alipay.sofa.dashboard.app.model;

import java.io.Serializable;

/**
 * Application host definition
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class AppHost implements Serializable {

    private static final int serialVersionUID = 0x11;

    /**
     * Application binding host
     */
    private String           host;

    /**
     * Application binding rest port
     */
    private int              restPort;

    public String toURL(String... path) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(host).append(":").append(restPort);
        for (String nextPath : path) {
            String pathWithoutPadding = nextPath.startsWith("/") ? nextPath.substring(1) : nextPath;
            builder.append("/").append(pathWithoutPadding);
        }
        return builder.toString();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getRestPort() {
        return restPort;
    }

    public void setRestPort(int restPort) {
        this.restPort = restPort;
    }
}
