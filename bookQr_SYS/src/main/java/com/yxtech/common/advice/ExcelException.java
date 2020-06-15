package com.yxtech.common.advice;


/**
 * 导入Excel异常
 *
 * @author lyj
 * @since 2015/8/13
 */
public class ExcelException extends RuntimeException {

    /**
     * 构造函数
     *
     * @author lyj
     * @since 2015-8-13
     */
    public ExcelException() {
        super("");
    }


    /**
     * 构造函数
     * @param msg 异常信息
     *
     * @author lyj
     * @since 2015-8-13
     */
    public ExcelException(String msg) {
        super(msg);
    }


    /**
     * 构造函数
     * @param msg 异常信息
     * @param cause 异常原因
     *
     * @author lyj
     * @since 2015-8-13
     */
    public ExcelException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
