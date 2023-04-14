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
        // TODO 后续可改使用可限速的多线程/分布式的技术方案
        for (FilePipe filePipe : filePipeList) {
            filePipe.run();
            filePipe.close();
        }
    }
}
