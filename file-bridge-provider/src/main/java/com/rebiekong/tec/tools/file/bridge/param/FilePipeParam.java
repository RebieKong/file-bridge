package com.rebiekong.tec.tools.file.bridge.param;

import com.rebiekong.tec.tools.file.bridge.entity.FileMeta;
import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import lombok.Builder;
import lombok.Data;

import java.util.function.Predicate;

/**
 * FilePipeParam
 *
 * @author rebie
 * @since 2023/04/13.
 */
@Data
@Builder
public class FilePipeParam {
    /**
     * 源插件
     */
    private final IFileService input;
    /**
     * 目标插件
     */
    private final IFileService output;
    /**
     * 文件过滤器
     */
    private final Predicate<FileMeta> filePredicate;
    /**
     * 镜像模式参数 不设置则为移动模式
     */
    private final MirrorParam mirrorParam;
}
