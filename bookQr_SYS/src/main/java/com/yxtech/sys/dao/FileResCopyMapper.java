package com.yxtech.sys.dao;

import com.yxtech.sys.domain.FileRes;
import com.yxtech.sys.domain.FileResCopy;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface FileResCopyMapper extends Mapper<FileResCopy> {
    public List<FileResCopy> getFilesNoUpload();

    public int updateFileResCopy(Map<String,Object> map);
}