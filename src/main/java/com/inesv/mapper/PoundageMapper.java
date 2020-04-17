package com.inesv.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.inesv.model.Poundage;

import java.util.List;

@Mapper
public interface PoundageMapper {

    /**
     * 批量新增手续费记录
     * @param poundageDtos
     * @return
     * @throws Exception
     */
    int insertPoundatges(List<Poundage> poundages) throws Exception;
    
    
    /**
     * 修改记录
     * @param poundage
     * @return
     * @throws Exception
     */
    int updatePoundage(Poundage poundage)throws Exception;
    
    /**
     * 获得记录
     * @param orderNo
     * @return
     */
    public Poundage getByOrderNo(String orderNo);
    
}
