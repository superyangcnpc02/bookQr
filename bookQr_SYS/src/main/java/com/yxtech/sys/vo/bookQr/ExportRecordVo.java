package com.yxtech.sys.vo.bookQr;

import com.alibaba.fastjson.annotation.JSONField;
import com.yxtech.sys.domain.QrExport;

import java.util.Date;

/**
 * @author cuihao
 * @create 2016-12-14-8:42
 */

public class ExportRecordVo {
    private String exportAdmin;
    private int exportNum;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date exportTime;

    public String getExportAdmin() {
        return exportAdmin;
    }

    public void setExportAdmin(String exportAdmin) {
        this.exportAdmin = exportAdmin;
    }

    public int getExportNum() {
        return exportNum;
    }

    public void setExportNum(int exportNum) {
        this.exportNum = exportNum;
    }

    public Date getExportTime() {
        return exportTime;
    }

    public void setExportTime(Date exportTime) {
        this.exportTime = exportTime;
    }
    public ExportRecordVo(){}
    public ExportRecordVo(QrExport export){
        this.exportNum = export.getNum();
        this.exportAdmin = export.getCreatorAccount();
        this.exportTime = export.getCreateTime();
    }
}
