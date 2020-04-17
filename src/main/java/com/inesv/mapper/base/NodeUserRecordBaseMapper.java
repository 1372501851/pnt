package com.inesv.mapper.base;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.inesv.model.NodeUserRecord;
/**
*  @author author
*/
public interface NodeUserRecordBaseMapper {

    int insertNodeUserRecord(NodeUserRecord object);

    int updateNodeUserRecord(NodeUserRecord object);

    int update(NodeUserRecord.UpdateBuilder object);

    List<NodeUserRecord> queryNodeUserRecord(NodeUserRecord object);

    NodeUserRecord queryNodeUserRecordLimit1(NodeUserRecord object);

}