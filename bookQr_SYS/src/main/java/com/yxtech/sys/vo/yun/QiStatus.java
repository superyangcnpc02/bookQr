package com.yxtech.sys.vo.yun;

import java.util.List;

/**
 * @author cuihao
 * @create 2017-04-17-9:30
 */

public class QiStatus {
    /**
     * 持久化处理的进程ID，即 persistentId
     */
    public String id;
    /**
     * 状态码 0 成功，1 等待处理，2 正在处理，3 处理失败，4 通知提交失败
     */
    public int code;
    /**
     * 与状态码相对应的详细描述
     */
    public String desc;
    /**
     * 处理源文件的文件名
     */
    public String inputKey;
    /**
     * 处理源文件所在的空间名
     */
    public String inputBucket;
    /**
     * 云处理操作的处理队列
     */
    public String pipeline;
    /**
     * 云处理请求的请求id，主要用于七牛技术人员的问题排查
     */
    public String reqid;
    /**
     * 云处理操作列表，包含每个云处理操作的状态信息
     */
    public List<QiResult> items;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInputKey() {
        return inputKey;
    }

    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }

    public String getInputBucket() {
        return inputBucket;
    }

    public void setInputBucket(String inputBucket) {
        this.inputBucket = inputBucket;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public String getReqid() {
        return reqid;
    }

    public void setReqid(String reqid) {
        this.reqid = reqid;
    }

    public List<QiResult> getItems() {
        return items;
    }

    public void setItems(List<QiResult> items) {
        this.items = items;
    }

    public QiStatus() {
    }
}
