package com.inesv.mapper;

import com.inesv.model.News;
import com.inesv.model.NewsSeeUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface NewsSeeUserMapper {

    //根据用户id查询已读公告id列表
    NewsSeeUser getSeeNewsListByUserId(@Param("userId") Integer userId);

    //修改已读公告id
    Integer updateNewsId(@Param("userId") Integer userId,@Param("newsIdList") String newsIdList);

    //新增已读表用户公告id
    Integer addNewsSeeUser(NewsSeeUser newsSeeUser);
}
