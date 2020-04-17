package com.inesv.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.inesv.model.NodeMineProfit;
import com.inesv.mapper.base.NodeMineProfitBaseMapper;
import org.springframework.stereotype.Repository;

/**
*  @author author
*/
@Repository
public interface NodeMineProfitMapper extends NodeMineProfitBaseMapper{


    List<NodeMineProfit> digMineProfitRecord(@Param("nodeId") Long nodeId);
}