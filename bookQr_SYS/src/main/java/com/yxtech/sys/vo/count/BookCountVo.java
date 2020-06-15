package com.yxtech.sys.vo.count;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liukailong on 2016-09-10.
 */
public class BookCountVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private int bookId;
    private String bookName;
    private int viewNum;
    private List<QrCountVo> items; // 数据集合
    private String itemsStr; // 数据集合字符串形式
    private int pressId;
    private String pressName;
    private String editor;
    private String code;
    private Integer editorId;
    private Integer formal;      // 0：正式出版物 1：非正式出版物
    private String flag;

    public String getPressName() {
        return pressName;
    }

    public void setPressName(String pressName) {
        this.pressName = pressName;
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

    public int getViewNum() {
        return viewNum;
    }

    public void setViewNum(int viewNum) {
        this.viewNum = viewNum;
    }

    public List<QrCountVo> getItems() {
        return items;
    }

    public void setItems(List<QrCountVo> items) {
        this.items = items;
    }

    public int getPressId() {
        return pressId;
    }

    public void setPressId(int pressId) {
        this.pressId = pressId;
    }

    public String getItemsStr() {
        return itemsStr;
    }

    public void setItemsStr(String itemsStr) {
        this.itemsStr = itemsStr;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getEditorId() {
        return editorId;
    }

    public void setEditorId(Integer editorId) {
        this.editorId = editorId;
    }

    public Integer getFormal() {
        return formal;
    }

    public void setFormal(Integer formal) {
        this.formal = formal;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
