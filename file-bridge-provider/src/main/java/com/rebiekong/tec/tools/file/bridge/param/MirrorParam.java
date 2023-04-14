package com.rebiekong.tec.tools.file.bridge.param;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;

/**
 * MirrorParam
 *
 * @author rebie
 * @since 2023/04/13.
 */
@Data
@Builder
public class MirrorParam {
    /**
     * 镜像模式 1：追加模式、2：全量模式
     */
    private final Integer mirrorMode;
    /**
     * 镜像标记文件地址 不设置则为输出目录
     */
    private final String cloneResultPath;

    public static MirrorParam fromJsonObj(JSONObject obj) {
        return MirrorParam.builder()
                .mirrorMode(obj.getInteger("mirror_mode"))
                .cloneResultPath(obj.getString("clone_result_path")).build();
    }
}
