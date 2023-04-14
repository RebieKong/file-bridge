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
package com.rebiekong.tec.tools.file.bridge;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rebiekong.tec.tools.file.bridge.filter.IFilter;
import org.reflections.Reflections;

import java.util.*;

/**
 * FilterFactory
 *
 * @author rebie
 * @since 2023/04/13.
 */
public class FilterFactory {

    public static IFilter getFilter(JSONArray object) {
        List<IFilter> filters = new ArrayList<>();
        for (int i = 0; i < object.size(); i++) {
            JSONObject conf = object.getJSONObject(i);
            IFilter filter = FilterFactory.create(conf);
            filters.add(filter);
        }
        return f -> filters.stream().allMatch(s -> s.test(f));
    }

    public static IFilter create(JSONObject obj) {
        if (obj == null) {
            return null;
        }
        Reflections reflections = new Reflections(IFilter.class.getPackage().getName());
        Set<Class<? extends IFilter>> fs = reflections.getSubTypesOf(IFilter.class);

        Map<String, Object> params = new HashMap<>();
        for (String key : obj.keySet()) {
            Object value = obj.get(key);
            if (value != null) {
                params.put(key, value);
            }
        }
        for (Class<? extends IFilter> f : fs) {
            try {
                IFilter inst = f.newInstance();
                if (inst.flag().equals(obj.getString("type"))) {
                    inst.init(params);
                    return inst;
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
