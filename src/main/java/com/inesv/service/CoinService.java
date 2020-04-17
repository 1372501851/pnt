
package com.inesv.service;

import com.inesv.model.Coin;
import com.inesv.util.BaseResponse;

import java.util.List;

public interface CoinService {

    BaseResponse getCoin(String data) throws Exception;

    BaseResponse getCoinByNo(String data) throws Exception;

    BaseResponse queryRoutineList(String data);

    BaseResponse queryOpenPntCoin(String data);

    BaseResponse queryList(String data);
    
    BaseResponse coinKchartsData(String data);

    BaseResponse coinMarket(String data);

    BaseResponse Kline(String data);

    BaseResponse queryOpenCoinList(String data);

    BaseResponse queryAllCoinList(String data) throws Exception;
}
