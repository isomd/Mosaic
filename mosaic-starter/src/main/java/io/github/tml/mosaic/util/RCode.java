package io.github.tml.mosaic.util;

import lombok.Getter;

/**
 * 描述: 自定义状态码
 * @author suifeng
 * 日期: 2025/6/14
 */
@Getter
public enum RCode {

    // 成功响应
    SUCCESS(200, "成功"),

    /**
     * 失败返回码
     */
    ERROR(400, "服务器繁忙，请稍后重试"),

    // 参数错误
    PARAM_ERROR(400, "参数错误"),

    // 文件相关错误
    FILE_NOT_FOUND(404, "文件不存在"),
    FILE_ALREADY_EXISTS(409, "文件已存在"),
    FILE_UPLOAD_ERROR(500, "文件上传失败"),
    FILE_DELETE_ERROR(500, "文件删除失败"),
    FILE_RENAME_ERROR(500, "文件重命名失败"),

    // JAR包相关错误
    JAR_INVALID_FORMAT(400, "只支持JAR文件格式"),
    JAR_FILE_TOO_LARGE(413, "JAR文件大小超出限制"),
    JAR_FILENAME_INVALID(400, "JAR文件名包含非法字符");

    /**
     * 自定义状态码
     **/
    private final int code;
    /**
     * 自定义描述
     **/
    private final String message;

    RCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

