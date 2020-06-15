package com.yxtech.common.json;

/**
 * Josn 数据传输封装
 *
 * @author yanfei
 * @since 2015-6-5
 */
public class JsonResultData<T> extends JsonResult {
    public JsonResultData(T item){
        super(true, "");

        this.item = item;
    }

    public JsonResultData(T item, String message){
        super(true, message);

        this.item = item;
    }

    public JsonResultData(boolean status, String message){
        super(status, message);

        this.item = null;
    }

    public JsonResultData(boolean status, String message, T item) {
        super(status, message);

        this.item = item;
    }

    private T item;

    public T getItem() {
        return item;
    }
}
