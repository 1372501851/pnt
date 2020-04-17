package com.inesv.mapper;

import com.inesv.model.UserWallet;

import java.util.List;
import java.util.Map;

public interface UserWalletMapper {

	int insertUserWallets(List<UserWallet> userWallets);

	int updateUserWalletByCondition(UserWallet userWallet);

	UserWallet getUserWalletByCondition(UserWallet userWallet);

	List<UserWallet> getUserWalletByConditions(UserWallet userWallet);

	UserWallet getUserWalletByConditionForUpdate(UserWallet userWallet);

	List<UserWallet> getuserWallet(Long userId);

	List<UserWallet> getuserWalletStates(Long userId);

	Long editState(UserWallet userWallet);

	List<UserWallet> queryByUserAndOpen(Long userId);

	UserWallet queryByUserState(Map<String, Object> params);

	int updateBalance(UserWallet userWallet);

	List<UserWallet> getAllUserWallet();

	List<UserWallet> getUserWalletByAddress(String address);

	int delUserWalletById(long walletId);

	int userWalletCount(Map<String, Object> params);

	int add(UserWallet userWallet);

	int updateThisWallet(UserWallet userWallet);

	List<UserWallet> getAllUserWalletWithoutPTN();
}
