package com.yxtech.sys.vo.bookQr;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Chenxh on 2015/10/14.
 */
public class QrCodeLogoVo {
    private MultipartFile data;
    private Integer flag;

    public MultipartFile getData() {
        return data;
    }

    public void setData(MultipartFile data) {
        this.data = data;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }
}
