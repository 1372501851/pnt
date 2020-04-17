package com.inesv.mapper;

import com.inesv.model.IDCard;

import java.util.List;


public interface IDCardMapper {
    int add(IDCard idCard);

    List<IDCard> getByConditions(IDCard idCard);

    int update(IDCard idCard);

    List<IDCard> getByConditionsWithLimit(IDCard idCard);
}
