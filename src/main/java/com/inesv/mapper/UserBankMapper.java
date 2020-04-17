package com.inesv.mapper;

import com.inesv.model.UserBank;

import java.util.List;

public interface UserBankMapper {

	int insertUserBank(UserBank userBank) throws Exception;

	List<UserBank> selectUserBankByUserNo(UserBank userBank) throws Exception;

	UserBank selectUserBankByID(UserBank userBank) throws Exception;

	int updateUserBank(UserBank userBank) throws Exception;

	int deleteUserBank(UserBank userBank) throws Exception;
}
