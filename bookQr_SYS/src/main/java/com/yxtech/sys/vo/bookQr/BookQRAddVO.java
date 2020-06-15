package com.yxtech.sys.vo.bookQr;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2015/10/14.
 */
public class BookQRAddVO {
    @NotNull(message = "图书不能为空")
    private Integer id;
    @NotBlank(message = "名称不能为空")
    private String name;

    // 文件ID类型：1、图书；2、章节
    //@NotNull(message = "qrType不能为空")
    private Integer qrType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQrType() {
        return qrType;
    }

    public void setQrType(Integer qrType) {
        this.qrType = qrType;
    }

	public BookQRAddVO(Integer id, String name,Integer qrType) {
        this.id = id;
        this.name = name;
        this.qrType = qrType;
    }

    public BookQRAddVO(){}
}
