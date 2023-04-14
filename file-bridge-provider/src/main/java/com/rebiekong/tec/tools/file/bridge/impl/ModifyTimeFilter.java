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
