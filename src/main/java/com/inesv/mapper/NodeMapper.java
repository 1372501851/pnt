package com.inesv.mapper;

import com.inesv.mapper.base.NodeBaseMapper;
import com.inesv.model.Node;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
*  @author author
*/
@Repository
public interface NodeMapper extends NodeBaseMapper{

    /**
     * 获取用户到parId的层级关系，不符合返回null
     * @param userId
     * @param parId
     * @return
     */
    String getParListByPid(@Param("userId") Long userId,@Param("parId") Long parId);

    void updateIntoAmount(@Param("nodeId") Long nodeId, @Param("amount") BigDecimal amount);

    Map<String,Object> getFlowRate(@Param("sum") BigDecimal sum);

    void addNodeProfitRecord(Map<String,Object> map);

    Integer getBurnGrade();

    List<Map<String,Object>> getAllUserStaticAmount();

    Integer getStateByNodeId(@Param("nodeId") Integer nodeId);

    Node getNodeByNodeId(@Param("nodeId") Long nodeId);

    BigDecimal getMaxAmountMin();

    Map<String,Object> getFlowRate2(@Param("maxAmountMin") BigDecimal maxAmountMin);

    List<Node> getAllNode();

    Node getDetails(@Param("nodeId") Long nodeId);
}