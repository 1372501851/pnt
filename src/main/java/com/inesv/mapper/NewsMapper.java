package com.inesv.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.inesv.model.News;



public interface NewsMapper {
   
	
	//查询获得资讯集合
    List<News> getNewsList();
    
    //根据语言获得资讯集合
    List<News> getNewsListByLangue(@Param("newsLangue")Integer newsLangue);
    
    //查询记录数
    Integer getNewsCounts();
    
    //根据语言查询记录数
    Integer getNewsCountsByLangue(@Param("newsLangue")Integer newsLangue);
    
    //资讯详情
    News getNewsDetail(@Param("id")Long id);
    
}
