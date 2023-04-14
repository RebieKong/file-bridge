package com.rebiekong.tec.tools.file.bridge.exception;

/**
 * FileShippingException 文件同步通用异常
 *
 * @author rebie
 * @since 2023/04/13.
 */
abstract public class FileShippingException extends RuntimeException {
    public FileShippingException() {

    }

    public FileShippingException(Exception e) {
        super(e);
    }
}
