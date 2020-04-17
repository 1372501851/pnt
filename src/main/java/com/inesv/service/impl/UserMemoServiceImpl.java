package com.inesv.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.inesv.annotation.UnLogin;
import com.inesv.mapper.UserMapper;
import com.inesv.mapper.UserMemoMapper;
import com.inesv.mapper.UserWalletMapper;
import com.inesv.model.User;
import com.inesv.model.UserMemo;
import com.inesv.model.UserWallet;
import com.inesv.service.UserMemoService;
import com.inesv.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class UserMemoServiceImpl implements UserMemoService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserMemoMapper userMemoMapper;
    @Resource
    private UserWalletMapper userWalletMapper;

    /***
     * 获取登录助记词(打乱)
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse getLoginMemo(@RequestParam("data") String data) throws Exception {
        Map map = new HashMap(5);
        JSONObject json = JSONObject.parseObject(data);
        //用户名
        String username = json.getString("username");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));

        if (username == null || "".equals(username))
            return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
        User user = new User();
        user.setUsername(username);
        user = userMapper.getUserInfoByCondition(user);
        if (user == null) {
            // 用户名不存在
            return RspUtil.error(responseParamsDto.USER_EXIST_FAIL,
                    RspUtil.USER_EXIST_FAIL);
        }

        //在userMemo表里进行查找有无重复助记词，重复则重试
        UserMemo userMemo = new UserMemo();
        userMemo.setUserId(Math.toIntExact(user.getId()));
        userMemo = userMemoMapper.getUserMemoByCondition(userMemo);
        if (userMemo == null) {
            map.put("isExist", false);
            return RspUtil.success(200, responseParamsDto.GET_MEMO_FAIL, map);
        }

        String memo = userMemo.getUserMemo();
        //解密
        memo = DESUtil.memoDecode(memo);
        String[] split = memo.split(" ");
        List<String> list = new ArrayList<>();
        Collections.addAll(list, split);
        Collections.shuffle(list);
        map.put("isExist", true);
        map.put("userMemo", list);
        return RspUtil.success(map);

    }

    /***
     * 获取随机助记词
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse getMemo(@RequestParam("data") String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        //用户名
        String username = json.getString("username");
        //推荐人
        String invitationCode = json.getString("invitationCode");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));

        if (username == null || "".equals(username))
            return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
        //用户名格式验证
        if (!ValidataUtil.isUserName(username))
            return RspUtil.rspError(responseParamsDto.FAIL_USERNAME_DATA_FORMAT_DESC);


        User user = new User();
        user.setUsername(username);
        user = userMapper.getUserInfoByCondition(user);
        if (user != null) {
            // 注册用户名存在
            return RspUtil.error(responseParamsDto.USER_EXIST_SUCCESS,
                    RspUtil.USER_EXIST_FAIL);
        }

        if (!ValidataUtil.isNull(invitationCode)) {
            User refereeUser = new User();
            refereeUser.setInvitationCode(invitationCode);
            refereeUser = userMapper.getUserInfoByCondition(refereeUser);
            if (refereeUser == null) {
                // 推荐用户不存在
                return RspUtil.error(responseParamsDto.INVITE_USER_IS_NOT_EXIST,
                        RspUtil.USER_EXIST_FAIL);
            }
        }

        String memoData = MemoUtil.createMemo();
        JSONObject json1 = JSONObject.parseObject(memoData);
        String memo = json1.getString("memo");
        Map map = new HashMap();
        String[] split = memo.split(" ");
        List<String> list = new ArrayList<>();
        Collections.addAll(list, split);
        map.put("memo", list);
        return RspUtil.success(map);
    }

    /***
     * 判断用户不存在助记词之后调用此接口添加助记词
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse addMemo(@RequestParam("data") String data) throws Exception {

        JSONObject json = JSONObject.parseObject(data);
        /**用户名*/
        String token = json.getString("token");
        String memo = json.getString("memo");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));

        //根据用户名查到用户id
        User user = new User();
        user.setToken(token);
        user = userMapper.getUserInfoByCondition(user);
        if (user != null) {
            UserMemo userMemo = new UserMemo();
            userMemo.setUserId(Math.toIntExact(user.getId()));
            userMemo = userMemoMapper.getUserMemoByCondition(userMemo);
            if (userMemo == null) {
                //添加助记词
                UserMemo um = new UserMemo();
                um.setUserId(Math.toIntExact(user.getId()));
                um.setUserMemo(memo);
                int num = userMemoMapper.addUserMemo(um);
                if (num > 0) {
                    log.info("token: {} ,用户: {} 添加助记词成功！！", user.getToken(), user.getUsername());

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
                    return RspUtil.success(200, responseParamsDto.SUCCESS, JSONFormat.getStr(user));
                }
            } else {
                return RspUtil.rspError("用户已添加助记词！！");
            }
        } else {
            return RspUtil.rspError(responseParamsDto.USER_EXIST_FAIL);
        }
        return RspUtil.success(200, responseParamsDto.SUCCESS);
    }


    /***
     * 输入交易密码备份助记词
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse backupMemo(@RequestParam("data") String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        /**token*/
        String token = json.getString("token");
        /**交易密码*/
        String dealPwd = json.getString("dealPwd");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        //根据toekn查到用户信息 对对比交易密码
        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        if (ValidataUtil.isNull(user.getTradePassword()))
            return RspUtil.error(responseParamsDto.DEAL_PWD_SETTING_NULL, RspUtil.DEALPWD_NULL);

        Map map = new HashMap();
        if (dealPwd.equalsIgnoreCase(user.getTradePassword())) {
            //拿到用户id,根据id查到助记词
            UserMemo userMemo = new UserMemo();
            userMemo.setUserId(Math.toIntExact(user.getId()));
            userMemo = userMemoMapper.getUserMemoByCondition(userMemo);
            if (userMemo != null) {
                //拿到加密后的助记词
                String memo = userMemo.getUserMemo();
                //解密返回给前端
                memo = DESUtil.memoDecode(memo);
                String[] split = memo.split(" ");
                List<String> list = new ArrayList<>();
                Collections.addAll(list, split);
                map.put("memo", list);
                map.put("userId", userMemo.getUserId());
                return RspUtil.success(200, responseParamsDto.SUCCESS, map);
            }
        } else {
            return RspUtil.rspError(responseParamsDto.DEALPWD_FAIL_DESC);
        }
        return RspUtil.success();
    }
}
