package io.github.tml.mosaic.util;

public class StringUtil {
    // 获取传入字符串的首字母小写字符串
    public static String getFirstLowerCase(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

}
