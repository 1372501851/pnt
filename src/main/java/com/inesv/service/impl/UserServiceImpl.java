package com.inesv.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.BonusService;
import com.inesv.service.UserService;
import com.inesv.util.*;
import com.inesv.util.CoinAPI.PNTCoinApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class UserServiceImpl implements UserService {

    @Resource
    private ParamsMapper paramsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CoinMapper coinMapper;

    @Resource
    private PushMapper pushMapper;

    @Resource
    private AddressMapper addressMapper;

    @Resource
    private UserWalletMapper userWalletMapper;

    @Resource
    private UserMemoMapper userMemoMapper;

    @Resource
    private MemoAddressMapper memoAddressMapper;

    @Resource
    private UserRelationMapper userRelationMapper;

    @Resource
    private BonusDetailMapper bonusDetailMapper;

    @Resource
    private BonusService bonusService;
    @Autowired
    private VerifyCodeRecordMapper verifyCodeRecordMapper;
    @Autowired
    private VersionMapper versionMapper;

    /**
     * 用户注册
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public BaseResponse addUser(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String username = json.getString("username");
        String pwd = json.getString("pwd");
        String memo = json.getString("memo");
//        //邀请人
//        String referee = json.getString("referee");

        /*推荐码*/
        String invitationCode = json.getString("invitationCode");

        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        //用户名格式验证
        if (!ValidataUtil.isUserName(username))
            return RspUtil.rspError(responseParamsDto.FAIL_USERNAME_DATA_FORMAT_DESC);

        if (ValidataUtil.isNull(username))
            return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
        if (ValidataUtil.isNull(pwd))
            return RspUtil.rspError(responseParamsDto.USERPWD_NULL_DESC);

        //.判断助记词是否存在，在userMemo表里进行查找有无重复助记词，重复则重试
        UserMemo userMemo = new UserMemo();
        userMemo.setUserMemo(memo);
        userMemo = userMemoMapper.getUserMemoByCondition(userMemo);
        if (userMemo != null) {
            return RspUtil.rspError(responseParamsDto.EXPORT_MEMO_REPEAT);
        }

        //.判断注册用户是否存在
        User user = new User();
        user.setUsername(username);
        user = userMapper.getUserInfoByCondition(user);
        if (user != null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_EXIST_DESC);

        //（无推荐人）
        if (ValidataUtil.isNull(invitationCode)) {
            //.添加用户
            User userInsert = new User();
            userInsert.setUsername(username);
            userInsert.setPassword(pwd);
            userInsert.setTradePassword(pwd);
            userInsert.setState(1);
            userInsert.setToken("");
            userInsert.setInvitationCode(getInviteCode());
            userMapper.insertUserInfo(userInsert);
            //（有推荐人）
        } else if (!ValidataUtil.isNull(invitationCode)) {
            // 判断推荐用户是否存在
            User userRec = new User();
            //推荐码
            userRec.setInvitationCode(invitationCode);
            userRec = userMapper.getUserInfoByCondition(userRec);
            if (ValidataUtil.isNull(userRec)) {
                return RspUtil.rspError(responseParamsDto.INVITE_USER_IS_NOT_EXIST);
            }
            //.添加用户（有推荐人添加推荐关系）
            User userInsert = new User();
            userInsert.setUsername(username);
            userInsert.setPassword(pwd);
            userInsert.setTradePassword(pwd);
            userInsert.setState(1);
            userInsert.setToken("");
            userInsert.setInvitationCode(getInviteCode());
            userMapper.insertUserInfo(userInsert);

            UserRelation userRelation = new UserRelation();
            userRelation.setUserId(userInsert.getId());
            userRelation.setRecId(userRec.getId());
            UserRelation userRelation1 = new UserRelation();
            userRelation1.setUserId(userRec.getId());
            /*userRelation.setUserId(userInsert.getId());
            userRelation.setRecId(userRec.getId());*/
            UserRelation userRelation3 = userRelationMapper.getUserRelationByCondition(userRelation1);
            UserRelation userRelation2 = new UserRelation();
            userRelation2.setUserId(userInsert.getId());
            userRelation2.setRecId(userRec.getId());
            if (userRelation3 != null){
                userRelation2.setTreeTrade(userRelation3.getTreeTrade()+1);
                userRelation2.setPath(userRelation3.getPath()+","+userInsert.getId().toString());
            }else {
                userRelation2.setTreeTrade(2);
                userRelation2.setPath(userRec.getId()+","+userInsert.getId().toString());
            }
            userRelationMapper.insertUserRelation(userRelation2);

            //添加路径

            ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
            service.schedule(() -> {
                try {
                    bonusService.invitationBonus(userRelation.getUserId(), userRelation.getRecId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, TimeUnit.MILLISECONDS);
            service.shutdown();
        }

        //添加助记词
        User addMemoUser = new User();
        addMemoUser.setUsername(username);
        addMemoUser = userMapper.getUserInfoByCondition(addMemoUser);
        UserMemo addMemo = new UserMemo();
        addMemo.setUserId(Math.toIntExact(addMemoUser.getId()));
        addMemo.setUserMemo(memo);
        userMemoMapper.addUserMemo(addMemo);


        String token = ValidataUtil.generateUUID();
        User userUpdate = new User();
        userUpdate.setId(addMemoUser.getId());
        userUpdate.setToken(token);
        userUpdate.setTimeout(new Date());
        userMapper.updateUserInfo(userUpdate);


        //.拿到注册用户信息，返回登录信息给前端
        User addUserInfo = new User();
        addUserInfo.setUsername(username);
        addUserInfo = userMapper.getUserInfoByCondition(addUserInfo);

        UserWallet userWallet = new UserWallet();
        userWallet.setType(40); // PTN
        userWallet.setUserId(addUserInfo.getId());
        userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
        if (userWallet == null) {
            addUserInfo.setPtnaddress("");
        } else {
            addUserInfo.setPtnaddress(ValidataUtil.isNull(userWallet.getAddress()) ? ""
                    : userWallet.getAddress());
        }
        addUserInfo.setToken(token);
        addUserInfo.setPassword("");
        addUserInfo.setTradePassword("".

                equals(addUserInfo.getTradePassword()) ? "0" : "1");

        JSONObject json1 = JSONObject.parseObject(JSONFormat.getStr(addUserInfo).toString());
        return RspUtil.success(json1);
    }


//	/**
//	 * 旧用户注册
//	 *
//	 * @param data
//	 * @return
//	 * @throws Exception
//	 */
//	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
//	@Override
//	public BaseResponse addUser(String data) throws Exception {
//		JSONObject json = JSONObject.parseObject(data);
//		String username = json.getString("username");
//		String pwd = json.getString("pwd");
//		String code = json.getString("code");
//		String invitationCode = json.getString("invitationCode");
//		ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
//				.getString("language"));
//		if (ValidataUtil.isNull(username))
//			return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
//		if (ValidataUtil.isNull(pwd))
//			return RspUtil.rspError(responseParamsDto.USERPWD_NULL_DESC);
//		if (ValidataUtil.isNull(code))
//			return RspUtil.rspError(responseParamsDto.CODE_NULL_DESC);
//
//		//取有效时间内最后一条验证码进行对比
//		VerifyCodeRecord record = new VerifyCodeRecord();
//		if(responseParamsDto instanceof ResponseParamsDto.ResponseParamsKRDto){
//			record.setMobile("+82"+username);
//		}else{
//			record.setMobile(username);
//		}
//
//
//		record.setType(1);//type=1用户注册
//		record.setValidTime(5 * 60);//验证码5分钟内有效
//		record.setState(0);
//		VerifyCodeRecord codeRecord = verifyCodeRecordMapper.getValidVerifyCode4LastOne(record);
//		if (codeRecord == null || !code.equals(codeRecord.getVerifyCode()))
//			return RspUtil.rspError(responseParamsDto.CODE_FAIL_DESC);
//
//		User user = new User();
//		user.setUsername(username);
//		user = userMapper.getUserInfoByCondition(user);
//		if (user != null)
//			return RspUtil.rspError(responseParamsDto.ACCOUNT_EXIST_DESC);
//
//		User userInsert = new User();
//		userInsert.setUsername(username);
//		userInsert.setPassword(pwd);
//		userInsert.setTradePassword("");
//		userInsert.setState(1);
//		userInsert.setToken("");
//		userInsert.setInvitationCode(getInviteCode());
//		userMapper.insertUserInfo(userInsert);
//
//		if (!ValidataUtil.isNull(invitationCode)) {
//			User userRec = new User();
//			userRec.setInvitationCode(invitationCode);
//			userRec = userMapper.getUserInfoByCondition(userRec);
//			if (userRec != null) {
//				UserRelation userRelation = new UserRelation();
//				userRelation.setUserId(userInsert.getId());
//				userRelation.setRecId(userRec.getId());
//				userRelationMapper.insertUserRelation(userRelation);
//
//				ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
//				service.schedule(() -> {
//					try {
//						bonusService.invitationBonus(userRelation.getUserId(), userRelation.getRecId());
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}, 0, TimeUnit.MILLISECONDS);
//				service.shutdown();
//			}
//		}
//
//		//更新验证码状态为已使用
//		codeRecord.setState(1);
//		verifyCodeRecordMapper.update(codeRecord);
//
//		return RspUtil.success(200, responseParamsDto.SUCCESS);
//	}

    /**
     * 用户登陆
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse login(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String username = json.getString("username");
        String pwd = json.getString("pwd");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(username))
            return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
        if (ValidataUtil.isNull(pwd))
            return RspUtil.rspError(responseParamsDto.USERPWD_NULL_DESC);

        User user = new User();
        user.setUsername(username);
        user = userMapper.getUserInfoByCondition(user);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        if (!user.getPassword().equals(pwd))
            return RspUtil.rspError(responseParamsDto.USERPWD_FAIL_DESC);

        String token = ValidataUtil.generateUUID();
        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setToken(token);
        userUpdate.setTimeout(new Date());
        userMapper.updateUserInfo(userUpdate);


//        //  老用户登录时，检测用户在用户助记词表有无助记词，无助记词随机生成助记词【添加】到对应的用户助记词表中，
//        //根据助记词查到用户id
//        UserMemo userMemo = new UserMemo();
//        userMemo.setUserId(Math.toIntExact(user.getId()));
//        userMemo = userMemoMapper.getUserMemoByCondition(userMemo);
//        if (userMemo == null) {
//            // TODO 给用户添加助记词，返回助记词给前端
//            String memoData = MemoUtil.createMemo();
//            JSONObject json1 = JSONObject.parseObject(memoData);
//            String memo = json1.getString("memo");
//
//            //在userMemo表里进行查找有无重复助记词，重复则重新生成
//            UserMemo isExistUserMemo = new UserMemo();
//            //加密助记词
//            isExistUserMemo.setUserMemo(DESUtil.memoEncode(memo));
//            isExistUserMemo = userMemoMapper.getUserMemoByCondition(isExistUserMemo);
//            if (isExistUserMemo != null) {
//                return RspUtil.rspError("Mnemonic failed to get started, please try again!!");
//            }
//            Map map = new HashMap();
//            String[] split = memo.split(" ");
//            List<String> list = new ArrayList<>();
//            Collections.addAll(list, split);
//
//            map.put("memo", list);
//            map.put("token", userUpdate.getToken());
//            map.put("isExist", false);
//            return RspUtil.success(200, responseParamsDto.SUCCESS, map);
//        }


        UserWallet userWallet = new UserWallet();
        userWallet.setType(40); // PTN
        userWallet.setUserId(user.getId());
        userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
        if (userWallet == null) {
            user.setPtnaddress("");
        } else {
            user.setPtnaddress(ValidataUtil.isNull(userWallet.getAddress()) ? ""
                    : userWallet.getAddress());
        }
        user.setToken(token);
        user.setPassword("");
        user.setTradePassword("".equals(user.getTradePassword()) ? "0" : "1");

        JSONObject json1 = JSONObject.parseObject(JSONFormat.getStr(user).toString());
        json1.put("isExist", true);
        return RspUtil.success(json1);
    }

    /**
     * 用户注销
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse logout(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        User userLogout = new User();
        userLogout.setId(user.getId());
        userLogout.setToken("");
        userMapper.updateUserInfo(userLogout);
        return RspUtil.success(200, responseParamsDto.SUCCESS);
    }

//    /**
//     * 设置用户头像(旧)
//     *
//     * @param data
//     * @param photo
//     * @return
//     * @throws Exception
//     */
//    @Override
//    public BaseResponse setPhoto(String data, MultipartFile photo)
//            throws Exception {
//
//        JSONObject json = JSONObject.parseObject(data);
//        String token = json.getString("token");
//        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
//                .getString("language"));
//        if (ValidataUtil.isNull(token))
//            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
//
//        User user = userMapper.getUserInfoByToken(token);
//        if (user == null)
//            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
//
//        User userUpdate = new User();
//        userUpdate.setId(user.getId());
//        if (photo != null) {
//            String fileName = QiniuUploadUtil.createFileName("userHead");
//            try {
//                fileName = QiniuUploadUtil.upLoadImage(photo.getInputStream(),
//                        fileName);
//                userUpdate.setPhoto("http://opd74myxl.bkt.clouddn.com/"
//                        + fileName);
//            } catch (Exception e) {
//                return RspUtil.error();
//            }
//        }
//        userMapper.updateUserInfo(userUpdate);
//        return RspUtil.success(ValidataUtil.isNull(userUpdate.getPhoto()) ? ""
//                : userUpdate.getPhoto());
//    }

    /**
     * 设置用户头像
     *
     * @param data
     * @param photo
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse setPhoto(String data, MultipartFile photo)
            throws Exception {

        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        User userUpdate = new User();
        userUpdate.setId(user.getId());
        if (photo != null) {
            String urlPath = executeUpload(photo, responseParamsDto);
            userUpdate.setPhoto(urlPath);
        }
        userMapper.updateUserInfo(userUpdate);
        return RspUtil.success(ValidataUtil.isNull(userUpdate.getPhoto()) ? ""
                : userUpdate.getPhoto());
    }

    /**
     * 设置用户呢称
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse setNickName(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        String nickName = json.getString("nickName");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(nickName))
            return RspUtil.rspError(responseParamsDto.NICKNAME_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setNickname(nickName);
        userMapper.updateUserInfo(userUpdate);
        return RspUtil.success(ValidataUtil.isNull(nickName) ? "" : nickName);
    }

    /**
     * 设置交易密码
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Deprecated
    @Override
    public BaseResponse setDealPwd(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        String dealPwd = json.getString("dealPwd");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(dealPwd))
            return RspUtil.rspError(responseParamsDto.DEALPWD_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        if (!ValidataUtil.isNull(user.getTradePassword()))
            return RspUtil.rspError(responseParamsDto.DEALPWD_EXIST_DESC);

        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setTradePassword(dealPwd);
        userMapper.updateUserInfo(userUpdate);
        return RspUtil.success(200, responseParamsDto.SUCCESS);
    }

    /**
     * 修改交易密码（新）
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse editDealPwd(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        String memo = json.getString("memo");
        String dealPwd = json.getString("dealPwd");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(memo))
            return RspUtil.rspError(responseParamsDto.PARAMS_NULL_DESC);
        if (ValidataUtil.isNull(dealPwd))
            return RspUtil.rspError(responseParamsDto.DEALPWD_NULL_DESC);


        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        //助记词验证
        UserMemo userMemo = new UserMemo();
        userMemo.setUserId(Math.toIntExact(user.getId()));
        userMemo = userMemoMapper.getUserMemoByCondition(userMemo);
        if (!(userMemo.getUserMemo().equals(memo)))
            return RspUtil.rspError(responseParamsDto.CONTRAST_FAIL);

        User userUpdate = new User();
        userUpdate.setUsername(user.getUsername());
        userUpdate.setTradePassword(dealPwd);
        userMapper.updateUserInfo(userUpdate);

        return RspUtil.success(200, responseParamsDto.SUCCESS);
    }

//	/**
//	 * (原版)修改交易密码
//	 *
//	 * @param data
//	 * @return
//	 * @throws Exception
//	 */
//	@Override
//	public BaseResponse editDealPwd(String data) throws Exception {
//		JSONObject json = JSONObject.parseObject(data);
//		String token = json.getString("token");
//		String username = json.getString("username");
//		String dealPwd = json.getString("dealPwd");
//		String code = json.getString("code");
//		ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
//				.getString("language"));
//		if (ValidataUtil.isNull(token))
//			return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
//		if (ValidataUtil.isNull(username))
//			return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
//		if (ValidataUtil.isNull(dealPwd))
//			return RspUtil.rspError(responseParamsDto.DEALPWD_NULL_DESC);
//		if (ValidataUtil.isNull(code))
//			return RspUtil.rspError(responseParamsDto.CODE_NULL_DESC);
//
//		//取有效时间内最后一条验证码进行对比
//		VerifyCodeRecord record = new VerifyCodeRecord();
//		if(responseParamsDto instanceof ResponseParamsDto.ResponseParamsKRDto) {
//			record.setMobile("+82"+username);
//		}else {
//			record.setMobile(username);
//		}
//		record.setType(3);//type=3设置和修改交易密码
//		record.setValidTime(5 * 60);//验证码5分钟内有效
//		record.setState(0);
//		VerifyCodeRecord codeRecord = verifyCodeRecordMapper.getValidVerifyCode4LastOne(record);
//		if (codeRecord == null || !code.equals(codeRecord.getVerifyCode()))
//			return RspUtil.rspError(responseParamsDto.CODE_FAIL_DESC);
//
//		User user = userMapper.getUserInfoByToken(token);
//		if (user == null)
//			return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
//
//		User userUpdate = new User();
//		userUpdate.setUsername(username);
//		userUpdate.setTradePassword(dealPwd);
//		userMapper.updateUserInfo(userUpdate);
//
//		//更新验证码状态为已使用
//		codeRecord.setState(1);
//		verifyCodeRecordMapper.update(codeRecord);
//
//		return RspUtil.success(200, responseParamsDto.SUCCESS);
//	}

    /**
     * 找回交易密码
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Deprecated
    @Override
    public BaseResponse forgetDealPwd(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String username = json.getString("username");
        String dealPwd = json.getString("dealPwd");
        String code = json.getString("code");

        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(username))
            return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
        if (ValidataUtil.isNull(dealPwd))
            return RspUtil.rspError(responseParamsDto.DEALPWD_NULL_DESC);
        if (ValidataUtil.isNull(code))
            return RspUtil.rspError(responseParamsDto.CODE_NULL_DESC);

        String codeKey = "";
        if (responseParamsDto instanceof ResponseParamsDto.ResponseParamsKRDto) {
            codeKey = MapUtil.forgetDealPwd + "+82" + username;
            String getCode = (String) MapUtil.getCode(codeKey);
            if (!code.equals(getCode))
                return RspUtil.rspError(responseParamsDto.CODE_FAIL_DESC);
        } else {
            codeKey = MapUtil.forgetDealPwd + username;
            String getCode = (String) MapUtil.getCode(codeKey);
            if (!code.equals(getCode))
                return RspUtil.rspError(responseParamsDto.CODE_FAIL_DESC);
        }

        User user = new User();
        user.setUsername(username);
        user = userMapper.getUserInfoByCondition(user);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        User userUpdate = new User();
        userUpdate.setUsername(username);
        userUpdate.setTradePassword(dealPwd);
        userMapper.updateUserInfo(userUpdate);

        MapUtil.deleteCode(codeKey);

        return RspUtil.success(200, responseParamsDto.SUCCESS);
    }

    /**
     * 获取交易密码状态
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse getDealPwdState(String data) throws Exception {
        // 参数判断
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        if (ValidataUtil.isNull(user.getTradePassword()))
            return RspUtil.success(0);
        return RspUtil.success(1);
    }

    /**
     * 修改密码
     *
     * @param data
     * @throws Exception
     */
    @Deprecated
    @Override
    public BaseResponse editPwd(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        String newPwd = json.getString("newPwd");
        String oldPwd = json.getString("oldPwd");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(newPwd))
            return RspUtil.rspError(responseParamsDto.NEW_USERPWD_NULL_DESC);
        if (ValidataUtil.isNull(oldPwd))
            return RspUtil.rspError(responseParamsDto.OLD_USERPWD_NULL_DESC);
        if (newPwd.equals(oldPwd))
            return RspUtil.rspError(responseParamsDto.NEW_OLD_FAIL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        if (!user.getPassword().equals(oldPwd))
            return RspUtil.rspError(responseParamsDto.USERPWD_FAIL_DESC);

        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setPassword(newPwd);
        userMapper.updateUserInfo(userUpdate);

        return RspUtil.success(200, responseParamsDto.SUCCESS);
    }

    /**
     * 忘记密码
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse forgetPwd(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String username = json.getString("username");
        String pwd = json.getString("pwd");
        String code = json.getString("code");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(username))
            return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
        if (ValidataUtil.isNull(pwd))
            return RspUtil.rspError(responseParamsDto.USERPWD_NULL_DESC);
        if (ValidataUtil.isNull(code))
            return RspUtil.rspError(responseParamsDto.CODE_NULL_DESC);

        //取有效时间内最后一条验证码进行对比
        VerifyCodeRecord record = new VerifyCodeRecord();
        if (responseParamsDto instanceof ResponseParamsDto.ResponseParamsKRDto) {
            record.setMobile("+82" + username);
        } else {
            record.setMobile(username);
        }
        record.setType(2);//type=2忘记密码
        record.setValidTime(5 * 60);//验证码5分钟内有效
        record.setState(0);
        VerifyCodeRecord codeRecord = verifyCodeRecordMapper.getValidVerifyCode4LastOne(record);
        if (codeRecord == null || !code.equals(codeRecord.getVerifyCode()))
            return RspUtil.rspError(responseParamsDto.CODE_FAIL_DESC);

        User user = new User();
        user.setUsername(username);
        user = userMapper.getUserInfoByCondition(user);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        User userUpdate = new User();
        userUpdate.setUsername(username);
        userUpdate.setPassword(pwd);
        userMapper.updateUserInfo(userUpdate);

        //更新验证码状态为已使用
        codeRecord.setState(1);
        verifyCodeRecordMapper.update(codeRecord);

        return RspUtil.success(200, responseParamsDto.SUCCESS);
    }

    /**
     * 获取用户信息
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse getUserInfo(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        UserWallet userWallet = new UserWallet();
        userWallet.setType(40); // PTN
        userWallet.setUserId(user.getId());
        userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
        if (user.getPhoto()==null){
            user.setPhoto("");
        }
        if (userWallet == null) {
            user.setPtnaddress("");
        } else {
            user.setPtnaddress(ValidataUtil.isNull(userWallet.getAddress()) ? ""
                    : userWallet.getAddress());
        }
        user.setPassword("");
        user.setTradePassword("".equals(user.getTradePassword()) ? "0" : "1");

        return RspUtil.success(user);
    }

    /**
     * 导入钱包地址
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse importAddress(String data) throws Exception {
        BaseResponse<List<UserWallet>> response = new BaseResponse<>();
        JSONObject jsonData = JSONObject.parseObject(data);
        // 前端传的json
        String addressJson = jsonData.getString("addressJson");
        addressJson = UrlCodeUtil.decode(addressJson);
        // 助记词
        String memorandum = jsonData.getString("memorandum");
        // 用户id
        String userNo = jsonData.getString("userNo");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(jsonData
                .getString("language"));
        if (addressJson == null || ValidataUtil.isNull(addressJson)
                || ValidataUtil.isNull(memorandum)
                || ValidataUtil.isNull(userNo)) {
            response.setCode(100);
            response.setMessage(responseParamsDto.IMPORT_ADDRESS_LESS);
            return response;
        }
        memorandum = memorandum.replaceAll(",", " ");
        // 获取钱包的类型
        Coin pntCoin = new Coin();
        pntCoin.setCoinName("PTN");
        pntCoin = coinMapper.getCoinByCondition(pntCoin);

        // 判断用户是否已经有钱包地址
        UserWallet hasUserWallet = new UserWallet();
        hasUserWallet.setType(Integer.parseInt(pntCoin.getCoinNo().toString()));
        hasUserWallet.setUserId(Long.parseLong(userNo));
        hasUserWallet = userWalletMapper
                .getUserWalletByCondition(hasUserWallet);
        if (hasUserWallet != null) {
            response.setCode(100);
            response.setMessage(responseParamsDto.IMPORT_ADDRESS_HAS);
            return response;
        }
        Coin pntCnyCoin = new Coin();
        pntCnyCoin.setCoinName("PTNCNY");
        pntCnyCoin = coinMapper.getCoinByCondition(pntCnyCoin);

        // 获取钱包服务器信息
        Address coinAddress = new Address();
        coinAddress.setCoinNo(pntCoin.getCoinNo());
        coinAddress = addressMapper.getAddressByCondition(coinAddress);

        // 判断用户是否已存在地址
        if (coinAddress == null) {
            response.setCode(100);
            response.setMessage(responseParamsDto.IMPORT_ADDRESS_FAIL);
            return response;
        }
        String address = PNTCoinApi.getApi(coinAddress).importAddressJson(
                addressJson, memorandum);
        if (ValidataUtil.isNull(address)) {
            response.setCode(100);
            response.setMessage(responseParamsDto.IMPORT_ADDRESS_FAIL);
            return response;
        }
        UserWallet pntUserWallet = new UserWallet();
        UserWallet pntcnyUserWallet = new UserWallet();

        // 插入用户地址表
        List<UserWallet> wallets = new ArrayList<UserWallet>();

        pntUserWallet.setAddress(address);
        pntUserWallet.setUnbalance(BigDecimal.ZERO);
        pntUserWallet.setUserId(Long.parseLong(userNo));
        pntUserWallet.setBalance(BigDecimal.ZERO);
        pntUserWallet.setDate(new Date());
        pntUserWallet.setType(Integer.parseInt(pntCoin.getCoinNo().toString()));
        pntUserWallet.setState(1);
        pntUserWallet.setFlag((pntCoin.getUnlockState() == 1) + "");

        pntcnyUserWallet.setAddress(address);
        pntcnyUserWallet.setUnbalance(BigDecimal.ZERO);
        pntcnyUserWallet.setUserId(Long.parseLong(userNo));
        pntcnyUserWallet.setBalance(BigDecimal.ZERO);
        pntcnyUserWallet.setDate(new Date());
        pntcnyUserWallet.setType(Integer.parseInt(pntCnyCoin.getCoinNo()
                .toString()));
        pntcnyUserWallet.setState(1);
        pntcnyUserWallet.setFlag((pntCnyCoin.getUnlockState() == 1) + "");

        wallets.add(pntUserWallet);
        wallets.add(pntcnyUserWallet);
        List<UserWallet> delWallets = userWalletMapper
                .getUserWalletByAddress(address);
        // 给新用户复制地址
        for (int i = 0; delWallets != null && i < delWallets.size(); i++) {
            UserWallet delWall = delWallets.get(i);
            if (delWall != null) {
                if ("PTN".equalsIgnoreCase(delWall.getCoinName())) {
                    pntUserWallet.setBalance(delWall.getBalance());
                    pntUserWallet.setUnbalance(delWall.getUnbalance());
                    pntUserWallet.setMoneyRate(delWall.getMoneyRate());
                } else if ("PTNCNY".equalsIgnoreCase(delWall.getCoinName())) {
                    pntcnyUserWallet.setBalance(delWall.getBalance());
                    pntcnyUserWallet.setUnbalance(delWall.getUnbalance());
                    pntcnyUserWallet.setMoneyRate(delWall.getMoneyRate());
                }
            }
        }
        try {
            try {
                LockUtil.getUtil().lock("PTNAddress" + address);
            } catch (Exception e) {
                e.printStackTrace();
                response.setCode(100);
                response.setMessage(responseParamsDto.IMPORT_ADDRESS_FAIL);
                return response;
            }
            if (delWallets != null && delWallets.size() > 0) {
                // 删除前用户的地址
                for (UserWallet delWall : delWallets) {
                    userWalletMapper.delUserWalletById(delWall.getId());
                }
            }
            // 增加现用户地址
            userWalletMapper.insertUserWallets(wallets);
            response.setCode(200);
            response.setMessage(responseParamsDto.IMPORT_ADDRESS_SUCCESS);
            response.setData(wallets);
        } catch (Exception e) {
            e.printStackTrace();
            // 异常，回滚
            throw e;
        } finally {
            LockUtil.getUtil().unlock("PTNAddress" + address);
        }
        return response;
    }

    /**
     * 备份钱包地址
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse exportAddress(String data) throws Exception {
        BaseResponse<String> response = new BaseResponse<>();
        JSONObject dataJson = JSONObject.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
                .getString("language"));
        String userNo = dataJson.getString("userNo");
        // 用户注册时的 助记词 （加密传过来）
        String memorandum = dataJson.getString("memorandum");
        if (ValidataUtil.isNull(userNo)) {// 缺少参数
            response.setCode(100);
            response.setMessage(responseParamsDto.LACK_PARAMETERS);
            return response;
        }

        //助记词验证
        UserMemo userMemo = new UserMemo();
        userMemo.setUserId(Integer.valueOf(userNo));
        userMemo = userMemoMapper.getUserMemoByCondition(userMemo);
        if (!(userMemo.getUserMemo().equals(memorandum)))
            return RspUtil.rspError(responseParamsDto.CONTRAST_FAIL);


        // 获取MOC信息
        Coin coin = new Coin();
        coin.setCoinName("MOC");
        coin = coinMapper.getCoinByCondition(coin);
        if (coin == null) {
            response.setData("");
            response.setCode(100);
            response.setMessage(responseParamsDto.EXPORT_ADDRESS_FAIL);
            return response;
        }

        // 服务器信息
        Address address = new Address();
        address.setCoinNo(coin.getCoinNo());
        address = addressMapper.getAddressByCondition(address);
        if (address == null) {
            response.setData("");
            response.setCode(100);
            response.setMessage(responseParamsDto.EXPORT_ADDRESS_FAIL);
            return response;
        }

        UserWallet userWallet = new UserWallet();
        userWallet.setUserId(Long.parseLong(userNo));
        userWallet.setType(Integer.parseInt(coin.getCoinNo().toString()));
        userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
        // 获取用户钱包地址
        if (userWallet == null) {
            response.setData("");
            response.setCode(100);
            response.setMessage(responseParamsDto.EXPORT_FAIL_NULL_ADDRESS);
            return response;
        }

        MemoAddress addMemoAddress = new MemoAddress();
        addMemoAddress.setAddress(userWallet.getAddress());
        addMemoAddress.setMemo(memorandum);
        memoAddressMapper.addMemoAddress(addMemoAddress);
        // 通过api获取备份json
        String json = PNTCoinApi.getApi(address).exportAddressJson(
                userWallet.getAddress(), memorandum);
        if (ValidataUtil.isNull(json)) {
            throw new Exception("备份失败");
        }
        response.setData(json);
        response.setCode(200);
        response.setMessage(responseParamsDto.EXPORT_ADDRESS_SUCCESS);
        return response;
    }
//	/**
//	 * 备份钱包地址
//	 *
//	 * @param data
//	 * @return
//	 * @throws Exception
//	 */
//	@Override
//	public BaseResponse exportAddress(String data) throws Exception {
//		BaseResponse<String> response = new BaseResponse<>();
//		JSONObject dataJson = JSONObject.parseObject(data);
//		ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
//				.getString("language"));
//		String userNo = dataJson.getString("userNo");
//		// 助记词
//		String memorandum = dataJson.getString("memorandum");
//		if (ValidataUtil.isNull(userNo)) {// 缺少参数
//			response.setCode(100);
//			response.setMessage(responseParamsDto.LACK_PARAMETERS);
//			return response;
//		}
//		memorandum = memorandum.replaceAll(",", " ");
//		// 根据助记词判断是否重复
//		MemoAddress selectMemoAddress = new MemoAddress();
//		selectMemoAddress.setMemo(memorandum);
//		selectMemoAddress = memoAddressMapper
//				.getMemoAddressByCondition(selectMemoAddress);
//		if (selectMemoAddress != null) {
//			response.setData("");
//			response.setCode(100);
//			response.setMessage(responseParamsDto.EXPORT_MEMO_REPEAT);
//			return response;
//		}
//
//		// 获取MOC信息
//		Coin coin = new Coin();
//		coin.setCoinName("MOC");
////		coin.setCoinName("PTN");
//		coin = coinMapper.getCoinByCondition(coin);
//		if (coin == null) {
//			response.setData("");
//			response.setCode(100);
//			response.setMessage(responseParamsDto.EXPORT_ADDRESS_FAIL);
//			return response;
//		}
//
//		// 服务器信息
//		Address address = new Address();
//		address.setCoinNo(coin.getCoinNo());
//		address = addressMapper.getAddressByCondition(address);
//		if (address == null) {
//			response.setData("");
//			response.setCode(100);
//			response.setMessage(responseParamsDto.EXPORT_ADDRESS_FAIL);
//			return response;
//		}
//
//		UserWallet userWallet = new UserWallet();
//		userWallet.setUserId(Long.parseLong(userNo));
//		userWallet.setType(Integer.parseInt(coin.getCoinNo().toString()));
//		userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
//		// 获取用户钱包地址
//		if (userWallet == null) {
//			response.setData("");
//			response.setCode(100);
//			response.setMessage(responseParamsDto.EXPORT_FAIL_NULL_ADDRESS);
//			return response;
//		}
//
//		selectMemoAddress = new MemoAddress();
//		selectMemoAddress.setAddress(userWallet.getAddress());
//		selectMemoAddress = memoAddressMapper
//				.getMemoAddressByCondition(selectMemoAddress);
//		if (selectMemoAddress != null) {
//			response.setData("");
//			response.setCode(100);
//			response.setMessage(responseParamsDto.EXPORT_ADDRESS_REPEAT);
//			return response;
//		}
//		MemoAddress addMemoAddress = new MemoAddress();
//		addMemoAddress.setAddress(userWallet.getAddress());
//		addMemoAddress.setMemo(memorandum);
//		memoAddressMapper.addMemoAddress(addMemoAddress);
//		// 通过api获取备份json
//		String json = PNTCoinApi.getApi(address).exportAddressJson(
//				userWallet.getAddress(), memorandum);
//		if (ValidataUtil.isNull(json)) {
//			throw new Exception("备份失败");
//		}
//		response.setData(json);
//		response.setCode(200);
//		response.setMessage(responseParamsDto.EXPORT_ADDRESS_SUCCESS);
//		return response;
//	}

    /**
     * 绑定微信号
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse bindWeChat(String data) throws Exception {
        JSONObject dataJson = JSONObject.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
                .getString("language"));
        String token = dataJson.getString("token");
        String weChat = dataJson.getString("weChat");

        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(weChat))
            return RspUtil.rspError(responseParamsDto.FAIL_WEIXIN_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setWeChat(weChat);
        userMapper.updateUserInfo(userUpdate);

        return RspUtil.success();
    }

    /**
     * 绑定支付宝
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse bindApay(String data) throws Exception {
        JSONObject dataJson = JSONObject.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
                .getString("language"));
        String token = dataJson.getString("token");
        String alipay = dataJson.getString("alipay");

        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(alipay))
            return RspUtil.rspError(responseParamsDto.FAIL_ALIPAY_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setApay(alipay);
        userMapper.updateUserInfo(userUpdate);

        return RspUtil.success();
    }

    /**
     * 邀请页面-个人信息
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse getUserByInvitation(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String userNo = json.getString("userNo");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(userNo))
            return RspUtil.rspError(responseParamsDto.ID_NULL_DESC);


        User user = new User();
        user.setId(Long.valueOf(userNo));
        user = userMapper.getUserInfoByCondition(user);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        if (ValidataUtil.isNull(user.getInvitationCode())) {
            User userUpdate = new User();
            userUpdate.setInvitationCode(getInviteCode());
            userUpdate.setId(user.getId());
            userMapper.updateUserInfo(userUpdate);

            user = new User();
            user.setId(Long.valueOf(userNo));
            user = userMapper.getUserInfoByCondition(user);
        }

        Map resultMap = new HashMap();
        resultMap.put("username", user.getUsername());
        resultMap.put("nickname", ValidataUtil.isNull(user.getNickname()) ? ""
                : user.getNickname());
        resultMap.put("userphoto", ValidataUtil.isNull(user.getPhoto()) ? ""
                : user.getPhoto());
        resultMap.put("invitationCode", user.getInvitationCode());

        BonusDetail bonusDetail = new BonusDetail();
        bonusDetail.setRecId(Long.valueOf(userNo));
        bonusDetail.setState(1);
        bonusDetail = bonusDetailMapper
                .getBonusPriceAndCountByRecNo(bonusDetail);
        resultMap.put("sumPrice", bonusDetail.getSumPrice());
        resultMap.put("sumNumber", bonusDetail.getSumNumber());



        if(ValidataUtil.isNull(json.getString("type"))){
            resultMap.put("downLoadUrl","");
        }else{
            /**增加下载地址*/
            Version version=versionMapper.getVersionInfo(Integer.valueOf(json.getString("type")));
            resultMap.put("downLoadUrl", version.getVersionUrl());
        }


        List<Push> push = pushMapper.getPushs();
        if (push.size() >= 1) {
            resultMap.put("title",
                    ValidataUtil.isNull(push.get(0).getTitle()) ? "" : push
                            .get(0).getTitle());
            resultMap.put("detail", ValidataUtil
                    .isNull(push.get(0).getDetail()) ? "" : push.get(0)
                    .getDetail());
            resultMap.put(
                    "logo",
                    ValidataUtil.isNull(push.get(0).getLogo()) ? "" : push.get(
                            0).getLogo());
            resultMap.put("url", ValidataUtil.isNull(push.get(0).getUrl()) ? ""
                    : push.get(0).getUrl());
        } else {
            resultMap.put("title", "");
            resultMap.put("detail", "");
            resultMap.put("logo", "");
            resultMap.put("url", "");
        }

        return RspUtil.success(resultMap);
    }

    public String getInviteCode() throws Exception {
        String InviteCode = "";
        for (int i = 0; i < 6; i++) {
            InviteCode = InviteCode + (char) (Math.random() * 26 + 'A');
        }

        User user = new User();
        user.setInvitationCode(InviteCode);
        user = userMapper.getUserInfoByCondition(user);

        if (user != null) {
            getInviteCode();
        }

        return InviteCode;
    }


    /**
     * 用户是否存在
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse userIsExistence(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String userName = json.getString("userName");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (userName == null || "".equals(userName)) {
            return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
        }
        User user = new User();
        user.setUsername(userName);
        user = userMapper.getUserInfoByCondition(user);
        if (user != null) {
            // 用户存在
            return RspUtil.error(responseParamsDto.USER_EXIST_SUCCESS,
                    RspUtil.USER_EXIST_FAIL);
        }
        return RspUtil.success(responseParamsDto.USER_EXIST_FAIL);
    }


    /**
     * 助记词登录
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse memoLogin(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String memo = json.getString("memo");
        /**
         * 返回接口语言选择	 默认 0（中文）	1（英文）
         * @param language
         */
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));

        if (ValidataUtil.isNull(memo))
            return RspUtil.rspError(responseParamsDto.PARAMS_NULL_DESC);

        //根据助记词查到用户id
        UserMemo userMemo = new UserMemo();
        userMemo.setUserMemo(memo);
        userMemo = userMemoMapper.getUserMemoByCondition(userMemo);
        if (userMemo == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        User user = new User();
        user.setId(Long.valueOf(userMemo.getUserId()));
        user = userMapper.getUserInfoByCondition(user);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        String token = ValidataUtil.generateUUID();
        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setToken(token);
        userUpdate.setTimeout(new Date());
        userMapper.updateUserInfo(userUpdate);

        UserWallet userWallet = new UserWallet();
        userWallet.setType(40); // PTN
        userWallet.setUserId(user.getId());
        userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
        if (userWallet == null) {
            user.setPtnaddress("");
        } else {
            user.setPtnaddress(ValidataUtil.isNull(userWallet.getAddress()) ? ""
                    : userWallet.getAddress());
        }
        user.setToken(token);
        user.setPassword("");
        user.setTradePassword("".equals(user.getTradePassword()) ? "0" : "1");
        return RspUtil.success(JSONFormat.getStr(user));
    }


    /**
     * 单个文件服务器上传
     *
     * @param index
     * @param file
     * @param responseParamsDto
     * @param idCard
     */
    @Value("${headPic.path}")
    public String headPic;
    @Value("${mapping.pic.path}")
    public String mappingPicPath;

    private String executeUpload(MultipartFile file, ResponseParamsDto responseParamsDto) {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        if (!".jpg".equals(suffix) && !".png".equals(suffix)) {
            throw new RuntimeException(responseParamsDto.ID_PIC_INVALID);
        }

        String filename = UUID.randomUUID() + suffix;
        String filePath = headPic + filename;//本地存放路径
        String urlPath = mappingPicPath + filename;//url访问路径

        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            log.error("保存文件到本地失败", e);
            throw new RuntimeException(responseParamsDto.FAIL);
        }
        return urlPath;

    }

    @Override
    public boolean isTradePaswd(Long userId, String tradePasswd) {
        User user = userMapper.getUserInfoById(userId);
        if (user != null) {
            String userPasswd = user.getTradePassword();
            if (StringUtils.isNotBlank(userPasswd)) {
                if (userPasswd.equals(tradePasswd)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public BaseResponse bindPhone(String data) throws Exception {
        JSONObject dataJson = JSONObject.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
                .getString("language"));
        String token = dataJson.getString("token");
        //手机号
        String phone = dataJson.getString("phone");
        String code = dataJson.getString("code");
        //区号
        String areaCode = dataJson.getString("areaCode");
        areaCode=areaCode.replaceAll(" ","+");
        if(areaCode.indexOf("+")<0){
            return  RspUtil.rspError("areaCode error!");
        }

        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(phone))
            return RspUtil.rspError(responseParamsDto.FAIL_PHONE_DATA_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        if (ValidataUtil.isNull(code))
            return RspUtil.rspError(responseParamsDto.CODE_NULL_DESC);

        //取有效时间内最后一条验证码进行对比
        VerifyCodeRecord record = new VerifyCodeRecord();
        //区号+手机号
        record.setMobile(areaCode + phone);
        record.setType(0);//type=0用户绑定手机号
        record.setValidTime(5 * 60);//验证码5分钟内有效
        record.setState(0);
        VerifyCodeRecord codeRecord = verifyCodeRecordMapper.getValidVerifyCode4LastOne(record);
        if (codeRecord == null || !code.equals(codeRecord.getVerifyCode()))
            return RspUtil.rspError(responseParamsDto.CODE_FAIL_DESC);


        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setPhone(phone);
        userUpdate.setAreaCode(areaCode);
        userMapper.updateUserInfo(userUpdate);
		//更新验证码状态为已使用
        record.setState(1);
		verifyCodeRecordMapper.update(record);

        return RspUtil.success();
    }

    @Override
    public BaseResponse bindImToken(String data) throws Exception {
        JSONObject dataJson = JSONObject.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
                .getString("language"));
        String token = dataJson.getString("token");
        String imToken = dataJson.getString("imToken");

        //地址筛选判断
        if(ValidataUtil.isUsdtAddress(imToken)==false){
            return RspUtil.rspError(responseParamsDto.USDT_FORMAT_ERROR);
        }

        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(imToken))
            return RspUtil.rspError(responseParamsDto.FAIL_IMTOKEN_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);



        User userUpdate = new User();
        userUpdate.setId(user.getId());
        userUpdate.setImToken(imToken);
        userMapper.updateUserInfo(userUpdate);

        return RspUtil.success();
    }


    /**
     * 是否存在手机号
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse isExistPhone(String data) throws Exception {
        JSONObject dataJson = JSONObject.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
                .getString("language"));
        String token = dataJson.getString("token");

        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        JSONObject json1 =new JSONObject();
        if(user.getAreaCode().equals("") ||  user.getPhone().equals("") ){
            json1.put("isExist", false);
            json1.put("areaCode", user.getAreaCode());
            json1.put("phone", user.getPhone());
        }else{
            json1.put("isExist", true);
            json1.put("areaCode", user.getAreaCode());
            json1.put("phone", user.getPhone());
        }
        return RspUtil.success(json1);
    }

    @Override
    public BaseResponse isExistImToken(String data) throws Exception {
        JSONObject dataJson = JSONObject.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson
                .getString("language"));
        String token = dataJson.getString("token");

        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        JSONObject json1 =new JSONObject();
        if(user.getImToken().equals("") ){
            json1.put("isExist", false);
            json1.put("imToken", user.getImToken());
        }else{
            json1.put("isExist", true);
            json1.put("imToken", user.getImToken());
        }
        return RspUtil.success(json1);
    }
}
