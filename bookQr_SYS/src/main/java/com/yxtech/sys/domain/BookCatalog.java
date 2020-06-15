package com.yxtech.sys.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "t_book_catalog")
public class BookCatalog {
    /**
     * 图书大纲主键
     */
    @Id
    private Integer id;

    /**
     * 层级数
     */
    @Column(name = "level_id")
    private Integer levelId;

    /**
     * 名称
     */
    private String name;

    /**
     * 父节点ID
     */
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    @Transient
    private Boolean isParent;

    /**
     * 获取图书大纲主键
     *
     * @return id - 图书大纲主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置图书大纲主键
     *
     * @param id 图书大纲主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取层级数
     *
     * @return level_id - 层级数
     */
    public Integer getLevelId() {
        return levelId;
    }

    /**
     * 设置层级数
     *
     * @param levelId 层级数
     */
    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
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
     * 获取父节点ID
     *
     * @return parent_id - 父节点ID
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 设置父节点ID
     *
     * @param parentId 父节点ID
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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

    public Boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(Boolean isParent) {
        this.isParent = isParent;
    }
}