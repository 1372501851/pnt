package com.inesv.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.inesv.model.NodeLevel;
import com.inesv.mapper.base.NodeLevelBaseMapper;
import org.springframework.stereotype.Repository;

/**
*  @author author
*/
@Repository
public interface NodeLevelMapper extends NodeLevelBaseMapper{


    /**
     * 获取购买节点最小金额
     * @return
     */
    BigDecimal getNodeBuyMin();
}