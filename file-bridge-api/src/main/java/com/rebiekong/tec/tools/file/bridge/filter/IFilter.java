package com.rebiekong.tec.tools.file.bridge.filter;

import com.rebiekong.tec.tools.file.bridge.entity.FileMeta;

import java.util.Map;
import java.util.function.Predicate;

/**
 * IFilter 文件过滤器接口
 *
 * @author rebie
 * @since 2023/04/13.
 */
public interface IFilter extends Predicate<FileMeta> {

    /**
     * 返回配置文件中的flag
     *
     * @return flag
     */
    default String flag() {
        return null;
    }

    /**
     * 初始化接口服务
     *
     * @param obj 参数
     */
    default void init(Map<String, Object> obj) {
    }

}
