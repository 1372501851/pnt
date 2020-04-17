package com.inesv.mapper;

import com.inesv.model.Kline;

public interface KlineMapper {

    Kline queryKlineByCoinNo(Long coinNo);

    int updateByCoinNo(Kline kline);

}