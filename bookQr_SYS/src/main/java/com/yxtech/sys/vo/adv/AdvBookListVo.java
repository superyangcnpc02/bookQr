package com.yxtech.sys.vo.adv;

/**
 * create by zml on 2018/1/11 9:09
 */
public class AdvBookListVo {
    private String author;
    private String category;
    private Integer categoryId;
    private String code;
    private String editor;
    private Integer editorId;
    private Integer formal;
    private Integer id;
    private String isbn;
    private String name;
    private String press;
    private Integer pressId;
    private String remark;

    public AdvBookListVo(){}

    public AdvBookListVo(String author, String category, Integer categoryId, String code, String editor, Integer editorId, Integer formal, Integer id, String isbn, String name, String press, Integer pressId, String remark) {
        this.author = author;
        this.category = category;
        this.categoryId = categoryId;
        this.code = code;
        this.editor = editor;
        this.editorId = editorId;
        this.formal = formal;
        this.id = id;
        this.isbn = isbn;
        this.name = name;
        this.press = press;
        this.pressId = pressId;
        this.remark = remark;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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

    public Integer getFormal() {
        return formal;
    }

    public void setFormal(Integer formal) {
        this.formal = formal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public Integer getPressId() {
        return pressId;
    }

    public void setPressId(Integer pressId) {
        this.pressId = pressId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
