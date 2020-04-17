package com.inesv.mapper;


import com.inesv.model.SpotDealDetail;

import java.util.List;

public interface SpotDealDetailMapper {
    /**
     * 新增广告交易记录
     * @param spotDealDetailDto
     * @return
     */
    int insertSpotDealDetail(SpotDealDetail spotDealDetailDto) throws Exception;

    /**
     * 批量新增广告交易记录
     * @param spotDealDetailDtos
     * @return
     */
    int insertSpotDealDetails(List<SpotDealDetail> spotDealDetailDtos) throws Exception;

    /**
     * 修改广告交易记录
     * @param spotDealDetailDto
     * @return
     */
    int updateSpotDealDetail(SpotDealDetail spotDealDetailDto) throws Exception;

    /**
     * 主键查询广告交易记录（行级锁）
     * @param spotDealDetailDto
     * @return
     */
    SpotDealDetail selectSpotDealDetailByPrimaryKey(SpotDealDetail spotDealDetailDto) throws Exception;

    /**
     * 条件查询广告交易记录
     * @param spotDealDetailDto
     * @return
     * @throws Exception
     */
    SpotDealDetail selectSpotDealDetailByCondition(SpotDealDetail spotDealDetailDto) throws Exception;

    /**
     * 条件查询广告交易记录
     * @param spotDealDetailDto
     * @return
     * @throws Exception
     */
    List<SpotDealDetail> selectSpotDealDetailByConditions(SpotDealDetail spotDealDetailDto) throws Exception;

    /**
     * 条件查询广告交易记录（时间限制）
     * @param spotDealDetailDto
     * @return
     * @throws Exception
     */
    SpotDealDetail selectSpotDealDetailByConditionAndDate(SpotDealDetail spotDealDetailDto) throws Exception;

    /**
     * 条件查询广告交易记录（时间限制）
     * @param spotDealDetailDto
     * @return
     * @throws Exception
     */
    List<SpotDealDetail> selectSpotDealDetailByConditionsAndDate(SpotDealDetail spotDealDetailDto) throws Exception;

    /**
     * 条件查询广告交易记录（总记录数）
     * @param spotDealDetailDto
     * @return
     * @throws Exception
     */
    int selectCountByConditions(SpotDealDetail spotDealDetailDto) throws Exception;

    /**
     *查询订单详情
     * */
    SpotDealDetail queryByOrderNo(String orderNo);
    
    
    /**
     * 查询已到纠纷时间的订单
     * @param spotDealDetail
     * @return
     */
    List<SpotDealDetail> selectAutoSpute(SpotDealDetail spotDealDetail);

    /**
     *根据广告号查询状态为进行中的订单数量
     * */
    int selectCountByState(SpotDealDetail spotDealDetailDto) throws Exception;
    
}
