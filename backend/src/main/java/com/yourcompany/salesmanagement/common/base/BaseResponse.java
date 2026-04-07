package com.yourcompany.salesmanagement.common.base;

public record BaseResponse<T>(
        boolean success,
        String message,
        T data
) {
    public static <T> BaseResponse<T> ok(String message, T data) {
        return new BaseResponse<>(true, message, data);
    }

    public static <T> BaseResponse<T> fail(String message, T data) {
        return new BaseResponse<>(false, message, data);
    }
}
