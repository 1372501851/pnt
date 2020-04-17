package com.inesv.service;

import com.inesv.model.Address;
import com.inesv.model.Params;
import com.inesv.model.UserWallet;
import com.inesv.util.BaseResponse;

public interface BonusService {

    void invitationBonus(Long userId, Long recId) throws Exception;

    void invitationBonusByCreateWallet(Address address, Params centerAddress, String userAddress, Long userNo, UserWallet userWallet) throws Exception;

    BaseResponse getBonusDetailByUserNo(String data) throws Exception;

    Long insertBonusDetail(String bonusKey, Long recId, Long userId, Integer coinNo, Double price, Integer state, String remark, String bonusLevel) throws Exception;
}
