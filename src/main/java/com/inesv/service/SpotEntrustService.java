package com.inesv.service;


import com.inesv.util.BaseResponse;
import com.inesv.util.ResponseParamsDto;
import java.math.BigDecimal;
import java.util.Map;

public interface SpotEntrustService {

    /**
     * 添加广告委托记录并修改用户资产
     * @param userNo
     * @param coinNo
     * @param tradeType
     * @param tradePrice
     * @param tradeNum
     * @param entrustRange
     * @param receivablesType
     * @param minPirce
     * @param maxPrice
     * @param securityState
     * @param dealPwd
     * @param remark
     * @param bankcardId
     * @return
     * @throws Exception
     */
	BaseResponse<Map<String, Object>> addEntrust(Long userNo, Integer coinNo, String tradeType, BigDecimal tradePrice, BigDecimal tradeNum, Integer receivablesType, BigDecimal minPirce,
                                                 String dealPwd, String remark, Integer bankcardId, ResponseParamsDto responseParamsDto) throws Exception;

    /**
     * C2C点对点交易
     * @param userNo
     * @param entrustNo
     * @param tradePrice
     * @param tradeNum
     * @param dealPwd
     * @param responseParamsDto
     * @return
     * @throws Exception
     */
	BaseResponse<Map<String, Object>> spotTrade(Long userNo, String entrustNo, BigDecimal tradeNum, String tradePassword, String remark, Integer bankId, ResponseParamsDto responseParamsDto) throws Exception;

    /**
     * 撤销广告
     * @param userNo
     * @param entrustNo
     * @param responseParamsDto
     * @return
     * @throws Exception
     */
	BaseResponse<Map<String, Object>> delEntrust(Integer userNo, String entrustNo, ResponseParamsDto responseParamsDto) throws Exception;

    /**
     * 获取平台广告
     * @param data
     * @return
     * @throws Exception
     */
    BaseResponse getSpotEntrust(String data) throws Exception;

    /**
     * 获取用户广告
     * @param data
     * @return
     * @throws Exception
     */
    BaseResponse getSpotEntrustByUserNo(String data) throws Exception;

    /**
     * 获取广告详情
     * @param data
     * @return
     * @throws Exception
     */
    BaseResponse getSpotEntrustById(String data) throws Exception;

    /**
     * 获取开启C2C广告的货币列表
     * @param
     * @return
     * @throws Exception
     */
    BaseResponse getOpenTransCoins() throws Exception;
}
