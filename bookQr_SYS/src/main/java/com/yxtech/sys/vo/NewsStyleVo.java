package com.yxtech.sys.vo;

import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * @author cuihao
 * @create 2017-05-09-9:29
 */

public class NewsStyleVo {
    private List<Integer> ids;
    private String fileId;
    @NotBlank(message = "二维码宽高度不能为空9999")
    private String width;
    private String text;

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
