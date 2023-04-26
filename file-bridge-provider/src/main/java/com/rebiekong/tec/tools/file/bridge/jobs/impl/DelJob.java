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
import com.rebiekong.tec.tools.file.bridge.jobs.param.SingleSideParam;
import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import lombok.ToString;

/**
 * DelJob
 *
 * @author rebie
 * @since 2023/04/13.
 */
@ToString
public class DelJob implements IJob {
    private final String path;
    private final IFileService fileService;

    private DelJob(SingleSideParam param) {
        this.path = param.getPath();
        this.fileService = param.getFileService();
    }

    public static DelJob of(SingleSideParam param) {
        return new DelJob(param);
    }

    @Override
    public void run() {
        fileService.rm(path);
    }
}
