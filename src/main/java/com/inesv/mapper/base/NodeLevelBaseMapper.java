package com.inesv.mapper.base;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.inesv.model.NodeLevel;
/**
*  @author author
*/
public interface NodeLevelBaseMapper {

    int insertNodeLevel(NodeLevel object);

    int updateNodeLevel(NodeLevel object);

    int update(NodeLevel.UpdateBuilder object);

    List<NodeLevel> queryNodeLevel(NodeLevel object);

    NodeLevel queryNodeLevelLimit1(NodeLevel object);

}