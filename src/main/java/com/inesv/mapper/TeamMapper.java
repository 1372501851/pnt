package com.inesv.mapper;

import com.inesv.model.Team;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TeamMapper {
    Team teamInfo(@Param("userId") Long userId);

    List<Long> getUserIds();

    String getPath(@Param("userId") Long userId);

    void insert(@Param("userId") Long userId, @Param("path") String path, @Param("grade") Integer grade);

    Integer isFirstUserId(@Param("userId") Long userId);

    List<Long> getLowerUserId(@Param("userId") String userId);

    List<Long> getLowerUserId2(@Param("userId") String userId);

    String getTotalAmount(@Param("userId") String userId);

    String getTotalAmount2(@Param("userId") String userId);

    List<Long> getSearch(@Param("userId") String userId, @Param("username") String username);

    List<Long> getSearch2(@Param("userId") String userId, @Param("username") String username);

    Integer getRecCount(@Param("userId") Long userId);

    List<Long> getRecId(@Param("userId") Long id);
}
