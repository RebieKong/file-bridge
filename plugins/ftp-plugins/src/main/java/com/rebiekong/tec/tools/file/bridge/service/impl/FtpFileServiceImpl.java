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

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.thread.ThreadUtil;
import com.rebiekong.tec.tools.file.bridge.entity.FileMeta;
import com.rebiekong.tec.tools.file.bridge.exception.*;
import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import com.rebiekong.tec.tools.file.bridge.utils.NumberUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * FtpFileServiceImpl
 *
 * @author rebie
 * @since 2023/04/13.
 */
public class FtpFileServiceImpl implements IFileService {

    private String ip;
    private Integer port;
    private String userName;
    private String userPassword;
    private String root;
    private FTPClient ftpClient;
    private ScheduledExecutorService exe;

    @Override
    public void close() {
        exe.shutdown();
    }

    @Override
    public String fileServiceFlag() {
        return "ftp";
    }

    @Override
    public void init(Map<String, String> obj) {
        this.root = obj.get("root");
        this.ip = obj.get("ip");
        this.port = Integer.parseInt(obj.get("port"));
        this.userName = obj.get("username");
        this.userPassword = obj.get("password");
        try {
            initFtp();
        } catch (IOException e) {
            throw new FileShippingFtpServerException();
        }
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNamePrefix("ftp-timer").build();
        exe = new ScheduledThreadPoolExecutor(2, namedThreadFactory);
        exe.scheduleAtFixedRate(this::noOp, 500, 1000, TimeUnit.SECONDS);
    }

    private void initFtp() throws IOException {
        ftpClient = new FTPClient();
        ftpClient.connect(ip, port);
        ftpClient.login(userName, userPassword);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setKeepAlive(true);
    }

    private void noOp() {
        try {
            ftpClient.sendNoOp();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                initFtp();
            } catch (IOException ignored) {
            }
        }
    }

    private synchronized void tryFtp() {
        if (!ftpClient.isAvailable()) {
            try {
                initFtp();
            } catch (IOException e) {
                throw new FileShippingFtpServerException();
            }
        }
    }

    @Override
    public void closeInputStream(InputStream input) throws IOException {
        AtomicInteger ct = new AtomicInteger(0);
        while (true) {
            if (ftpClient.completePendingCommand() || ct.incrementAndGet() > 5) {
                input.close();
                break;
            }
            ThreadUtil.sleep(1, TimeUnit.SECONDS);
        }
    }


    @Override
    public InputStream read(String path) {
        tryFtp();
        try {
            return ftpClient.retrieveFileStream(translate(path));
        } catch (IOException e) {
            throw new FileShippingReadException();
        }
    }

    @Override
    public void rename(String src, String dst) throws FileShippingRmException {
        try {
            ftpClient.rename(translate(src), translate(dst));
        } catch (IOException e) {
            throw new FileShippingReadException();
        }
    }

    @Override
    public long fileSize(String path) throws FileShippingReadException {
        try {
            if (NumberUtil.isNumber(ftpClient.getSize(translate(path)))) {
                return Long.parseLong(ftpClient.getSize(translate(path)));
            } else {
                return fileSize(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rm(String path) {
        tryFtp();
        try {
            ftpClient.deleteFile(translate(path));
        } catch (IOException e) {
            throw new FileShippingRmException();
        }
    }

    @Override
    public void mkdir(String path) {
        tryFtp();
        try {
            ftpClient.makeDirectory(translate(path));
        } catch (IOException e) {
            throw new FileShippingMkdirException();
        }
    }

    @Override
    public boolean fileExists(String path) {
        tryFtp();
        try {
            InputStream is = ftpClient.retrieveFileStream(translate(path));
            if (is == null || ftpClient.getReplyCode() == FTPReply.FILE_UNAVAILABLE) {
                return false;
            }
            is.close();
            return true;
        } catch (IOException e) {
            throw new FileShippingCheckFileExistsException(e);
        }
    }

    @Override
    public void write(String path, InputStream inputStream) {
        tryFtp();
        try {
            ftpClient.storeFile(translate(path + ".temp"), inputStream);
            ftpClient.rename(translate(path + ".temp"), translate(path));
        } catch (IOException e) {
            throw new FileShippingWriteException();
        }
    }

    private String translate(String path) {
        if (Objects.equals(path, "/")) {
            return new String((root).getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        } else {
            return new String((root + path).getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }
    }

    @Override
    public List<FileMeta> listDir(String path) {
        tryFtp();
        FTPFile[] dirs;
        try {
            dirs = ftpClient.listFiles(translate(path));
        } catch (IOException e) {
            try {
                initFtp();
            } catch (IOException ex) {
                throw new FileShippingFtpServerException();
            }
            throw new FileShippingListException();
        }
        return Arrays.stream(dirs).map(file -> FileMeta.builder()
                .isDir(file.isDirectory())
                .path(("/".equals(path) ? path : (path + "/")) + new String(file.getName().getBytes(StandardCharsets.ISO_8859_1)).replace("\\\\", "/").replaceAll(translate(""), ""))
                .lastModifyTime(file.getTimestamp().getTimeInMillis())
                .build()).collect(Collectors.toList());
    }
}
