package com.rebiekong.tec.tools.file.bridge.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * IOUtil
 *
 * @author rebie
 * @since 2023/04/14.
 */
public class IOUtil {

    public static void streamPipe(InputStream input, OutputStream output) throws IOException {
        int bufferSize = 4096;
        byte[] buffer;

        buffer = new byte[bufferSize];
        int readSize = input.read(buffer);
        while (readSize > 0) {
            output.write(Arrays.copyOfRange(buffer, 0, readSize));
            buffer = new byte[bufferSize];
            readSize = input.read(buffer);
        }
    }
}
