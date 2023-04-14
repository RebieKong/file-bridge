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
package com.rebiekong.tec.tools.file.bridge.service.impl;

import com.rebiekong.tec.tools.file.bridge.entity.FileMeta;
import com.rebiekong.tec.tools.file.bridge.exception.*;
import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import io.minio.*;
import io.minio.messages.Item;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * MinioFileServiceImpl
 *
 * @author rebie
 * @since 2023/04/13.
 */
public class MinioFileServiceImpl implements IFileService {

    private String accessKey;
    private String secretKey;
    private String url;
    private String bucket;
    private Boolean trace;

    private MinioClient client = null;

    @Override
    public String fileServiceFlag() {
        return "minio";
    }

    @Override
    public void init(Map<String, String> obj) {
        this.accessKey = obj.get("accessKey");
        this.secretKey = obj.get("secretKey");
        this.url = obj.get("url");
        this.bucket = obj.get("bucket");
        this.trace = Boolean.valueOf(obj.getOrDefault("trace", "false"));

        client = MinioClient.builder()
                .endpoint(this.url)
                .credentials(this.accessKey, this.secretKey)
                .build();
        if (this.trace) {
            client.traceOn(System.out);
        }
    }

    @Override
    public boolean fileExists(String path) {
        try {
            Iterator<Result<Item>> b = null;
            b = client.listObjects(ListObjectsArgs.builder()
                    .bucket(bucket)
                    .prefix(path)
                    .build()).iterator();
            if (!b.hasNext()) {
                return false;
            }
            Item item = b.next().get();
            String on = item.objectName();
            if (!on.startsWith("/")) {
                return path.equals("/" + on);
            } else {
                return path.equals(on);
            }
        } catch (Throwable e) {
            throw new FileShippingMinioServerException();
        }
    }

    @Override
    public InputStream read(String path) throws FileShippingReadException {
        try {
            return client.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new FileShippingReadException();
        }

    }

    @Override
    public long fileSize(String path) throws FileShippingReadException {
        try {
            return client.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build()).size();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FileMeta> listDir(String path) throws FileShippingListException {
        try {
            List<FileMeta> fileMetas = new ArrayList<>();
            for (Result<Item> listObject : client.listObjects(ListObjectsArgs.builder()
                    .bucket(bucket)
                    .prefix(path)
                    .build())) {
                fileMetas.add(FileMeta.builder()
                        .lastModifyTime(listObject.get().lastModified().toEpochSecond())
                        .path("/" + listObject.get().objectName())
                        .isDir(listObject.get().isDir())
                        .build());
            }
            return fileMetas;
        } catch (Throwable e) {
            throw new FileShippingMinioServerException();
        }
    }

    @Override
    public void mkdir(String path) throws FileShippingMkdirException {

    }

    @Override
    public void write(String path, InputStream inputStream) throws FileShippingWriteException {
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
        } catch (Throwable e) {
            throw new FileShippingMinioServerException();
        }
    }

    @Override
    public void rm(String path) throws FileShippingRmException {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build());
        } catch (Throwable e) {
            throw new FileShippingMinioServerException();
        }
    }

    @Override
    public void rename(String src, String dst) throws FileShippingRmException {
        try {
            client.copyObject(CopyObjectArgs.builder()
                    .bucket(bucket)
                    .object(dst)
                    .source(CopySource.builder()
                            .bucket(bucket)
                            .object(src)
                            .build())
                    .build());
            rm(src);
        } catch (Throwable e) {
            throw new FileShippingMinioServerException();
        }
    }
}
