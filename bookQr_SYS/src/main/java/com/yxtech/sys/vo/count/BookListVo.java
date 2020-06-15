package com.yxtech.sys.vo.count;

/**
 * Created by lsn on 2017-10-21.
 */
public class BookListVo {

    private int bookId;
    private String bookName;
    private int pressId;//出版社Id
    private String pressName;//出版社
    private String author;//作者
    private String code;//产品码
    private String editor;//编辑
    private Integer editorId;//编辑id
    private String category;//图书分类
    /**
     * 1 是  2 否
     */
    private String quoteId;//是否创建

    private String qrName;//二维码名称
    private String qrType;//二维码类别
    private Integer viewNum;//扫描次数
    private Integer previewNum;//预览次数
    private Integer resId;//资源Id
    private String resName;//资源名称
    private String suffix;//资源后缀
    private Integer resSize;//资源大小
    private Integer downNum;//下载次数
    private String uuid;
    private Integer qrid;//二维码Id

    private Integer tfrId;//资源文件id

    public Integer getTfrId() {
        return tfrId;
    }

    public void setTfrId(Integer tfrId) {
        this.tfrId = tfrId;
    }

    public Integer getQrid() {
        return qrid;
    }

    public void setQrid(Integer qrid) {
        this.qrid = qrid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public Integer getViewNum() {
        return viewNum;
    }

    public void setViewNum(Integer viewNum) {
        this.viewNum = viewNum;
    }

    public Integer getPreviewNum() {
        return previewNum;
    }

    public void setPreviewNum(Integer previewNum) {
        this.previewNum = previewNum;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Integer getResSize() {
        return resSize;
    }

    public void setResSize(Integer resSize) {
        this.resSize = resSize;
    }

    public Integer getDownNum() {
        return downNum;
    }

    public void setDownNum(Integer downNum) {
        this.downNum = downNum;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getPressId() {
        return pressId;
    }

    public void setPressId(int pressId) {
        this.pressId = pressId;
    }

    public String getPressName() {
        return pressName;
    }

    public void setPressName(String pressName) {
        this.pressName = pressName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public Integer getEditorId() {
        return editorId;
    }

    public void setEditorId(Integer editorId) {
        this.editorId = editorId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getQrType() {
        return qrType;
    }

    public void setQrType(String qrType) {
        this.qrType = qrType;
    }
}
