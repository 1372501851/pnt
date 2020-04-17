package com.inesv.mapper;

import com.inesv.mapper.base.NodeBaseMapper;
import com.inesv.model.Node;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface NodeMapperTDelete {
    List<Map<String,Integer>> getUserIds();

    List<Integer> getParentIds(@Param("userId") Integer userId);

    List<Integer> getChildIds(@Param("userId") Integer userId);

    Integer getNoRecommendIds(@Param("userId") Integer userId);

    BigDecimal getSumByUserId(@Param("userId") Integer userId);

    Map<String,Object> getFlowRate(@Param("sum") BigDecimal sum);

    void addNodeProfitRecord(Map<String,Object> map);

    void deleteRepeat(@Param("recordTable") String recordTable);

    BigDecimal getSumByRecordUserId(@Param("recordTable") String recordTable,@Param("userId") Integer userId);

    BigDecimal getSumByRecordUserId2(@Param("recordTable") String recordTable,@Param("userId") Integer userId);

    void insertSumRecord(@Param("userId") Integer userId, @Param("sum") BigDecimal sum, @Param("state") int state, @Param("status") int status);

    List<Map<String,Object>> getTotalSum();

    List<Node> getNodeFounderIds();

    void insertNodeRecord(@Param("userId") Long userId, @Param("nodeId") Integer nodeId, @Param("amount") Long amount, @Param("state") int state);

    void withdraw(@Param("userId") Long userId);

    void updateStatus(@Param("userId") Integer userId);

    void insertTradeRecord(@Param("userId") Integer userId, @Param("amount") BigDecimal amount,@Param("hash") String hash);

    void insertUserExitRecord(@Param("userId") Long userId, @Param("amount") BigDecimal amount, @Param("hash") String hash);

    Integer getStateByNodeId(@Param("nodeId") Integer nodeId);

    Integer getBurnGrade();

    List<Map<String,Object>> getAllUserStaticAmount();
}
