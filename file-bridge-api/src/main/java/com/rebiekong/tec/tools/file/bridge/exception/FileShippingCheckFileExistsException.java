package com.rebiekong.tec.tools.file.bridge.exception;

/**
 * FileShippingCheckFileExistsException 文件存在检测异常
 *
 * @author rebie
 * @since 2023/04/13.
 */
public class FileShippingCheckFileExistsException extends FileShippingException {
    public FileShippingCheckFileExistsException(Exception e) {
        super(e);
    }
}
