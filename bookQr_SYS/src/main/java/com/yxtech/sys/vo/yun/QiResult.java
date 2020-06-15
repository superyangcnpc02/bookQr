package com.yxtech.sys.vo.yun;

/**
 * @author cuihao
 * @create 2017-04-17-9:30
 */

public class QiResult {
    /**
     * 所执行的云处理操作命令fopN
     */
    public String cmd;
    /**
     * 所执行的云处理操作命令状态码
     */
    public int code;
    /**
     * 所执行的云处理操作命令状态描述
     */
    public String desc;
    /**
     * 如果处理失败，该字段会给出失败的详细原因
     */
    public String error;
    /**
     * 云处理结果保存在目标空间文件的hash值
     */
    public String hash;
    /**
     * 云处理结果保存在目标空间的文件名
     */
    public String key;
    /**
     * 默认为0。当用户执行saveas时，如果未加force且指定的bucket：key存在，则返回1 ，告诉用户返回的是旧数据
     */
    public int returnOld;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getReturnOld() {
        return returnOld;
    }

    public void setReturnOld(int returnOld) {
        this.returnOld = returnOld;
    }

    public QiResult() {
    }
}
