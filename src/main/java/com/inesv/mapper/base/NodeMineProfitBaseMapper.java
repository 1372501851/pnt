package com.inesv.mapper.base;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.inesv.model.NodeMineProfit;
/**
*  @author author
*/
public interface NodeMineProfitBaseMapper {

    int insertNodeMineProfit(NodeMineProfit object);

    int updateNodeMineProfit(NodeMineProfit object);

    int update(NodeMineProfit.UpdateBuilder object);

    List<NodeMineProfit> queryNodeMineProfit(NodeMineProfit object);

    NodeMineProfit queryNodeMineProfitLimit1(NodeMineProfit object);

}