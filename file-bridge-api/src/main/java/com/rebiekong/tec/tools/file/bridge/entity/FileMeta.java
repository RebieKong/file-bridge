package com.rebiekong.tec.tools.file.bridge.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * FileMeta 文件meta信息
 *
 * @author rebie
 * @since 2023/04/13.
 */
@Builder
@ToString
public class FileMeta {
    /**
     * 相对路径
     */
    @Getter
    private String path;
    /**
     * 是否目录
     */
    private Boolean isDir;
    /**
     * 最后编辑时间
     */
    @Getter
    private Long lastModifyTime;

    public boolean isDir() {
        return isDir;
    }

    public boolean isFile() {
        return !isDir;
    }
}
