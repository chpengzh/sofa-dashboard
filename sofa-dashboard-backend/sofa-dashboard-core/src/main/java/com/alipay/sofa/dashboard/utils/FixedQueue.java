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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A queue instance which has limit size.
 * When it reach its maximum capacity, the first element in queue will be discarded
 * when a new element is offered.
 * It can be update by a thread pool but read by another thread pool.
 * So it should and must be thread safe!!
 *
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/5/9 5:19 PM
 * @author chpengzh (chpengzh@foxmail.com)
 **/
public class FixedQueue<E> {

    /**
     * Maximum capacity size
     */
    private final int           maxCapacity;

    private final ReadWriteLock lock  = new ReentrantReadWriteLock();

    private final List<E>       queue = new CopyOnWriteArrayList<>();

    public FixedQueue(int maxCapacity) {
        super();
        this.maxCapacity = maxCapacity;
    }

    /**
     * Add an element into fixed queue
     *
     * @param e element to be offered
     */
    public void offer(E e) {
        lock.writeLock().lock();
        try {
            if (size() > maxCapacity) {
                queue.remove(0);
            }
            queue.add(e);

        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Access element at a certain position
     *
     * @param position element position
     * @return element instance
     */
    public E get(int position) {
        lock.readLock().lock();
        try {
            return queue.get(position);

        } finally {
            lock.readLock().unlock();
        }
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int size() {
        return queue.size();
    }

    /**
     * Get A readonly queue
     *
     * @return a read only queue instance
     */
    public List<E> getReadOnlyQueue() {
        return Collections.unmodifiableList(queue);
    }
}
