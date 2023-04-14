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
package com.rebiekong.tec.tools.file.bridge.impl;

import com.rebiekong.tec.tools.file.bridge.entity.FileMeta;
import com.rebiekong.tec.tools.file.bridge.filter.IFilter;

import java.util.Map;

/**
 * ModifyTimeFilter 修改时间筛选器
 *
 * @author rebie
 * @since 2023/04/13.
 */
public class ModifyTimeFilter implements IFilter {
    long modifyTime;

    @Override
    public String flag() {
        return "modifyTime";
    }

    @Override
    public void init(Map<String, Object> obj) {
        this.modifyTime = Long.parseLong(obj.getOrDefault("modifyWatcher", "10").toString());
    }

    @Override
    public boolean test(FileMeta fileMeta) {
        if (modifyTime >= 0) {
            return fileMeta.getLastModifyTime() < System.currentTimeMillis() - modifyTime * 1000;
        }
        return false;
    }
}
