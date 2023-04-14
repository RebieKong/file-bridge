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
package com.rebiekong.tec.tools.file.bridge.service;

import com.rebiekong.tec.tools.file.bridge.entity.FileMeta;
import com.rebiekong.tec.tools.file.bridge.exception.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * FileService 文件传输接口
 * 定义文件处理逻辑
 *
 * @author rebie
 * @since 2023/04/13.
 */
public interface IFileService {

    /**
     * 返回配置文件中的flag
     *
     * @return flag
     */
    String fileServiceFlag();

    /**
     * 初始化接口服务
     *
     * @param obj 参数
     */
    void init(Map<String, String> obj);

    /**
     * 自定义关闭退出方法
     */
    default void close() {
    }

    /**
     * 打开文件
     *
     * @param path 当前数据源的相对路径
     * @return 返回文件流
     * @throws FileShippingReadException 当路径为目录或文件不存在时抛出
     */
    InputStream read(String path) throws FileShippingReadException;

    /**
     * 获取文件大小
     *
     * @param path 文件路径
     * @return 文件大小
     * @throws FileShippingReadException 当路径为目录或文件不存在时抛出
     */
    long fileSize(String path) throws FileShippingReadException;

    /**
     * 获取目录中的文件信息（文件/目录）
     *
     * @param path 当前数据源的相对路径
     * @return 目录文件信息
     * @throws FileShippingListException 当路径为文件或目录不存在时抛出
     */
    List<FileMeta> listDir(String path) throws FileShippingListException;

    /**
     * 新建目录
     *
     * @param path 当前数据源的相对路径
     * @throws FileShippingMkdirException 当路径为文件或父目录不存在时抛出
     */
    void mkdir(String path) throws FileShippingMkdirException;

    /**
     * 判断目录是否存在
     *
     * @param path 当前数据源的相对路径
     * @return 是否存在
     */
    default boolean dirExists(String path) {
        return true;
    }

    /**
     * 判断文件是否存在
     *
     * @param path 当前数据源的相对路径
     * @return 是否存在
     */
    default boolean fileExists(String path) {
        return true;
    }

    /**
     * 写文件
     *
     * @param path        当前数据源的相对路径
     * @param inputStream 输入流
     * @throws FileShippingWriteException 当路径为目录或父目录不存在时抛出
     */
    void write(String path, InputStream inputStream) throws FileShippingWriteException;

    /**
     * 删除文件
     *
     * @param path 当前数据源的相对路径
     * @throws FileShippingRmException 当路径为目录时抛出
     */
    void rm(String path) throws FileShippingRmException;

    /**
     * 重命名文件
     *
     * @param src source
     * @param dst dst
     * @throws FileShippingRmException 重命名失败时抛出
     */
    void rename(String src, String dst) throws FileShippingRmException;

    /**
     * 关闭流方法
     * 允许文件接口自定义读取流的关闭逻辑
     *
     * @param input 读取流
     * @throws IOException 流关闭异常时抛出
     */
    default void closeInputStream(InputStream input) throws IOException {
        input.close();
    }
}
