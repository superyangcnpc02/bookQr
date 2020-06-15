package com.yxtech.sys.vo.client;

/**
 * Created by zml on 2016/9/13.
 */
public class ClientVo {
    //客户的openId
    private String openId;
    // 客户邮箱
    private String email;
    // 二维码id
    private int qrcodeId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * 获取客户邮箱
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置客户邮箱
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取客户邮箱
     * @return
     */
    public int getQrcodeId() {
        return qrcodeId;
    }

    /**
     * 设置客户邮箱
     * @param qrcodeId
     */
    public void setQrcodeId(int qrcodeId) {
        this.qrcodeId = qrcodeId;
    }

}
