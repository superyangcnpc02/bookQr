package com.yxtech.common.json;

/**
 * Json 数据传输封装
 * Created by yanfei on 2015/6/5.
 */
public class JsonResultString extends JsonResult{
    private String dataId; //数据ID

    public JsonResultString(String dataId) {
        this(true, "", dataId);
    }

    public JsonResultString(String dataId, String message) {
        this(true, message, dataId);
    }

    public JsonResultString(boolean status, String message) {
        this(status, message, "0");
    }

    public JsonResultString(boolean status, String message, String dataId) {
        super(status, message);
        this.dataId = dataId;
    }

    public String getDataId() {
        return dataId;
    }
}
