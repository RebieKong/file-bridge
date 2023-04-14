package com.rebiekong.tec.tools.file.bridge.service.impl;

import com.rebiekong.tec.tools.file.bridge.entity.FileMeta;
import com.rebiekong.tec.tools.file.bridge.exception.*;
import com.rebiekong.tec.tools.file.bridge.service.IFileService;
import com.rebiekong.tec.tools.file.bridge.utils.IOUtil;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * LocalFileServiceImpl
 *
 * @author rebie
 * @since 2023/04/13.
 */
public class LocalFileServiceImpl implements IFileService {
    protected String root;

    @Override
    public String fileServiceFlag() {
        return "local";
    }

    @Override
    public void init(Map<String, String> obj) {
        this.root = new File(obj.get("root")).getAbsolutePath().replaceAll("\\\\", "/");
    }

    @Override
    public InputStream read(String path) throws FileShippingReadException {
        File nf = new File(root + path);
        if (nf.exists()) {
            try {
                return new FileInputStream(nf);
            } catch (FileNotFoundException ignored) {
            }
        } else {
            throw new FileShippingReadException();
        }
        throw new FileShippingReadException();
    }

    @Override
    public long fileSize(String path) throws FileShippingReadException {
        File nf = new File(root + path);
        return nf.length();
    }

    @Override
    public void rename(String src, String dst) throws FileShippingRmException {
        File s = new File(root + src);
        s.renameTo(new File(root + dst));
    }

    @Override
    public void rm(String path) throws FileShippingRmException {
        File nf = new File(root + path);
        if (nf.exists()) {
            if (!nf.isFile()) {
                throw new FileShippingRmException();
            }
            if (!nf.delete()) {
                throw new FileShippingRmException();
            }

        }
    }

    @Override
    public void mkdir(String path) throws FileShippingMkdirException {
        File nf = new File(root + path);
        if (!nf.getParentFile().exists()) {
            throw new FileShippingMkdirException();
        }
        if (nf.exists()) {
            if (nf.isFile()) {
                throw new FileShippingMkdirException();
            }
        } else {
            if (!nf.mkdir()) {
                throw new FileShippingMkdirException();
            }
        }
    }

    @Override
    public void write(String path, InputStream inputStream) throws FileShippingWriteException {
        File nf = new File(root + path);
        if (!nf.getParentFile().exists()) {
            throw new FileShippingWriteException();
        }
        if (nf.exists()) {
            throw new FileShippingWriteException();
        }
        try {
            nf.createNewFile();
            IOUtil.streamPipe(inputStream, Files.newOutputStream(nf.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileShippingWriteException();
        }
    }

    @Override
    public List<FileMeta> listDir(String path) throws FileShippingListException {
        File nf = new File(root + path);
        if (!nf.exists()) {
            throw new FileShippingListException();
        }
        if (!nf.isDirectory()) {
            throw new FileShippingListException();
        }
        File[] fs = nf.listFiles();
        if (fs != null) {
            return Arrays.stream(fs).map(s -> FileMeta.builder()
                            .path(s.getAbsolutePath().replaceAll("\\\\", "/").replace(root, ""))
                            .isDir(s.isDirectory())
                            .lastModifyTime(s.lastModified())
                            .build())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean fileExists(String path) {
        File nf = new File(root + path);
        return nf.isFile() && nf.exists();
    }

    @Override
    public String toString() {
        return "LocalFileService(root=" + root + ")";
    }
}
