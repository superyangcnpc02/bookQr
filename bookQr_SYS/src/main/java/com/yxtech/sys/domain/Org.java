package com.yxtech.sys.domain;

import javax.persistence.*;

@Table(name = "t_organization")
public class Org {
    /**
     * 职能机构表ID
     */
    @Id
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 获取职能机构表ID
     *
     * @return id - 职能机构表ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置职能机构表ID
     *
     * @param id 职能机构表ID
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
}