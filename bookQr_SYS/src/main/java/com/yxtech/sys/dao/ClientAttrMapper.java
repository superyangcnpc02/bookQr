package com.yxtech.sys.dao;

import com.yxtech.sys.domain.ClientAttr;
import com.yxtech.sys.vo.ClientBookVo;
import com.yxtech.sys.vo.client.ClientExportVO;
import com.yxtech.sys.vo.clientAttr.ClientAttrBookVo;
import com.yxtech.sys.vo.clientAttr.ClientAttrExamineVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface ClientAttrMapper extends Mapper<ClientAttr> {
    Integer getCountExamineList(@Param(value = "status") int status, @Param(value = "bookId") int bookId, @Param(value = "userId") int userId, @Param(value = "role") Integer role, @Param(value = "pageNo") int pageNo, @Param(value = "pageSize") int pageSize);
    List<ClientAttrBookVo> getExamineList(@Param(value = "status") int status, @Param(value = "bookId") int bookId, @Param(value = "userId") int userId, @Param(value = "role") Integer role, @Param(value = "pageNo") int pageNo, @Param(value = "pageSize") int pageSize);
    //审核列表导出
    List<ClientBookVo> exportList(Map map);
    List<ClientAttrExamineVo> getExamineVoList(@Param(value = "bookId") int bookId);

    List<ClientExportVO> exportClient(@Param("bookId") int bookId, @Param("pressId") int pressId, @Param("status") int status, @Param("keyword") String keyword);
}