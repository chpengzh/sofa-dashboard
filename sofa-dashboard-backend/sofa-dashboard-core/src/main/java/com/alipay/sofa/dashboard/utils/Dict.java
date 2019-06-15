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
package com.alipay.sofa.dashboard.utils;

import com.alibaba.fastjson.JSON;

import java.util.LinkedHashMap;

/**
 * 方便易用的字典类
 */
public class Dict extends LinkedHashMap<String, Object> {

    /**
     * 创建一个字典
     *
     * @return 字典实例
     */
    public static Dict create() {
        return new Dict();
    }

    /**
     * 创建一个字典并设置初始化值
     *
     * @param key   初始化键
     * @param value 初始化值
     * @return 字典实例
     */
    public static Dict set(String key, Object value) {
        return create().and(key, value);
    }

    /**
     * 为字典添加一个属性
     *
     * @param key   添加键
     * @param value 添加值
     * @return 字典实例
     */
    public Dict and(String key, Object value) {
        put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}