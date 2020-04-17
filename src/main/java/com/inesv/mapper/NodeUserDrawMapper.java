package com.inesv.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.inesv.model.NodeUserDraw;
import com.inesv.mapper.base.NodeUserDrawBaseMapper;
/**
*  @author author
*/
public interface NodeUserDrawMapper extends NodeUserDrawBaseMapper{


    NodeUserDraw getUserDrawRecord(@Param("totalId") Long totalId);
}