package com.rebiekong.tec.tools.file.bridge.jobs;

/**
 * BaseJob
 *
 * @author rebie
 * @since 2023/07/26.
 */
abstract public class BaseJob implements IJob {

    private Integer jobIndex;
    private Integer inputIndex;

    @Override
    public void setJobIndex(Integer jobIndex) {
        this.jobIndex = jobIndex;
    }

    @Override
    public void setInputIndex(Integer inputIndex) {
        this.inputIndex = inputIndex;
    }

    @Override
    public String sortIndex() {
        return String.format("%08d___%08d", inputIndex, jobIndex);
    }
}
