package com.rebiekong.tec.tools.file.bridge.jobs.impl;

import com.rebiekong.tec.tools.file.bridge.jobs.IJob;
import com.rebiekong.tec.tools.file.bridge.jobs.param.DualSideParam;
import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import lombok.ToString;

/**
 * MoveJob
 *
 * @author rebie
 * @since 2023/04/13.
 */
@ToString
public class MoveJob implements IJob {

    private final IJob copyJob;
    private final IJob delJob;
    private final String path;
    private final IFileService input;
    private final IFileService output;

    public MoveJob(DualSideParam param) {
        this.path = param.getPath();
        this.input = param.getInput();
        this.output = param.getOutput();
        this.copyJob = RetryJob.wrap(new CopyJob(param));
        this.delJob = RetryJob.wrap(new DelJob(param.inputSideParam()), RetryJob.NEVER_RETRY);
    }

    @Override
    public void run() {
        copyJob.run();
        delJob.run();
    }
}
