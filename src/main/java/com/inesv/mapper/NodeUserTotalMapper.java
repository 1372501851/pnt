package com.inesv.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.inesv.model.NodeUserTotal;
import com.inesv.mapper.base.NodeUserTotalBaseMapper;
import org.springframework.stereotype.Repository;

/**
*  @author author
*/
@Repository
public interface NodeUserTotalMapper extends NodeUserTotalBaseMapper{


    NodeUserTotal  getSumByUserId(@Param("userId") Long userId);

    void updateByUserId(@Param("userId") Long userId);

    List<NodeUserTotal> getDrawRecord(Long id);

    List<NodeUserTotal> drawShowRecord(@Param("userId") Long userId);

    Integer getIsSecondPet(@Param("userId") Long userId);

    Integer getIsExist(@Param("userId") Long userId);

    String getPath(@Param("maxTreeGrade") Integer maxTreeGrade, @Param("nodeId") Integer nodeId);
}