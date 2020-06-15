package com.yxtech.sys.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table(name = "t_qr")
public class BookQr {
    /**
     * 二维码ID
     */
    @Id
    private Integer id;

    @Column(name = "book_Id")
    private Integer bookId;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 序号排序
     */
    private Integer indexs;

    /**
     * 二维码存储路径/文件ID
     */
    private String url;

    /**
     * 其他网站链接地址
     */
    @Column(name = "net_url")
    private String netUrl;

    /**
     * 二维码名称
     */
    private String name;

    /**
     * 资源数量
     */
    private Integer num;

    /**
     * 拥有者ID
     */
    private Integer onwer;

    /**
     * 备注
     */
    private String remark;

    /**
     * 文件ID类型：1、图书（课件）；2、章节(扩展)
     */
    @Column(name = "qr_Type")
    private Integer qrType;

    /**
     * 扫描次数
     */
    @Column(name = "view_Num")
    private Integer viewNum;

    /**
     * 预览次数
     */
    @Column(name = "preview_Num")
    private Integer previewNum;

    /**
     * 1：不保密 2：保密
     */
    private Integer secrecy;

    /**
     *  1：人工审核 2：自动审核
     */
    @Column(name = "is_send")
    private Integer isSend;

    @Transient
    private Integer sendCustomer;

    /**
     * 二维码下面资源压缩包在智学云上的id
     */
    @Column(name = "zip_Zhixueid")
    private Long zipZhixueid;

    /**
     * 1正在压缩2压缩成功;3压缩失败
     */
    @Column(name = "zip_Status")
    private Integer zipStatus;

    /**
     * 压缩持久化处理的进程 ID
     */
    @Column(name = "zip_Persistentid")
    private String zipPersistentId;

    /**
     * 压缩回调结果
     */
    @Column(name = "zip_callback_content")
    private String zipCallbackContent;

    /**
     * 1 直接读取 2 限制读取
     */
    @Column(name = "can_read")
    private Integer canRead;

    @Column(name = "create_Time")
    private Date createTime;


    /**
     * 获取二维码ID
     *
     * @return id - 二维码ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置二维码ID
     *
     * @param id 二维码ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return book_Id
     */
    public Integer getBookId() {
        return bookId;
    }

    /**
     * @param bookId
     */
    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    /**
     * 获取页码
     *
     * @return page - 页码
     */
    public Integer getPage() {
        return page;
    }

    /**
     * 设置页码
     *
     * @param page 页码
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * 获取序号排序
     *
     * @return indexs - 序号排序
     */
    public Integer getIndexs() {
        return indexs;
    }

    /**
     * 设置序号排序
     *
     * @param indexs 序号排序
     */
    public void setIndexs(Integer indexs) {
        this.indexs = indexs;
    }

    /**
     * 获取二维码存储路径/文件ID
     *
     * @return url - 二维码存储路径/文件ID
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置二维码存储路径/文件ID
     *
     * @param url 二维码存储路径/文件ID
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取二维码名称
     *
     * @return name - 二维码名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置二维码名称
     *
     * @param name 二维码名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取资源数量
     *
     * @return num - 资源数量
     */
    public Integer getNum() {
        return num;
    }

    /**
     * 设置资源数量
     *
     * @param num 资源数量
     */
    public void setNum(Integer num) {
        this.num = num;
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
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取文件ID类型：1、图书；2、章节
     *
     * @return qr_Type - 文件ID类型：1、图书；2、章节
     */
    public Integer getQrType() {
        return qrType;
    }

    /**
     * 设置文件ID类型：1、图书；2、章节
     *
     * @param qrType 文件ID类型：1、图书；2、章节
     */
    public void setQrType(Integer qrType) {
        this.qrType = qrType;
    }

    /**
     * 获取扫描次数
     *
     * @return view_Num - 扫描次数
     */
    public Integer getViewNum() {
        return viewNum;
    }

    /**
     * 设置扫描次数
     *
     * @param viewNum 扫描次数
     */
    public void setViewNum(Integer viewNum) {
        this.viewNum = viewNum;
    }

    //临时存储  资源ID  编辑二维码接口需要使用，绑定与资源的关系
    @Transient
    private List<Object> resource;

    public List<Object> getResource() {
        return resource;
    }

    public void setResource(List<Object> resource) {
        this.resource = resource;
    }

    public Integer getPreviewNum() {
        return previewNum;
    }

    public void setPreviewNum(Integer previewNum) {
        this.previewNum = previewNum;
    }

    public Long getZipZhixueid() {
        return zipZhixueid;
    }

    public void setZipZhixueid(Long zipZhixueid) {
        this.zipZhixueid = zipZhixueid;
    }

    public Integer getZipStatus() {
        return zipStatus;
    }

    public void setZipStatus(Integer zipStatus) {
        this.zipStatus = zipStatus;
    }

    public String getZipPersistentId() {
        return zipPersistentId;
    }

    public void setZipPersistentId(String zipPersistentId) {
        this.zipPersistentId = zipPersistentId;
    }

    public Integer getSecrecy() {
        return secrecy;
    }

    public void setSecrecy(Integer secrecy) {
        this.secrecy = secrecy;
    }

    public Integer getIsSend() {
        return isSend;
    }

    public void setIsSend(Integer isSend) {
        this.isSend = isSend;
    }

    public Integer getSendCustomer() {
        return sendCustomer;
    }

    public void setSendCustomer(Integer sendCustomer) {
        this.sendCustomer = sendCustomer;
    }

    public Integer getCanRead() {
        return canRead;
    }

    public void setCanRead(Integer canRead) {
        this.canRead = canRead;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }

    public String getZipCallbackContent() {
        return zipCallbackContent;
    }

    public void setZipCallbackContent(String zipCallbackContent) {
        this.zipCallbackContent = zipCallbackContent;
    }

    public BookQr(){}
    public BookQr(BookQrAuth bookQrAuth){
        this.setId(bookQrAuth.getId());
        this.setBookId(bookQrAuth.getBookId());
        this.setName(bookQrAuth.getName());
    }

    @Override
    public String toString() {
        return "BookQr{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", page=" + page +
                ", indexs=" + indexs +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", num=" + num +
                ", onwer=" + onwer +
                ", remark='" + remark + '\'' +
                ", qrType=" + qrType +
                ", viewNum=" + viewNum +
                ", previewNum=" + previewNum +
                ", secrecy=" + secrecy +
                ", isSend=" + isSend +
                ", sendCustomer=" + sendCustomer +
                ", zipZhixueid=" + zipZhixueid +
                ", zipStatus=" + zipStatus +
                ", zipPersistentId='" + zipPersistentId + '\'' +
                ", canRead=" + canRead +
                ", resource=" + resource +
                '}';
    }
}