package com.yxtech.common.json;

/**
 * Created by Administrator on 2015/10/8.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页列表。
 *
 * @param <T>
 * @author yanfei
 * @since 2015/6/9
 */
public class Page<T> implements Serializable {

    private final long total; // 总条数。
    private final int size;  // 每页数量。
    private final int count; // 总页码。
    private final int num;   // 当前页码。
    private final List<T> items; // 记录集合。

    /**
     * 默认构造方法。
     *
     * @author yanfei
     * @since 2015/6/9
     */
    public Page() {
        this.total = 0;
        this.size = 0;
        this.num = 1;
        this.count = 0;
        this.items = new ArrayList<>();
    }

    /**
     * 构造方法。
     *
     * @param total 总条数。
     * @param size  每页数量。
     * @param num   当前页码。
     * @param items 记录集合。
     */
    public Page(final long total, final int num, final int size, final List<T> items) {
        this.total = total;
        this.size = size;
        this.num = num;
        this.items = new ArrayList<>(items);

        if (size > 0) {
            this.count = (int) Math.ceil((double) total / size);
        } else {
            this.count = 0;
        }
    }

    /**
     * 获取总条数。
     *
     * @return 总条数。
     */
    public long getTotal() {
        return total;
    }

    /**
     * 获取每页数量。
     *
     * @return 每页数量。
     */
    public int getSize() {
        return size;
    }

    /**
     * 获取总页数。
     *
     * @return 总页数。
     */
    public int getCount() {
        return count;
    }

    /**
     * 获取当前页码。
     *
     * @return 当前页码。
     */
    public int getNum() {
        return num;
    }

    /**
     * 获取记录集合。
     *
     * @return 记录集合。
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * 是否为空集合。
     *
     * @return true -- 空集合；false -- 非空集合。
     */
    public boolean isEmpty() {
        return this.items.isEmpty();
    }
}
