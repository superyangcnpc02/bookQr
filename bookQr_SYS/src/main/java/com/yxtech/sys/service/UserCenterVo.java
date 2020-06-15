package com.yxtech.sys.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yxtech.common.advice.ServiceException;
import com.yxtech.utils.password.Base64;
import com.yxtech.utils.qr.HttpTookit;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cuihao
 * @create 2018-01-08-17:21
 */

public class UserCenterVo {
    private Integer id;
    private String link;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
