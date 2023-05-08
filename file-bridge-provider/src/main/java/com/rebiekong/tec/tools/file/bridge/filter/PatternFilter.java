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
 * PatternFilter
 *
 * @author rebie
 * @since 2023/05/08.
 */
public class PatternFilter implements IFilter {
    private String pattern;

    @Override
    public String flag() {
        return "pattern";
    }

    @Override
    public void init(Map<String, Object> obj) {
        this.pattern = String.valueOf(obj.get("pattern"));
    }

    @Override
    public boolean test(FileMeta fileMeta) {
        if (fileMeta.isFile()) {
            return fileMeta.getFileName().matches(pattern);
        } else {
            return true;
        }
    }
}
