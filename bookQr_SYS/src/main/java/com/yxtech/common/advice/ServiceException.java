package com.yxtech.common.advice;

/**
 * 业务逻辑层异常（运行时异常）
 *
 * @author lyj
 * @since 2015-10-14
 */
public class ServiceException extends RuntimeException {
    /**
     * 构造函数
     *
     * @author lyj
     * @since 2015-10-14
     */
    public ServiceException() {
    }


    /**
     * 构造函数
     * @param message 异常提示信息
     *
     * @author lyj
     * @since 2015-10-14
     */
    public ServiceException(String message) {
        super(message);
    }


    /**
     * 构造函数
     * @param message 异常提示信息
     * @param cause 错误原因
     *
     * @author lyj
     * @since 2015-10-14
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
