package com.yxtech.sys.domain;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "t_book")
public class Book implements Cloneable,Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Integer id;

    /**
     * 作者
     */
    private String author;

    /**
     * 产品码
     */
    private String code;

    /**
     * 图书分类
     */
    private String category;

    /**
     * 图书一级分类id
     */
    @Column(name = "categorysuper_Id")
    private Integer categorySuperId;

    /**
     * 图书二级分类id
     */
    @Column(name = "category_Id")
    private Integer categoryId;

    /**
     * 封面图片ID
     */
    private String cover;

    /**
     * ISBN
     */
    private String isbn;

    /**
     * 书名
     */
    private String name;

    /**
     * 出版社
     */
    private String press;

    /**
     * 出版社id
     */
    @Column(name = "press_Id")
    private Integer pressId;

    /**
     * 简介
     */
    private String remark;

    /**
     * logo存储地址
     */
    private String logo;

    /**
     * 二维码默认宽度和高度
     */
    private Integer width;

    /**
     * 拥有者ID
     */
    private Integer onwer;

    /**
     * 营销人员邮箱（用于获取加密资源）
     */
    @Column(name = "auditor_Email")
    private String auditorEmail;

    /**
     * 在线审核人ID
     */
    @Column(name = "auditor_Id")
    private Integer auditorId;

    /**
     * 书问地址
     */
    @Column(name = "bookask_Url")
    private String bookaskUrl;

    @Column(name = "quoteId")
    private Integer quoteid;

    /**
     * 1:可编辑；2：上锁；3：解锁
     */
    private Integer status;

    /**
     * 所有二维码扫描总和次数
     */
    @Column(name = "view_Num")
    private Integer viewNum;

    private String tip;

    /**
     * 0 新增;1 导入
     */
    private Integer type;

    /**
     * 创建时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_Time")
    private Date createTime;

    /**
     * 编辑
     */
    private String editor;

    /**
     * 所有二维码预览总和次数
     */
    @Column(name = "preview_Num")
    private Integer previewNum;

    /**
     * 0：正式出版物 1：非正式出版物
     */
    private Integer formal;

    /**
     * 1 直接读取，2 限制读取
     */
    @Column(name = "is_publication")
    private Integer isPublication;

    /**
     * 只能微信扫描 1：是 2否
     */
    @Column(name = "only_webchat")
    private Integer onlyWebchat;

    @Transient
    private String qrName;

    /**
     * 社网分类
     */
    @Column(name = "sw_Category")
    private String swCategory;

    /**
     * 网店分类
     */
    @Column(name = "wd_Category")
    private String wdCategory;

    /**
     * 专业分类
     */
    @Column(name = "zy_Category")
    private String zyCategory;

    /**
     * 课程分类
     */
    @Column(name = "kc_Category")
    private String kcCategory;

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
     * 获取作者
     *
     * @return author - 作者
     */
    public String getAuthor() {
        return author;
    }

    /**
     * 设置作者
     *
     * @param author 作者
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * 获取产品码
     *
     * @return code - 产品码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置产品码
     *
     * @param code 产品码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取封面图片ID
     *
     * @return cover - 封面图片ID
     */
    public String getCover() {
        return cover;
    }

    /**
     * 设置封面图片ID
     *
     * @param cover 封面图片ID
     */
    public void setCover(String cover) {
        this.cover = cover;
    }

    /**
     * 获取ISBN
     *
     * @return isbn - ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * 设置ISBN
     *
     * @param isbn ISBN
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * 获取书名
     *
     * @return name - 书名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置书名
     *
     * @param name 书名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取出版社
     *
     * @return press - 出版社
     */
    public String getPress() {
        return press;
    }

    /**
     * 设置出版社
     *
     * @param press 出版社
     */
    public void setPress(String press) {
        this.press = press;
    }

    /**
     * 获取出版社id
     *
     * @return press_Id - 出版社id
     */
    public Integer getPressId() {
        return pressId;
    }

    /**
     * 设置出版社id
     *
     * @param pressId 出版社id
     */
    public void setPressId(Integer pressId) {
        this.pressId = pressId;
    }

    /**
     * 获取简介
     *
     * @return remark - 简介
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置简介
     *
     * @param remark 简介
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取logo存储地址
     *
     * @return logo - logo存储地址
     */
    public String getLogo() {
        return logo;
    }

    /**
     * 设置logo存储地址
     *
     * @param logo logo存储地址
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * 获取二维码默认宽度和高度
     *
     * @return width - 二维码默认宽度和高度
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * 设置二维码默认宽度和高度
     *
     * @param width 二维码默认宽度和高度
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * 获取拥有者ID
     *
     * @return onwer - 拥有者ID
     */
    public Integer getOnwer() {
        return onwer;
    }

    /**
     * 设置拥有者ID
     *
     * @param onwer 拥有者ID
     */
    public void setOnwer(Integer onwer) {
        this.onwer = onwer;
    }

    /**
     * 获取营销人员邮箱（用于获取加密资源）
     *
     * @return auditor_Email - 营销人员邮箱（用于获取加密资源）
     */
    public String getAuditorEmail() {
        return auditorEmail;
    }

    /**
     * 设置营销人员邮箱（用于获取加密资源）
     *
     * @param auditorEmail 营销人员邮箱（用于获取加密资源）
     */
    public void setAuditorEmail(String auditorEmail) {
        this.auditorEmail = auditorEmail;
    }

    /**
     * 获取在线审核人ID
     *
     * @return auditor_Id - 在线审核人ID
     */
    public Integer getAuditorId() {
        return auditorId;
    }

    /**
     * 设置在线审核人ID
     *
     * @param auditorId 在线审核人ID
     */
    public void setAuditorId(Integer auditorId) {
        this.auditorId = auditorId;
    }

    /**
     * 获取书问地址
     *
     * @return bookask_Url - 书问地址
     */
    public String getBookaskUrl() {
        return bookaskUrl;
    }

    /**
     * 设置书问地址
     *
     * @param bookaskUrl 书问地址
     */
    public void setBookaskUrl(String bookaskUrl) {
        this.bookaskUrl = bookaskUrl;
    }

    /**
     * @return quoteId
     */
    public Integer getQuoteid() {
        return quoteid;
    }

    /**
     * @param quoteid
     */
    public void setQuoteid(Integer quoteid) {
        this.quoteid = quoteid;
    }

    /**
     * 获取1:可编辑；2：上锁；3：解锁
     *
     * @return status - 1:可编辑；2：上锁；3：解锁
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置1:可编辑；2：上锁；3：解锁
     *
     * @param status 1:可编辑；2：上锁；3：解锁
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取所有二维码预览总和次数
     *
     * @return view_Num - 所有二维码预览总和次数
     */
    public Integer getViewNum() {
        return viewNum;
    }

    /**
     * 设置所有二维码预览总和次数
     *
     * @param viewNum 所有二维码预览总和次数
     */
    public void setViewNum(Integer viewNum) {
        this.viewNum = viewNum;
    }

    /**
     * @return tip
     */
    public String getTip() {
        return tip;
    }

    /**
     * @param tip
     */
    public void setTip(String tip) {
        this.tip = tip;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public Integer getPreviewNum() {
        return previewNum;
    }

    public void setPreviewNum(Integer previewNum) {
        this.previewNum = previewNum;
    }

    public Integer getFormal() {
        return formal;
    }

    public void setFormal(Integer formal) {
        this.formal = formal;
    }

    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public Integer getIsPublication() {
        return isPublication;
    }

    public void setIsPublication(Integer isPublication) {
        this.isPublication = isPublication;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getOnlyWebchat() {
        return onlyWebchat;
    }

    public void setOnlyWebchat(Integer onlyWebchat) {
        this.onlyWebchat = onlyWebchat;
    }

    public Integer getCategorySuperId() {
        return categorySuperId;
    }

    public void setCategorySuperId(Integer categorySuperId) {
        this.categorySuperId = categorySuperId;
    }    public String getSwCategory() {
        return swCategory;
    }

    public void setSwCategory(String swCategory) {
        this.swCategory = swCategory;
    }

    public String getWdCategory() {
        return wdCategory;
    }

    public void setWdCategory(String wdCategory) {
        this.wdCategory = wdCategory;
    }

    public String getZyCategory() {
        return zyCategory;
    }

    public void setZyCategory(String zyCategory) {
        this.zyCategory = zyCategory;
    }

    public String getKcCategory() {
        return kcCategory;
    }

    public void setKcCategory(String kcCategory) {
        this.kcCategory = kcCategory;
    }

    public Object Clone(){
        Book book = null;
        try {
            book = (Book) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return book;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", code='" + code + '\'' +
                ", category='" + category + '\'' +
                ", categorySuperId=" + categorySuperId +
                ", categoryId=" + categoryId +
                ", cover='" + cover + '\'' +
                ", isbn='" + isbn + '\'' +
                ", name='" + name + '\'' +
                ", press='" + press + '\'' +
                ", pressId=" + pressId +
                ", remark='" + remark + '\'' +
                ", logo='" + logo + '\'' +
                ", width=" + width +
                ", onwer=" + onwer +
                ", auditorEmail='" + auditorEmail + '\'' +
                ", auditorId=" + auditorId +
                ", bookaskUrl='" + bookaskUrl + '\'' +
                ", quoteid=" + quoteid +
                ", status=" + status +
                ", viewNum=" + viewNum +
                ", tip='" + tip + '\'' +
                ", type=" + type +
                ", createTime=" + createTime +
                ", editor='" + editor + '\'' +
                ", previewNum=" + previewNum +
                ", formal=" + formal +
                ", isPublication=" + isPublication +
                ", onlyWebchat=" + onlyWebchat +
                ", qrName='" + qrName + '\'' +
                ", swCategory='" + swCategory + '\'' +
                ", wdCategory='" + wdCategory + '\'' +
                ", zyCategory='" + zyCategory + '\'' +
                ", kcCategory='" + kcCategory + '\'' +
                '}';
    }
}