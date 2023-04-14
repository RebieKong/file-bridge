package com.rebiekong.tec.tools.file.bridge.jobs.impl;

import com.rebiekong.tec.tools.file.bridge.jobs.IJob;
import com.rebiekong.tec.tools.file.bridge.jobs.param.SingleSideParam;
import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import lombok.ToString;

/**
 * MkdirJob
 *
 * @author rebie
 * @since 2023/04/13.
 */
@ToString
public class MkdirJob implements IJob {

    private final String path;
    private final IFileService fileService;

    public MkdirJob(SingleSideParam param) {
        this.path = param.getPath();
        this.fileService = param.getFileService();
    }

    @Override
    public void run() {
        fileService.mkdir(path);
    }
}
