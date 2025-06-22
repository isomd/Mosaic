package io.github.tml.mosaic.util;

import java.util.UUID;

/**
 * 简短UUID生成工具
 */
public class ShortUuidUtil {
    
    /**
     * 生成8位简短UUID
     * 基于标准UUID的前8位字符
     */
    public static String generateShortId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /**
     * 生成带前缀的简短ID
     */
    public static String generateShortIdWithPrefix(String prefix) {
        return prefix + "_" + generateShortId();
    }
}