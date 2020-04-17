package com.inesv.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.BankCardService;
import com.inesv.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
public class BankCardServiceImpl implements BankCardService {

	@Resource
	private UserMapper userMapper;

	@Resource
	private UserBankMapper userBankMapper;

	/**
	 * 绑定银行卡
	 *
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@Override
	public BaseResponse addBankCard(String data) throws Exception {
		JSONObject dataJson = JSONObject.parseObject(data);
		String token = dataJson.getString("token");
		String bank = dataJson.getString("bank");
		String branch = dataJson.getString("branch");
		String code = dataJson.getString("code");
		String name = dataJson.getString("name");
		String address = dataJson.getString("address");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
				.getString("language"));

		if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
		if (ValidataUtil.isNull(bank) || ValidataUtil.isNull(branch)) return RspUtil.rspError(responseParamsDto.BANK_NOT_NULL);
		if (ValidataUtil.isNull(code)) return RspUtil.rspError(responseParamsDto.CODE_NULL_DESC);
		if (ValidataUtil.isNull(name)) return RspUtil.rspError(responseParamsDto.NAME_NOT_NULL);

		User user = userMapper.getUserInfoByToken(token);
		if (user == null)
			return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

		UserBank userBank = new UserBank();
		userBank.setUserId(Integer.valueOf(user.getId().toString()));
		userBank.setCode(code);
		List<UserBank> userBanks = userBankMapper.selectUserBankByUserNo(userBank);
		if (userBanks.size() >= 1)
			return RspUtil.rspError(responseParamsDto.BANK_EXISTEST_DESC);

		userBank = new UserBank();
		userBank.setBank(bank);
		userBank.setBranch(branch);
		userBank.setCode(code);
		userBank.setName(name);
		userBank.setAddress(address);
		userBank.setState(1);
		userBank.setUserId(Integer.valueOf(user.getId().toString()));

		userBankMapper.insertUserBank(userBank);

		return RspUtil.success();
	}

	/**
	 * 获取用户银行卡信息
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@Override
	public BaseResponse getBankCard(String data) throws Exception {
		JSONObject dataJson = JSONObject.parseObject(data);
		String token = dataJson.getString("token");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
				.getString("language"));

		if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);

		User user = userMapper.getUserInfoByToken(token);
		if (user == null)
			return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

		UserBank userBank = new UserBank();
		userBank.setUserId(Integer.valueOf(user.getId().toString()));

		List<UserBank> userBanks = userBankMapper.selectUserBankByUserNo(userBank);

		return RspUtil.success(JSONFormat.getStr(userBanks));
	}

	/**
	 * 获取银行卡详情
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@Override
	public BaseResponse getBankCardById(String data) throws Exception {
		JSONObject dataJson = JSONObject.parseObject(data);
		String id = dataJson.getString("id");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
				.getString("language"));

		if (ValidataUtil.isNull(id)) return RspUtil.rspError(responseParamsDto.ID_NULL_DESC);

		UserBank userBank = new UserBank();
		userBank.setId(Long.valueOf(id));
		userBank = userBankMapper.selectUserBankByID(userBank);

		return RspUtil.success(JSONFormat.getStr(userBank));
	}

	/**
	 * 修改银行卡信息
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@Override
	public BaseResponse editBankCardById(String data) throws Exception {
		JSONObject dataJson = JSONObject.parseObject(data);
		String token = dataJson.getString("token");
		String id = dataJson.getString("id");
		String bank = dataJson.getString("bank");
		String branch = dataJson.getString("branch");
		String code = dataJson.getString("code");
		String name = dataJson.getString("name");
		String address = dataJson.getString("address");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
				.getString("language"));

		if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
		if (ValidataUtil.isNull(id)) return RspUtil.rspError(responseParamsDto.ID_NULL_DESC);
		if (ValidataUtil.isNull(bank) || ValidataUtil.isNull(branch)) return RspUtil.rspError(responseParamsDto.BANK_NOT_NULL);
		if (ValidataUtil.isNull(code)) return RspUtil.rspError(responseParamsDto.CODE_NULL_DESC);
		if (ValidataUtil.isNull(name)) return RspUtil.rspError(responseParamsDto.NAME_NOT_NULL);

		User user = userMapper.getUserInfoByToken(token);
		if (user == null)
			return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

		UserBank userBank = new UserBank();
		userBank.setId(Long.valueOf(id));
		userBank.setUserId(Integer.valueOf(user.getId().toString()));
		userBank.setBank(bank);
		userBank.setBranch(branch);
		userBank.setCode(code);
		userBank.setName(name);
		userBank.setAddress(address);

		userBankMapper.updateUserBank(userBank);

		return RspUtil.success();
	}

	/**
	 * 删除指定银行卡
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@Override
	public BaseResponse deleteBankCardById(String data) throws Exception {
		JSONObject dataJson = JSONObject.parseObject(data);
		String token = dataJson.getString("token");
		String id = dataJson.getString("id");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
				.getString("language"));

		if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
		if (ValidataUtil.isNull(id)) return RspUtil.rspError(responseParamsDto.ID_NULL_DESC);

		User user = userMapper.getUserInfoByToken(token);
		if (user == null)
			return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

		UserBank userBank = new UserBank();
		userBank.setUserId(Integer.valueOf(user.getId().toString()));
		userBank.setId(Long.valueOf(id));
		userBankMapper.deleteUserBank(userBank);

		return RspUtil.success();
	}
}
