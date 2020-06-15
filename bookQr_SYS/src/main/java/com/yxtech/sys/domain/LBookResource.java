package com.yxtech.sys.domain;

import java.util.Date;
import javax.persistence.*;

/**
 *  智学云平台绑定图书和资源关系日志
 * @author wzf
 * @date    2019/04/30
 */
@Table(name = "l_book_resource")
public class LBookResource {
    @Id
    private Integer id;

    /**
     * 图书ID
     */
    @Column(name = "book_id")
    private Integer bookId;

    /**
     * 文件ID类型：1：课件资源；2：扩展资源  3:刮刮卡
     */
    @Column(name = "qr_type")
    private Integer qrType;

    /**
     * 资源ID
     */
    @Column(name = "res_id")
    private Integer resId;

    /**
     * 失败类型：1新增，2删除
     */
    @Column(name = "error_type")
    private Integer errorType;

    /**
     * 失败次数
     */
    @Column(name = "error_num")
    private Integer errorNum;

    @Column(name = "error_code")
    private Integer errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    /**
     * 状态：1失败；2成功
     */
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取图书ID
     *
     * @return book_id - 图书ID
     */
    public Integer getBookId() {
        return bookId;
    }

    /**
     * 设置图书ID
     *
     * @param bookId 图书ID
     */
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    /**
     * 获取文件ID类型：1：课件资源；2：扩展资源  3:刮刮卡
     *
     * @return qr_type - 文件ID类型：1：课件资源；2：扩展资源  3:刮刮卡
     */
    public Integer getQrType() {
        return qrType;
    }

    /**
     * 设置文件ID类型：1：课件资源；2：扩展资源  3:刮刮卡
     *
     * @param qrType 文件ID类型：1：课件资源；2：扩展资源  3:刮刮卡
     */
    public void setQrType(Integer qrType) {
        this.qrType = qrType;
    }

    /**
     * 获取资源ID
     *
     * @return res_id - 资源ID
     */
    public Integer getResId() {
        return resId;
    }

    /**
     * 设置资源ID
     *
     * @param resId 资源ID
     */
    public void setResId(Integer resId) {
        this.resId = resId;
    }

    /**
     * 获取失败类型：1新增，2删除
     *
     * @return error_type - 失败类型：1新增，2删除
     */
    public Integer getErrorType() {
        return errorType;
    }

    /**
     * 设置失败类型：1新增，2删除
     *
     * @param errorType 失败类型：1新增，2删除
     */
    public void setErrorType(Integer errorType) {
        this.errorType = errorType;
    }

    /**
     * 获取失败次数
     *
     * @return error_num - 失败次数
     */
    public Integer getErrorNum() {
        return errorNum;
    }

    /**
     * 设置失败次数
     *
     * @param errorNum 失败次数
     */
    public void setErrorNum(Integer errorNum) {
        this.errorNum = errorNum;
    }

    /**
     * @return error_code
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode
     */
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return error_message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * 获取状态：1失败；2成功
     *
     * @return status - 状态：1失败；2成功
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态：1失败；2成功
     *
     * @param status 状态：1失败；2成功
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}