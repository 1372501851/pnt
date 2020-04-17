package com.inesv.service;

import com.inesv.util.BaseResponse;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {

	BaseResponse memoLogin(String data) throws Exception;

	BaseResponse addUser(String data) throws Exception;

	BaseResponse login(String data) throws Exception;

	BaseResponse logout(String data) throws Exception;

	BaseResponse setPhoto(String data, MultipartFile photo) throws Exception;

	BaseResponse setNickName(String data) throws Exception;

	BaseResponse setDealPwd(String data) throws Exception;

	BaseResponse editDealPwd(String data) throws Exception;

	BaseResponse forgetDealPwd(String data) throws Exception;

	BaseResponse getDealPwdState(String data) throws Exception;

	BaseResponse editPwd(String data) throws Exception;

	BaseResponse forgetPwd(String data) throws Exception;

	BaseResponse getUserInfo(String data) throws Exception;

	BaseResponse importAddress(String data) throws Exception;

	BaseResponse exportAddress(String data) throws Exception;

	BaseResponse bindWeChat(String data) throws Exception;

	BaseResponse bindApay(String data) throws Exception;

	BaseResponse getUserByInvitation(String data) throws Exception;

	BaseResponse userIsExistence(String data) throws Exception;

	boolean isTradePaswd(Long userId, String tradePasswd);

	BaseResponse bindPhone(String data) throws Exception;

	BaseResponse bindImToken(String data) throws Exception;

	BaseResponse isExistPhone(String data) throws Exception;

	BaseResponse isExistImToken(String data) throws Exception;

}
