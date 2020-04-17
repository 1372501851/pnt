package com.inesv.mapper;


import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface AuthorizationMapper {
    Integer isAuthorize(@Param("userId") Long userId, @Param("programId") String programId);

    void register(@Param("programId") String programId, @Param("programName") String programName, @Param("picture") String picture);

    Map<String,String> info(@Param("programId") String programId);

    void insertRecord(@Param("userId") Long userId, @Param("programToken") String programToken,@Param("programId") String programId);
}
