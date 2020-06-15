package com.yxtech.common.json;

/**
 * Josn 数据传输封装
 *
 * @author yanfei
 * @since 2015-6-5
 */
public class JsonResult {
    public JsonResult(){
        this.status = true;
        this.message = "";
    }

    public JsonResult(boolean status, String message){
        this.status = status;
        this.message = message;
    }

    private boolean status;
    private String message;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
