package com.yxtech.common.advice;

/**
 * 工具类异常
 * Created by yanfei on 2015/6/12.
 */
public class UtilException extends RuntimeException  {

    /**
     * 构造函数
     *
     * @author lyj
     * @since 2015-8-4
     */
    public UtilException() {
        this(null, null);
    }

    /**
     * 构造函数
     *
     * @author lyj
     * @since 2015-8-4
     */
    public UtilException(Throwable cause) {
        this(null, cause);
    }

    /**
     * 构造函数
     * @param msg 异常信息
     *
     * @author lyj
     * @since 2015-8-4
     */
    public UtilException(int errorCode, String msg) {
        this(msg, null);
    }

    /**
     * 构造函数
     * @param msg 异常信息
     * @param cause 异常原因
     *
     * @author lyj
     * @since 2015-8-4
     *
     */
    public UtilException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
