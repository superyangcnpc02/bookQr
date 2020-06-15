package com.yxtech.sys.vo.sync;

import java.util.Date;

/**
 * create by zml on 2017/12/14 10:23
 */
public class BookInfo {
    private String _id;
    private String bookId; // 相当于图书的大纲！ 对应 code 字段
    private String bookName; // 相当于 book 对象中的 name
    private String ISBN;
    private String branch; // 相当于 book 对象中的 press
    private String editor; // 相当于 book 对象中的 editor
    private String author; // 相当于 book 对象中的 author
    private String ztfCategory; // 二级分类
    private String swCategory;
    private String wdCategory;
    private String zyCategory;
    private String kcCategory;
    private String publishDate;
    private String editRoom;
    private String price;
    private String seriesName;
    private String introduce;
    private String binding;
    private String bookType;
    private String bookNumber;
    private String wordNumber;
    private Date lastUpdateTime;
    private Date createTime;
    private String created_at;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getZtfCategory() {
        return ztfCategory;
    }

    public void setZtfCategory(String ztfCategory) {
        this.ztfCategory = ztfCategory;
    }

    public String getSwCategory() {
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

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getEditRoom() {
        return editRoom;
    }

    public void setEditRoom(String editRoom) {
        this.editRoom = editRoom;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(String bookNumber) {
        this.bookNumber = bookNumber;
    }

    public String getWordNumber() {
        return wordNumber;
    }

    public void setWordNumber(String wordNumber) {
        this.wordNumber = wordNumber;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "_id='" + _id + '\'' +
                ", bookId='" + bookId + '\'' +
                ", bookName='" + bookName + '\'' +
                ", ISBN='" + ISBN + '\'' +
                ", branch='" + branch + '\'' +
                ", editor='" + editor + '\'' +
                ", author='" + author + '\'' +
                ", ztfCategory='" + ztfCategory + '\'' +
                ", swCategory='" + swCategory + '\'' +
                ", wdCategory='" + wdCategory + '\'' +
                ", zyCategory='" + zyCategory + '\'' +
                ", kcCategory='" + kcCategory + '\'' +
                ", publishDate=" + publishDate +
                ", editRoom='" + editRoom + '\'' +
                ", price=" + price +
                ", seriesName='" + seriesName + '\'' +
                ", introduce='" + introduce + '\'' +
                ", binding='" + binding + '\'' +
                ", bookType='" + bookType + '\'' +
                ", bookNumber=" + bookNumber +
                ", wordNumber=" + wordNumber +
                ", lastUpdateTime=" + lastUpdateTime +
                ", createTime=" + createTime +
                ", created_at=" + created_at +
                '}';
    }
}
