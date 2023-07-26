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

import com.alibaba.fastjson.JSONArray;
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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * FilePipe
 *
 * @author rebie
 * @since 2023/04/13.
 */
public class FilePipe {

    private static final String ROOT = "/";
    private final MirrorParam mirrorParam;
    private final List<IFileService> inputs;
    private final IFileService output;
    private final Predicate<FileMeta> filePredicate;

    public FilePipe(FilePipeParam param) {
        this.inputs = param.getInputs();
        this.output = param.getOutput();
        this.filePredicate = param.getFilePredicate();
        this.mirrorParam = param.getMirrorParam();
    }

    public static FilePipe getPipeLine(JSONObject obj) {
        JSONArray inputArr = obj.getJSONArray("input");
        List<IFileService> inputFileServices = new ArrayList<>();
        if (inputArr != null) {
            for (int i = 0; i < inputArr.size(); i++) {
                inputFileServices.add(FileServiceFactory.getService(inputArr.getJSONObject(i)));
            }
        }
        return new FilePipe(FilePipeParam.builder()
                .inputs(inputFileServices)
                .output(FileServiceFactory.getService(obj.getJSONObject("output")))
                .filePredicate(FilterFactory.getFilter(obj.getJSONArray("file_filter")))
                .mirrorParam(MirrorParam.fromJsonObj(obj.getJSONObject("mirror_param")))
                .build());
    }

    public void run() {
        analyzePath(ROOT).forEach(IJob::run);
    }

    public void close() {
        inputs.forEach(IFileService::close);
        output.close();
    }

    private List<IJob> analyzePath(String path) {
        AtomicInteger inputCT = new AtomicInteger(0);
        Function<List<IJob>, Consumer<IJob>> jobAccepter = jobList -> {
            AtomicInteger listIndexHolder = new AtomicInteger(0);
            return job -> job.setJobIndex(listIndexHolder.getAndIncrement());
        };
        return inputs.stream().parallel()
                .flatMap(input -> {
                    int inputIndex = inputCT.getAndIncrement();
                    return input.listDir(path).stream().flatMap(fileMeta -> {
                        List<IJob> jobs = new ArrayList<>();
                        Consumer<IJob> jobAppender = jobAccepter.apply(jobs);
                        if (fileMeta.isDir()) {
                            jobAppender.accept(RetryJob.wrap(MkdirJob.of(SingleSideParam.builder()
                                    .path(fileMeta.getPath())
                                    .fileService(output)
                                    .build())));
                            analyzePath(fileMeta.getPath()).forEach(jobAppender);
                        } else if (fileMeta.isFile()) {
                            if (filePredicate.test(fileMeta)) {
                                DualSideParam param = DualSideParam.builder()
                                        .path(fileMeta.getPath())
                                        .input(input)
                                        .output(output)
                                        .build();
                                IJob job;
                                if (mirrorParam != null) {
                                    job = CloneJob.of(param
                                            .setSubParams(CloneJob.MIRROR_MODE_PARAM, mirrorParam.getMirrorMode().equals(1) ? CloneJob.MIRROR_MODE_APPEND : CloneJob.MIRROR_MODE_FULL)
                                            .setSubParams(CloneJob.CLONE_FLAG_STORE_PATH_PARAM, mirrorParam.getCloneResultPath())
                                    );
                                } else {
                                    job = MoveJob.of(param);
                                }
                                jobAppender.accept(RetryJob.wrap(job));
                            }
                        }
                        return jobs.stream();
                    }).peek(s -> s.setInputIndex(inputIndex));
                }).sorted(Comparator.comparing(IJob::sortIndex)).collect(Collectors.toList());
    }

}
