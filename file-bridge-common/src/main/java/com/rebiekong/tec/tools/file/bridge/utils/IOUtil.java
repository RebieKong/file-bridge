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
