package com.yxtech.sys.vo.bookQr;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Administrator on 2015/10/13.
 */
public class BookQrStyleVo {

    @NotNull(message = "{qr.bookId.null}")
    private Integer id;

    private List<Object> qrIds;

    private String url;
    private String fileId;
    private String name;
    @NotBlank(message = "二维码宽高度不能为空9999")
    private String width;

    @NotNull(message = "tab不能为空")
    private Integer tab;
    private String text;

    public Integer getTab() {
        return tab;
    }

    public void setTab(Integer tab) {
        this.tab = tab;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Object> getQrIds() {
        return qrIds;
    }

    public void setQrIds(List<Object> qrIds) {
        this.qrIds = qrIds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
