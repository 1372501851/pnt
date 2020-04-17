package com.inesv.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.github.pagehelper.PageHelper;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.TradeInfoService;
import com.inesv.service.UserWalletService;
import com.inesv.sms.YunPianSmsUtils;
import com.inesv.util.*;
import com.inesv.util.CoinAPI.BitcoinAPI;
import com.inesv.util.CoinAPI.EthcoinAPI;
import com.inesv.util.CoinAPI.PNTCoinApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Service
@Slf4j
public class TradeInfoServiceImpl implements TradeInfoService {
    private static final Logger logger = LoggerFactory.getLogger(TradeInfoServiceImpl.class);

    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ParamsMapper paramsMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private TradeInfoMapper tradeInfoMapper;

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private CycleTradeMapper cycleTradeMapper;

    @Autowired
    private SpotEntrustMapper spotEntrustMapper;

    @Autowired
    private SpotDealDetailMapper spotDealDetailMapper;

    @Autowired
    private PoundageMapper poundageMapper;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private TokensFreezeMapper tokensFreezeMapper;

    @Autowired
    private IDCardMapper idCardMapper;

    @Autowired
    private PayMapper payMapper;

    /**
     * 转账
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public BaseResponse addTradeInfo(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        String coinNo = json.getString("coinNo");
        String fee = json.getString("fee");
        String outAddress = json.getString("outAddress").trim();
        String inAddress = json.getString("inAddress").trim();
        String type = json.getString("type");
        String tradeNum = json.getString("tradeNum");
        String dealPwd = json.getString("dealPwd");
        String remark = json.getString("remark");
        String pubKey = json.getString("pubKey");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(coinNo))
            return RspUtil.rspError(responseParamsDto.CODE_NULL_DESC);
        if (ValidataUtil.isNull(fee))
            return RspUtil.rspError(responseParamsDto.FEE_NULL_DESC);
        if (ValidataUtil.isNull(outAddress))
            return RspUtil.rspError(responseParamsDto.OUT_ADDRESS_NULL_DESC);
        if (ValidataUtil.isNull(inAddress))
            return RspUtil.rspError(responseParamsDto.IN_ADDRESS_NULL_DESC);
        if (ValidataUtil.isNull(type))
            return RspUtil.rspError(responseParamsDto.TYPE_NULL_DESC);
        if (ValidataUtil.isNull(tradeNum))
            return RspUtil.rspError(responseParamsDto.PRICE_NUM_NULL_DESC);
        if (ValidataUtil.isNull(dealPwd))
            return RspUtil.rspError(responseParamsDto.DEALPWD_NULL_DESC);
        if (new BigDecimal(tradeNum).compareTo(new BigDecimal("0")) != 1)
            return RspUtil.rspError(responseParamsDto.PRICE_NUM_FAIL_DESC);
        if (tradeNum.contains(".")) {
            if (tradeNum.split("\\.")[1].length() > 6)
                return RspUtil.rspError(responseParamsDto.DECIMAL_6_FAIL);
        }
        if (outAddress.equals(inAddress))
            return RspUtil.rspError(responseParamsDto.ADDRESS_SAME_DESC);
        if (ValidataUtil.isNull(pubKey)) pubKey = "";

        BigDecimal sumNum = new BigDecimal(tradeNum).add(new BigDecimal(fee));
        if (sumNum.compareTo(new BigDecimal("0")) != 1) {
            return RspUtil.rspError(responseParamsDto.PRICE_NUM_FAIL_DESC);
        }

        User user = new User();
        Coin coin = new Coin();
        Address address = new Address();
        UserWallet inUserWallet = new UserWallet();
        UserWallet outUserWallet = new UserWallet();

        //条件判断
        BaseResponse response = conditionsJudgment(token, responseParamsDto, dealPwd, coinNo,
                fee, outAddress, inAddress, inUserWallet,
                outUserWallet, tradeNum, user, coin,
                address, sumNum, pubKey);
        if (response.getCode() != 200) return response;

        try {
            //把产生逻辑错误前的数据进行回滚
            response = handleTransfer(responseParamsDto, tradeNum, fee, sumNum, coin, outUserWallet, inUserWallet, outAddress, inAddress, address, user, pubKey, remark);
            if (response.getCode() != 200) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return response;
            }
        } catch (Exception e) {
            logger.error("处理转账失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return RspUtil.success(response.getData());
    }

    /**
     * 条件判断
     *
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public BaseResponse conditionsJudgment(String token, ResponseParamsDto responseParamsDto, String dealPwd, String coinNo, String fee, String outAddress,
                                           String inAddress, UserWallet inUserWallet, UserWallet outUserWallet, String tradeNum, User user, Coin coin, Address address, BigDecimal sumNum, String pubKey) throws Exception {
        User getUser = userMapper.getUserInfoByToken(token);
        user.setValue(getUser);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        if (ValidataUtil.isNull(user.getTradePassword()))
            return RspUtil.error(responseParamsDto.DEAL_PWD_SETTING_NULL, RspUtil.DEALPWD_NULL);
        if (!dealPwd.equals(user.getTradePassword()))
            return RspUtil.rspError(responseParamsDto.DEALPWD_FAIL_DESC);


        coin.setCoinNo(Long.valueOf(coinNo));
        coin.setState(1);
        Coin getCoin = coinMapper.getCoinByCondition(coin);
        coin.setValue(getCoin);
        if (coin == null)
            return RspUtil.rspError(responseParamsDto.COIN_FAIL_DESC);

        if (Double.valueOf(fee) < coin.getMinFee().doubleValue()
                || Double.valueOf(fee) > coin.getMaxFee().doubleValue())
            return RspUtil.rspError(responseParamsDto.FEE_FAIL_DESC);

        // 转出账户
        outUserWallet.setUserId(user.getId());
        outUserWallet.setType(Integer.valueOf(coinNo));
        outUserWallet.setAddress(outAddress);
        UserWallet getOutUserWallet = userWalletMapper.getUserWalletByConditionForUpdate(outUserWallet);

        //19-10-10判断币种状态  0开启，1关闭
        if(getOutUserWallet.getWalletState()==1){
            return RspUtil.rspError(responseParamsDto.TRADE_COIN_SUSPENDED);
        }


        outUserWallet.setValue(getOutUserWallet);
        if (outUserWallet == null || ValidataUtil.isNull(outUserWallet.getBalance()) || outUserWallet.getBalance().compareTo(new BigDecimal("0")) == -1
                || ValidataUtil.isNull(outUserWallet.getUnbalance()) || outUserWallet.getUnbalance().compareTo(new BigDecimal("0")) == -1)
            return RspUtil.rspError(responseParamsDto.BALANCE_FAIL_DESC);

        if ("ptn_api".equals(coin.getApiType())) {
            //代币
            if (!coin.getCoinName().equalsIgnoreCase("moc")) {
                //判断主币moc余额
                UserWallet uw = new UserWallet();
                uw.setAddress(outAddress);
                uw.setType(40);//主币MOC编号为40
                uw = userWalletMapper.getUserWalletByCondition(uw);
                //如果主币moc的余额小于手续费，则返回用户主币资金余额不足
                if (uw.getBalance().compareTo(new BigDecimal(fee)) == -1)
                    return RspUtil.rspError(responseParamsDto.MAIN_BALANCE_INSUFFICIENT_DESC);
            }else{
                //主币
                if (outUserWallet.getBalance().compareTo(sumNum) == -1)
                    return RspUtil.rspError(responseParamsDto.BALANCE_INSUFFICIENT_DESC);
            }
        } else {
            if (outUserWallet.getBalance().compareTo(sumNum) == -1)
                return RspUtil.rspError(responseParamsDto.BALANCE_INSUFFICIENT_DESC);
        }


        //只有指定账户才能进行PTN转账
        if ("ptn_api".equals(coin.getApiType())) {
            /*Params params = paramsMapper.getParams("ptn_transfer_user_white_list");
            StringTokenizer st = new StringTokenizer(params.getParamValue(), ",");
            boolean flag = false;
            while (st.hasMoreElements()) {
                if(user.getUsername().equals(st.nextElement())){
                    flag = true;
                }
            }
            if(!flag){
                return RspUtil.rspError(responseParamsDto.TRANSFER_SUSPENDED);
            }*/
            if (getUser.getState() == 0) {
                return RspUtil.rspError(responseParamsDto.TRANSFER_SUSPENDED);
            }

            /*处理代币转账用户存在主币，但不存在代币*/
            //收款方(先查询主币地址是否存在，如果存在则进行创建代币地址，不存在代币地址则创建，反之，则不创建)
            if (!coin.getCoinName().equalsIgnoreCase("moc")) {
                inUserWallet.setType(40);
                inUserWallet.setAddress(inAddress);
                UserWallet getInUserWallet = userWalletMapper.getUserWalletByConditionForUpdate(inUserWallet);
                if (!ValidataUtil.isNull(getInUserWallet)) {
                    //判断代币存不存在
                    UserWallet getCoinInfo = new UserWallet();
                    getCoinInfo.setAddress(inAddress);
                    getCoinInfo.setType(Math.toIntExact(coin.getCoinNo()));
                    getCoinInfo = userWalletMapper.getUserWalletByConditionForUpdate(getCoinInfo);
                    //如果代币地址不存在
                    if (ValidataUtil.isNull(getCoinInfo)) {
                        //创建代币地址
                        UserWallet coinInWallet = new UserWallet();
                        coinInWallet.setUserId(getCoinInfo.getUserId());
                        coinInWallet.setAddress(getInUserWallet.getAddress());
                        coinInWallet.setType(Math.toIntExact(coin.getCoinNo()));
                        coinInWallet.setBalance(new BigDecimal("0"));
                        coinInWallet.setUnbalance(BigDecimal.ZERO);
                        coinInWallet.setState(1);
                        coinInWallet.setFlag("true");
                        int count = userWalletMapper.add(coinInWallet);
                        if (count > 0) {
                            logger.info("收款方不存在 {} 地址，为收款方创建地址成功", coin.getCoinName());
                        }
                    }

                }
            }
        }

        // 转入账户
        inUserWallet.setType(Integer.valueOf(coinNo));
        inUserWallet.setAddress(inAddress);
        UserWallet getInUserWallet = userWalletMapper.getUserWalletByConditionForUpdate(inUserWallet);
        inUserWallet.setValue(getInUserWallet);
        if (inUserWallet.getId() != null && inUserWallet.getId() != 0L) {
            if (ValidataUtil.isNull(inUserWallet.getBalance()) || inUserWallet.getBalance().compareTo(new BigDecimal("0")) == -1
                    || ValidataUtil.isNull(inUserWallet.getUnbalance()) || inUserWallet.getUnbalance().compareTo(new BigDecimal("0")) == -1) {
                return RspUtil.rspError(responseParamsDto.BALANCE_FAIL_DESC);
            }
        }

        address.setCoinNo(Long.valueOf(coinNo));
        address.setStatus(1);
        Address getAddress = addressMapper.getAddressByCondition(address);
        address.setValue(getAddress);
        if (address == null) {
            return RspUtil.rspError(responseParamsDto.FAIL);
        }

        //代币数量判断
        if ("ptn_api".equals(coin.getApiType())) {
            if (!coin.getCoinName().equalsIgnoreCase("moc")) {
                //判断代币余额
                PNTCoinApi api = PNTCoinApi.getApi(address);
                String tokenCoinName = coin.getCoinName().toLowerCase();
                BigDecimal tokenBalance = new BigDecimal(api.getBalance(outAddress, tokenCoinName));
                //如果数据库代币余额小于交易数量，
                if (outUserWallet.getBalance().compareTo(new BigDecimal(tradeNum)) == -1 ||
                        //如果线上节点余额和数据库余额不相等
                        ! (tokenBalance.compareTo(outUserWallet.getBalance()) == 0) ||
                        //如果线上节点代币余额小于交易数量
                        tokenBalance.compareTo(new BigDecimal(tradeNum)) == -1)
                    //满足以上条件之一，则返回用户资金余额不足
                    return RspUtil.rspError(responseParamsDto.BALANCE_INSUFFICIENT_DESC);
            }
        }




        if (inUserWallet.getId() == null || inUserWallet.getId() == 0L) {
            // 判断地址与公钥是否正确
            /*if (coin.getCoinName().equalsIgnoreCase("PTN") || coin.getCoinName().equalsIgnoreCase("PTNCNY")) {
                if (!ValidataUtil.isNull(pubKey)) {
                    PNTCoinApi api = PNTCoinApi.getApi(address);
                    boolean pubKeyIsTrue = api.addressPubKeyIsRight(inAddress, pubKey);
                    if (!pubKeyIsTrue) {
                        return RspUtil.rspError(responseParamsDto.PUBKEY_ISFALSE);
                    }
                }
            }*/
        }
        return RspUtil.success();
    }

    /**
     * 内部和外部转账操作
     *
     * @param responseParamsDto
     * @param tradeNum               交易数量
     * @param fee                    手续费
     * @param sumNum                 交易数量+手续费
     * @param coin
     * @param outUserWalletForUpdate
     * @param inUserWalletForUpdate
     * @param outAddress             转出地址
     * @param inAddress              转入地址
     * @param address
     * @param user
     * @param pubKey                 公钥
     * @param remark                 备注
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public BaseResponse handleTransfer(ResponseParamsDto responseParamsDto, String tradeNum, String fee, BigDecimal sumNum, Coin coin, UserWallet outUserWalletForUpdate,
                                       UserWallet inUserWalletForUpdate, String outAddress, String inAddress, Address address, User user, String pubKey, String remark) throws Exception {
        String apiType = coin.getApiType();
        String coinName = coin.getCoinName();
        String transferResult = "error";
        String transferLogPrefix = "{}钱包地址转账参数tradeNum：{}，outAddress：{}，inAddress：{}，转账结果：{}";


        if ("ptn_api".equals(coin.getApiType())) {
            //代币
            if (!coin.getCoinName().equalsIgnoreCase("moc")) {
                //判断主币moc余额
                UserWallet uw = new UserWallet();
                uw.setType(40);//主币MOC编号为40
                uw.setAddress(outAddress);
                uw = userWalletMapper.getUserWalletByCondition(uw);
                //如果主币moc的余额小于手续费，则报用户资金余额不足
                if (uw.getBalance().compareTo(new BigDecimal(fee)) == -1)
                    return RspUtil.rspError(responseParamsDto.BALANCE_INSUFFICIENT_DESC);
            }else{
                //主币
                //用户数据库资产不足
                if (outUserWalletForUpdate.getBalance().compareTo(sumNum) == -1) {
                    return RspUtil.rspError(responseParamsDto.BALANCE_INSUFFICIENT_DESC);
                }
            }
        } else {
            //用户数据库资产不足
            if (outUserWalletForUpdate.getBalance().compareTo(sumNum) == -1) {
                return RspUtil.rspError(responseParamsDto.BALANCE_INSUFFICIENT_DESC);
            }
        }


        //添加转账记录，转出方用户数据库资产－（交易数量+手续费）
        Long tradeInfoId = addTradeInfoAndReduceUserWallet(tradeNum, fee, sumNum, outUserWalletForUpdate, outAddress, inAddress, user, coin, remark);

        if ("btc_api".equals(apiType)) {
            //平台处理转入方资产时，BTC内部转账只加数据库资产，不走公链，BTC外部转账不加数据库资产，要走公链
            if (inUserWalletForUpdate.getId() != null && inUserWalletForUpdate.getId() != 0L) {
                //转入方数据库资产增加
                editUserWallet(inUserWalletForUpdate, new BigDecimal(tradeNum), "0");
            } else {
                //执行钱包地址转账
                transferResult = transfer4Bit(tradeNum, inAddress, address);
                logger.info(transferLogPrefix, coinName, tradeNum, outAddress, inAddress, transferResult);
                if (ValidataUtil.isNull(transferResult)) {
                    return RspUtil.rspError(responseParamsDto.BALANCE_LOCKFAIL_DESC);
                }
            }
        } else {
            //平台处理转入方资产时， 非BTC内部转账要加数据库资产，但内部和外部转账都要走公链
            if (inUserWalletForUpdate.getId() != null && inUserWalletForUpdate.getId() != 0L) {
                //转入方数据库资产增加
                editUserWallet(inUserWalletForUpdate, new BigDecimal(tradeNum), "0");
            }
            //if (!"ptn_api".equals(apiType)) {//ptn直接走库}

            //钱包服务器资产减去冻结资产＝可用资产，是否够转账，转入方真实到账是tradeNum－平台矿工费
            BigDecimal balance = userWalletService.getEnableBalance(outUserWalletForUpdate, coin, address);
            if (balance.subtract(outUserWalletForUpdate.getUnbalance()).compareTo(new BigDecimal(tradeNum)) == -1) {
                return RspUtil.rspError(responseParamsDto.BALANCE_INSUFFICIENT_API_DESC);
            }

            if ("eth_api".equals(apiType)) {
                //执行钱包地址转账
                EthcoinAPI ethcoinAPI = new EthcoinAPI(address);
                transferResult = transfer4Eth(tradeNum, outAddress, inAddress, ethcoinAPI);
                logger.info(transferLogPrefix, coinName, tradeNum, outAddress, inAddress, transferResult);
                if (ValidataUtil.isNull(transferResult)) {
                    return RspUtil.rspError(responseParamsDto.BALANCE_LOCKFAIL_DESC);
                }
            } else if ("ptn_api".equals(apiType)) {

                //执行钱包地址转账
//                String tokenName = coinName.equalsIgnoreCase("MOC") ? "moc" : "moccny";
                String tokenName = coinName.toUpperCase();//对tokenName进行小写转换

                PNTCoinApi api = PNTCoinApi.getApi(address);
                // TODO moc代币判断，如果货币是moc的子代币，判断是否存在moc
                if (!tokenName.equalsIgnoreCase("moc")) {
                    //钱包服务器资产减去冻结资产＝可用资产，是否够转账，转入方真实到账是tradeNum
                    if (balance.subtract(outUserWalletForUpdate.getUnbalance()).compareTo(new BigDecimal(tradeNum)) == -1) {
                        return RspUtil.rspError(responseParamsDto.BALANCE_INSUFFICIENT_API_DESC);
                    }
                    String tokenAddress = outUserWalletForUpdate.getAddress();
                    UserWallet uw = new UserWallet();
                    uw.setAddress(tokenAddress);
                    uw.setType(40);//主币MOC编号为40
                    uw = userWalletMapper.getUserWalletByCondition(uw);
                    BigDecimal mainCoinBalance = new BigDecimal(api.getBalance(tokenAddress, "moc"));
                    //moc主币余额判断,同时满足以下三个条件才可进行代币转账，代币判断在前边
                    //节点上moc主币数量 >= 手续费
                    if (mainCoinBalance.compareTo(new BigDecimal(fee)) > -1
                            //数据库moc主币数量 >= 手续费
                            && uw.getBalance().compareTo(new BigDecimal(fee)) > -1
                            //节点上moc主币数量=数据库moc主币数量
                            && mainCoinBalance.compareTo(uw.getBalance()) == 0) {

                        if(ValidataUtil.isNull(remark)){
                            remark=coinName + "-API-平台转账,扣除" + fee + "MOC";
                        }
                        //代币
                        transferResult = api.sendTransaction(outAddress, inAddress, tradeNum, pubKey, remark, fee, tokenName);

                        //减去数据库内主币MOC的余额（交易代币，手续费收主币moc）
                        uw.setBalance(uw.getBalance().subtract(new BigDecimal(fee)));
                        int num = userWalletMapper.updateUserWalletByCondition(uw);
                        if (num > 0) {
                            logger.info("交易id: {},代币转账扣除 {} moc", tradeInfoId, fee);
                        }

                    } else {
                        return RspUtil.rspError(responseParamsDto.BALANCE_LOCKFAIL_DESC);
                    }
                } else {
                    //钱包服务器资产减去冻结资产＝可用资产，是否够转账，转入方真实到账是tradeNum
                    if (balance.subtract(outUserWalletForUpdate.getUnbalance()).compareTo(sumNum) == -1) {
                        return RspUtil.rspError(responseParamsDto.BALANCE_INSUFFICIENT_API_DESC);
                    }
                    if(ValidataUtil.isNull(remark)){
                        remark=coinName + "-API-平台转账";
                    }
                    //主币
                    transferResult = api.sendTransaction(outAddress, inAddress, tradeNum, pubKey, remark, fee, tokenName);
                }

                logger.info(transferLogPrefix, coinName, tradeNum, outAddress, inAddress, transferResult);
                if (ValidataUtil.isNull(transferResult)) {
                    return RspUtil.rspError(responseParamsDto.BALANCE_LOCKFAIL_DESC);
                }
            }
        }

        //成功转账后把hash值更新到转账记录中
        if (!"error".equals(transferResult)) {
            //更新订单号
//            String hash = MD5Util.GetMD5Code(coinName + transferResult);
            editTradeInfo(tradeInfoId, transferResult);
        }

        return RspUtil.success(tradeInfoId);
    }

    /**
     * 新增交易记录
     *
     * @throws Exception
     */
    public Long addTradeInfo(Long userId, Integer coinNo, String tradeNum,
                             BigDecimal fee, String outAddress, String inAddress,
                             String orderNo, Integer state, String remark, int tradeType,
                             BigDecimal unTradeNum) throws Exception {
        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setUserId(userId);
        tradeInfo.setCoinNo(coinNo);
        tradeInfo.setType(0);
        tradeInfo.setOrderNo(orderNo);
        tradeInfo.setTradeNum(new BigDecimal(tradeNum));
        tradeInfo.setRatio(fee);
        tradeInfo.setOutAddress(outAddress);
        tradeInfo.setInAddress(inAddress);
        tradeInfo.setRemark(remark);
        tradeInfo.setState(state);
        tradeInfo.setType(tradeType);
        tradeInfo.setUnTradeNum(unTradeNum == null ? BigDecimal.ZERO : unTradeNum);
        int result = tradeInfoMapper.insertTradeInfo(tradeInfo);
        if (result <= 0) {
            throw new Exception("转账：生成订单异常！");
        }
        return tradeInfo.getId();
    }

    /**
     * 修改交易记录订单号
     *
     * @throws Exception
     */
    public void editTradeInfo(Long tradeInfoId, String hash)
            throws Exception {
        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setId(tradeInfoId);
        tradeInfo.setHash(hash);
        tradeInfoMapper.updateTradeInfo(tradeInfo);
    }

    /**
     * 修改用户资产
     *
     * @throws Exception
     */
    public int editUserWallet(UserWallet userWallet, BigDecimal num, String editType) throws Exception {
        // editType | 0：加，1：减，2：冻结，3：解冻
        String errMsg = "用户资产不足，num：" + num + "，editType:" + editType;
        BigDecimal balance = userWallet.getBalance();
        BigDecimal unbalance = userWallet.getUnbalance();
        if (editType.equals("0")) {//加资产
            userWallet.setBalance(balance.add(num));
        }
        if (editType.equals("1")) {//减资产
            if (balance.compareTo(num) == -1) {
                throw new Exception(errMsg);
            }
            userWallet.setBalance(balance.subtract(num));
        }
        if (editType.equals("2")) {//冻结资产
            if (balance.compareTo(num) == -1) {
                throw new Exception(errMsg);
            }
            userWallet.setBalance(balance.subtract(num));
            userWallet.setUnbalance(unbalance.add(num));
        }
        if (editType.equals("3")) {//解冻资产
            if (unbalance.compareTo(num) == -1) {
                throw new Exception(errMsg);
            }
            userWallet.setBalance(balance.add(num));
            userWallet.setUnbalance(unbalance.subtract(num));
        }
        int result = userWalletMapper.updateUserWalletByCondition(userWallet);
        if (result <= 0) {
            throw new Exception("更新资产失败！");
        }
        return result;
    }

    /**
     * 获取交易记录
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse getTradeInfo(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        String coinNo = json.getString("coinNo");
        String type = json.getString("type"); // 0：转入，1：转出
//        String date = json.getString("date"); // 时间，2018-01
        String pageSize = json.getString("pageSize");
        String lineSize = json.getString("lineSize");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(coinNo))
            return RspUtil.rspError(responseParamsDto.COIN_NULL_DESC);
        if (ValidataUtil.isNull(type))
            return RspUtil.rspError(responseParamsDto.TYPE_NULL_DESC);
//        if (ValidataUtil.isNull(date))
//            return RspUtil.rspError(responseParamsDto.DATE_NULL_DESC);
        if (ValidataUtil.isNull(pageSize))
            return RspUtil.rspError(responseParamsDto.PAGEORLINE_NULL_DESC);
        if (ValidataUtil.isNull(lineSize))
            return RspUtil.rspError(responseParamsDto.PAGEORLINE_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null || ValidataUtil.isNull(user.getId().toString()))
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        List<TradeInfo> tradeInfos = new ArrayList<>();

        UserWallet userWallet = new UserWallet();
        userWallet.setUserId(user.getId());
        userWallet.setType(Integer.valueOf(coinNo));
        userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
        if (userWallet == null || ValidataUtil.isNull(userWallet.getAddress()))
            return RspUtil.success(tradeInfos);

        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setCoinNo(Integer.valueOf(coinNo));
//        tradeInfo.setDateFormat(date);
        if (type.equals("0")) { // 转入：别人转给自己
            tradeInfo.setInAddress(userWallet.getAddress());
        }
        if (type.equals("1")) { // 转出：转给别人
            tradeInfo.setOutAddress(userWallet.getAddress());
            tradeInfo.setUserId(user.getId());
        }
        if (!type.equals("1") && !type.equals("0")) { // 所有转账
            tradeInfo.setAddress(userWallet.getAddress());
        }
        if (pageSize.equals("0") && lineSize.equals("0")) {
            tradeInfos = tradeInfoMapper.getTradeInfoByConditions(tradeInfo);
        } else {
            PageHelper.startPage(Integer.valueOf(pageSize),
                    Integer.valueOf(lineSize));
            tradeInfos = tradeInfoMapper.getTradeInfoByConditions(tradeInfo);
        }
        // if (type.equals("1")) {
        // for (int i = 0; i < tradeInfos.size(); i++) {
        // if (tradeInfos.get(i).getUserId().equals(userWallet.getUserId())) {
        // tradeInfos.get(i).setType(1);
        // }
        // }
        // }
        // if (!type.equals("1") && !type.equals("0")) {
        // for (int i = 0; i < tradeInfos.size(); i++) {
        // if (tradeInfos.get(i).getUserId().equals(userWallet.getUserId())) {
        // tradeInfos.get(i).setType(1);
        // }
        // }
        // }
        for (int i = 0; i < tradeInfos.size(); i++) {
            if (tradeInfos.get(i).getUserId().equals(userWallet.getUserId())) {
                tradeInfos.get(i).setType(1);
            } else {
                tradeInfos.get(i).setType(0);
            }
        }
        return RspUtil.success(JSONFormat.getStr(tradeInfos));
    }

    /**
     * 获取交易记录详情
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse getTradeInfoByNo(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        String tradeNo = json.getString("tradeNo");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(tradeNo))
            return RspUtil.rspError(responseParamsDto.ID_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setId(Long.valueOf(tradeNo));
        tradeInfo = tradeInfoMapper.getTradeInfoByCondition(tradeInfo);

        UserWallet userWallet = new UserWallet();
        userWallet.setUserId(user.getId());
        userWallet.setType(tradeInfo.getCoinNo());
        userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
        if (userWallet == null || ValidataUtil.isNull(userWallet.getAddress())) {
            tradeInfo.setType(3);
        } else {
            if (userWallet.getAddress().equals(tradeInfo.getOutAddress())) {
                tradeInfo.setType(1);
            }
            if (userWallet.getAddress().equals(tradeInfo.getInAddress())) {
                tradeInfo.setType(0);
            }
        }
        return RspUtil.success(JSONFormat.getStr(tradeInfo));
    }

    /**
     * 获取交易好友
     *
     * @param data
     * @return
     */
    @Override
    public BaseResponse getTradeFriends(String data) {
        JSONObject json = JSONObject.parseObject(data);
        if (ValidataUtil.isNull(json.getString("token")))
            return RspUtil.rspError("token不能为空");

        User user = userMapper.getUserInfoByToken(json.getString("token"));
        if (user == null)
            return RspUtil.rspError("用户不存在");

        HashSet<Map<String, Object>> rspSet = new HashSet<>();
        List<UserWallet> userWalletList = userWalletMapper.getuserWallet(user
                .getId());
        for (UserWallet userWallet : userWalletList) {
            List<TradeInfo> list = tradeInfoMapper.getTradeList(userWallet
                    .getAddress());
            for (TradeInfo tradeInfo : list) {
                User userInfo = null;
                List<UserWallet> userWalletInfo = null;
                Map<String, Object> map = new HashMap<>();

                if (userWallet.getAddress().equals(tradeInfo.getInAddress())) {
                    if (tradeInfo.getOutAddress() != null
                            || !"".equals(tradeInfo.getOutAddress())) {
                        userWalletInfo = userWalletMapper
                                .getUserWalletByAddress(tradeInfo
                                        .getOutAddress());
                    }
                    if (!userWalletInfo.isEmpty()) {
                        userInfo = userMapper.getUserInfoById(userWalletInfo
                                .get(0).getUserId());
                    }
                }
                if (userWallet.getAddress().equals(tradeInfo.getOutAddress())) {
                    if (tradeInfo.getInAddress() != null
                            || !"".equals(tradeInfo.getInAddress())) {
                        userWalletInfo = userWalletMapper
                                .getUserWalletByAddress(tradeInfo
                                        .getInAddress());
                    }
                    if (!userWalletInfo.isEmpty()) {
                        userInfo = userMapper.getUserInfoById(userWalletInfo
                                .get(0).getUserId());
                    }
                }

                if (userInfo != null && userWalletInfo != null) {
                    map.put("id", userInfo.getId());
                    map.put("name", userInfo.getUsername() == null ? ""
                            : userInfo.getUsername());
                    map.put("photo", userInfo.getPhoto() == null ? ""
                            : userInfo.getPhoto());
                    rspSet.add(map);
                }

            }
        }

        return RspUtil.success(rspSet);
    }

    /**
     * 冻结用户数据库资产
     *
     * @param userWallet
     * @param freezePrice
     * @return
     * @throws Exception
     */
    @Override
    public int freezeUserWalletBalance(UserWallet userWallet, BigDecimal freezePrice)
            throws Exception {
        int result = 1;//委托购买时不需要冻结资产，默认成功

        //只有委托出售时，才需要冻结用户资产
        if (freezePrice.compareTo(new BigDecimal("0")) == 1) {
            if (userWallet.getBalance().compareTo(freezePrice) != 1) {
                throw new Exception(LanguageUtil.proving("0").NOT_ENOUGH_ENABLE_BALANCE);
            }
            UserWallet userWalletUpdate = new UserWallet();
            userWalletUpdate.setBalance(userWallet.getBalance().subtract(freezePrice));
            userWalletUpdate.setUnbalance(userWallet.getUnbalance().add(freezePrice));
            userWalletUpdate.setId(userWallet.getId());
            result = userWalletMapper.updateUserWalletByCondition(userWalletUpdate);
        }

        return result;
    }

    /**
     * 买-C2C匹配逻辑
     *
     * @param buySpotEntrustDto
     * @param sellSpotEntrustDto
     * @param spotDealDetailDtos
     * @param spotEntrustDtos
     * @param tradeNum
     * @param num
     * @param sizeNum
     * @throws Exception
     */
    public void addBuySpotEntrustData(SpotEntrust buySpotEntrust,
                                      SpotEntrust sellSpotEntrust, List<SpotDealDetail> spotDealDetails,
                                      List<SpotEntrust> spotEntrusts, BigDecimal tradeNum, Integer num,
                                      Integer sizeNum, BigDecimal tradePrice) throws Exception {
        // 添加委托记录列表-委托方（买方）
        buySpotEntrust.setMatchNum(buySpotEntrust.getMatchNum().add(tradeNum));
        SpotEntrust buySpotEntrustMatch = new SpotEntrust();
        buySpotEntrustMatch.setId(buySpotEntrust.getId());
        buySpotEntrustMatch.setEntrustType(buySpotEntrust.getEntrustType());
        buySpotEntrustMatch.setUserNo(buySpotEntrust.getUserNo());
        buySpotEntrustMatch.setAPIDealNumber(new BigDecimal("0"));
        buySpotEntrustMatch.setAPIDealEditType("0");
        buySpotEntrustMatch.setAPIMatchNumber(tradeNum);
        buySpotEntrustMatch.setAPIMatchEditType("0");
        buySpotEntrustMatch.setAPIType("1");
        int flag = 0;
        for (int k = 0; k < spotEntrusts.size(); k++) {
            if (buySpotEntrustMatch.getId().toString()
                    .equals(spotEntrusts.get(k).getId().toString())) {
                spotEntrusts.get(k).setAPIMatchNumber(
                        new BigDecimal(spotEntrusts.get(k).getAPIMatchNumber()
                                .add(tradeNum).toString()));
                flag = 1;
            }
        }
        if (flag == 0) {
            spotEntrusts.add(buySpotEntrustMatch);
        }
        // 添加委托记录列表-匹配方（卖方）
        SpotEntrust sellSpotEntrustMatch = new SpotEntrust();
        sellSpotEntrustMatch.setId(sellSpotEntrust.getId());
        sellSpotEntrustMatch.setEntrustType(sellSpotEntrust.getEntrustType());
        sellSpotEntrustMatch.setUserNo(sellSpotEntrust.getUserNo());
        sellSpotEntrustMatch.setAPIDealNumber(new BigDecimal("0"));
        sellSpotEntrustMatch.setAPIDealEditType("0");
        sellSpotEntrustMatch.setAPIMatchNumber(tradeNum);
        sellSpotEntrustMatch.setAPIMatchEditType("0");
        sellSpotEntrustMatch.setAPIType("1");
        spotEntrusts.add(sellSpotEntrustMatch);
        // 交易记录
        SpotDealDetail spotDealDetail = new SpotDealDetail();
        spotDealDetail.setBuyUserNo(buySpotEntrust.getUserNo());
        spotDealDetail.setSellUserNo(sellSpotEntrust.getUserNo());
        spotDealDetail.setCoinNo(buySpotEntrust.getEntrustCoin());
        spotDealDetail.setDealPrice(tradePrice);
        spotDealDetail.setEntrustPrice(buySpotEntrust.getEntrustPrice());
        spotDealDetail.setDealNum(tradeNum);
        spotDealDetail.setSumPrice(tradePrice.multiply(tradeNum));
        spotDealDetail.setPoundageScale(sellSpotEntrust.getPoundageScale());
        spotDealDetail.setPoundageCoin(sellSpotEntrust.getPoundageCoin());
        spotDealDetail.setPoundage(tradeNum.multiply(sellSpotEntrust
                .getPoundageScale()));
        spotDealDetail.setBuyEntrust(buySpotEntrust.getId());
        spotDealDetail.setSellEntrust(sellSpotEntrust.getId());
        spotDealDetail.setMatchNo(0L);
        spotDealDetail.setState(3);
        spotDealDetails.add(spotDealDetail);
    }

    /**
     * 卖-C2C匹配逻辑
     *
     * @param buySpotEntrust
     * @param sellSpotEntrust
     * @param spotDealDetails
     * @param spotEntrusts
     * @param tradeNum
     * @param num
     * @param sizeNum
     * @throws Exception
     */
    public void addSellSpotEntrustData(SpotEntrust buySpotEntrust,
                                       SpotEntrust sellSpotEntrust, List<SpotDealDetail> spotDealDetails,
                                       List<SpotEntrust> spotEntrusts, BigDecimal tradeNum, Integer num,
                                       Integer sizeNum, BigDecimal tradePrice) throws Exception {
        // 添加委托记录列表-委托方（买方）
        buySpotEntrust.setMatchNum(buySpotEntrust.getMatchNum().add(tradeNum));
        SpotEntrust buySpotEntrustMatch = new SpotEntrust();
        buySpotEntrustMatch.setId(buySpotEntrust.getId());
        buySpotEntrustMatch.setEntrustType(buySpotEntrust.getEntrustType());
        buySpotEntrustMatch.setUserNo(buySpotEntrust.getUserNo());
        buySpotEntrustMatch.setAPIDealNumber(new BigDecimal("0"));
        buySpotEntrustMatch.setAPIDealEditType("0");
        buySpotEntrustMatch.setAPIMatchNumber(tradeNum);
        buySpotEntrustMatch.setAPIMatchEditType("0");
        buySpotEntrustMatch.setAPIType("1");
        spotEntrusts.add(buySpotEntrustMatch);
        // 添加委托记录列表-匹配方（卖方）
        sellSpotEntrust
                .setMatchNum(sellSpotEntrust.getMatchNum().add(tradeNum));
        SpotEntrust sellSpotEntrustMatch = new SpotEntrust();
        sellSpotEntrustMatch.setId(sellSpotEntrust.getId());
        sellSpotEntrustMatch.setEntrustType(sellSpotEntrust.getEntrustType());
        sellSpotEntrustMatch.setUserNo(sellSpotEntrust.getUserNo());
        sellSpotEntrustMatch.setAPIDealNumber(new BigDecimal("0"));
        sellSpotEntrustMatch.setAPIDealEditType("0");
        sellSpotEntrustMatch.setAPIMatchNumber(tradeNum);
        sellSpotEntrustMatch.setAPIMatchEditType("0");
        sellSpotEntrustMatch.setAPIType("1");
        int flag = 0;
        for (int k = 0; k < spotEntrusts.size(); k++) {
            if (sellSpotEntrustMatch.getId().toString()
                    .equals(spotEntrusts.get(k).getId().toString())) {
                spotEntrusts.get(k).setAPIMatchNumber(
                        spotEntrusts.get(k).getAPIMatchNumber().add(tradeNum));
                flag = 1;
            }
        }
        if (flag == 0) {
            spotEntrusts.add(sellSpotEntrustMatch);
        }
        // 交易记录
        SpotDealDetail spotDealDetail = new SpotDealDetail();
        spotDealDetail.setBuyUserNo(buySpotEntrust.getUserNo());
        spotDealDetail.setSellUserNo(sellSpotEntrust.getUserNo());
        spotDealDetail.setCoinNo(buySpotEntrust.getEntrustCoin());
        spotDealDetail.setDealPrice(tradePrice);
        spotDealDetail.setEntrustPrice(sellSpotEntrust.getEntrustPrice());
        spotDealDetail.setDealNum(tradeNum);
        spotDealDetail.setSumPrice(tradePrice.multiply(tradeNum));
        spotDealDetail.setPoundageScale(sellSpotEntrust.getPoundageScale());
        spotDealDetail.setPoundageCoin(sellSpotEntrust.getPoundageCoin());
        spotDealDetail.setPoundage(tradeNum.multiply(sellSpotEntrust
                .getPoundageScale()));
        spotDealDetail.setBuyEntrust(buySpotEntrust.getId());
        spotDealDetail.setSellEntrust(sellSpotEntrust.getId());
        spotDealDetail.setMatchNo(0L);
        spotDealDetail.setState(3);
        spotDealDetails.add(spotDealDetail);
    }

    /**
     * 下单
     *
     * @param entrustNo
     * @param spotDealDetailDto
     * @param userBalanceDto
     * @param price
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Map<String, Object> spotManualTrade(String entrustNo,
                                               SpotDealDetail spotDealDetail, UserWallet userWallet,
                                               BigDecimal freezeNum, BigDecimal totalETHFreeze, Coin coin) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        // 查询委托记录
        SpotEntrust spotEntrust = new SpotEntrust();
        spotEntrust.setEntrustNo(entrustNo);
        spotEntrust.setState(0);
        spotEntrust = spotEntrustMapper.selectSpotEntrustByCondition(spotEntrust);
        if (spotEntrust == null || spotEntrust.getEntrustNum().compareTo(spotEntrust.getMatchNum().add(spotEntrust.getDealNum())) == -1) {
            throw new Exception("委托中记录不存在");
        }

        /**
         * 根据用户可匹配的数量确定最小限额和最大限额
         */
        // 剩余可被匹配量
        BigDecimal actualNum = spotEntrust.getEntrustNum()
                .subtract(spotEntrust.getDealNum())
                .subtract(spotEntrust.getMatchNum())
                .subtract(spotDealDetail.getDealNum());
        // 如果可被匹配量小于原先的最小限额,则最小限额修改为可被匹配量
        if (actualNum.compareTo(spotEntrust.getEntrustMinPrice()) == -1) {
            spotEntrust.setEntrustMinPrice(actualNum);
        }
        // 最大限额为剩余可被匹配量
        spotEntrust.setEntrustMaxPrice(actualNum);

        // 已匹配量
        spotEntrust.setMatchNum(spotEntrust.getMatchNum().add(
                spotDealDetail.getDealNum()));

        // 修改广告
        int entrustCode = spotEntrustMapper.updateByPrimaryKey(spotEntrust);
        if (entrustCode != 1)
            throw new Exception("修改广告失败");

        // 新增匹配记录(订单)
        spotDealDetailMapper.insertSpotDealDetail(spotDealDetail);

        // 冻结资产
        int code = freezeUserWalletBalance(userWallet, freezeNum);
        if (code != 1) {
            throw new Exception("freeze balance error !");
        }

        if ("eth_tokens_api".equals(coin.getApiType())) {
            //委托代币出售时，需要冻结ETH资产
            freezeETH4Tokens(totalETHFreeze, userWallet);
            if (totalETHFreeze.compareTo(new BigDecimal("0")) == 1) {
                //String entrustNo, String orderNo, Integer coinNo, BigDecimal unbalance
                tokensFreezeMapper.add(new TokensFreeze(null, spotDealDetail.getOrderNo(), spotEntrust.getEntrustCoin(), totalETHFreeze));
            }
        }

        User user = userMapper.getUserInfoById(spotEntrust.getUserNo().longValue());
        final String phone = user.getPhone();
        final String areaCode = user.getAreaCode();
        Long orderId = spotDealDetail.getId();
        result.put("orderId", orderId);
        result.put("phone", phone);
        result.put("areaCode", areaCode);

        // 异步：匹配成功-发送短信
        try {
            ThreadUtil.execute(new MyTaskUtil() {
                @Override
                public void run() {
                    try {
                        if (StringUtils.isBlank(phone) || StringUtils.isBlank(areaCode)) {
                            return;
                        }
//                        AliSmsUtil.sendOrderSms(phone);
                        //TODO 替换为云片国际短信
                        YunPianSmsUtils.sendOrderMatch(areaCode,phone);

                    } catch (Exception e) {
                    }
                }
            }, 2, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 冻结代币交易时的ETH
     *
     * @param totalETHFreeze
     * @param userWallet
     * @throws Exception
     */
    public void freezeETH4Tokens(BigDecimal totalETHFreeze, UserWallet userWallet) throws Exception {
        //委托代币出售时，需要冻结ETH资产
        if (totalETHFreeze.compareTo(new BigDecimal("0")) == 1) {
            UserWallet uWallet = new UserWallet();
            uWallet.setUserId(userWallet.getUserId());
            uWallet.setType(20);
            uWallet = userWalletMapper.getUserWalletByConditionForUpdate(uWallet);
            int res = freezeUserWalletBalance(uWallet, totalETHFreeze);
            if (res <= 0) {
                logger.warn("委托代币出售时，冻结ETH资产失败！");
                throw new Exception("委托代币出售时，冻结ETH资产失败！");
            }
        }
    }

    /**
     * 新增匹配记录
     *
     * @param spotDealDetailDtos
     * @throws Exception
     */
    public void insertSpotDealDetail(List<SpotDealDetail> spotDealDetailDtos)
            throws Exception {
        spotDealDetailMapper.insertSpotDealDetails(spotDealDetailDtos);
    }

    /**
     * c2c：钱包服务器转账，修改买卖双方库冻结资产
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public boolean tradeAndEditWalletWithServer(UserWallet buyUserWallet,
                                                UserWallet sellUserWallet, BigDecimal buyPrice,
                                                BigDecimal subFreezeNum, BigDecimal minerFee, BigDecimal poundage,
                                                String orderNo) throws Exception {
        boolean flag = false;
        String buyAddress = "";
        String sellAddress = "";
        Integer coinType = buyUserWallet.getType();
        Address serverAddress = new Address();
        serverAddress.setCoinNo((long) coinType);

        // 卖方钱包减去相对的冻结资产
        UserWallet updateSellUserWallet = new UserWallet();
        updateSellUserWallet.setId(sellUserWallet.getId());
        BigDecimal resultUnbalance = sellUserWallet.getUnbalance().subtract(subFreezeNum);
        updateSellUserWallet.setUnbalance(resultUnbalance);
        int resultCode = userWalletMapper.updateUserWalletByCondition(updateSellUserWallet);
        if (resultCode <= 0) {
            throw new Exception("c2c修改卖方资产失败");
        }

        /*
         * 转账
         */
        // ptn,ptncny
        if (coinType == 40 || coinType == 50) {
            serverAddress = addressMapper.getAddressByCondition(serverAddress);
            PNTCoinApi ptnApi = PNTCoinApi.getApi(serverAddress);
            buyAddress = buyUserWallet.getAddress();
            sellAddress = sellUserWallet.getAddress();
            String publicKey = ptnApi.getPublicKey(buyAddress,
                    coinType == 40 ? "moc" : "moccny");

            // 添加转账记录
            Long tradeInfoNo = addTradeInfo(sellUserWallet.getUserId(), coinType,
                    buyPrice.toString(), minerFee, sellAddress, buyAddress,
                    orderNo, 1, "c2c:转账到买方", 0, null);

            // 转账到买方地址
            String tradeHash = ptnApi.sendTransaction(sellAddress, buyAddress,
                    buyPrice.setScale(5, BigDecimal.ROUND_DOWN).toString(),
                    publicKey, "c2c转账至买方地址", minerFee.toString(),
                    coinType == 40 ? "moc" : "moccny");
            if (StringUtils.isBlank(tradeHash))
                throw new Exception("c2c转账" + coinType + ":卖方(" + sellAddress
                        + ")到买方(" + buyAddress + ")时失败");

            //更新hash值到转账记录
            updateTradeInfoWithHash(coinType, tradeInfoNo, tradeHash);

            // 转移手续费至中间帐号
            Params params = paramsMapper.getParams("PTN_Transfer_Station");
            String centerAddress = params.getParamValue();
            String centerPublicKey = ptnApi.getPublicKey(centerAddress,
                    coinType == 40 ? "moc" : "moccny");
            String centerHash = ptnApi.sendTransaction(sellAddress,
                    centerAddress, poundage.setScale(5, BigDecimal.ROUND_DOWN)
                            .toString(), centerPublicKey, "c2c转账，手续费至平台",
                    "0.01", coinType == 40 ? "moc" : "moccny");
            // 如果手续费转账失败则重新冻结手续费，并修改手续费记录状态
            if (StringUtils.isBlank(centerHash)) {
                logger.info("c2c转账手续费到中间地址时失败");
                UserWallet reFreezeUserWallet = new UserWallet();
                reFreezeUserWallet.setId(updateSellUserWallet.getId());
                reFreezeUserWallet.setUnbalance(updateSellUserWallet
                        .getUnbalance().add(poundage));
                userWalletMapper
                        .updateUserWalletByCondition(reFreezeUserWallet);
                Poundage poundageRecord = poundageMapper.getByOrderNo(orderNo);
                Poundage updatePoundage = new Poundage();
                updatePoundage.setId(poundageRecord.getId());
                updatePoundage.setState(0);
                poundageMapper.updatePoundage(updatePoundage);
            } else {
                // 添加转账记录
                addTradeInfo(sellUserWallet.getUserId(), coinType,
                        poundage.subtract(new BigDecimal("0.000001"))
                                .toString(), new BigDecimal("0.000001"),
                        sellAddress, centerAddress, orderNo, 1, "c2c:转账手续费到平台",
                        0, null);
            }
            flag = true;
        }
        // eth、etc
        if (coinType == 20 || coinType == 60 || coinType == 90) {
            serverAddress = addressMapper.getAddressByCondition(serverAddress);
            EthcoinAPI ethcoinAPI = new EthcoinAPI(serverAddress.getAddress(),
                    serverAddress.getPort(), serverAddress.getName(),
                    serverAddress.getPassword(),
                    serverAddress.getLockPassword());
            buyAddress = buyUserWallet.getAddress();
            sellAddress = sellUserWallet.getAddress();

            // 添加转账记录
            Long tradeInfoNo = addTradeInfo(sellUserWallet.getUserId(), coinType,
                    buyPrice.toString(), minerFee, sellAddress, buyAddress,
                    orderNo, 1, "c2c:转账到买方", 0, null);

            /*
             * 转账到买方地址
             */
            BigDecimal tradeNumWei = buyPrice.multiply(EthcoinAPI.wei);
            String bigIntStr = tradeNumWei.setScale(0, BigDecimal.ROUND_DOWN)
                    .toString();
            String ethPrice = new BigInteger(bigIntStr, 10).toString(16);
            String ethResult = ethcoinAPI.sendTransaction(sellAddress,
                    buyAddress, "0x" + ethPrice);
            if (StringUtils.isBlank(ethResult))
                throw new Exception("c2c转账" + coinType + "卖方(" + sellAddress
                        + "(到买方(" + buyAddress + ")时失败");

            //更新hash值到转账记录
            updateTradeInfoWithHash(coinType, tradeInfoNo, ethResult);

            // 转移手续费至中间帐号
            String addressKey = coinType == 20 ? "ETH_Transfer_Station"
                    : "ETC_Transfer_Station";
            Params params = paramsMapper.getParams(addressKey);
            String centerAddress = params.getParamValue();
            BigDecimal poundageNumWei = poundage.multiply(EthcoinAPI.wei);
            String poundageEthPrice = new BigInteger(poundageNumWei.toString(),
                    10).toString(16);
            // 先计算手续费会产生的矿工费
            BigDecimal Fee = ethcoinAPI.getGasAndGasPrice(sellAddress,
                    centerAddress, "0x" + poundageEthPrice).divide(
                    EthcoinAPI.wei);
            // 手续费减去矿工费:实际转账数量（存在些许误差）
            BigDecimal actualPoundage = poundage.subtract(Fee);
            String actualGigIntStr = actualPoundage.setScale(0,
                    BigDecimal.ROUND_DOWN).toString();
            String actualEthPrice = new BigInteger(actualGigIntStr, 10)
                    .toString(16);
            // 实际手续费转账到中心帐号
            String actualEthResult = ethcoinAPI.sendTransaction(sellAddress,
                    buyAddress, "0x" + actualEthPrice);

            Coin coin = new Coin();
            coin.setCoinNo(coinType.longValue());
            coin.setState(1);
            coin = coinMapper.getCoinByCondition(coin);


            // 如果手续费转账失败则重新冻结手续费，并修改手续费记录状态
            if (StringUtils.isBlank(actualEthResult)) {
                logger.info("c2c转账手续费到平台时失败");
                UserWallet reFreezeUserWallet = new UserWallet();
                reFreezeUserWallet.setId(updateSellUserWallet.getId());
                reFreezeUserWallet.setUnbalance(updateSellUserWallet
                        .getUnbalance().add(poundage));
                userWalletMapper
                        .updateUserWalletByCondition(reFreezeUserWallet);
                Poundage poundageRecord = poundageMapper.getByOrderNo(orderNo);
                Poundage updatePoundage = new Poundage();
                updatePoundage.setId(poundageRecord.getId());
                updatePoundage.setState(0);
                poundageMapper.updatePoundage(updatePoundage);
            } else {
                // 添加转账记录
                addTradeInfo(sellUserWallet.getUserId(), coinType,
                        actualPoundage.toString(), Fee, sellAddress,
                        centerAddress, orderNo, 1, "c2c:转账手续费到平台", 0, null);
            }
            flag = true;
        }
        return flag;
    }

    /**
     * 更新调用钱包服务器转账接口返回的hash值
     *
     * @param coinType    币种编号
     * @param tradeInfoNo 转账记录id
     * @param tradeHash   调用钱包服务器转账接口返回的hash值
     */
    private void updateTradeInfoWithHash(Integer coinType, Long tradeInfoNo, String tradeHash) {
        Coin coin = new Coin();
        coin.setCoinNo(coinType.longValue());
        coin.setState(1);
        Coin getCoin = coinMapper.getCoinByCondition(coin);
        String hash = MD5Util.GetMD5Code(getCoin.getCoinName() + tradeHash);
        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setId(tradeInfoNo);
        tradeInfo.setHash(hash);
        tradeInfoMapper.updateTradeInfo(tradeInfo);
    }

    /**
     * 获取矿工费
     */
    @Override
    public BigDecimal getMinerFee(Long coinType, String outAddress,
                                  String inAddress, BigDecimal tradeNum) {

        BigDecimal minerFee = new BigDecimal("0");

        Coin coin = coinMapper.getCoinByCoinNo(coinType);
        String coinName = coin.getCoinName();

        // btc
        if ("BTC".equalsIgnoreCase(coinName)) {
            minerFee = tradeNum.multiply(coin.getMinFee());
        }
        // ltc
        if ("LTC".equalsIgnoreCase(coinName)) {
            minerFee = tradeNum.multiply(coin.getMinFee());
        }
        // eth、etc
        if ("ETH".equalsIgnoreCase(coinName)
                || "ETC".equalsIgnoreCase(coinName)) {
            Address address = new Address();
            address.setCoinNo(coin.getCoinNo());
            address = addressMapper.getAddressByCondition(address);
            EthcoinAPI ethcoinAPI = new EthcoinAPI(address.getAddress(),
                    address.getPort(), address.getName(),
                    address.getPassword(), address.getLockPassword());
            BigDecimal tradeNumWei = tradeNum.multiply(EthcoinAPI.wei);
            String ethPrice = new BigInteger(tradeNumWei.setScale(0,
                    BigDecimal.ROUND_DOWN).toString(), 10).toString(16);
            minerFee = ethcoinAPI.getGasAndGasPrice(outAddress, inAddress,
                    "0x" + ethPrice).divide(EthcoinAPI.wei);
            if (minerFee == null)
                minerFee = new BigDecimal("0");
        }
        // ptn,ptncny
        if ("PTN".equalsIgnoreCase(coinName)
                || "PTNCNY".equalsIgnoreCase(coinName)) {
            minerFee = tradeNum.multiply(coin.getMinFee());
        }

        // HSR
        if ("HSR".equalsIgnoreCase(coinName)) {
            minerFee = tradeNum.multiply(coin.getMinFee());
        }

        // QTUM
        if ("QTUM".equalsIgnoreCase(coinName)) {
            minerFee = tradeNum.multiply(coin.getMinFee());
        }

        return minerFee;
    }

    /**
     * c2c:钱包转账资产更新(只走库)
     *
     * @param buyUserWallet
     * @param sellUserWallet
     * @param dealNum        交易数量
     * @param subFreezeNum   交易数量+手续费+矿工费
     * @param minerFee       矿工费
     * @param orderNo
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public boolean tradeAndEditWalletWithLocal(UserWallet buyUserWallet,
                                               UserWallet sellUserWallet, BigDecimal dealNum,
                                               BigDecimal subFreezeNum, BigDecimal poundage, String orderNo) throws Exception {
        boolean flag = false;
        Integer coinType = buyUserWallet.getType();
        // 添加转账记录
        addTradeInfo(sellUserWallet.getUserId(), coinType,
                dealNum.toString(), poundage, sellUserWallet.getAddress(),
                buyUserWallet.getAddress(), orderNo, 1, "c2c:转账到买方", 0, null);

        //买方添加可用资产
        UserWallet updateBuyUserWallet = new UserWallet();
        updateBuyUserWallet.setId(buyUserWallet.getId());
        updateBuyUserWallet.setBalance(buyUserWallet.getBalance().add(dealNum));//可用资产加上成交量
        int buyResult = userWalletMapper.updateUserWalletByCondition(updateBuyUserWallet);
        if (buyResult <= 0) {
            throw new Exception("c2c:添加买方资产失败");
        }

        //卖方减少冻结资产
        UserWallet updateSellUserWallet = new UserWallet();
        updateSellUserWallet.setId(sellUserWallet.getId());
        updateSellUserWallet.setUnbalance(sellUserWallet.getUnbalance().subtract(subFreezeNum));//冻结资产减去成交量、手续费和矿工费
        int sellResult = userWalletMapper.updateUserWalletByCondition(updateSellUserWallet);
        if (sellResult <= 0) {
            throw new Exception("c2c:修改卖方资产失败");
        }
        flag = true;
        return flag;
    }

    /**
     * C2C交易出售方确认付款后进行钱包服务器转账
     *
     * @param buyUserWallet
     * @param sellUserWallet
     * @param dealNum
     * @param subFreezeNum
     * @param poundage
     * @param orderNo
     * @param responseParamsDto
     * @return
     * @throws Exception
     */
    public BaseResponse transfer4C2C(UserWallet buyUserWallet, UserWallet sellUserWallet, BigDecimal dealNum,
                                     BigDecimal subFreezeNum, BigDecimal poundage, SpotDealDetail spotDealDetail, ResponseParamsDto responseParamsDto) throws Exception {
        String transferResult = "";
        String transferLogPrefix = "{}钱包地址C2C转账参数tradeNum：{}，outAddress：{}，inAddress：{}，转账结果：{}";
        String outAddress = sellUserWallet.getAddress();
        String inAddress = buyUserWallet.getAddress();
        Integer coinNo = buyUserWallet.getType();

        Address address = new Address();
        address.setCoinNo(coinNo.longValue());
        address = addressMapper.getAddressByCondition(address);
        Coin coin = new Coin();
        coin.setCoinNo(coinNo.longValue());
        coin.setState(1);
        coin = coinMapper.getCoinByCondition(coin);

        //添加转账记录
        Long tradeInfoId = addTradeInfo(sellUserWallet.getUserId(), coinNo, dealNum.toString(),
                poundage, outAddress, inAddress, spotDealDetail.getOrderNo(), 1, "c2c:转账到买方", 0, null);

        //if (!"ptn_api".equals(coin.getApiType())) {}

        //钱包服务器资产减去冻结资产＝可用资产，是否够转账，转入方真实到账是tradeNum－平台矿工费
        BigDecimal balance = userWalletService.getEnableBalance(sellUserWallet, coin, address);
        log.info("balance: {}",balance);
        if (sellUserWallet.getUnbalance().compareTo(dealNum) == -1) {
            log.info("冻结金额小于交易金额");
            log.info("sellUserWallet.getUnbalance(): {}",sellUserWallet.getUnbalance());
            log.info("dealNum: {}",dealNum);
            return RspUtil.rspError(responseParamsDto.BALANCE_INSUFFICIENT_API_DESC);
        }

        String apiType = coin.getApiType();
        String coinName = coin.getCoinName();
        String transferErrMsg = coinName + "-地址转账失败";
        if ("eth_api".equals(apiType)) {
            //执行钱包地址转账
            EthcoinAPI ethcoinAPI = new EthcoinAPI(address);
            transferResult = transfer4Eth(String.valueOf(dealNum), outAddress, inAddress, ethcoinAPI);
            if (ValidataUtil.isNull(transferResult)) {
                logger.info(transferLogPrefix, coinName, dealNum, outAddress, inAddress, transferResult);
                return RspUtil.rspError(responseParamsDto.BALANCE_LOCKFAIL_DESC);
            }
        } else if ("ptn_api".equals(apiType)) {
            //钱包服务器资产减去冻结资产＝可用资产，是否够转账，转入方真实到账是tradeNum
            if (sellUserWallet.getUnbalance().compareTo(subFreezeNum) == -1) {
                log.info("subFreezeNum: {}",subFreezeNum);
                return RspUtil.rspError(responseParamsDto.BALANCE_INSUFFICIENT_API_DESC);
            }

            //执行钱包地址转账
            String tokenName = coinName.equalsIgnoreCase("MOC") ? "moc" : "moccny";
            PNTCoinApi api = PNTCoinApi.getApi(address);
            transferResult = api.sendTransaction(outAddress, inAddress, String.valueOf(dealNum.setScale(6, BigDecimal.ROUND_DOWN)), "", "c2c:转账到买方 ", String.valueOf(poundage), tokenName);
            if (ValidataUtil.isNull(transferResult)) {
                logger.info(transferLogPrefix, coinName, dealNum, outAddress, inAddress, transferResult);
                return RspUtil.rspError(responseParamsDto.BALANCE_LOCKFAIL_DESC);
            }
        }

        //成功转账后把hash值更新到转账记录中
        if (!"error".equals(transferResult)) {
            //2019年7月9日 12:16:15 更新订单号(去掉MD5加密)
//            String hash = MD5Util.GetMD5Code(coinName + transferResult);
            editTradeInfo(tradeInfoId, transferResult);
        }

        return RspUtil.success(tradeInfoId);
    }

    /**
     * 返还之前代币交易时冻结的ETH
     *
     * @param userWallet
     * @param enstrustNo 委托单号
     * @param count      释放冻结的份数
     * @throws Exception
     */
    public void freezeReturn4ETHTokens(UserWallet userWallet, String enstrustNo, String orderNo, int num) throws Exception {
        TokensFreeze tokensFreeze = new TokensFreeze();
        tokensFreeze.setEntrustNo(enstrustNo);
        List<TokensFreeze> conditions = tokensFreezeMapper.getByConditions(tokensFreeze);
        if (conditions.size() <= 0) {
            TokensFreeze tFreeze = new TokensFreeze();
            tFreeze.setOrderNo(orderNo);
            conditions = tokensFreezeMapper.getByConditions(tFreeze);
        }

        if (conditions.size() > 0) {
            //系统设置的C2C每笔交易冻结ETH资产数量
            Params paramPerTrade = paramsMapper.queryByKey("ethTokens_freezeNum_perTrade");
            BigDecimal paramPerTradeBigDecimal = new BigDecimal(paramPerTrade.getParamValue());
            //计算需要总返还的冻结ETH资产数量
            BigDecimal totalTradeBigDecimal = paramPerTradeBigDecimal.multiply(new BigDecimal(num));

            TokensFreeze tFreeze = conditions.get(0);
            BigDecimal subtract = tFreeze.getUnbalance().subtract(totalTradeBigDecimal);
            if (subtract.compareTo(new BigDecimal("0")) != -1) {
                tFreeze.setUnbalance(subtract);
                tFreeze.setUpdateTime(new Date());
                int count = tokensFreezeMapper.update(tFreeze);
                if (count > 0) {
                    //返还委托代币出售时，冻结过ETH资产
                    UserWallet uWallet = new UserWallet();
                    uWallet.setUserId(userWallet.getUserId());
                    uWallet.setType(20);
                    uWallet = userWalletMapper.getUserWalletByConditionForUpdate(uWallet);

                    editUserWallet(uWallet, totalTradeBigDecimal, "3");
                }
            }
        }
    }

    /**
     * 出售订单下架处理冻结的ETH
     *
     * @param userWallet
     * @param enstrustNo
     * @param successNum 已经成功匹配的订单
     * @throws Exception
     */
    @Override
    public void freezeAllReturn4ETHTokens(UserWallet userWallet, String enstrustNo, int successNum) throws Exception {
        TokensFreeze tokensFreeze = new TokensFreeze();
        tokensFreeze.setEntrustNo(enstrustNo);
        List<TokensFreeze> conditions = tokensFreezeMapper.getByConditions(tokensFreeze);
        if (conditions.size() > 0) {
            //系统设置的C2C每笔交易冻结ETH资产数量
            Params paramPerTrade = paramsMapper.queryByKey("ethTokens_freezeNum_perTrade");
            BigDecimal paramPerTradeBigDecimal = new BigDecimal(paramPerTrade.getParamValue());
            //计算已经消耗的ETH冻结资产数量
            BigDecimal totalTradeBigDecimal = paramPerTradeBigDecimal.multiply(new BigDecimal(successNum));

            TokensFreeze tFreeze = conditions.get(0);
            //剩余应该返还的ETH数量
            BigDecimal ethLeft = tFreeze.getTotalUnbalance().subtract(totalTradeBigDecimal);
            BigDecimal subtract = tFreeze.getUnbalance().subtract(ethLeft);
            if (subtract.compareTo(new BigDecimal("0")) != -1) {
                tFreeze.setUnbalance(subtract);
                tFreeze.setUpdateTime(new Date());
                int count = tokensFreezeMapper.update(tFreeze);
                if (count > 0) {
                    //返还委托代币出售时，冻结过ETH资产
                    UserWallet uWallet = new UserWallet();
                    uWallet.setUserId(userWallet.getUserId());
                    uWallet.setType(20);
                    uWallet = userWalletMapper.getUserWalletByConditionForUpdate(uWallet);

                    editUserWallet(uWallet, ethLeft, "3");
                }
            }
        }
    }

    /**
     * 解冻用户数据库资产
     *
     * @param userWallet
     * @param unfreezeNum
     * @return
     * @throws Exception
     */
    @Override
    public int unfreezeUserWalletBalance(UserWallet userWallet,
                                         BigDecimal unfreezeNum) throws Exception {
        int result = 0;

        if (userWallet == null) {
            throw new Exception("解冻资产失败,钱包不存在");
        }
        UserWallet userWalletUpdate = new UserWallet();
        userWalletUpdate.setBalance(userWallet.getBalance().add(unfreezeNum));
        BigDecimal subUnBalance = userWallet.getUnbalance().subtract(unfreezeNum);
        if (subUnBalance.compareTo(new BigDecimal("0")) == -1) {
            logger.warn("c2c:解冻卖方资产后冻结资产为负，解冻异常");
            return result;
        }
        userWalletUpdate.setUnbalance(subUnBalance);
        userWalletUpdate.setId(userWallet.getId());
        result = userWalletMapper.updateUserWalletByCondition(userWalletUpdate);

        return result;
    }


    /**
     * 比特币系币种转账
     *
     * @param tradeNum  转账数量
     * @param inAddress 转入地址
     * @param address
     * @return
     * @throws Exception
     */
    private String transfer4Bit(String tradeNum, String inAddress, Address address) throws Exception {
        String APIResult = null;

        BitcoinAPI bitcoinAPI = new BitcoinAPI(address.getAddress(), address.getPort(), address.getName(), address.getPassword(), address.getLockPassword());
        bitcoinAPI.closewallet();
        bitcoinAPI.openwallet();
        APIResult = bitcoinAPI.sendToAddress(inAddress, new BigDecimal(tradeNum));
        bitcoinAPI.closewallet();
        if (ValidataUtil.isNull(APIResult)) {
            throw new Exception("BTC-地址转账失败！");
        }
        return APIResult;
    }

    /**
     * 以太坊系币种转账
     *
     * @param tradeNum   转账数量
     * @param outAddress 转出地址
     * @param inAddress  转入地址
     * @param ethcoinAPI
     * @return
     * @throws Exception
     */
    private String transfer4Eth(String tradeNum, String outAddress, String inAddress, EthcoinAPI ethcoinAPI) throws Exception {
        String APIResult = null;

        //交易数量转换为16进制
        BigDecimal tradeNumWei = new BigDecimal(tradeNum).multiply(EthcoinAPI.wei);//先转换成以太坊单位
        String bigIntStr = tradeNumWei.setScale(0, BigDecimal.ROUND_DOWN).toString();
        String tradeNumEth = new BigInteger(bigIntStr, 10).toString(16);//转换成ETH转账用的16进制数值
        APIResult = ethcoinAPI.sendTransaction(outAddress, inAddress, "0x" + tradeNumEth);
        if (ValidataUtil.isNull(APIResult)) {
            throw new Exception("ETH-地址转账失败");
        }
        return APIResult;
    }

    /**
     * TODO 需要把每笔交易获得的手续费记录下来
     * 添加转账记录并修改数据库资产
     *
     * @param tradeNum               转账数量
     * @param fee                    手续费
     * @param sumNum                 交易总数量
     * @param outUserWalletForUpdate
     * @param outAddress             转出地址
     * @param inAddress              转入地址
     * @param user
     * @param coinNo                 币种编号
     * @param remark
     * @return
     * @throws Exception
     */
    private Long addTradeInfoAndReduceUserWallet(String tradeNum, String fee, BigDecimal sumNum, UserWallet outUserWalletForUpdate, String outAddress, String inAddress, User user, Coin coin, String remark) throws Exception {
        String apiType = coin.getApiType();
        String tokenName = coin.getCoinName().toLowerCase();
        //添加交易记录
        Long tradeInfoId = addTradeInfo(user.getId(), coin.getCoinNo().intValue(), tradeNum, new BigDecimal(fee), outAddress, inAddress, ValidataUtil.generateUUID(), 1, remark, 0, null);
        //修改用户数据库资产
        if ("ptn_api".equals(apiType)) {
            //代币
            if (!tokenName.equalsIgnoreCase("moc")) {
                //如果是代币，则不扣代币手续费，扣除主币手续费
                editUserWallet(outUserWalletForUpdate, new BigDecimal(tradeNum), "1");
            }else {
                //主币
                editUserWallet(outUserWalletForUpdate, sumNum, "1");
            }
        } else {
            //其他货币
            editUserWallet(outUserWalletForUpdate, sumNum, "1");
        }
        return tradeInfoId;
    }

    /**
     * 注册奖励或邀请奖励后
     * 1.新增交易记录，更新转账hash值
     * 2.增加用户资产
     *
     * @param tradeNum       转账数量
     * @param fee            手续费
     * @param address        用户PTN钱包地址
     * @param coinNo         币种编号
     * @param remark         备注
     * @param transferResult 转账返回的hash值
     * @return
     * @throws Exception
     */
    public void addTradeInfoAndAddUserWallet(String tradeNum, String fee, String address, String remark, String transferResult, UserWallet userWallet) throws Exception {
        Integer coinNo = 40;

        //添加交易记录
        Long tradeInfoId = addTradeInfo(0L, coinNo, tradeNum, new BigDecimal(fee), "", address, ValidataUtil.generateUUID(), 1, remark, 0, null);
        //更新订单号
        String hash = MD5Util.GetMD5Code("PTN" + transferResult);
        editTradeInfo(tradeInfoId, hash);

        //修改用户数据库资产
        if (userWallet != null) {
            editUserWallet(userWallet, new BigDecimal(tradeNum), "0");
        }
    }

    @Override
    public String tradeMocToSystem(Long userId, BigDecimal amount, BigDecimal fee,
                              String toAddress, String remark) {
        String coinName = "moc";
        String txHash = null;
        Coin coin = coinMapper.queryByCoinName(coinName);
        Long coinNo = coin.getCoinNo();
        String apiType = coin.getApiType();

        UserWallet qeryCond = new UserWallet();
        qeryCond.setUserId(userId);
        qeryCond.setType(coinNo.intValue());
        UserWallet userWallet = userWalletMapper.getUserWalletByCondition(qeryCond);
        String fromAddress = userWallet.getAddress();
        if("ptn_api".equalsIgnoreCase(apiType)) {
            PNTCoinApi api = PNTCoinApi.getApi(getAddressByCoinNo());
            txHash = api.sendTransaction(fromAddress, toAddress, amount.toString(), "", remark, fee.toString(), coinName);

            logger.info(txHash);
            if (StringUtils.isNotBlank(txHash)) {
                //添加转账记录
                TradeInfo tradeInfo = new TradeInfo();
                tradeInfo.setHash(txHash);
                tradeInfo.setCoinNo(coinNo.intValue());
                tradeInfo.setDate(new Date(System.currentTimeMillis()));
                tradeInfo.setUserId(userId);
                tradeInfo.setTradeNum(amount);
                // 0为转入1为转出
                tradeInfo.setType(0);
                tradeInfo.setOrderNo(ValidataUtil.generateUUID());
                tradeInfo.setOutAddress(fromAddress);
                tradeInfo.setInAddress(toAddress);
                // 1转账成功
                tradeInfo.setState(1);
                tradeInfo.setRatio(fee);
                tradeInfo.setRemark(remark);
                tradeInfoMapper.insertTradeInfo(tradeInfo);
                //修改数据库账户余额
                //更新转出方数据库金额
                BigDecimal balance = userWallet.getBalance();
                BigDecimal newBalance = balance.subtract(amount.add(fee));
                UserWallet update = new UserWallet();
                update.setId(userWallet.getId());
                update.setBalance(newBalance);
                userWalletMapper.updateUserWalletByCondition(update);
            } else {
                logger.info("转账失败,userID={}", userId);
            }
        } else {
            logger.error("coin={},apiType不是ptn_api", coin);
        }
        return txHash;
    }

    @Override
    public String payTrade(Long userId,String coinType, BigDecimal amount, BigDecimal fee, String toAddress, String remark,String appId) {
        String coinName = coinType;
        String txHash = null;
        Coin coin = coinMapper.queryByCoinName(coinName);
        Long coinNo = coin.getCoinNo();
        String apiType = coin.getApiType();

        UserWallet qeryCond = new UserWallet();
        qeryCond.setUserId(userId);
        qeryCond.setType(coinNo.intValue());
        UserWallet userWallet = userWalletMapper.getUserWalletByCondition(qeryCond);
        String fromAddress = userWallet.getAddress();
        if("ptn_api".equalsIgnoreCase(apiType)) {
            PNTCoinApi api = PNTCoinApi.getApi(getAddressByCoinNo());
            txHash = api.sendTransaction(fromAddress, toAddress, amount.toString(), "", remark, fee.toString(), coinName);

            logger.info(txHash);
            if (StringUtils.isNotBlank(txHash)) {
                //添加转账记录
                TradeInfo tradeInfo = new TradeInfo();
                tradeInfo.setHash(txHash);
                tradeInfo.setCoinNo(coinNo.intValue());
                tradeInfo.setDate(new Date(System.currentTimeMillis()));
                tradeInfo.setUserId(userId);
                tradeInfo.setTradeNum(amount);
                // 0为转入1为转出
                tradeInfo.setType(0);
                tradeInfo.setOrderNo(ValidataUtil.generateUUID());
                tradeInfo.setOutAddress(fromAddress);
                tradeInfo.setInAddress(toAddress);
                // 1转账成功
                tradeInfo.setState(1);
                tradeInfo.setRatio(fee);
                tradeInfo.setRemark(remark);
                tradeInfoMapper.insertTradeInfo(tradeInfo);
                //修改数据库账户余额
                //更新转出方数据库金额
               /* BigDecimal balance = userWallet.getBalance();
                BigDecimal newBalance = balance.subtract(amount.add(fee));
                UserWallet update = new UserWallet();
                update.setId(userWallet.getId());
                update.setBalance(newBalance);
                update.setType(Integer.valueOf(coinNo.toString()));
                userWalletMapper.updateUserWalletByCondition(update);*/
                //释放冻结
                payMapper.freeFreeze(userId,coin.getCoinNo(),amount.add(BigDecimal.valueOf(0.01)).toString());
                //往商户地址添加相应数量的币
                payMapper.grantMoney(appId,amount.toString(),coin.getCoinNo());
                //插入记录
                payMapper.insertRecord(appId,toAddress,userId,coinType,txHash,amount.toString());

            } else {
                logger.info("用户支付给商家转账失败,userID={},fromAddress={},toAddress={}", userId,fromAddress,toAddress);
            }
        } else {
            logger.error("coin={},apiType不是ptn_api", coin);
        }
        return txHash;
    }

    @Override
    public String buyPointTrade(Long userId, BigDecimal amount, BigDecimal fee, String toAddress, String coinName, String remark) {
        String txHash = null;
        Coin coin = coinMapper.queryByCoinName(coinName);
        Long coinNo = coin.getCoinNo();
        String apiType = coin.getApiType();

        UserWallet qeryCond = new UserWallet();
        qeryCond.setUserId(userId);
        qeryCond.setType(coinNo.intValue());
        UserWallet userWallet = userWalletMapper.getUserWalletByCondition(qeryCond);
        String fromAddress = userWallet.getAddress();
        if("ptn_api".equalsIgnoreCase(apiType)) {
            PNTCoinApi api = PNTCoinApi.getApi(getAddressByCoinNo());
            txHash = api.sendTransaction(fromAddress, toAddress, amount.toString(), "", remark, fee.toString(), coinName);

            logger.info(txHash);
            if (StringUtils.isNotBlank(txHash)) {
                //添加转账记录
                TradeInfo tradeInfo = new TradeInfo();
                tradeInfo.setHash(txHash);
                tradeInfo.setCoinNo(coinNo.intValue());
                tradeInfo.setDate(new Date(System.currentTimeMillis()));
                tradeInfo.setUserId(userId);
                tradeInfo.setTradeNum(amount);
                // 0为转入1为转出
                tradeInfo.setType(0);
                tradeInfo.setOrderNo(ValidataUtil.generateUUID());
                tradeInfo.setOutAddress(fromAddress);
                tradeInfo.setInAddress(toAddress);
                // 1转账成功
                tradeInfo.setState(1);
                tradeInfo.setRatio(fee);
                tradeInfo.setRemark(remark);
                tradeInfoMapper.insertTradeInfo(tradeInfo);
                //修改数据库账户余额
                //更新转出方数据库金额
                BigDecimal balance = userWallet.getBalance();
                BigDecimal newBalance = balance.subtract(amount.add(fee));
                UserWallet update = new UserWallet();
                update.setId(userWallet.getId());
                update.setBalance(newBalance);
                update.setType(Integer.valueOf(coinNo.toString()));
                userWalletMapper.updateUserWalletByCondition(update);

            } else {
                logger.info("用户购买积分转账失败,userID={},fromAddress={},toAddress={}", userId,fromAddress,toAddress);
            }
        } else {
            logger.error("coin={},apiType不是ptn_api", coin);
        }
        return txHash;
    }

    @Override
    public String systemToUserTrade(String mocAddress, Long userId,String amount, String pubKey,  String remark,String fee,String coinName) {

        String txHash = null;
        Coin coin = coinMapper.queryByCoinName(coinName);
        Long coinNo = coin.getCoinNo();
        String apiType = coin.getApiType();

        UserWallet qeryCond = new UserWallet();
        qeryCond.setUserId(userId);
        qeryCond.setType(coinNo.intValue());
        UserWallet userWallet = userWalletMapper.getUserWalletByCondition(qeryCond);
        String toAddress = userWallet.getAddress();
        if("ptn_api".equalsIgnoreCase(apiType)) {
            PNTCoinApi api = PNTCoinApi.getApi(getAddressByCoinNo());
            txHash = api.sendTransaction(mocAddress, toAddress, amount.toString(), pubKey, remark, fee.toString(), coinName);

            logger.info(txHash);
            if (StringUtils.isNotBlank(txHash)) {
                //添加转账记录
                TradeInfo tradeInfo = new TradeInfo();
                tradeInfo.setHash(txHash);
                tradeInfo.setCoinNo(coinNo.intValue());
                tradeInfo.setDate(new Date(System.currentTimeMillis()));
                tradeInfo.setUserId(Long.valueOf(0));
                tradeInfo.setTradeNum(new BigDecimal(amount));
                // 0为转入1为转出
                tradeInfo.setType(0);
                tradeInfo.setOrderNo(ValidataUtil.generateUUID());
                tradeInfo.setOutAddress(mocAddress);
                tradeInfo.setInAddress(toAddress);
                // 1转账成功
                tradeInfo.setState(1);
                tradeInfo.setRatio(new BigDecimal(fee));
                tradeInfo.setRemark(remark);
                tradeInfoMapper.insertTradeInfo(tradeInfo);
                //修改数据库账户余额
                //更新转出方数据库金额
                BigDecimal balance = userWallet.getBalance();
                BigDecimal newBalance = balance.add(new BigDecimal(amount));
                UserWallet update = new UserWallet();
                update.setId(userWallet.getId());
                update.setBalance(newBalance);
                userWalletMapper.updateUserWalletByCondition(update);
            } else {
                logger.info("转账失败,userID={}", userId);
            }
        } else {
            logger.error("coin={},apiType不是ptn_api", coin);
        }
        return txHash;
    }

    private Address getAddressByCoinNo() {
        Address address = new Address();
        address.setCoinNo(40L);
        address.setStatus(1);
        address = addressMapper.getAddressByCondition(address);
        return address;
    }
}
