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
import com.rebiekong.tec.tools.file.bridge.jobs.param.DualSideParam;
import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * MoveJob
 *
 * @author rebie
 * @since 2023/04/13.
 */
@ToString
@Slf4j
public class MoveJob implements IJob {

    private final IJob copyJob;
    private final IJob rollBackJob;
    private final IJob delJob;
    private final String path;
    private final IFileService input;
    private final IFileService output;

    private MoveJob(DualSideParam param) {
        this.path = param.getPath();
        this.input = param.getInput();
        this.output = param.getOutput();
        this.copyJob = RetryJob.wrap(CopyJob.of(param));
        this.rollBackJob = RetryJob.wrap(DelJob.of(param.outputSideParam()), RetryJob.NEVER_RETRY);
        this.delJob = RetryJob.wrap(DelJob.of(param.inputSideParam()), RetryJob.NEVER_RETRY);
    }

    public static MoveJob of(DualSideParam param) {
        return new MoveJob(param);
    }

    @Override
    public void run() {
        try {
            copyJob.run();
            delJob.run();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            rollBackJob.run();
        }
    }
}
