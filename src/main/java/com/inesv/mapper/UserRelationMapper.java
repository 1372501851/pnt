package com.inesv.mapper;

import com.inesv.model.UserRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserRelationMapper {

    int insertUserRelation(UserRelation userRelation);

    UserRelation getUserRelationByCondition(UserRelation userRelation);

    Integer getMaxTreeGrade();

    List<Long> getMaxTreeGradeList( @Param("treeGrade") Integer treeGrade);
}
