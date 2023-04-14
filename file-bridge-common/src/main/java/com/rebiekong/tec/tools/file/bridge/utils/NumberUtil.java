package com.rebiekong.tec.tools.file.bridge.utils;

/**
 * NumberUtil
 *
 * @author rebie
 * @since 2023/04/14.
 */
public class NumberUtil {
    public static boolean isNumber(CharSequence str) {
        if (StrUtil.isBlank(str)) {
            return false;
        } else {
            char[] chars = str.toString().toCharArray();
            int sz = chars.length;
            boolean hasExp = false;
            boolean hasDecPoint = false;
            boolean allowSigns = false;
            boolean foundDigit = false;
            int start = chars[0] != '-' && chars[0] != '+' ? 0 : 1;
            int i;
            if (sz > start + 1 && chars[start] == '0' && (chars[start + 1] == 'x' || chars[start + 1] == 'X')) {
                i = start + 2;
                if (i == sz) {
                    return false;
                } else {
                    while (i < chars.length) {
                        if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) {
                            return false;
                        }

                        ++i;
                    }

                    return true;
                }
            } else {
                --sz;

                for (i = start; i < sz || i < sz + 1 && allowSigns && !foundDigit; ++i) {
                    if (chars[i] >= '0' && chars[i] <= '9') {
                        foundDigit = true;
                        allowSigns = false;
                    } else if (chars[i] == '.') {
                        if (hasDecPoint || hasExp) {
                            return false;
                        }

                        hasDecPoint = true;
                    } else if (chars[i] != 'e' && chars[i] != 'E') {
                        if (chars[i] != '+' && chars[i] != '-') {
                            return false;
                        }

                        if (!allowSigns) {
                            return false;
                        }

                        allowSigns = false;
                        foundDigit = false;
                    } else {
                        if (hasExp) {
                            return false;
                        }

                        if (!foundDigit) {
                            return false;
                        }

                        hasExp = true;
                        allowSigns = true;
                    }
                }

                if (i < chars.length) {
                    if (chars[i] >= '0' && chars[i] <= '9') {
                        return true;
                    } else if (chars[i] != 'e' && chars[i] != 'E') {
                        if (chars[i] == '.') {
                            return !hasDecPoint && !hasExp && foundDigit;
                        } else if (allowSigns || chars[i] != 'd' && chars[i] != 'D' && chars[i] != 'f' && chars[i] != 'F') {
                            if (chars[i] != 'l' && chars[i] != 'L') {
                                return false;
                            } else {
                                return foundDigit && !hasExp;
                            }
                        } else {
                            return foundDigit;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return !allowSigns && foundDigit;
                }
            }
        }
    }
}
