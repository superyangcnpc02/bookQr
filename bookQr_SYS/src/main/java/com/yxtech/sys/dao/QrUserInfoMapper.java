package com.yxtech.sys.dao;

import com.yxtech.sys.domain.QrUserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface QrUserInfoMapper extends Mapper<QrUserInfo> {
    List<String> getOpenIds();

    //查询实体，检查是否有数据
    List<QrUserInfo> getListByMap(Map map);

    @Select("SELECT * FROM t_qr_userinfo where (qr_Id=627 OR qr_Id=532) and openid=#{openId} and type=#{type} and authkey is not NULL")
    List<QrUserInfo> getByCondition(@Param("type")int type, @Param("qrId")int qrId,@Param("openId") String openId);
}