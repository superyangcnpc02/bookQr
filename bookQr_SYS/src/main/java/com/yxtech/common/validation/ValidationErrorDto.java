package com.yxtech.common.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * 校验错误dto类
 * Created by yanfei on 2015/6/5.
 */
public class ValidationErrorDto {
    private List<FieldErrorDto> fieldErrors = new ArrayList<>();

    public ValidationErrorDto() {}

    public void addFieldError(String path, String message) {
        FieldErrorDto error = new FieldErrorDto(path, message);
        fieldErrors.add(error);
    }

    public List<FieldErrorDto> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldErrorDto> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
