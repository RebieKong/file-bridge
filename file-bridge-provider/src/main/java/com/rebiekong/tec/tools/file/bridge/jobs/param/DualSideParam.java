package com.rebiekong.tec.tools.file.bridge.jobs.param;

import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * DualSideParams
 *
 * @author rebie
 * @since 2023/04/13.
 */
@Data
@Builder
public class DualSideParam {
    private String path;
    private IFileService input;
    private IFileService output;
    @Builder.Default
    private Map<String, String> subParams = new HashMap<>();

    public DualSideParam setSubParams(String key, String value) {
        subParams.put(key, value);
        return this;
    }

    public String getSubParams(String key, String defaultValue) {
        return subParams.getOrDefault(key, defaultValue);
    }

    public SingleSideParam inputSideParam() {
        return SingleSideParam.builder()
                .path(path)
                .fileService(input)
                .build();
    }

    public SingleSideParam outputSideParam() {
        return SingleSideParam.builder()
                .path(path)
                .fileService(output)
                .build();
    }

}
