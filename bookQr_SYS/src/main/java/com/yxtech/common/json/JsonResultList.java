package com.yxtech.common.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Json 数据传输封装
 *
 * @author yanfei
 * @since 2015-6-5
 */
public class JsonResultList<T> extends JsonResult {

    private List<T> items;

    public JsonResultList(List<T> items){
        this(true, "", items);
    }

    public JsonResultList(List<T> items, String message) {
        this(true, message, items);
    }

    public JsonResultList(boolean status, String message){
        this(status, message, new ArrayList(0) );
    }

    public JsonResultList(boolean status, String message, List<T> items) {
        super(status, message);

        //绑定集合
        this.items = items;
    }

    /**
     * 获取数据列表。
     *
     * @return 集合
     */
    public List<T> getItems() {
        return items;
    }
}
