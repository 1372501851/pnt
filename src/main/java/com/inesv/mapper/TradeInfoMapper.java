package com.inesv.mapper;

import com.inesv.model.TradeFriends;
import com.inesv.model.TradeInfo;

import java.util.List;

public interface TradeInfoMapper {

    int insertTradeInfo(TradeInfo tradeInfo);

    int updateTradeInfo(TradeInfo tradeInfo);

    TradeInfo getTradeInfoByCondition(TradeInfo tradeInfo);

    TradeInfo getTradeInfoByConditionLast(TradeInfo tradeInfo);

    List<TradeInfo> getTradeInfoByConditions(TradeInfo tradeInfo);

    List<TradeInfo> getTradeList(String address);
}
