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
        if (obj == null) {
            return null;
        }
        return MirrorParam.builder()
                .mirrorMode(obj.getInteger("mirror_mode"))
                .cloneResultPath(obj.getString("clone_result_path")).build();
    }
}
