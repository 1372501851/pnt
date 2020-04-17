package com.inesv.mapper.base;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.inesv.model.Node;
/**
*  @author author
*/
public interface NodeBaseMapper {

    int insertNode(Node object);

    int updateNode(Node object);

    int update(Node.UpdateBuilder object);

    List<Node> queryNode(Node object);

    Node queryNodeLimit1(Node object);

}