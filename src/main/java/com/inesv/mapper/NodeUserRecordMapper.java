package com.inesv.mapper;

import com.inesv.mapper.base.NodeUserRecordBaseMapper;
import com.inesv.model.NodeUserRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
*  @author author
*/
@Repository
public interface NodeUserRecordMapper extends NodeUserRecordBaseMapper{

    /**
     * 查询用户投票节点总金额
     * @param userId
     * @param nodeId
     * @return
     */
    BigDecimal getUserNodeAllAmount(@Param("userId") Long userId,@Param("nodeId") Long nodeId);

    /**
     * 查询节点当前总投入量
     * @param nodeId
     * @return
     */
    BigDecimal getNodeAllAmount(@Param("nodeId") Long nodeId);

    List<NodeUserRecord> getUserPetRecord(@Param("userId") Long userId);

    void updateStateByUserId(@Param("userId") Long userId);

    List<Map<String,Object>> getUserIds();

    BigDecimal getSumByuseId(@Param("userId") Long userId);


    Long getNodeId(@Param("userId") Long userId);
}