package com.inesv.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.inesv.model.NodeUserProfit;
import com.inesv.mapper.base.NodeUserProfitBaseMapper;
/**
*  @author author
*/
public interface NodeUserProfitMapper extends NodeUserProfitBaseMapper{


    List<NodeUserProfit> getUserProfitRecord( @Param("userId") Long userId);

    void insertSumRecord(@Param("userId") Integer userId, @Param("amount") BigDecimal amount, @Param("type") int type);

    List<Long> getUserIds();

    BigDecimal getSumByUserId(Long userId);

    void updateStatus(@Param("userId") Long userId, @Param("hash") String hash);
}