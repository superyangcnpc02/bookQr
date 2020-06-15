package com.yxtech.common.json;

/**
 * Json 数据传输封装
 * Created by yanfei on 2015/6/5.
 */
public class JsonResultId extends JsonResult{
    private long dataId; //数据ID

    public JsonResultId(long dataId) {
        this(true, "", dataId);
    }

    public JsonResultId(long dataId, String message) {
        this(true, message, dataId);
    }

    public JsonResultId(boolean status, String message) {
        this(status, message, 0);
    }

    public JsonResultId(boolean status, String message, long dataId) {
        super(status, message);
        this.dataId = dataId;
    }

    public Long getDataId() {
        return dataId;
    }
}
