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
package com.rebiekong.tec.tools.file.bridge;

import com.alibaba.fastjson.JSONObject;
import com.rebiekong.tec.tools.file.bridge.entity.FileMeta;
import com.rebiekong.tec.tools.file.bridge.jobs.IJob;
import com.rebiekong.tec.tools.file.bridge.jobs.impl.CloneJob;
import com.rebiekong.tec.tools.file.bridge.jobs.impl.MkdirJob;
import com.rebiekong.tec.tools.file.bridge.jobs.impl.MoveJob;
import com.rebiekong.tec.tools.file.bridge.jobs.impl.RetryJob;
import com.rebiekong.tec.tools.file.bridge.jobs.param.DualSideParam;
import com.rebiekong.tec.tools.file.bridge.jobs.param.SingleSideParam;
import com.rebiekong.tec.tools.file.bridge.param.FilePipeParam;
import com.rebiekong.tec.tools.file.bridge.param.MirrorParam;
import com.rebiekong.tec.tools.file.bridge.service.IFileService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * FilePipe
 *
 * @author rebie
 * @since 2023/04/13.
 */
public class FilePipe {

    private static final String ROOT = "/";
    private final MirrorParam mirrorParam;
    private final IFileService input;
    private final IFileService output;
    private final Predicate<FileMeta> filePredicate;

    public FilePipe(FilePipeParam param) {
        this.input = param.getInput();
        this.output = param.getOutput();
        this.filePredicate = param.getFilePredicate();
        this.mirrorParam = param.getMirrorParam();
    }

    public static FilePipe getPipeLine(JSONObject obj) {
        return new FilePipe(FilePipeParam.builder()
                .input(FileServiceFactory.getService(obj.getJSONObject("input")))
                .output(FileServiceFactory.getService(obj.getJSONObject("output")))
                .filePredicate(FilterFactory.getFilter(obj.getJSONArray("file_filter")))
                .mirrorParam(MirrorParam.fromJsonObj(obj.getJSONObject("mirror_param")))
                .build());
    }

    public void run() {
        List<IJob> jobs = analyzePath(ROOT);
        jobs.forEach(IJob::run);
    }

    public void close() {
        input.close();
        output.close();
    }

    private List<IJob> analyzePath(String path) {
        List<IJob> jobs = new ArrayList<>();
        input.listDir(path).forEach(fileMeta -> {
            if (fileMeta.isDir()) {
                jobs.add(RetryJob.wrap(new MkdirJob(SingleSideParam.builder()
                        .path(fileMeta.getPath())
                        .fileService(output)
                        .build())));
                jobs.addAll(analyzePath(fileMeta.getPath()));
            } else if (fileMeta.isFile()) {
                if (filePredicate.test(fileMeta)) {
                    DualSideParam param = DualSideParam.builder()
                            .path(fileMeta.getPath())
                            .input(input)
                            .output(output)
                            .build();
                    if (mirrorParam != null) {
                        jobs.add(RetryJob.wrap(new CloneJob(param
                                .setSubParams(CloneJob.MIRROR_MODE_PARAM, mirrorParam.getMirrorMode().equals(1) ? CloneJob.MIRROR_MODE_APPEND : CloneJob.MIRROR_MODE_FULL)
                                .setSubParams(CloneJob.CLONE_FLAG_STORE_PATH_PARAM, mirrorParam.getCloneResultPath())
                        )));
                    } else {
                        jobs.add(RetryJob.wrap(new MoveJob(param)));
                    }
                }
            }
        });
        return jobs;
    }

}
