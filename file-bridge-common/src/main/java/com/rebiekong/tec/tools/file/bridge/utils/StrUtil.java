package com.rebiekong.tec.tools.file.bridge.utils;

/**
 * StrUtil
 *
 * @author rebie
 * @since 2023/04/14.
 */
public class StrUtil {

    public static boolean isBlank(CharSequence str) {
        int length;
        if (str != null && (length = str.length()) != 0) {
            for (int i = 0; i < length; ++i) {
                if (!CharUtil.isBlankChar(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }
}
