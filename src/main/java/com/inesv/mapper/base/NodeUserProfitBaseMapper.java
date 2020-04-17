package com.inesv.mapper.base;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.inesv.model.NodeUserProfit;
/**
*  @author author
*/
public interface NodeUserProfitBaseMapper {

    int insertNodeUserProfit(NodeUserProfit object);

    int updateNodeUserProfit(NodeUserProfit object);

    int update(NodeUserProfit.UpdateBuilder object);

    List<NodeUserProfit> queryNodeUserProfit(NodeUserProfit object);

    NodeUserProfit queryNodeUserProfitLimit1(NodeUserProfit object);

}