package com.inesv.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.BonusService;
import com.inesv.service.TradeInfoService;
import com.inesv.service.UserWalletService;
import com.inesv.util.*;
import com.inesv.util.CoinAPI.BitcoinAPI;
import com.inesv.util.CoinAPI.EthcoinAPI;
import com.inesv.util.CoinAPI.PNTCoinApi;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class UserWalletServiceImpl implements UserWalletService {

    private static final Logger log = LoggerFactory
            .getLogger(UserWalletServiceImpl.class);

    @Resource
    private UserWalletMapper userWalletMapper;
    @Resource
    private CoinMapper coinMapper;
    @Resource
    private ParamsMapper paramsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private AddressMapper addressMapper;
    @Resource
    private TradeInfoMapper tradeInfoMapper;
    @Resource
    private BonusService bonusService;
    @Resource
    private TradeInfoService tradeInfoService;
    @Resource
    private BonusDetailMapper bonusDetailMapper;
    @Autowired
    private PayMapper payMapper;

    String USD_TO_CNY_RATE = "USD_TO_CNY_RATE";// 美元转人民币
    String BTC_USDT = "btc_usdt";// 比特币行情
    String ETH_USDT = "eth_usdt";// 以太坊行情
    String LTC_USDT = "ltc_usdt";// 莱特币行情
    String PTN_USDT = "moc_usdt";// 光子链行情
    String PTNCNY = "MOCCNY";// PTNCNY行情
    String ETC = "etc_usdt";
    String HSR = "hsr_usdt";
    String QTUM = "qtum_usdt";
    String ENSA = "ensa_usdt";

    /**
     * 获取总资产，币值，行情
     */
    @Override
    public BaseResponse getWalletBalance(String data) {
        Map<String, Object> map = new HashMap<String, Object>();
        Long langueCode = (long) 0;

        log.info("进入首页"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("进入首页参数:" + data);

        if (ValidataUtil.isNull(data)) {
            log.info("参数data为空");
            return RspUtil.rspError("params: data is null");
        }

        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            String token = jsonObject.getString("token");

            User user = userMapper.getUserInfoByToken(token);
            Long langue = jsonObject.getLong("language");
            if (langue == null)
                langue = (long) 0;
            langueCode = langue;

            if (user == null) {
                log.info("用户为空");
                return RspUtil.rspError("user is null");
            }
            Long userId = user.getId();

            //TODO 进入首页自动创建moc钱包地址
            String userAddress;
            //查询用户是否创建摩云地址
            UserWallet uWallet = new UserWallet();
            uWallet.setType(40);
            uWallet.setUserId(userId);
            uWallet = userWalletMapper.getUserWalletByCondition(uWallet);
            if (uWallet==null) {
                // 获取钱包服务器信息
                Address address = new Address();
                address.setCoinNo(Long.valueOf(40));
                address = addressMapper.getAddressByCondition(address);
                // 获取新的钱包地址
                userAddress = PNTCoinApi.getApi(address).createAddress();
                if ("".equalsIgnoreCase(userAddress))
                    throw new Exception("创建钱包失败！");
                UserWallet mocWallet = new UserWallet();
                mocWallet.setUserId(user.getId());
                mocWallet.setAddress(userAddress);
                mocWallet.setType(40);
                mocWallet.setBalance(new BigDecimal("0"));
                mocWallet.setUnbalance(BigDecimal.ZERO);
                mocWallet.setState(1);
                mocWallet.setFlag("true");
                int count = userWalletMapper.add(mocWallet);
                if (count > 0) {
                    //在积分表中添加用户的积分信息
                    payMapper.insertPointInfo(userAddress,user.getId());
                    rewardPTN(userAddress, address, user.getId());
                } else {
                    throw new Exception("添加失败");
                }
            }


            List<UserWallet> userWallets = userWalletMapper
                    .getuserWallet(userId);
            // 新建包含币值与行情的钱包记录集合
            List<UserWallet> resultWallets = new ArrayList<UserWallet>();
            // 总资产
            BigDecimal totalMoney = new BigDecimal(0);

            /**
             * 遍历用户钱包记录，将接口读取到的币值存储到UserWallet中,获得相关转换行情，统计总资产
             */
            if (!userWallets.isEmpty()) {
                BlockChainUtil blockChainUtil = new BlockChainUtil();
                for (UserWallet userWallet : userWallets) {
                    BigDecimal balance = new BigDecimal("0.00000");// 币值

                    //获得对应货币的服务器地址
					/*Address addressConditions = new Address();
					Integer addressCoinNo = userWallet.getType();
					addressConditions.setCoinNo(addressCoinNo.longValue());
					Address serverAddress = addressMapper.getAddressByCondition(addressConditions);
					if (serverAddress != null) {
						//为兼容之前的PTN节点故障，PTN和PTNCNY资产查询钱包服务器，TODO 后期需要修改为全部从数据库取余额
						if (addressCoinNo == 40 || addressCoinNo == 50) {
							PNTCoinApi pntCoinApi = PNTCoinApi.getApi(serverAddress);
							try {
								String balanceString = pntCoinApi.getBalance(userWallet.getAddress(), addressCoinNo == 40 ? "ptn" : "ptncny");
								if (StringUtils.isNotBlank(balanceString)) {
									balance = new BigDecimal(balanceString).setScale(8, BigDecimal.ROUND_DOWN);
								}

								userWallet.setBalance(balance.subtract(userWallet.getUnbalance()));
								userWallet.setTotalBalance(balance);
							} catch (Exception e) {
								log.error("PTN或PTNCNY查询钱包服务器资产失败", e);
							}
						}else{
							userWallet.setTotalBalance(userWallet.getBalance().add(userWallet.getUnbalance()));
						}
					}*/
                    userWallet.setTotalBalance(userWallet.getBalance().add(userWallet.getUnbalance()));

                    /**
                     * 根据代币编号获得代码名称
                     */
                    Integer coinNo = userWallet.getType();
                    Coin coin = coinMapper.getCoinByCoinNo(coinNo.longValue());
                    String coinName = coin.getCoinName();
                    String coinImg = coin.getCoinImg();
                    if (coinImg==null){
                        coinImg="";
                    }
                    userWallet.setCoinImg(coinImg);
                    userWallet.setCoinType(coin.getCoinBlock());
                    // 币名存入钱包
                    userWallet.setCoinName(coinName);
                    /**
                     * 根据代码名称，获取行情
                     */
                    BigDecimal moneyRate = new BigDecimal(0);

//                    if ("BTC".equalsIgnoreCase(coinName)) {
//                        moneyRate = getCoinRate(BTC_USDT);
//                    }
//                    if ("ETH".equalsIgnoreCase(coinName)) {
//                        moneyRate = getCoinRate(ETH_USDT);
//                    }
//                    if ("LTC".equalsIgnoreCase(coinName)) {
//                        moneyRate = getCoinRate(LTC_USDT);
//                    }
//                    if ("MOC".equalsIgnoreCase(coinName)) {
//                        moneyRate = getCoinRate(PTN_USDT);
//                    }
//                    if ("MOCCNY".equalsIgnoreCase(coinName)) {
//                        moneyRate = getCoinRate(PTNCNY);
//                    }
//                    if ("ETC".equalsIgnoreCase(coinName)) {
//                        moneyRate = getCoinRate(ETC);
//                    }
//                    if ("HSR".equalsIgnoreCase(coinName)) {
//                        moneyRate = getCoinRate(HSR);
//                    }
//                    if ("QTUM".equalsIgnoreCase(coinName)) {
//                        moneyRate = getCoinRate(QTUM);
//                    }
//                    if ("ENSA".equalsIgnoreCase(coinName)) {
//                        moneyRate = getCoinRate(ENSA);
//                    }
                    if ("MOCCNY".equalsIgnoreCase(coinName)) {
                        moneyRate = getCoinRate(PTNCNY);
                    }else{
                        moneyRate = getCoinRate(coinName.toLowerCase()+"_usdt");
                    }
                    // 如果为中文，则转换为人民币
                    if (langue == 0) {
                        // PTN、PTNCNY的是人民币，无需转换,只转换BTC、ETH、LTC
                        if (!"PTN".equalsIgnoreCase(coinName)) {
                            String financeRateString = "6.365";
                            Params rateParam = paramsMapper.getParams("usd_cny_rate");
                            if (rateParam != null) {
                                financeRateString = rateParam.getParamValue();
                            }
                            BigDecimal rate = new BigDecimal(financeRateString);
                            // 如果获得汇率为0，默认使用6.315
                            if (rate == null || rate.compareTo(new BigDecimal(0)) == 0) {
                                rate = new BigDecimal("6.315");
                            }
                            moneyRate = moneyRate.multiply(rate);
                        }
                    }
                    // 如果为英文，则转换为美元
                    if (langue != 0) {
                        // BTC、ETH、LTC的是美元，无需转换，只转换PTN、PTNCNY
//                        if ("PTNCNY".equalsIgnoreCase(coinName)) {
                        if (PTNCNY.equalsIgnoreCase(coinName)) {
                            String financeRateString = "6.315";
                            Params rateParam = paramsMapper.getParams("usd_cny_rate");
                            if (rateParam != null) {
                                financeRateString = rateParam.getParamValue();
                            }
                            BigDecimal one = new BigDecimal(1);
                            BigDecimal rate = new BigDecimal(financeRateString);
                            // 如果获得汇率为0，默认使用6.315
                            if (rate == null || rate.compareTo(new BigDecimal(0)) == 0) {
                                rate = new BigDecimal("6.315");
                                // 人民币转美元汇率
                                BigDecimal changeRate = one.divide(rate, 4, BigDecimal.ROUND_HALF_EVEN);
                                moneyRate = moneyRate.multiply(changeRate);
                            } else {
                                // 人民币转美元汇率
                                BigDecimal changeRate = one.divide(rate, 4, BigDecimal.ROUND_HALF_EVEN);
                                moneyRate = moneyRate.multiply(changeRate);
                            }
                        }
                    }

                    // 存入行情到钱包
                    userWallet.setMoneyRate(moneyRate);
                    BigDecimal money = userWallet.getTotalBalance().multiply(moneyRate);
                    //加上位数限制
                    totalMoney = totalMoney.add(money).setScale(5,BigDecimal.ROUND_HALF_EVEN);
                    resultWallets.add(userWallet);
                }
            }
            map.put("langue", langue);
            map.put("totalMoney", totalMoney);
            map.put("userWallets", resultWallets);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String exception = sw.toString();
            log.error(exception);
            if (langueCode == 0) {
                return RspUtil.error();
            }
            return RspUtil.error("error", 500);
        }
        BaseResponse baseResponse = RspUtil.success(map);
        log.info("返回首页参数：" + JSON.toJSONString(baseResponse));
        return baseResponse;
    }

    @Override
    public BaseResponse getStates(String data) {

        log.info("进入钱包代币状态列表"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("进入钱包代币状态列表参数:" + data);

        Map<String, Object> map = new HashMap<String, Object>();
        Long langueCode = (long) 0;

        if (ValidataUtil.isNull(data)) {
            log.info("参数data为空");
            return RspUtil.rspError("params: data is null");
        }

        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            String token = jsonObject.getString("token");
            User user = userMapper.getUserInfoByToken(token);
            Long langue = jsonObject.getLong("language");
            if (langue == null)
                langue = (long) 0;
            langueCode = langue;
            if (user == null) {
                log.info("用户为空");
                return RspUtil.rspError("user is null");
            }
            Long userId = user.getId();

            Coin coinConditions = new Coin();
            coinConditions.setState(1);

            List<Coin> coins = coinMapper.getCoinByConditions(coinConditions);

            List<UserWallet> resultWallets = new ArrayList<UserWallet>();
            if (!coins.isEmpty()) {
                for (Coin coin : coins) {

                    UserWallet userWallet = null;
                    /**
                     * 根据代币编号获得代码名称,币名存入钱包
                     */
                    Long coinNo = coin.getCoinNo();
                    UserWallet conditions = new UserWallet();
                    conditions.setUserId(userId);
                    conditions.setType(coinNo.intValue());
                    String coinName = coin.getCoinName();
                    String coinImg = coin.getCoinImg();
                    userWallet = userWalletMapper
                            .getUserWalletByCondition(conditions);
                    if (userWallet == null) {
                        userWallet = new UserWallet();
                        userWallet.setState(0);
                        userWallet.setType(coinNo.intValue());
                    }
                    userWallet.setCoinName(coinName);
                    userWallet.setCoinImg(coinImg);
                    resultWallets.add(userWallet);
                }
            }
            map.put("userWallets", resultWallets);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String exception = sw.toString();
            log.error(exception);
            if (langueCode == 0) {
                return RspUtil.error();
            }
            return RspUtil.error("error", 500);
        }
        BaseResponse baseResponse = RspUtil.success(map);
        log.info("返回代币状态集合参数：" + JSON.toJSONString(baseResponse));
        return baseResponse;
    }

    @Override
    public BaseResponse editState(String data) {

        log.info("修改钱包代币状态"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("修改钱包代币状态参数:" + data);

        Map<String, Object> map = new HashMap<String, Object>();
        Long langueCode = (long) 0;

        if (ValidataUtil.isNull(data)) {
            log.info("参数data为空");
            return RspUtil.rspError("params: data is null");
        }

        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            String token = jsonObject.getString("token");
            User user = userMapper.getUserInfoByToken(token);
            Long langue = jsonObject.getLong("language");
            Integer walletState = jsonObject.getInteger("walletState");
            Integer walletType = jsonObject.getInteger("walletType");
            if (langue == null)
                langue = (long) 0;
            langueCode = langue;
            if (user == null) {
                log.info("用户为空");
                String msg = langueCode == 1 ? "user is null" : "用户为空";
                return RspUtil.rspError(msg);
            }
            if (walletState == null) {
                log.info("修改状态为空");
                String msg = langueCode == 1 ? "no state" : "无此状态";
                return RspUtil.rspError(msg);
            }

            if (walletType == null) {
                log.info("币编号为空");
                String msg = langueCode == 1 ? "no this coinType" : "无此货币";
                return RspUtil.rspError(msg);
            }
            Long userId = user.getId();
            UserWallet editUserWallet = new UserWallet();
            editUserWallet.setUserId(userId);
            editUserWallet.setState(walletState);
            editUserWallet.setType(walletType);

            // 如果是开启（walletState=1）,判断该类型币的钱包是否存在，不存在则开通
            checkWallet(walletState, walletType, userId, token);

            Long resultCode = userWalletMapper.editState(editUserWallet);
            map.put("resultCode", resultCode);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String exception = sw.toString();
            log.error(exception);
            if (langueCode == 0) {
                return RspUtil.error();
            }
            return RspUtil.error("error", 500);
        }
        BaseResponse baseResponse = RspUtil.success(map);
        log.info("返回参数：" + JSON.toJSONString(baseResponse));
        return baseResponse;
    }

    /**
     * 新增用户钱包
     */
    public BaseResponse addWallet(String data) {
        BaseResponse<UserWallet> response = new BaseResponse<>();
        try {

            JSONObject dataJson = JSONObject.parseObject(data);
            String coinNo = dataJson.getString("coinNo");
            if (coinNo == null || "".equals(coinNo)) {
                coinNo = dataJson.getString("walletType");
            }
            // String userNo = dataJson.getString("userId");
            User user = userMapper.getUserInfoByToken(dataJson
                    .getString("token"));
            // 获取钱包服务器信息
            Address address = new Address();
            address.setCoinNo(Long.parseLong(coinNo));
            address = addressMapper.getAddressByCondition(address);
            // 获取货币类型信息
            Coin coin = new Coin();
            coin.setCoinNo(Long.parseLong(coinNo));
            coin = coinMapper.getCoinByCondition(coin);

            // 用户新增的用户钱包地址
            String userAddress = null;
            // 用户钱包地址
            UserWallet userWallet = new UserWallet();
            userWallet.setUserId(user.getId());
            userWallet.setType(Integer.valueOf(coin.getCoinNo().toString()));
            UserWallet userWalletSelect = userWalletMapper
                    .getUserWalletByCondition(userWallet);

            List<UserWallet> userWallets = new ArrayList<>();
            if (address == null
                    || coin == null
                    || (userWalletSelect != null && !ValidataUtil
                    .isNull(userWalletSelect.getAddress())))

                return RspUtil.rspError("创建钱包失败！");
            if (coin.getCoinName().equalsIgnoreCase("ETH")
                    || coin.getCoinName().equalsIgnoreCase("ETC")
                    || coin.getCoinName().equalsIgnoreCase("ENSA")) {
                EthcoinAPI eth = new EthcoinAPI(address.getAddress(),
                        address.getPort(), address.getName(),
                        address.getPassword(), address.getLockPassword());
                userAddress = eth.newAccount(address.getLockPassword());
                if (ValidataUtil.isNull(userAddress)) {
                    throw new Exception("创建钱包失败！");
                }

                //ENSA依赖ETH因此有ENSA肯定有ETH
                String coinName = coin.getCoinName();
                if ("ENSA".equalsIgnoreCase(coinName)) {
                    //获取ETH币种信息
                    Coin c = new Coin();
                    c.setCoinName("ETH");
                    c = coinMapper.getCoinByCondition(c);
                    //获取ETH钱包记录
                    UserWallet uWallet = new UserWallet();
                    uWallet.setUserId(user.getId());
                    uWallet.setType(c.getCoinNo().intValue());
                    UserWallet userWalletETH = userWalletMapper.getUserWalletByCondition(uWallet);

                    if (userWalletETH == null) {//ETH不存在则ENSA和ETH使用同一个新增的钱包地址
                        uWallet.setAddress(userAddress);
                        uWallet.setBalance(new BigDecimal("0"));
                        uWallet.setUnbalance(BigDecimal.ZERO);
                        uWallet.setState(1);
                        userWallets.add(uWallet);
                    } else {//ETH存在则ENSA使用ETH之前创建的钱包地址
                        userAddress = userWalletETH.getAddress();
                    }
                }
            }
            if (coin.getCoinName().equalsIgnoreCase("BTC")
                    || coin.getCoinName().equalsIgnoreCase("LTC")
                    || coin.getCoinName().equalsIgnoreCase("HSR")
                    || coin.getCoinName().equalsIgnoreCase("QTUM")) {
                BitcoinAPI btc = new BitcoinAPI(address.getAddress(),
                        address.getPort(), address.getName(),
                        address.getPassword(), address.getLockPassword());
                userAddress = btc.getnewaddress(user.getUsername());
                if (ValidataUtil.isNull(userAddress))
                    throw new Exception("创建钱包失败！");
            }
            if (coin.getCoinName().equalsIgnoreCase("MOC")) {
                // 获取新的钱包地址
                userAddress = PNTCoinApi.getApi(address).createAddress();
                if (ValidataUtil.isNull(userAddress))
                    throw new Exception("创建钱包失败！");
            }

            // todo 摩云代币
            //根据币种编号查看是否是moc代币
            if(coin.getApiType().equalsIgnoreCase("ptn_api")){
                if (! coin.getCoinName().equalsIgnoreCase("MOC")) {
                    // 获取用户主币moc的钱包地址
                    UserWallet userMainWallet = new UserWallet();
                    userMainWallet.setUserId(user.getId());
                    userMainWallet.setType(40);
                    UserWallet mocUserWallet = userWalletMapper .getUserWalletByCondition(userMainWallet);
                    //如果主币不存在，则创建失败
                    if(ValidataUtil.isNull(mocUserWallet)){
                        throw new Exception("用户主币不存在！");
                    }
                    userAddress = mocUserWallet.getAddress();
                    if (ValidataUtil.isNull(userAddress))
                        throw new Exception("创建钱包失败！");
                }
            }



            userWallet.setAddress(userAddress);
            userWallet.setBalance(new BigDecimal("0"));
            userWallet.setUnbalance(BigDecimal.ZERO);
            userWallet.setState(1);
            userWallets.add(userWallet);
            // 获取是否开启了冻结
            String flag = coin.getState() == 1 ? "true" : "false";
            for (UserWallet uw : userWallets) {
                uw.setFlag(flag);
            }
            int count = userWalletMapper.insertUserWallets(userWallets);
            if (count > 0) {
                if (coin.getCoinName().equalsIgnoreCase("MOC")) {
                    // 创建钱包地址进行奖励光子
                    rewardPTN(userAddress, address, user.getId());
                }
            } else {
                throw new Exception("添加失败");
            }
            response.setCode(200);
            response.setData(userWallet);
            response.setMessage("新增钱包地址成功");
        } catch (Exception e) {
            log.error(e.toString());
            //打印错误堆栈
            StackTraceElement stackTrace[] = e.getStackTrace();
            if (stackTrace != null) {
                for (StackTraceElement element : stackTrace) {
                    log.error(element.toString());
                }
            }
            response.setCode(100);
            response.setMessage("新增钱包地址失败!");
        }
        return response;
    }

    protected void rewardPTN(String userAddress, Address address, Long userNo) {
        try {
            // 思路：获取是否开启奖励与奖励金额以及中间账号地址参数然后进行转账相应数量的ptn
            // 获取相关参数
            Params startReward = paramsMapper.getParams("start_reward");
            Params rewardNumber = paramsMapper.getParams("reward_number");

            Params centerAddress = paramsMapper
                    .getParams("PTN_Transfer_Station");
            if (ValidataUtil.isNull(startReward.getParamValue())
                    || ValidataUtil.isNull(rewardNumber.getParamValue())
                    || ValidataUtil.isNull(centerAddress.getParamValue())
                    || ValidataUtil.isNull(userAddress)) {
                // 有空的数据就返回
                return;
            }

            UserWallet userWallet = new UserWallet();
            userWallet.setType(40);
            userWallet.setAddress(userAddress);
            userWallet = userWalletMapper.getUserWalletByCondition(userWallet);

            if (Integer.parseInt(startReward.getParamValue()) == 0) {
                //增加奖励记录，放到邀请奖励表中
                Long detailNo = bonusService.insertBonusDetail("register", 0L, userNo, 40, Double.parseDouble(rewardNumber.getParamValue()), 0, "注册奖励", null);

                //修改标记1开始
                //更新奖励记录
				/*BonusDetail bonusDetail = new BonusDetail();
				bonusDetail.setId(detailNo);
				bonusDetail.setState(1);
				int count = bonusDetailMapper.updateBonusDetail(bonusDetail);
				if(count > 0 ){
					//新增交易记录并增加用户资产
					String result = "PTN";//当成转账的hash用
					tradeInfoService.addTradeInfoAndAddUserWallet(rewardNumber.getParamValue(), "0", userAddress, "注册奖励", result, userWallet);
				}*/
                //修改标记1结束

                //修改标记1开始
                // 开启奖励
                // 金额进行保留六位小数
                BigDecimal decimal = new BigDecimal(rewardNumber.getParamValue());
                decimal = decimal.setScale(0, BigDecimal.ROUND_DOWN);
                PNTCoinApi api = PNTCoinApi.getApi(address);

                String result = api.sendTransaction(centerAddress.getParamValue(), userAddress, decimal.toString(), "", "创建钱包奖励moc", "0.001", "moc");
                log.info("创建账号，奖励用户：{}，{}：个光子币，转账结果：{}", userAddress, decimal, result);
                if (!ValidataUtil.isNull(result)) {
                    String hash = MD5Util.GetMD5Code("MOC" + result);
                    TradeInfo tradeInfo = new TradeInfo();
                    tradeInfo.setHash(hash);
                    List<TradeInfo> tradeInfos = tradeInfoMapper.getTradeInfoByConditions(tradeInfo);
                    if (tradeInfos.size() == 0) {
                        //更新奖励记录
                        BonusDetail bonusDetail = new BonusDetail();
                        bonusDetail.setId(detailNo);
                        bonusDetail.setState(1);
                        int count = bonusDetailMapper.updateBonusDetail(bonusDetail);
                        if (count > 0) {
                            //新增交易记录并增加用户资产
                            tradeInfoService.addTradeInfoAndAddUserWallet(String.valueOf(decimal), "0", userAddress, "注册奖励", result, userWallet);
                        }
                    }
                }
                //修改标记1结束
            }
        } catch (Exception e) {
            log.error("奖励光子币失败");
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 钱包代币详情
     */
    @Override
    public BaseResponse getCoinDetail(String data) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> wallet = new HashMap<String, Object>();
        Long langueCode = (long) 0;

        log.info("获取代币详情请求"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取代币详情入参:" + data);

        if (ValidataUtil.isNull(data)) {
            log.info("参数data为空");
            return RspUtil.rspError("params: data is null");
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            String token = jsonObject.getString("token");
            User user = userMapper.getUserInfoByToken(token);
            Long langue = jsonObject.getLong("language");
            Integer type = jsonObject.getInteger("type");
            if (langue == null)
                langue = (long) 0;
            langueCode = langue;
            if (user == null) {
                log.info("用户为空");
                return RspUtil.rspError("user is null");
            }
            if (type == null) {
                log.info("代币编号为空");
                return RspUtil.rspError("type is null");
            }

            Long userId = user.getId();
            UserWallet conditions = new UserWallet();
            conditions.setUserId(userId);
            conditions.setType(type);
            conditions.setState(1);
            UserWallet userWallet = userWalletMapper
                    .getUserWalletByCondition(conditions);
            // 资产
            BigDecimal money = new BigDecimal(0);
            /**
             * 将接口读取到的币值存储到UserWallet中,获得相关转换行情，统计资产
             */
            if (userWallet != null) {
                BigDecimal balance = new BigDecimal(0);// 币值

                //获得对应货币的服务器地址
				/*Address addressConditions = new Address();
				Integer addressCoinNo = userWallet.getType();
				addressConditions.setCoinNo(addressCoinNo.longValue());
				Address serverAddress = addressMapper.getAddressByCondition(addressConditions);
				if (serverAddress != null) {
					//为兼容之前的PTN节点故障，PTN和PTNCNY资产查询钱包服务器，TODO 后期需要修改为全部从数据库取余额
					if (addressCoinNo == 40 || addressCoinNo == 50) {
						PNTCoinApi pntCoinApi = PNTCoinApi.getApi(serverAddress);
						try {
							String balanceString = pntCoinApi.getBalance(userWallet.getAddress(), addressCoinNo == 40 ? "ptn" : "ptncny");
							if (StringUtils.isNotBlank(balanceString)) {
								balance = new BigDecimal(balanceString).setScale(8, BigDecimal.ROUND_DOWN);
							}

							userWallet.setBalance(balance.subtract(userWallet.getUnbalance()));
							userWallet.setTotalBalance(balance);
						} catch (Exception e) {
							log.error("PTN或PTNCNY查询钱包服务器资产失败", e);
						}
					}else{
						userWallet.setTotalBalance(userWallet.getBalance().add(userWallet.getUnbalance()));
					}
				}*/
                userWallet.setTotalBalance(userWallet.getBalance().add(userWallet.getUnbalance()));

                /**
                 * 根据代币编号获得代码名称
                 */
                Integer coinNo = userWallet.getType();
                Coin coin = coinMapper.getCoinByCoinNo((long) coinNo);
                String coinName = coin.getCoinName();
                String coinImg = coin.getCoinImg();
                userWallet.setCoinImg(coinImg);
                // 币名存入钱包
                userWallet.setCoinName(coinName);
                /**
                 * 根据代码名称，获取行情
                 */
                BigDecimal moneyRate = new BigDecimal(0);
                if ("BTC".equalsIgnoreCase(coinName)) {
                    moneyRate = getCoinRate(BTC_USDT);
                }
                if ("ETH".equalsIgnoreCase(coinName)) {
                    moneyRate = getCoinRate(ETH_USDT);
                }
                if ("LTC".equalsIgnoreCase(coinName)) {
                    moneyRate = getCoinRate(LTC_USDT);
                }
                if ("PTN".equalsIgnoreCase(coinName)) {
                    moneyRate = getCoinRate(PTN_USDT);
                }
                if ("PTNCNY".equalsIgnoreCase(coinName)) {
                    moneyRate = getCoinRate(PTNCNY);
                }
                if ("ETC".equalsIgnoreCase(coinName)) {
                    moneyRate = getCoinRate(ETC);
                }
                if ("HSR".equalsIgnoreCase(coinName)) {
                    moneyRate = getCoinRate(HSR);
                }
                if ("QTUM".equalsIgnoreCase(coinName)) {
                    moneyRate = getCoinRate(QTUM);
                }
                if ("ENSA".equalsIgnoreCase(coinName)) {
                    moneyRate = getCoinRate(ENSA);
                }

                // 如果为中文，则转换为人民币
                if (langue == 0) {
                    // PTN、PTNCNY的是人民币，无需转换,只转换BTC、ETH、LTC
                    if ("BTC".equalsIgnoreCase(coinName)
                            || "ETH".equalsIgnoreCase(coinName)
                            || "LTC".equalsIgnoreCase(coinName)
                            || "ETC".equalsIgnoreCase(coinName)
                            || "HSR".equalsIgnoreCase(coinName)
                            || "QTUM".equalsIgnoreCase(coinName)
                            || "ENSA".equalsIgnoreCase(coinName)
                            || "PTN".equalsIgnoreCase(coinName)) {
                        String financeRateString = "6.365";
                        Params rateParam = paramsMapper.getParams("usd_cny_rate");
                        if (rateParam != null) {
                            financeRateString = rateParam.getParamValue();
                        }
                        BigDecimal rate = new BigDecimal(financeRateString);
                        // 如果获得汇率为0，默认使用6.315
                        if (rate == null || rate.compareTo(new BigDecimal(0)) == 0) {
                            rate = new BigDecimal("6.315");
                        }
                        moneyRate = moneyRate.multiply(rate);
                    }
                }
                // 如果为英文，则转换为美元
                if (langue == 1) {
                    // BTC、ETH、LTC的是美元，无需转换，只转换PTN、PTNCNY
                    if ("PTNCNY".equalsIgnoreCase(coinName)) {
                        String financeRateString = "6.315";
                        Params rateParam = paramsMapper.getParams("usd_cny_rate");
                        if (rateParam != null) {
                            financeRateString = rateParam.getParamValue();
                        }
                        BigDecimal one = new BigDecimal(1);
                        BigDecimal rate = new BigDecimal(financeRateString);
                        // 如果获得汇率为0，默认使用6.315
                        if (rate == null || rate.compareTo(new BigDecimal(0)) == 0) {
                            rate = new BigDecimal("6.315");
                            // 人民币转美元汇率
                            BigDecimal changeRate = one.divide(rate, 4, BigDecimal.ROUND_HALF_EVEN);
                            moneyRate = moneyRate.multiply(changeRate);
                        } else {
                            // 人民币转美元汇率
                            BigDecimal changeRate = one.divide(rate, 4, BigDecimal.ROUND_HALF_EVEN);
                            moneyRate = moneyRate.multiply(changeRate);
                        }
                    }
                }

                // 存入行情到钱包
                userWallet.setMoneyRate(moneyRate);
                money = userWallet.getTotalBalance().multiply(moneyRate);

                wallet.put("id", userWallet.getId());
                wallet.put("type", userWallet.getType());

                // 总资产
                wallet.put("balance", userWallet.getTotalBalance().setScale(6, BigDecimal.ROUND_DOWN).toPlainString());
                // 冻结资产
                wallet.put(
                        "unableBalance",
                        userWallet.getUnbalance()
                                .setScale(6, BigDecimal.ROUND_DOWN)
                                .toPlainString());
                // 可用资产
                wallet.put(
                        "enableBalance",
                        userWallet.getBalance()
                                .setScale(6, BigDecimal.ROUND_DOWN)
                                .toPlainString());
                wallet.put("moneyRate", userWallet.getMoneyRate());
                wallet.put("coinName", userWallet.getCoinName());
                wallet.put("coinImg", userWallet.getCoinImg());
                wallet.put("address", userWallet.getAddress());
            }

            map.put("langue", langue);
            map.put("Money", money);
            map.put("userWallet", wallet);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String exception = sw.toString();
            log.error(exception);
            if (langueCode == 0) {
                return RspUtil.error();
            }
            return RspUtil.error("error", 500);
        }
        BaseResponse baseResponse = RspUtil.success(map);
        log.info("代币详情请求出参：" + JSON.toJSONString(baseResponse));
        return baseResponse;
    }

    /*
     * // @Override public BaseResponse openWallet(String data) { JSONObject
     * json = JSON.parseObject(data); ResponseParamsDto responseParamsDto =
     * LanguageUtil.proving(json .getString("language")); String token =
     * json.getString("token"); if (ValidataUtil.isNull(token)) return
     * RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC); User checkUser =
     * userMapper.getUserInfoByToken(token); if (checkUser == null) return
     * RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC); Address address =
     * addressMapper .queryAddressInfo(json.getLong("coinNo")); Coin coin =
     * coinMapper.queryCoinByCoinNo(json.getLong("coinNo")); User user = new
     * User(); user.setId(json.getLong("userId")); user =
     * userMapper.getUserInfoByCondition(user); Map<String, Object> params = new
     * HashMap<String, Object>(); params.put("coinNo", json.getLong("coinNo"));
     * params.put("userId", json.getLong("userId")); int walletCount =
     * userWalletMapper.userWalletCount(params); // 判断该币种是能够启用 if
     * (coin.getState() == 0) return
     * RspUtil.rspError(responseParamsDto.ADDRESS_OFF); // 判断用户是否存在该类型钱包 if
     * (walletCount == 0) { // 没有则新增一个 UserWallet userWallet = new UserWallet();
     * userWallet.setUserId(user.getId());
     * userWallet.setType(Integer.valueOf(coin.getCoinNo().toString()));
     * UserWallet userWalletSelect = userWalletMapper
     * .getUserWalletByCondition(userWallet); String userAddress = null; if
     * (coin.getCoinName().equalsIgnoreCase("ETH")) { EthcoinAPI eth = new
     * EthcoinAPI(address.getAddress(), address.getPort(), address.getName(),
     * address.getPassword(), address.getLockPassword()); userAddress =
     * eth.newAccount(address.getLockPassword()); if
     * (ValidataUtil.isNull(userAddress)) return
     * RspUtil.rspError(responseParamsDto.WALLET_ADD_ERROR); } if
     * (coin.getCoinName().equalsIgnoreCase("BTC") ||
     * coin.getCoinName().equalsIgnoreCase("LTC")) { BitcoinAPI btc = new
     * BitcoinAPI(address.getAddress(), address.getPort(), address.getName(),
     * address.getPassword(), address.getLockPassword()); userAddress =
     * btc.getnewaddress(user.getUsername()); if
     * (ValidataUtil.isNull(userAddress)) return
     * RspUtil.rspError(responseParamsDto.WALLET_ADD_ERROR); } if
     * (coin.getCoinName().equalsIgnoreCase("PTN") ||
     * coin.getCoinName().equalsIgnoreCase("PTNCNY")) { // 获取新的钱包地址 userAddress
     * = PNTCoinApi.getApi(address).createAddress(); if
     * (ValidataUtil.isNull(userAddress)) return
     * RspUtil.rspError(responseParamsDto.WALLET_ADD_ERROR); // 创建钱包地址进行奖励光子
     * rewardPTN(userAddress, address); } userWallet.setAddress(userAddress);
     * userWallet.setBalance(new BigDecimal("0"));
     * userWallet.setUnbalance(BigDecimal.ZERO); // userWallet
     * userWallet.setState(1); if (userWalletMapper.add(userWallet) == 1) {
     * return RspUtil.success(responseParamsDto.WALLET_ADD_SUCCESS); } else {
     * return RspUtil.rspError(responseParamsDto.WALLET_ADD_ERROR); } } else {
     * // 有则修改状态 UserWallet userWallet =
     * userWalletMapper.queryByUserState(params); if (userWallet.getState() ==
     * 1) { userWallet.setState(0); } else if (userWallet.getState() == 0) {
     * userWallet.setState(1); } if
     * (userWalletMapper.updateThisWallet(userWallet) == 1) { return
     * RspUtil.success(responseParamsDto.WALLET_ADD_SUCCESS); } else { return
     * RspUtil.rspError(responseParamsDto.WALLET_ADD_ERROR); } } }
     */

    @Override
    public BaseResponse assetsList(String data) {
        JSONObject json = JSON.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        String token = json.getString("token");
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        Map<String, Object> params = new HashMap<String, Object>();
        List<Coin> coins = coinMapper.getCoinByConditions();
        params.put("userId", json.getLong("userId"));
        for (Coin coin : coins) {
            params.put("coinNo", coin.getCoinNo());
            UserWallet userWallet = userWalletMapper.queryByUserState(params);
            if (userWallet != null) {
                coin.setWalletState(userWallet.getState());
            } else {
                coin.setWalletState(0);
            }

        }
        return RspUtil.success(coins);
    }

    /**
     * 如果是开启（walletState=1）,判断该类型币的钱包是否存在，不存在则开通
     *
     * @throws Exception
     */
    void checkWallet(Integer state, Integer type, Long userId, String token)
            throws Exception {
        if (state == 1) {
            UserWallet conditions = new UserWallet();
            conditions.setUserId(userId);
            conditions.setType(type);
            UserWallet checkWallet = userWalletMapper
                    .getUserWalletByCondition(conditions);
            if (checkWallet == null) {
                // addWallet((long) type, userId);
                JSONObject job = new JSONObject();
                job.put("coinNo", type);
                job.put("token", token);
                addWallet(job.toJSONString());
            }
        }
    }

    /**
     * 新增錢包
     *
     * @param coinNo
     * @param userNo
     * @return
     * @throws Exception
     */
    /*
     * int toAddWallet(Long coinNo, Long userNo) throws Exception { // 获取钱包服务器信息
     * Address address = new Address(); address.setCoinNo(coinNo); address =
     * addressMapper.getAddressByCondition(address); // 获取货币类型信息 Coin coin = new
     * Coin(); coin.setCoinNo(coinNo); coin =
     * coinMapper.getCoinByCondition(coin); // 获取用户信息 User user = new User();
     * user.setId(userNo); user = userMapper.getUserInfoByCondition(user); //
     * 用户新增的用户钱包地址 String userAddress = null; // 用户钱包地址 UserWallet userWallet =
     * new UserWallet(); userWallet.setUserId(user.getId());
     * userWallet.setType(Integer.valueOf(coin.getCoinNo().toString()));
     * UserWallet userWalletSelect = userWalletMapper
     * .getUserWalletByCondition(userWallet);
     *
     * List<UserWallet> userWallets = new ArrayList<>(); if (address == null ||
     * coin == null || (userWalletSelect != null && !ValidataUtil
     * .isNull(userWalletSelect.getAddress()))) return 0; if
     * (coin.getCoinName().equalsIgnoreCase("ETH") ||
     * coin.getCoinName().equalsIgnoreCase("ETC")) { EthcoinAPI eth = new
     * EthcoinAPI(address.getAddress(), address.getPort(), address.getName(),
     * address.getPassword(), address.getLockPassword()); userAddress =
     * eth.newAccount(address.getLockPassword()); if
     * (ValidataUtil.isNull(userAddress)) throw new Exception("创建钱包失败！"); } if
     * (coin.getCoinName().equalsIgnoreCase("BTC") ||
     * coin.getCoinName().equalsIgnoreCase("LTC") ||
     * coin.getCoinName().equalsIgnoreCase("HSR") ||
     * coin.getCoinName().equalsIgnoreCase("QTUM")) { BitcoinAPI btc = new
     * BitcoinAPI(address.getAddress(), address.getPort(), address.getName(),
     * address.getPassword(), address.getLockPassword()); userAddress =
     * btc.getnewaddress(user.getUsername()); if
     * (ValidataUtil.isNull(userAddress)) throw new Exception("创建钱包失败！"); } if
     * (coin.getCoinName().equalsIgnoreCase("PTN") ||
     * coin.getCoinName().equalsIgnoreCase("PTNCNY")) { // 获取新的钱包地址 userAddress
     * = PNTCoinApi.getApi(address).createAddress(); if
     * (ValidataUtil.isNull(userAddress)) throw new Exception("创建钱包失败！");
     *
     * // 同时添加PNT和PNTCNY UserWallet otherWallet = new UserWallet(); Coin
     * otherCoin = new Coin(); otherCoin
     * .setCoinName(coin.getCoinName().equalsIgnoreCase("PTN") ? "PTNCNY" :
     * "PTN"); // 获取到另外一个币种信息 otherCoin =
     * coinMapper.getCoinByCondition(otherCoin);
     * otherWallet.setUserId(user.getId());
     * otherWallet.setType(Integer.valueOf(otherCoin.getCoinNo() .toString()));
     * UserWallet otherWallet_ = userWalletMapper
     * .getUserWalletByCondition(otherWallet); if (otherWallet_ == null) { //
     * 同时添加另外一个地址 otherWallet.setAddress(userAddress);
     * otherWallet.setBalance(new BigDecimal("0"));
     * otherWallet.setUnbalance(BigDecimal.ZERO); otherWallet.setState(1);
     * userWallets.add(otherWallet); } } String flag = coin.getState() == 1 ?
     * "true" : "false"; for (UserWallet uw : userWallets) { uw.setFlag(flag); }
     * userWallet.setAddress(userAddress); userWallet.setBalance(new
     * BigDecimal("0")); userWallet.setUnbalance(BigDecimal.ZERO);
     * userWallet.setState(1); userWallets.add(userWallet); return
     * userWalletMapper.insertUserWallets(userWallets); }
     */
    @Override
    public boolean rechargeAddress(long userWalletId, TradeInfo tradeInfo,
                                   String coinName, Address address) throws Exception {
        // TODO
        UserWallet userWallet = new UserWallet();
        userWallet.setId(userWalletId);
        userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
        BigDecimal tradeNum = tradeInfo.getTradeNum();
        tradeInfo.setUnTradeNum(BigDecimal.ZERO);
        userWallet.setBalance(userWallet.getBalance().add(tradeNum));
        int count = userWalletMapper.updateBalance(userWallet);
        if (count > 0) {
            int tradeCount = tradeInfoMapper.insertTradeInfo(tradeInfo);
            if (tradeCount > 0) {
                return true;
            } else {
                throw new Exception("充值记录日志异常");
            }
        }
        // if ("ETH".equalsIgnoreCase(coinName)
        // || "ETC".equalsIgnoreCase(coinName)) {
        // // 转到中间
        // String centerAddress = "";
        // if ("ETH".equalsIgnoreCase(coinName)) {
        // Params para = paramsMapper.getParams("ETH_Transfer_Station");
        // centerAddress = para.getParamValue();
        // } else {
        // Params para = paramsMapper.getParams("ETC_Transfer_Station");
        // centerAddress = para.getParamValue();
        // }
        // if (centerAddress == null || "".equals(centerAddress)) {
        // throw new Exception("中间账号异常");
        // }
        // // 进行转账
        // EthcoinAPI ethcoinAPI = new EthcoinAPI(address.getAddress(),
        // address.getPort(), address.getName(),
        // address.getPassword(), address.getLockPassword());
        // String result1 = ethcoinAPI
        // .sendTransaction(
        // userWallet.getAddress(),
        // centerAddress,
        // "0x"
        // + new BigInteger(money.toString(), 10)
        // .toString(16));
        // if (result1 == null || "".equals(result1)) {
        // throw new Exception("转入中间账号异常");
        // }
        // }
        return false;
    }

    @Override
    public BaseResponse getCoinWallet(String data) {
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        String coinNo = json.getString("coinNo");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(coinNo))
            return RspUtil.rspError(responseParamsDto.COIN_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        UserWallet userWallet = new UserWallet();
        userWallet.setUserId(user.getId());
        userWallet.setType(Integer.valueOf(coinNo));
        userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
        if (userWallet != null) {
            resultMap.put("address", ValidataUtil.isNull(userWallet
                    .getAddress()) ? "" : userWallet.getAddress());
        }

        Coin coin = new Coin();
        coin.setCoinNo(Long.valueOf(coinNo));
        coin = coinMapper.getCoinByCondition(coin);
        resultMap.put("userNo", user.getId());
        resultMap.put("coinNo", coinNo);
        resultMap.put("coinName", ValidataUtil.isNull(coin.getCoinName()) ? ""
                : coin.getCoinName());
        resultMap.put("coinImg", ValidataUtil.isNull(coin.getCoinImg()) ? ""
                : coin.getCoinImg());
        return RspUtil.success(resultMap);
    }

    // 获取行情
    public BigDecimal getCoinRate(String rateNamme) {
        Params param = paramsMapper.getParams(rateNamme);
        if (param != null) {
            String coinRateString = param.getParamValue();
            BigDecimal coinRate = new BigDecimal(coinRateString);
            return coinRate;
        }
        return new BigDecimal("0");
    }

    /**
     * 获取钱包服务器资产
     *
     * @param userWallet
     * @return
     */
    public BigDecimal getEnableBalance(UserWallet userWallet, Coin coin, Address address) {
        BigDecimal balanceBigDecimal = null;// 可用资产
        Long coinNo = userWallet.getType().longValue();//币种编号
        Address addressNew = null;
        Coin coinNew = null;

        //如调用方已有address和coin则不需要再次查询
        if (address == null) {
            //获得对应货币的服务器地址
            addressNew = new Address();
            addressNew.setCoinNo(coinNo);
            addressNew = addressMapper.getAddressByCondition(addressNew);
        } else {
            addressNew = address;
        }
        if (coin == null) {
            coinNew = new Coin();
            coinNew.setCoinNo(coinNo);
            coinNew.setState(1);
            coinNew = coinMapper.getCoinByCondition(coinNew);
        } else {
            coinNew = coin;
        }

        String apiType = coin.getApiType();
        String addr = userWallet.getAddress();
        if (addressNew != null) {
            if ("eth_api".equals(apiType)) {
                EthcoinAPI api = new EthcoinAPI(addressNew);
                String balance = api.getBalance(addr);
                balanceBigDecimal = ValidataUtil.Progressive(balance).divide(EthcoinAPI.wei);
            } else if ("ptn_api".equals(apiType)) {
                String coinName = coinNew.getCoinName();
//                String tokenName = coinName.equalsIgnoreCase("MOC") ? "moc" : "moccny";
                String tokenName = coinName.toLowerCase();

                PNTCoinApi pntCoinApi = PNTCoinApi.getApi(addressNew);
                String balance = pntCoinApi.getBalance(addr, tokenName);
                balanceBigDecimal = new BigDecimal(balance);
            } else if ("btc_api".equals(apiType)) {//这里直接走库
                balanceBigDecimal = userWallet.getBalance().add(userWallet.getUnbalance());
            }
        }
        return balanceBigDecimal;
    }
















    /************ C2C管理员纠纷相关   ****/
    /**
     * 修改用户资产(基于数据库)
     * @param type		// 0： 增加可用资产， 1：减少可用资产，2 ：冻结， 3：解冻，4：减少冻结, 5：增加虚拟资产，6：减少虚拟资产
     * @throws Exception
     */
    @Transactional(rollbackFor = { Exception.class, RuntimeException.class })
    @Override
    public int editUserWalletBalance(UserWallet userWallet, BigDecimal price, Integer type, String remark) throws Exception{
        log.info("用户编号：{}，货币编号：{}，用户可用金额：{}，用户冻结金额：{}，修改金额：{}，修改类型：{}",
                userWallet.getUserId(), userWallet.getType(), userWallet.getBalance(), userWallet.getUnbalance(), price, type);

        String errMsg = "用户资产不足，walletId:" + userWallet.getId() + "，price：" + price + "，editType:" + type;

        Integer code = null;
        UserWallet userWalletUpdate = new UserWallet();
        userWalletUpdate.setId(userWallet.getId());
        if (type == 0){
            userWalletUpdate.setBalance(userWallet.getBalance().add(price));
            code = userWalletMapper.updateUserWalletByCondition(userWalletUpdate);
        }
        if (type == 1){
            if (userWallet.getBalance().compareTo(price) == -1)
                throw new Exception(errMsg);

            userWalletUpdate.setBalance(userWallet.getBalance().subtract(price));
            code = userWalletMapper.updateUserWalletByCondition(userWalletUpdate);
        }
        if (type == 2){
            if (userWallet.getBalance().compareTo(price) == -1)
                throw new Exception(errMsg);

            userWalletUpdate.setBalance(userWallet.getBalance().subtract(price));
            userWalletUpdate.setUnbalance(userWallet.getUnbalance().add(price));
            code = userWalletMapper.updateUserWalletByCondition(userWalletUpdate);
        }
        if (type == 3){
            if (userWallet.getUnbalance().compareTo(price) == -1)
                throw new Exception(errMsg);

            userWalletUpdate.setBalance(userWallet.getBalance().add(price));
            userWalletUpdate.setUnbalance(userWallet.getUnbalance().subtract(price));
            code = userWalletMapper.updateUserWalletByCondition(userWalletUpdate);
        }
        if (type == 4){
            if (userWallet.getUnbalance().compareTo(price) == -1)
                throw new Exception(errMsg);

            userWalletUpdate.setUnbalance(userWallet.getUnbalance().subtract(price));
            code = userWalletMapper.updateUserWalletByCondition(userWalletUpdate);
        }

        if (code <= 0) {
            throw new Exception("用户资产 : 修改用户资产异常!");
        }

        return code;
    }


}
