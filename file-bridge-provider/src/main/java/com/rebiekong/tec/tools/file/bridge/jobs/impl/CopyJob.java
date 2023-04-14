package com.rebiekong.tec.tools.file.bridge.jobs.impl;

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
public class CopyJob implements IJob {

    private final String path;
    private final IFileService input;
    private final IFileService output;
    private final IJob deleteJob;

    public CopyJob(DualSideParam param) {
        this.path = param.getPath();
        this.input = param.getInput();
        this.output = param.getOutput();
        this.deleteJob = RetryJob.fastFailWrap(new DelJob(param.outputSideParam()));
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
