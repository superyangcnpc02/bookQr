package com.yxtech.common.json;


import com.github.pagehelper.PageInfo;

/**
 * Json 数据传输封装
 *
 * @author yanfei
 * @since 2015-6-5
 */
public class JsonResultPage<T> extends JsonResultList {

    private final long total; // 总条数。
    private final int size;  // 每页数量。
    private final int count; // 总页码。
    private final int num;   // 当前页码。
    private boolean hasMore;

    public JsonResultPage(Page<T> page) {
        super(page.getItems());

        this.total = page.getTotal();
        this.size = page.getSize();
        this.num = page.getNum();
        this.count = page.getCount();
        this.hasMore = page.getNum() < page.getCount();
    }

    public JsonResultPage(boolean status, String message) {
        super(status, message);

        this.total = 0;
        this.size = 0;
        this.num = 1;
        this.count = 0;
        this.hasMore = false;
    }

    /**
     *
     * @param page
     */
    public JsonResultPage(PageInfo<T> page) {
        super(page.getList());

        this.total = page.getTotal();
        this.size = page.getPageSize();
        this.num = page.getPageNum();
        this.count = page.getPages();
        this.hasMore = num < count;
    }

    /**
     * 获取总条数
     *
     * @return 总条数
     */
    public long getTotal() {
        return total;
    }

    /**
     * 获取每页数量
     *
     * @return 每页数量
     */
    public int getSize() {
        return size;
    }

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public int getCount() {
        return count;
    }

    /**
     * 获取当前页码
     *
     * @return 当前页码
     */
    public int getNum() {
        return num;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
