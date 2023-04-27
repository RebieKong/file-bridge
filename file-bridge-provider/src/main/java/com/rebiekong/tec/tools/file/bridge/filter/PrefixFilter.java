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

import java.io.File;
import java.util.Map;

/**
 * PrefixFilter 前缀名筛选器
 *
 * @author rebie
 * @since 2023/04/24.
 */
public class PrefixFilter implements IFilter {
    private String prefix;

    @Override
    public String flag() {
        return "prefix";
    }

    @Override
    public void init(Map<String, Object> obj) {
        this.prefix = String.valueOf(obj.get("prefix"));

    }

    @Override
    public boolean test(FileMeta s) {
        return new File(s.getPath()).getName().startsWith(prefix);
    }
}
