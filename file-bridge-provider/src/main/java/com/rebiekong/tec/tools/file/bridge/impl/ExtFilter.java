package com.rebiekong.tec.tools.file.bridge.impl;

import com.rebiekong.tec.tools.file.bridge.entity.FileMeta;
import com.rebiekong.tec.tools.file.bridge.filter.IFilter;

import java.io.File;
import java.util.Map;

/**
 * ExtFilter 后缀名筛选器
 *
 * @author rebie
 * @since 2023/04/13.
 */
public class ExtFilter implements IFilter {
    private String ext;

    @Override
    public String flag() {
        return "ext";
    }

    @Override
    public void init(Map<String, Object> obj) {
        this.ext = String.valueOf(obj.get("ext"));

    }

    @Override
    public boolean test(FileMeta s) {
        return new File(s.getPath()).getName().endsWith(ext);
    }
}
