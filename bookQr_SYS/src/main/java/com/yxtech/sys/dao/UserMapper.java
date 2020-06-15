package com.yxtech.sys.dao;

import com.yxtech.sys.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

@Repository
public interface UserMapper extends Mapper<User> {
    Integer getUserIdByUserName(@Param(value = "editor") String editor, @Param(value = "pressId") int pressId);
    public int updateUserPressById(Map<String,Object> map);
    public int getPressEditorIfExists(@Param(value = "name") String name, @Param(value = "pressId") int pressId);
    public int getEmailIfExists(@Param("email") String email);

    User getUserByUserName(@Param(value = "editor") String editor, @Param(value = "pressId") int pressId);
}