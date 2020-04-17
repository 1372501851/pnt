package com.inesv.mapper;

import com.inesv.model.NodeProfitRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Repository
public interface NodeProfitRecordMapper {
    List<NodeProfitRecord> queryAmountByUser(@Param("userId") Long userId,@Param("recordTable") String recordTable,@Param("nodeId") Integer nodeId);

    void insertRecord(HashMap<String, Object> insertModel);

    BigDecimal getSumByRecordUserId(@Param("recordTable") String recordTable, @Param("userId") Integer userId,@Param("nodeId") Integer nodeId);

    BigDecimal getSumByRecordUserId2(@Param("recordTable") String recordTable,@Param("userId") Integer userId);

    BigDecimal getStateAmount(@Param("userId") Long userId,@Param("recordTable") String recordTable);
}
