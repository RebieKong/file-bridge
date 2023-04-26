/*
 *   Copyright 2023 rebiekong
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.rebiekong.tec.tools.file.bridge.jobs.impl;

import com.rebiekong.tec.tools.file.bridge.jobs.IJob;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * RetryJob
 *
 * @author rebie
 * @since 2023/04/13.
 */
@ToString
public class RetryJob implements IJob {

    public static final int NEVER_RETRY = -1;
    private final IJob job;
    private final Integer maxFail;

    private RetryJob(IJob job, Integer maxFail) {
        this.job = job;
        this.maxFail = maxFail;
    }

    public static IJob wrap(IJob job) {
        return wrap(job, 3);
    }

    public static IJob wrap(IJob job, Integer maxFail) {
        return new RetryJob(job, maxFail);
    }

    public static IJob fastFailWrap(IJob job) {
        return new RetryJob(job, NEVER_RETRY);
    }

    @Override
    public void run() {
        if (maxFail == NEVER_RETRY) {
            try {
                job.run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            AtomicInteger failCounter = new AtomicInteger(0);
            while (failCounter.getAndIncrement() < maxFail) {
                try {
                    job.run();
                    break;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
