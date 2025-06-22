package io.github.tml.mosaic.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 通用返回对象
 * @author suifeng
 */
@Data
@AllArgsConstructor

public class R<T> {

    private int code;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    private long timestamp;

    public static <T> R<T> success(String message, T data) {
        return new R<T>(RCode.SUCCESS.getCode(), message, data, System.currentTimeMillis());
    }

    public static <T> R<T> success(String message) {
        return success(message, null);
    }

    public static <T> R<T> success() {
        return success(RCode.SUCCESS.getMessage());
    }

    public static <T> R<T> success(T data) {
        return success(RCode.SUCCESS.getMessage(), data);
    }

    public static <T> R<T> error(int code, String message) {
        return new R<>(code, message, null, System.currentTimeMillis());
    }

    public static <T> R<T> error(String message) {
        return error(RCode.ERROR.getCode(), message);
    }

    public static <T> R<T> error() {
        return error(RCode.ERROR.getMessage());
    }

    public static <T> R<T> error(RCode rCode) {
        return error(rCode.getCode(), rCode.getMessage());
    }
}
