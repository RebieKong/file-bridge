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

import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileBridge {

    public static void main(String... args) {
        // 判断配置文件是否传入且存在
        if (args.length < 1) {
            System.exit(-1);
        }
        String configPath = args[0];
        if (!(new File(configPath).exists())) {
            System.exit(-2);
        }
        FileReader fileReader = new FileReader(configPath);
        JSONArray res = JSON.parseArray(fileReader.readString());
        List<FilePipe> filePipeList = new ArrayList<>();
        for (int i = 0; i < res.size(); i++) {
            JSONObject pipeLineInfo = res.getJSONObject(i);
            filePipeList.add(FilePipe.getPipeLine(pipeLineInfo));
        }
        for (FilePipe filePipe : filePipeList) {
            filePipe.run();
            filePipe.close();
        }
    }
}
