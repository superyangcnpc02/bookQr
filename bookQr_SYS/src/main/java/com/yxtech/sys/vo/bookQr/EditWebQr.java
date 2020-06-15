package com.yxtech.sys.vo.bookQr;

/**
 * @author cuihao
 * @create 2017-12-12-10:52
 */

public class EditWebQr {
    private Integer id;//二维码id
    private String name;//链接名称
    private String netUrl;//链接地址

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }


}
