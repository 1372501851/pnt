package com.inesv.mapper.base;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.inesv.model.NodeUserTotal;
/**
*  @author author
*/
public interface NodeUserTotalBaseMapper {

    int insertNodeUserTotal(NodeUserTotal object);

    int updateNodeUserTotal(NodeUserTotal object);

    int update(NodeUserTotal.UpdateBuilder object);

    List<NodeUserTotal> queryNodeUserTotal(NodeUserTotal object);

    NodeUserTotal queryNodeUserTotalLimit1(NodeUserTotal object);

}