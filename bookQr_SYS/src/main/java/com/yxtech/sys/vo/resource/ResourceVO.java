package com.yxtech.sys.vo.resource;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by yanfei on 2015/11/7.
 */
public class ResourceVO {
    // 二维码ID
    @NotNull(message = "二维码ID不能为空")
    private Integer id;
    // 多个新资源ID
    @NotEmpty(message = "新资源ID至少得有一个")
    private List<FilesVo> newFiles;
    // 多个旧资源ID
    @NotEmpty(message = "旧资源ID至少得有一个")
    private List<Object> oldIds;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<FilesVo> getNewFiles() {
        return newFiles;
    }

    public void setNewFiles(List<FilesVo> newFiles) {
        this.newFiles = newFiles;
    }

    public List<Object> getOldIds() {
        return oldIds;
    }

    public void setOldIds(List<Object> oldIds) {
        this.oldIds = oldIds;
    }
}
