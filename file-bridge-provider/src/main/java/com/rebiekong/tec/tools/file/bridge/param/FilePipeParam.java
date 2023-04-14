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
