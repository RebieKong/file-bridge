package com.rebiekong.tec.tools.file.bridge.impl;

import com.rebiekong.tec.tools.file.bridge.entity.FileMeta;
import com.rebiekong.tec.tools.file.bridge.filter.IFilter;

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
