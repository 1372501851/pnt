package com.inesv.service;


import com.inesv.model.Coin;
import com.inesv.model.SpotEntrust;
import com.inesv.model.UserWallet;

import java.math.BigDecimal;

public interface EntrustService {


    /**
     * 添加广告
     * @param spotEntrustDto
     * @param userBalanceDto
     * @param editType
     * @param number
     * @return
     * @throws Exception
     */
    Long addSpotEntrust(SpotEntrust spotEntrustDto, UserWallet userWallet,BigDecimal freezePrice, BigDecimal totalETHFreeze, Coin coin) throws Exception;

}
