package com.inesv.mapper.base;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.inesv.model.NodeUserDraw;
/**
*  @author author
*/
public interface NodeUserDrawBaseMapper {

    int insertNodeUserDraw(NodeUserDraw object);

    int updateNodeUserDraw(NodeUserDraw object);

    int update(NodeUserDraw.UpdateBuilder object);

    List<NodeUserDraw> queryNodeUserDraw(NodeUserDraw object);

    NodeUserDraw queryNodeUserDrawLimit1(NodeUserDraw object);

}