package com.yxtech.sys.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_qr_down_rank")
public class QrDownRank {
    @Id
    private Integer id;

    @Column(name = "press_Id")
    private Integer pressId;

    @Column(name = "book_Id")
    private Integer bookId;

    /**
     * 维码二ID
     */
    @Column(name = "qr_Id")
    private Integer qrId;

    @Column(name = "res_Id")
    private Integer resId;


    /**
     * 用户的唯一标识
     */
    private String openid;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    private String sex;

    /**
     * 用户个人资料填写的省份
     */
    private String province;

    /**
     * 普通用户个人资料填写的城市
     */
    private String city;

    /**
     * 国家，如中国为CN
     */
    private String country;

    /**
     * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     */
    private String headimgurl;

    /**
     * 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
     */
    private String privilege;

    /**
     * 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。详见：获取用户个人信息（UnionID机制）
     */
    private String unionid;

    /**
     * 刮乐刮序列码
     */
    private String activation;

    /**
     * 创建时间
     */
    @Column(name = "create_Time")
    private Date createTime;

    private Integer num;

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
     * 获取维码二ID
     *
     * @return qr_Id - 维码二ID
     */
    public Integer getQrId() {
        return qrId;
    }

    /**
     * 设置维码二ID
     *
     * @param qrId 维码二ID
     */
    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

    /**
     * 获取用户的唯一标识
     *
     * @return openid - 用户的唯一标识
     */
    public String getOpenid() {
        return openid;
    }

    /**
     * 设置用户的唯一标识
     *
     * @param openid 用户的唯一标识
     */
    public void setOpenid(String openid) {
        this.openid = openid;
    }

    /**
     * 获取用户昵称
     *
     * @return nickname - 用户昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置用户昵称
     *
     * @param nickname 用户昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     *
     * @return sex - 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    public String getSex() {
        return sex;
    }

    /**
     * 设置用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     *
     * @param sex 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * 获取用户个人资料填写的省份
     *
     * @return province - 用户个人资料填写的省份
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置用户个人资料填写的省份
     *
     * @param province 用户个人资料填写的省份
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取普通用户个人资料填写的城市
     *
     * @return city - 普通用户个人资料填写的城市
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置普通用户个人资料填写的城市
     *
     * @param city 普通用户个人资料填写的城市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取国家，如中国为CN
     *
     * @return country - 国家，如中国为CN
     */
    public String getCountry() {
        return country;
    }

    /**
     * 设置国家，如中国为CN
     *
     * @param country 国家，如中国为CN
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 获取用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     *
     * @return headimgurl - 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     */
    public String getHeadimgurl() {
        return headimgurl;
    }

    /**
     * 设置用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     *
     * @param headimgurl 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     */
    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    /**
     * 获取用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
     *
     * @return privilege - 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
     */
    public String getPrivilege() {
        return privilege;
    }

    /**
     * 设置用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
     *
     * @param privilege 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
     */
    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    /**
     * 获取只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。详见：获取用户个人信息（UnionID机制）
     *
     * @return unionid - 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。详见：获取用户个人信息（UnionID机制）
     */
    public String getUnionid() {
        return unionid;
    }

    /**
     * 设置只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。详见：获取用户个人信息（UnionID机制）
     *
     * @param unionid 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。详见：获取用户个人信息（UnionID机制）
     */
    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    /**
     * 获取刮乐刮序列码
     *
     * @return activation - 刮乐刮序列码
     */
    public String getActivation() {
        return activation;
    }

    /**
     * 设置刮乐刮序列码
     *
     * @param activation 刮乐刮序列码
     */
    public void setActivation(String activation) {
        this.activation = activation;
    }

    /**
     * 获取创建时间
     *
     * @return create_Time - 创建时间
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

    public Integer getPressId() {
        return pressId;
    }

    public void setPressId(Integer pressId) {
        this.pressId = pressId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }
}