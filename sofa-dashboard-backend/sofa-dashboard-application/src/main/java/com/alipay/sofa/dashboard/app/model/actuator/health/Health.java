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
package com.alipay.sofa.dashboard.app.model.actuator.health;

import com.alipay.sofa.dashboard.model.monitor.HealthInfo;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A model copy of {@link org.springframework.boot.actuate.health.Health}
 *
 * @author chen.pengzhi (chpengzh@foxmail.com)
 */
public class Health implements Serializable {

    private static final int              serialVersionUID = 0x11;

    private static final transient String UNKNOWN_STATUS   = "UNKNOWN";

    private String                        status;

    private final Map<String, Object>     details          = new HashMap<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    /**
     * Convert to health info entity
     *
     * @return healthInfo entity
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public HealthInfo toHealthInfo() {
        HealthInfo result = new HealthInfo();
        result.setStatus(StringUtils.isEmpty(status) ? UNKNOWN_STATUS : status);

        List<HealthInfo.HealthItemInfo> detailList = details.entrySet().stream().map(entry -> {
            HealthInfo.HealthItemInfo healthItemInfo = new HealthInfo.HealthItemInfo();
            healthItemInfo.setName(entry.getKey());

            Object value = entry.getValue();
            if (value instanceof Map) {
                Map<String, Object> healthItemMap = (Map<String, Object>) value;
                Object statusObj = healthItemMap.get("status");
                healthItemInfo.setStatus(StringUtils.isEmpty(statusObj)
                        ? UNKNOWN_STATUS
                        : (String) statusObj);
                Object healthItemDetails = healthItemMap.get("details");
                if (healthItemDetails instanceof Map) {
                    Map<String, Object> innerDetail = (Map<String, Object>) healthItemDetails;
                    healthItemInfo.setDetails(innerDetail);
                }
            }

            return healthItemInfo;
        }).collect(Collectors.toList());
        result.setDetails(detailList);

        return result;
    }
}
