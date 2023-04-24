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
package com.rebiekong.tec.tools.file.bridge.filter;

import com.rebiekong.tec.tools.file.bridge.entity.FileMeta;

import java.util.Map;

/**
 * FileSizeFilter
 *
 * @author rebie
 * @since 2023/04/23.
 */
public class FileSizeFilter implements IFilter {
    long maxSize;
    long minSize;

    @Override
    public String flag() {
        return "fileSize";
    }

    @Override
    public void init(Map<String, Object> obj) {
        this.maxSize = Long.parseLong(obj.getOrDefault("maxSize", String.valueOf(Integer.MAX_VALUE)).toString());
        this.minSize = Long.parseLong(obj.getOrDefault("minSize", "0").toString());
    }

    @Override
    public boolean test(FileMeta fileMeta) {
        if (fileMeta.isFile()) {
            return fileMeta.getFileSize() >= minSize && fileMeta.getFileSize() <= maxSize;
        } else {
            return true;
        }
    }
}
