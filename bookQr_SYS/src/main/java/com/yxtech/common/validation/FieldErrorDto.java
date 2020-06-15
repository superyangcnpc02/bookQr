package com.yxtech.common.validation;

/**
 * 字段错误提示类
 * Created by yanfei on 2015/6/5.
 */
public class FieldErrorDto {
    private String field; //字段名称
    private String message; //校验提示信息

    public FieldErrorDto(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
