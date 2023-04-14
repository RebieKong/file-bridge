package com.rebiekong.tec.tools.file.bridge.jobs.param;

import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import lombok.Builder;
import lombok.Data;

/**
 * SingleSideParam
 *
 * @author rebie
 * @since 2023/04/13.
 */
@Data
@Builder
public class SingleSideParam {
    private String path;
    private IFileService fileService;
}
