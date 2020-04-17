package com.inesv.service;


import java.math.BigDecimal;
import java.util.Map;

import com.inesv.model.Coin;
import com.inesv.model.SpotDealDetail;
import com.inesv.model.UserWallet;
import com.inesv.util.BaseResponse;
import com.inesv.util.ResponseParamsDto;

public interface SpotDealDetailService {

    /**
     * 用户确认收款
     * @param dealNo
     * @param userNo
     * @param responseParamsDto
     * @return
     * @throws Exception
     */
	BaseResponse<Map<String, Object>> confirmReceive(String token, String orderNo, String tradePassword, ResponseParamsDto responseParamsDto) throws Exception;

    /**
     * 用户确认付款
     * @param dealNo
     * @param userNo
     * @param responseParamsDto
     * @return
     * @throws Exception
     */
	BaseResponse<Map<String, Object>> confirmPay(String orderNo, Integer userNo, String tradePassword, ResponseParamsDto responseParamsDto) throws Exception;

    /**
     * 用户取消订单
     * @param dealNo
     * @param userNo
     * @param responseParamsDto
     * @return
     * @throws Exception
     */
	BaseResponse<Map<String, Object>> cancel(String orderNo, Long userNo, Integer type, ResponseParamsDto responseParamsDto) throws Exception;

    /**
     * 获取用户广告
     * @param data
     * @return
     * @throws Exception
     */
    BaseResponse getSpotDealDetailByUserNo(String data) throws Exception;

    /**
     * 获取广告详情
     * @param data
     * @return
     * @throws Exception
     */
    BaseResponse getSpotDealDetailById(String data) throws Exception;
    
    
    /**
     * 短信提醒卖家，买家已付款
     */
     BaseResponse remindSms(String orderNo, Integer userId, ResponseParamsDto responseParamsDto)throws Exception;



    void confirmMatchLogic(SpotDealDetail spotDealDetail, UserWallet buyUserWallet, UserWallet sellUserWallet, Coin coin,
                                   BigDecimal dealNum, BigDecimal poundage, BigDecimal sumNum, Integer type, ResponseParamsDto responseParamsDto) throws Exception ;


}
