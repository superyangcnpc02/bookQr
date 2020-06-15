package com.yxtech.sys.dao;

import com.yxtech.sys.domain.FileRes;
import com.yxtech.sys.domain.Res;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface FileResMapper extends Mapper<FileRes> {
    public List<FileRes> getFileResList(Map<String,List<Res>> map);
    public int updateFilePath(Map<String,Object> map);
    public int updateCourseQrFileNamePath(Map<String,Object> map);

    public List<FileRes> getFileRess(Map map);

    public  List<Object> findResByName(String keyword);
}