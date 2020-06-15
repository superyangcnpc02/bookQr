package com.yxtech.sys.vo.client;


/**
 * 编写过的图书VO
 * Created by yanfei on 2015/11/6.
 */
public class ClientBookVO {
    // 曾经出版的书名
    private String name;
    // 曾经出版的出版社
    private String press;
    // 出版时间
    private String prssTime;
    // 客户ID
    private Integer clientId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getPrssTime() {
        return prssTime;
    }

    public void setPrssTime(String prssTime) {
        this.prssTime = prssTime;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
}
