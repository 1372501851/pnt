package com.inesv.mapper;

import org.apache.ibatis.annotations.Param;

import com.inesv.model.User;

public interface UserMapper {

    int insertUserInfo(User user);

    int updateUserInfo(User user);

    User getUserInfoByCondition(User user);

    User getUserInfoByToken(@Param("token")String token);

    User getUserInfoById(Long userId);
}
