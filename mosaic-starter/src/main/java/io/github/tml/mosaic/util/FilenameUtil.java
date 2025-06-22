package io.github.tml.mosaic.util;

import org.springframework.util.StringUtils;

/**
 * 文件名处理工具类
 */
public class FilenameUtil {
    
    private static final String JAR_EXTENSION = ".jar";
    
    /**
     * 生成存储文件名
     * 格式: 原始文件名(不含扩展名)_简短UUID.jar
     */
    public static String generateStorageFilename(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            throw new IllegalArgumentException("原始文件名不能为空");
        }
        
        String nameWithoutExt = removeJarExtension(originalFilename);
        String shortId = ShortUuidUtil.generateShortId();
        return nameWithoutExt + "_" + shortId + JAR_EXTENSION;
    }
    
    /**
     * 生成新的存储文件名(用于重命名)
     */
    public static String generateNewStorageFilename(String currentStorageFilename, String newDisplayName) {
        String newNameWithoutExt = removeJarExtension(newDisplayName);
        
        // 从当前存储文件名中提取UUID后缀
        String currentNameWithoutExt = removeJarExtension(currentStorageFilename);
        int lastUnderscoreIndex = currentNameWithoutExt.lastIndexOf('_');
        
        String uuidSuffix;
        if (lastUnderscoreIndex > 0) {
            String possibleUuid = currentNameWithoutExt.substring(lastUnderscoreIndex + 1);
            if (possibleUuid.length() == 8 && possibleUuid.matches("[a-f0-9]{8}")) {
                uuidSuffix = possibleUuid;
            } else {
                uuidSuffix = ShortUuidUtil.generateShortId();
            }
        } else {
            uuidSuffix = ShortUuidUtil.generateShortId();
        }
        
        return newNameWithoutExt + "_" + uuidSuffix + JAR_EXTENSION;
    }
    
    /**
     * 移除.jar扩展名
     */
    public static String removeJarExtension(String filename) {
        if (StringUtils.hasText(filename) && filename.toLowerCase().endsWith(JAR_EXTENSION)) {
            return filename.substring(0, filename.length() - JAR_EXTENSION.length());
        }
        return filename;
    }
    
    /**
     * 确保文件名有.jar扩展名
     */
    public static String ensureJarExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        return filename.toLowerCase().endsWith(JAR_EXTENSION) ? filename : filename + JAR_EXTENSION;
    }
    
    /**
     * 验证文件名合法性
     */
    public static void validateFilename(String filename, String fieldName) {
        if (!StringUtils.hasText(filename)) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
        
        if (filename.contains("/") || filename.contains("\\") || 
            filename.contains("..") || filename.contains(":") || 
            filename.contains("*") || filename.contains("?") || 
            filename.contains("\"") || filename.contains("<") || 
            filename.contains(">") || filename.contains("|")) {
            throw new IllegalArgumentException(fieldName + "包含非法字符");
        }
        
        if (filename.length() > 255) {
            throw new IllegalArgumentException(fieldName + "长度不能超过255个字符");
        }
    }
}