package com.inesv.service;

import com.inesv.util.BaseResponse;

public interface BankCardService {

	BaseResponse addBankCard(String data) throws Exception;

	BaseResponse getBankCard(String data) throws Exception;

	BaseResponse getBankCardById(String data) throws Exception;

	BaseResponse editBankCardById(String data) throws Exception;

	BaseResponse deleteBankCardById(String data) throws Exception;

}
