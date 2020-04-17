package com.inesv.mapper;

import com.inesv.model.BonusDetail;

import java.util.List;

public interface BonusDetailMapper {

    int insertBonusDetail(BonusDetail bonusDetail);

    int updateBonusDetail(BonusDetail bonusDetail);

    List<BonusDetail> getBonusDetailByCondition(BonusDetail bonusDetail);

    BonusDetail getBonusPriceAndCountByRecNo(BonusDetail bonusDetail);

    List<BonusDetail> getBonudDetailsByRecNo(BonusDetail bonusDetail);
}
