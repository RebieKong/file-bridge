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

import com.rebiekong.tec.tools.file.bridge.jobs.BaseJob;
import com.rebiekong.tec.tools.file.bridge.jobs.IJob;
import com.rebiekong.tec.tools.file.bridge.jobs.param.DualSideParam;
import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;

/**
 * CopyJob
 *
 * @author rebie
 * @since 2023/04/13.
 */
@ToString
public class CopyJob extends BaseJob {

    private final String path;
    private final IFileService input;
    private final IFileService output;
    private final IJob deleteJob;

    private CopyJob(DualSideParam param) {
        this.path = param.getPath();
        this.input = param.getInput();
        this.output = param.getOutput();
        this.deleteJob = RetryJob.fastFailWrap(DelJob.of(param.outputSideParam()));
    }

    public static CopyJob of(DualSideParam param) {
        return new CopyJob(param);
    }

    @Override
    public void run() {
        deleteJob.run();
        InputStream in = input.read(path);
        output.write(path, in);
        try {
            input.closeInputStream(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
