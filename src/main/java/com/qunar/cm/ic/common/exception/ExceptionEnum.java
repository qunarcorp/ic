package com.qunar.cm.ic.common.exception;

/**
 * Created by dandan.sha on 2018/08/24.
 */
public enum ExceptionEnum {

    DATA_CONVERTER_ERROR("数据转换失败"),
    IP_LIMITED("IP禁止访问"),
    PARAMS_INVALID("参数错误");

    private String message;

    ExceptionEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
