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

import com.alibaba.fastjson.JSONObject;
import com.rebiekong.tec.tools.file.bridge.exception.FileShippingServiceException;
import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * FileServiceFactory
 *
 * @author rebie
 * @since 2023/04/13.
 */
public class FileServiceFactory {
    public static IFileService getService(JSONObject obj) {
        IFileService fileService = create(obj);
        if (fileService == null) {
            throw new FileShippingServiceException();
        }
        return fileService;
    }

    public static IFileService create(JSONObject obj) {

        Reflections reflections = new Reflections(IFileService.class.getPackage().getName());
        Set<Class<? extends IFileService>> fs = reflections.getSubTypesOf(IFileService.class);

        Map<String, String> params = new HashMap<>();
        for (String key : obj.keySet()) {
            Object value = obj.get(key);
            if (value != null) {
                params.put(key, String.valueOf(value));
            }
        }
        for (Class<? extends IFileService> f : fs) {
            try {
                IFileService inst = f.newInstance();
                if (inst.fileServiceFlag().equals(obj.getString("type"))) {
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
