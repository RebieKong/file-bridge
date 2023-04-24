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
     * 文件大小，目录需要设置为-1
     */
    @Getter
    private Long fileSize;
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
