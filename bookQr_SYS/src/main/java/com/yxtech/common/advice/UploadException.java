package com.yxtech.common.advice;

/**
 * 文件上传异常
 *
 * @author yanfei
 * @since 2015.06.10
 */
public class UploadException extends RuntimeException {

    /**
     * 构造函数
     *
     * @author lyj
     * @since 2015-8-4
     */
    public UploadException() {
        this(null, null);
    }

    /**
     * 构造函数
     *
     * @author lyj
     * @since 2015-8-4
     */
    public UploadException(Throwable cause) {
        this(null, cause);
    }

    /**
     * 构造函数
     * @param msg 异常信息
     *
     * @author lyj
     * @since 2015-8-4
     */
    public UploadException(String msg) {
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
    public UploadException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
