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
