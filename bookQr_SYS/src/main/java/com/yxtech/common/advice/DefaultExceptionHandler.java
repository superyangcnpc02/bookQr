package com.yxtech.common.advice;



import com.yxtech.common.json.JsonResult;
import com.yxtech.common.json.JsonResultData;
import com.yxtech.common.validation.ValidationErrorDto;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * 默认异常处理类

 * @author lyj
 * @since 2015-8-4
 */
@ControllerAdvice
public class DefaultExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class); //日志记录器

    /**
     * 捕获自定义运行时异常
     * @param ex 异常类
     * @return json
     *
     * @author lyj
     * @since 2015-8-4
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public JsonResult runtimeErrorHandler(Exception ex) {
        RuntimeException e = (RuntimeException) ex;
        String msg = e.getMessage();

        //记录日志
        log.error(msg, ex);

        return new JsonResult(false, msg);
    }

    /**
     * 捕获权限异常
     * @param ex 异常类
     * @return json
     *
     * @author lyj
     * @since 2015-10-28
     */
    @ExceptionHandler({AuthorizationException.class,AuthenticationException.class,UnauthorizedException.class,HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    public JsonResult permissionErrorHandler(Exception ex) {
        String msg = ex.getMessage();

        //记录日志
        log.error(msg, ex);

        return new JsonResult(false, "权限异常");
    }


    /**
     * 捕获Assert校验异常
     * @param ex 异常类
     * @return json
     *
     * @author lyj
     * @since 2015-8-4
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public JsonResult argsErrorHandler(Exception ex) {

        //记录日志
        log.error("参数校验异常", ex);

        return new JsonResult(false, ex.getMessage());
    }

    /**
     * 参数异常捕获
     * @param ex 异常类
     * @return json
     *
     * @author lyj
     * @since 2015-8-4
     */
    @ExceptionHandler({MissingServletRequestParameterException.class, TypeMismatchException.class, IllegalStateException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public JsonResult conversionErrorHandler(Exception ex) {
        //记录日志
        log.error("请求参数异常", ex);

        return new JsonResult(false, "请求参数引发异常");
    }

    /**
     * 捕获注解校验异常
     * @param ex 异常类
     * @return json
     *
     * @author lyj
     * @since 2015-8-4
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public JsonResult argsErrorHandler(MethodArgumentNotValidException ex) {
        //记录日志
        log.error("数据注解校验异常", ex);

        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        return new JsonResultData<>(false, "请求参数校验出错", processFieldErrors(fieldErrors));
    }

    /**
     * 处理捕获到的错误字段
     * @param fieldErrors 错误字段集合
     * @return ValidationErrorDto
     *
     * @author lyj
     * @since 2015-8-4
     */
    private ValidationErrorDto processFieldErrors(List<FieldError> fieldErrors) {
        ValidationErrorDto dto = new ValidationErrorDto();

        for (FieldError fieldError: fieldErrors) {
            dto.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return dto;
    }

    /**
     * 统一捕获异常
     * @param ex 异常类
     * @return json
     *
     * @author lyj
     * @since 2015-8-4
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public JsonResult defaultErrorHandler(Exception ex) {
        //记录日志
        log.error("统一捕获异常处理", ex);

        return new JsonResult(false, "数据处理出现错误");
    }
}