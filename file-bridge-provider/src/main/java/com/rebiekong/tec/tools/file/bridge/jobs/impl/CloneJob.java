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

import cn.hutool.core.io.IoUtil;
import com.rebiekong.tec.tools.file.bridge.jobs.IJob;
import com.rebiekong.tec.tools.file.bridge.jobs.param.DualSideParam;
import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import lombok.ToString;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.function.Predicate;

/**
 * CloneJob
 *
 * @author rebie
 * @since 2023/04/13.
 */
@ToString
public class CloneJob implements IJob {

    public static final String DEFAULT_CLONE_FINISHED_FLAG = ".cloneFinish";
    public static final String MIRROR_MODE_PARAM = "MIRROR_MODE_PARAM";
    public static final String MIRROR_MODE_APPEND = "MIRROR_MODE_APPEND";
    public static final String MIRROR_MODE_FULL = "MIRROR_MODE_APPEND";

    public static final String CLONE_FLAG_STORE_PATH_PARAM = "CLONE_FLAG_STORE_PATH_PARAM";

    private final String path;
    private final IFileService input;
    private final IFileService output;

    private final String cloneFinishedFlag;
    private final String cloneResultStorePath;
    private final boolean isAppend;

    private CloneJob(DualSideParam param) {
        this.path = param.getPath();
        this.input = param.getInput();
        this.output = param.getOutput();
        this.cloneFinishedFlag = DEFAULT_CLONE_FINISHED_FLAG;
        this.isAppend = param.getSubParams(MIRROR_MODE_PARAM, MIRROR_MODE_FULL).equals(MIRROR_MODE_APPEND);
        this.cloneResultStorePath = param.getSubParams(CLONE_FLAG_STORE_PATH_PARAM, null);
    }

    public static CloneJob of(DualSideParam param) {
        return new CloneJob(param);
    }

    @Override
    public void run() {
        try {
            Predicate<String> p = s -> !output.fileExists(path);
            if (isAppend) {
                p = p.and(s -> (!checkFlag()));
            }
            // 移动模式下当文件文件存在但大小不同时覆盖
            Predicate<String> ff = s -> output.fileExists(path) && (input.fileSize(path) != output.fileSize(path));

            // 检查本地文件是否存在，如果存在则不进行复制
            input.fileSize(path);
            if (ff.test(path) || p.test(path)) {
                if (output.fileExists(path)) {
                    output.rm(path);
                    output.rm(getFlagPath());
                }
                InputStream in = input.read(path);
                output.write(path + ".tmp", in);
                output.rename(path + ".tmp", path);
                input.closeInputStream(in);
                if (isAppend) {
                    ByteArrayInputStream flagIn = new ByteArrayInputStream(("clone finish" + new Date()).getBytes(StandardCharsets.UTF_8));
                    writeFlag(getFlagPath(), flagIn);
                    flagIn.close();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void writeFlag(String path, InputStream data) {
        if (cloneResultStorePath == null) {
            output.write(path, data);
        } else {
            byte[] buffer = IoUtil.readBytes(data);
            try (OutputStream fo = Files.newOutputStream(Paths.get(path))) {
                fo.write(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean checkFlag() {
        if (cloneResultStorePath == null) {
            return output.fileExists(getFlagPath());
        } else {
            return Files.exists(Paths.get(getFlagPath()));
        }
    }

    private String getFlagPath() {
        if (cloneResultStorePath != null) {
            return cloneResultStorePath + path + cloneFinishedFlag;
        }
        return path + cloneFinishedFlag;
    }
}
