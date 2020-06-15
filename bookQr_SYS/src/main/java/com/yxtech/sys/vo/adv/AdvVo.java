package com.yxtech.sys.vo.adv;

import com.alibaba.fastjson.annotation.JSONField;
import com.yxtech.sys.domain.Adv;

import java.util.Date;
import java.util.List;

/**
 * create by zml on 2017/10/23 17:25
 */
public class AdvVo {

    /**
     * 广告id
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 超链接(广告跳转的地址)
     */
    private String url;

    /**
     * 名称
     */
    private String imgUrl;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public AdvVo() {}

    public AdvVo(Adv adv) {
        this.id = adv.getId();
        this.name = adv.getName();
        this.url = adv.getUrl();
        this.imgUrl = adv.getImgUrl();
        this.setCreateTime(adv.getCreateTime());
    }

    /**
     * 获取广告id
     *
     * @return id - 广告id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置广告id
     *
     * @param id 广告id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取超链接(广告跳转的地址)
     *
     * @return url - 超链接(广告跳转的地址)
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置超链接(广告跳转的地址)
     *
     * @param url 超链接(广告跳转的地址)
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取名称
     *
     * @return img_url - 名称
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * 设置名称
     *
     * @param imgUrl 名称
     */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
