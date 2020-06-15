package com.yxtech.sys.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.yxtech.sys.vo.adv.AdvVo;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_adv")
public class Adv {
    /**
     * 广告id
     */
    @Id
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
    @Column(name = "img_url")
    private String imgUrl;

    /**
     * 创建时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;


    public Adv() {}

    public Adv(AdvVo advVo) {
        this.id = advVo.getId();
        this.name = advVo.getName();
        this.url = advVo.getUrl();
        this.imgUrl = advVo.getImgUrl();
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