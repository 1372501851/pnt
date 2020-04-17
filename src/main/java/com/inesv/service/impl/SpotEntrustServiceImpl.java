package com.inesv.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.EntrustService;
import com.inesv.service.SpotEntrustService;
import com.inesv.service.TradeInfoService;
import com.inesv.service.UserWalletService;
import com.inesv.sms.YunPianSmsUtils;
import com.inesv.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;

@Service
public class SpotEntrustServiceImpl implements SpotEntrustService {

    private static Logger logger = LoggerFactory.getLogger(SpotEntrustServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CoinMapper coinTypeMapper;

    @Autowired
    private SpotEntrustMapper spotEntrusMapper;

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private UserBankMapper userBankMapper;

    @Autowired
    private EntrustService entrustService;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private TradeInfoService tradeService;

    @Autowired
    private ParamsMapper paramsMapper;

    @Autowired
    private SpotDealDetailMapper spotDealDetailMapper;

    @Autowired
    private TokensFreezeMapper tokensFreezeMapper;

    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private IDCardMapper idCardMapper;

    /**
     * C2C发布广告
     * @param userNo 用户id
     * @param coinNo 币种编号
     * @param tradeType 交易类型0：买，1：卖
     * @param tradePrice 交易价格
     * @param tradeNum 交易数量
     * @param receivablesType 收付款类型（1：银行卡，2：支付宝，3：微信）
     * @param minTradeNum 最小交易数量
     * @param tradePassword 交易密码
     * @param remark
     * @param bankcardId 当receivablesType＝1时bankcardId不能为空
     * @param responseParamsDto
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public BaseResponse<Map<String, Object>> addEntrust(Long userNo, Integer coinNo, String tradeType,
                                                        BigDecimal tradePrice, BigDecimal tradeNum, Integer receivablesType, BigDecimal minTradeNum,
                                                        String tradePassword, String remark, Integer bankcardId, ResponseParamsDto responseParamsDto)throws Exception {
        //C2C交易前置判断
        BaseResponse<Map<String, Object>> result = conditionJudge(userNo, tradePrice, tradeNum, receivablesType, minTradeNum, tradePassword, responseParamsDto);
        if (result != null && result.getMessage() != null){
            return result;
        }

        // 支收付方式为银行卡则银行卡号不可为空
        if (receivablesType == 1) {
            if (bankcardId == null) {
                return RspUtil.rspError(responseParamsDto.FAIL_SPOT_NO_BANK_DESC);
            }
        } else {
            bankcardId = 0;
        }

        // 判断交易货币状态
        Coin coin = new Coin();
        coin.setCoinNo((long) coinNo);
        coin.setState(1);
        coin = coinTypeMapper.getCoinByCondition(coin);
        if (coin == null) {
            return RspUtil.rspError(responseParamsDto.FAIL_COIN_TYPE_STATE_DESC);
        }
        if (coin.getMinFee() == null || coin.getMinFee().toString().equals("")) {
            logger.warn("手续费率为0，发布广告拒绝！");
            return RspUtil.rspError(responseParamsDto.FAIL_SPOT_ADDTRUST_STATE_DESC);
        }

        // 判断用户交易货币资产
        UserWallet userWallet = new UserWallet();
        userWallet.setUserId(userNo.longValue());
        userWallet.setType(coinNo);
        userWallet = userWalletMapper.getUserWalletByConditionForUpdate(userWallet);
        if (userWallet == null) {
            return RspUtil.rspError(responseParamsDto.FAIL_TRADE_BALANCE_DESC);
        }

        SpotEntrust spotEntrust = new SpotEntrust();
        BigDecimal sumTradeNum = new BigDecimal("0"); //交易总数量（出售数量+手续费）
        BigDecimal sellPoundage = new BigDecimal("0"); //出售手续费
        BigDecimal recordMinTradeNum = minTradeNum;//最小出售数量
        BigDecimal recordMaxTradeNum = tradeNum;//最高出售数量
        String entrustNo = OrderUtil.makeOrderCode();//生成委托号
        BigDecimal totalETHFreeze = new BigDecimal("0");//委托代币出售需要冻结的ETH数量

        SpotEntrust.Builder builder = new SpotEntrust.Builder()
                .setId(0L).setEntrustNo(entrustNo)
                .setUserNo(userNo.intValue())
                .setEntrustCoin(coinNo)
                .setEntrustPrice(tradePrice)
                .setEntrustMinPrice(recordMinTradeNum)
                .setEntrustMaxPrice(recordMaxTradeNum)
                .setRecordMinPrice(recordMinTradeNum)
                .setRecordMaxPrice(recordMaxTradeNum)
                .setEntrustNum(tradeNum)
                .setDealNum(new BigDecimal("0"))
                .setMatchNum(new BigDecimal("0"))
                .setPoundageCoin(coinNo)
                .setPoundageScale(coin.getMinFee())
                .setReceivablesType(receivablesType)
                .setBankcardId(bankcardId)
                .setMatchingType(0)
                .setState(0)
                .setRemark(remark);

        if (tradeType.equals("buy")) { // 买
            // 添加委托实体
            spotEntrust = builder.setEntrustType(0).setPoundage(new BigDecimal("0")).build();
        }

        if (tradeType.equals("sell")) { // 卖
            sellPoundage = tradeNum.multiply(coin.getMinFee());//出售手续费=出售数量*手续费率，目前只有min_fee有效，max_fee暂时弃用
            sumTradeNum = tradeNum.add(sellPoundage);//交易总数量（出售数量+手续费）

            //判断数据库和钱包服务器可用资产是否小于出售数量+手续费sumTradeNum
            BigDecimal enableBalance = userWallet.getBalance();//数据库可用资产
            if (enableBalance.compareTo(sumTradeNum) == -1) {
                return RspUtil.rspError(responseParamsDto.NOT_ENOUGH_ENABLE_BALANCE);
            }

            //if (!"ptn_api".equals(coin.getApiType())) {}

            enableBalance = userWalletService.getEnableBalance(userWallet, coin, null);//钱包服务器可用资产
            if (enableBalance.subtract(userWallet.getUnbalance()).compareTo(sumTradeNum) == -1) {
                return RspUtil.rspError(responseParamsDto.NOT_ENOUGH_ENABLE_BALANCE);
            }

            //代币交易还需要判断ETH资产是否足够及冻结ETH资产
            if ("eth_tokens_api".equals(coin.getApiType())) {
                Map<String, Object> map = ifETHEnough4ETHTokensEntrustSell(recordMaxTradeNum, recordMinTradeNum, userWallet);

                //委托ETH代币出售时,ETH资产必须足够冻结
                if (!CastUtils.castBoolean(map.get("flag"))) {
                    return RspUtil.rspError(responseParamsDto.NOT_ENOUGH_ETH_ENABLE_BALANCE);
                }

                totalETHFreeze = new BigDecimal(CastUtils.castString(map.get("totalETHFreeze")));
            }

            // 添加委托实体
            spotEntrust = builder.setEntrustType(1).setPoundage(sellPoundage).build();
        }
        try {
            // 发布广告逻辑
            Long entrustId = entrustService.addSpotEntrust(spotEntrust, userWallet, sumTradeNum, totalETHFreeze, coin);
            Map<String, Object> data = new HashMap<>();
            data.put("entrustId", entrustId);

            //新增提醒管理员
            Params params = paramsMapper.queryByKey("C2C_TIPS_PHONE");
            String tipsNumber=params.getParamValue();
            if (tipsNumber != null) {
                YunPianSmsUtils.tipsOrderMatch("+86",params.getParamValue());
            }

            return RspUtil.success(200, responseParamsDto.SUCCESS_SPOT_ADDTRUST_STATE_DESC, data);
        } catch (Exception e) {
            logger.error("C2C挂单失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return RspUtil.rspError(responseParamsDto.FAIL_SPOT_ADDTRUST_STATE_DESC);
        }
    }

    /**
     * 用户匹配代币购买单时，判断ETH资产是否足够
     * @param totalETHFreeze 代币委托出售时需要冻结的ETH数量
     * @param userWallet
     * @return
     */
    private Map<String, Object> ifETHEnough4ETHTokensMatchBuy(BigDecimal totalETHFreeze, UserWallet userWallet){
        Map<String, Object> map = new HashMap<>();

        //系统设置的C2C每笔交易冻结ETH资产数量
        Params paramPerTrade = paramsMapper.queryByKey("ethTokens_freezeNum_perTrade");
        BigDecimal paramPerTradeBigDecimal = new BigDecimal(paramPerTrade.getParamValue());
        //出售交易需冻结的ETH资产数量
        totalETHFreeze = paramPerTradeBigDecimal;

        //取得ETH钱包服务器可用资产
        Coin ethCoin = new Coin();
        ethCoin.setCoinNo(20L);
        ethCoin.setState(1);
        ethCoin = coinTypeMapper.getCoinByCondition(ethCoin);
        BigDecimal enableBalance = userWalletService.getEnableBalance(userWallet, ethCoin, null);

        UserWallet uWallet = new UserWallet();
        uWallet.setType(20);
        uWallet.setAddress(userWallet.getAddress());
        uWallet = userWalletMapper.getUserWalletByCondition(uWallet);

        //委托ETH代币出售时,ETH资产必须足够冻结
        if (enableBalance.subtract(uWallet.getUnbalance()).compareTo(totalETHFreeze) == -1) {
            map.put("flag", false);
        }else{
            map.put("flag", true);
        }
        map.put("totalETHFreeze", totalETHFreeze);
        return map;
    }

    /**
     * ETH代币委托出售时，判断ETH资产是否足够
     * @param recordMaxTradeNum 委托数量
     * @param recordMinTradeNum 最小交易数量
     * @param totalETHFreeze 代币委托出售时需要冻结的ETH数量
     * @param userWallet
     * @return
     */
    private Map<String, Object> ifETHEnough4ETHTokensEntrustSell(BigDecimal recordMaxTradeNum, BigDecimal recordMinTradeNum, UserWallet userWallet){
        Map<String, Object> map = new HashMap<>();

        //根据委托交易数量和最小交易数量计算出最大交易笔数
        BigDecimal[] bigDecimals = recordMaxTradeNum.divideAndRemainder(recordMinTradeNum);
        BigDecimal resultFirst = bigDecimals[0];//商
        BigDecimal resultSecond = bigDecimals[1];//余数
        int freezeTimes = resultFirst.intValue();
        if(resultSecond.compareTo(new BigDecimal("0")) == 1){
            freezeTimes = freezeTimes + 1;
        }

        //系统设置的C2C最大交易笔数
        Params paramTotalTrade = paramsMapper.queryByKey("ethTokens_freezeTimes_totalTrade");
        int freezeTimesTotalTrade = CastUtils.castInt(paramTotalTrade.getParamValue());
        //计算出来的最大交易笔数大于系统设置的最大交易笔数，则取系统设置的最大交易笔数
        if(freezeTimes > freezeTimesTotalTrade){
            freezeTimes = freezeTimesTotalTrade;
        }

        //系统设置的C2C每笔交易冻结ETH资产数量
        Params paramPerTrade = paramsMapper.queryByKey("ethTokens_freezeNum_perTrade");
        BigDecimal paramPerTradeBigDecimal = new BigDecimal(paramPerTrade.getParamValue());
        //总冻结ETH资产数量计算
        BigDecimal totalETHFreeze = paramPerTradeBigDecimal.multiply(new BigDecimal(freezeTimes));

        //取得ETH钱包服务器可用资产
        Coin ethCoin = new Coin();
        ethCoin.setCoinNo(20L);
        ethCoin.setState(1);
        ethCoin = coinTypeMapper.getCoinByCondition(ethCoin);
        BigDecimal enableBalance = userWalletService.getEnableBalance(userWallet, ethCoin, null);

        UserWallet uWallet = new UserWallet();
        uWallet.setType(20);
        uWallet.setAddress(userWallet.getAddress());
        uWallet = userWalletMapper.getUserWalletByCondition(uWallet);

        //委托ETH代币出售时,ETH资产必须足够冻结
        if (enableBalance.subtract(uWallet.getUnbalance()).compareTo(totalETHFreeze) == -1) {
            map.put("flag", false);
        }else{
            map.put("flag", true);
        }
        map.put("totalETHFreeze", totalETHFreeze);

        return map;
    }

    /**
     * c2c:建立交易订单（匹配成功-发送短信）
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public BaseResponse<Map<String, Object>> spotTrade(Long userNo, String entrustNo, BigDecimal tradeNum,
                                                       String tradePassword, String remark, Integer bankId, ResponseParamsDto responseParamsDto) throws Exception {
        //判断委托记录是否存在
        SpotEntrust spotEntrust = new SpotEntrust();
        spotEntrust.setEntrustNo(entrustNo);
        spotEntrust = spotEntrusMapper.selectSpotEntrustByConditionForUpdate(spotEntrust);
        if (spotEntrust == null) {
            return RspUtil.rspError(responseParamsDto.FAIL_SPOT_USER_STATE_DESC);
        }

        // 用户是否为null
        User user = userMapper.getUserInfoById(userNo);
        if (user == null) {
            return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
        }
        // 交易密码是否为null
        if (StringUtils.isBlank(user.getTradePassword())) {
            return RspUtil.error(responseParamsDto.NO_TRADE_PASSWORD_DESC, 300);
        }
        // 判断交易密码是否一致
        if (!tradePassword.equals(user.getTradePassword())) {
            return RspUtil.rspError(responseParamsDto.FAIL_TRADE_PASSWORD_DESC);
        }
        // 判断是否本人的广告
        if (spotEntrust.getUserNo().toString().equals(userNo.toString())) {
            return RspUtil.rspError(responseParamsDto.FAIL_SPOT_USER_STATE_DESC);
        }

        // 判断交易数量不可小于最小限额,交易数量不可大于最大限额
        if (spotEntrust.getEntrustMinPrice().doubleValue() > tradeNum.doubleValue()
                || spotEntrust.getEntrustMaxPrice().doubleValue() < tradeNum.doubleValue()) {
            return RspUtil.rspError(responseParamsDto.FAIL_TRADE_MAXMIN_DATA_FORMAT_DESC);
        }
        // 判断剩余可匹配数量是否大于0,且可匹配数量是否大于等于交易量
        if (spotEntrust.getEntrustNum().compareTo(spotEntrust.getDealNum().add(spotEntrust.getMatchNum())) == 0 ||
                (spotEntrust.getEntrustNum() .compareTo(spotEntrust.getDealNum().add(spotEntrust.getMatchNum()).add(tradeNum))) == -1) {
            return RspUtil.rspError(responseParamsDto.NOT_ENOUGH_NUM_TO_MATCH);
        }

        // 判断交易货币状态及手续费
        Coin coin = new Coin();
        coin.setCoinNo((long) spotEntrust.getEntrustCoin());
        coin.setState(1);
        coin = coinTypeMapper.getCoinByCondition(coin);
        if (coin == null || coin.getMinFee().compareTo(new BigDecimal("0")) == -1) {
            return RspUtil.rspError(responseParamsDto.FAIL_COIN_TYPE_STATE_DESC);
        }

        //支付方式(1：银行卡，2：支付宝，3：微信，4：其他，5：ImToken)
        Integer receivablesType = spotEntrust.getReceivablesType();
        // 支付宝绑定验证
        if (receivablesType == 2 && StringUtils.isBlank(user.getApay())) {
            return RspUtil.rspError(responseParamsDto.FAIL_ALIPAY_NULL_DESC);
        }
        //微信绑定验证
        if (receivablesType == 3 && StringUtils.isBlank(user.getWeChat())) {
            return RspUtil.rspError(responseParamsDto.FAIL_WEIXIN_NULL_DESC);
        }
        //ImToken绑定验证
        if (receivablesType == 5 && StringUtils.isBlank(user.getImToken())) {
            return RspUtil.rspError(responseParamsDto.FAIL_IMTOKEN_NULL_DESC);
        }

        //匹配方银行卡信息验证
        String bankAccount = "";
        UserBank userBank = null;
        if (receivablesType == 1) {
            if (ValidataUtil.isNull(bankId.toString())) {
                return RspUtil.rspError(responseParamsDto.FAIL_SPOT_NO_BANK_DESC);
            }
            UserBank condition = new UserBank();
            condition.setId(bankId.longValue());
            userBank = userBankMapper.selectUserBankByID(condition);
            bankAccount = userBank.getCode();
            if (userBank == null && StringUtils.isBlank(bankAccount)) {
                return RspUtil.rspError(responseParamsDto.FAIL_SPOT_NO_BANK_DESC);
            }

            //查询委托方银行信息
            Integer bankId1 = spotEntrust.getBankcardId();
            condition = new UserBank();
            condition.setId(bankId1.longValue());
            userBank = userBankMapper.selectUserBankByID(condition);
        }

        //判断手续费是否异常
        if (spotEntrust.getPoundageScale() == null || spotEntrust.getPoundageScale().compareTo(new BigDecimal("0")) == -1) {
            return RspUtil.rspError(responseParamsDto.FAIL_SPOT_ADDTRUST_STATE_DESC);
        }
        // 判断用户交易货币资产
        BigDecimal freezeNum = new BigDecimal("0");// 须冻结量
        BigDecimal sellPoundage = tradeNum.multiply(spotEntrust.getPoundageScale());//手续费
        BigDecimal sumNum = sellPoundage.add(tradeNum);//交易数量+手续费
        BigDecimal totalETHFreeze = new BigDecimal("0");//委托代币出售需要冻结的ETH数量

        //判断进来匹配用户钱包地址情况
        UserWallet userWallet = new UserWallet();
        userWallet.setUserId(userNo.longValue());
        userWallet.setType(spotEntrust.getEntrustCoin());
        userWallet = userWalletMapper.getUserWalletByConditionForUpdate(userWallet); // 用户交易货币资产信息
        if (userWallet == null) {
            return RspUtil.rspError(responseParamsDto.EXPORT_FAIL_NULL_ADDRESS);
        }

        //查询委托方用户信息
        User userAccount = userMapper.getUserInfoById(Long.valueOf(spotEntrust.getUserNo()));
        //查询匹配方用户信息
        User userAccountMatch = userMapper.getUserInfoById(userNo);
        if (ValidataUtil.isNull(userAccount.getApay())) {
            userAccount.setApay("");
        }
        if (ValidataUtil.isNull(userAccountMatch.getApay())) {
            userAccountMatch.setApay("");
        }

        // 订单号
        String orderNo = OrderUtil.makeOrderCode();
        // 交易价总价
        BigDecimal tradePrice = tradeNum.multiply(spotEntrust.getEntrustPrice());
        //交易详情
        SpotDealDetail spotDealDetail = new SpotDealDetail();

        // 如果支付方式为银行卡，记录双方银行卡信息
        String buyAccount = "";
        String sellAccount = "";
        // 添加匹配单号实体
        SpotDealDetail.Builder builder = new SpotDealDetail.Builder()
                .setId(0L)
                .setOrderNo(orderNo)
                .setEntrustNo(spotEntrust.getEntrustNo())
                .setCoinNo(spotEntrust.getEntrustCoin())
                .setDealPrice(spotEntrust.getEntrustPrice())
                .setEntrustPrice(spotEntrust.getEntrustPrice())
                .setDealNum(tradeNum)
                .setSumPrice(tradePrice)
                .setPoundageScale(spotEntrust.getPoundageScale())
                .setPoundageCoin(spotEntrust.getEntrustCoin())//12
                .setState(3)
                .setMinerFee(new BigDecimal("0"))
                .setRemark(remark)
                .setReceivablesType(receivablesType)
                .setDate(new Date());
        if (spotEntrust.getEntrustType() == 0) { // 委托方为买，出售的用户进来匹配
            //查询数据库和钱包服务器可用资产是否小于出售数量+手续费sumNum
            BigDecimal enableBalance = userWallet.getBalance();//数据库可用资产
            if (enableBalance.compareTo(sumNum) == -1 ) {
                return RspUtil.rspError(responseParamsDto.FAIL_TRADE_INSUFFICIENT_DESC);
            }

            //if (!"ptn_api".equals(coin.getApiType())) {}

            enableBalance = userWalletService.getEnableBalance(userWallet, coin, null);//钱包服务器可用资产
            if (enableBalance.subtract(userWallet.getUnbalance()).compareTo(sumNum) == -1 ) {
                return RspUtil.rspError(responseParamsDto.FAIL_TRADE_INSUFFICIENT_DESC);
            }

            //代币交易还需要判断ETH资产是否足够及冻结ETH资产
            if ("eth_tokens_api".equals(coin.getApiType())) {
                Map<String, Object> map = ifETHEnough4ETHTokensMatchBuy(totalETHFreeze, userWallet);

                //委托ETH代币出售时,ETH资产必须足够冻结
                if (!CastUtils.castBoolean(map.get("flag"))) {
                    return RspUtil.rspError(responseParamsDto.NOT_ENOUGH_ETH_ENABLE_BALANCE);
                }

                totalETHFreeze = new BigDecimal(CastUtils.castString(map.get("totalETHFreeze")));
            }

            freezeNum = sumNum;

            // 如果支付方式为银行卡，记录双方银行卡信息
            if (receivablesType == 1) {
                buyAccount = userBank.getCode();//买方银行卡号
                sellAccount = bankAccount;//卖方银行卡号
            }
            if (receivablesType == 2) {
                buyAccount = userAccount.getApay();
                sellAccount = userAccountMatch.getApay();
            }
            if (receivablesType == 3) {
                buyAccount = userAccount.getWeChat();
                sellAccount = userAccountMatch.getWeChat();
            }
            if (receivablesType == 5) {
                buyAccount = userAccount.getImToken();
                sellAccount = userAccountMatch.getImToken();
            }

            spotDealDetail = builder.setBuyUserNo(spotEntrust.getUserNo())//4
                    .setSellUserNo(userNo.intValue())//5
                    .setPoundage(tradeNum.multiply(spotEntrust.getPoundageScale()))//13
                    .setBuyEntrust(spotEntrust.getId())//14
                    .setSellEntrust(0L)//15
                    .setMatchNo(0L)//16
                    .setBuyAccount(buyAccount)
                    .setSellAccount(sellAccount)
                    .build();
        }
        if (spotEntrust.getEntrustType() == 1) { // 委托方为卖，购买的用户进来匹配
            if (receivablesType == 1) {
                buyAccount = bankAccount;//买方银行卡号
                sellAccount = userBank.getCode();//卖方银行卡号
            }
            if (receivablesType == 2) {
                buyAccount = userAccountMatch.getApay();
                sellAccount = userAccount.getApay();
            }
            if (receivablesType == 3) {
                buyAccount = userAccountMatch.getWeChat();
                sellAccount = userAccount.getWeChat();
            }
            if (receivablesType == 5) {
                buyAccount = userAccountMatch.getImToken();
                sellAccount = userAccount.getImToken();
            }

            // 添加匹配单号实体
            spotDealDetail = builder.setBuyUserNo(userNo.intValue())//4
                    .setSellUserNo(spotEntrust.getUserNo())//5
                    .setPoundage(sellPoundage)//13
                    .setBuyEntrust(0L)//14
                    .setSellEntrust(spotEntrust.getId())//15
                    .setMatchNo(0L)//16
                    .setBuyAccount(buyAccount)
                    .setSellAccount(sellAccount)
                    .build();
        }

        try {
            logger.info("spot_passive_trading | params - user: " + userNo + " - trade begin !");
            // 生成订单处理
            Map<String, Object> data = tradeService.spotManualTrade(entrustNo, spotDealDetail, userWallet,
                    freezeNum, totalETHFreeze, coin);
            return RspUtil.success(200, responseParamsDto.SUCCESS_SPOT_MATCH_STATE_DESC, data);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String exception = sw.toString();
            logger.info(exception);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return RspUtil.rspError(responseParamsDto.FAIL_SPOT_MATCH_STATE_DESC);
        }
    }

    /**
     * 撤销广告
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public BaseResponse<Map<String, Object>> delEntrust(Integer userNo, String entrustNo,
                                                        ResponseParamsDto responseParamsDto) throws Exception {

        // 用户委托
        SpotEntrust spotEntrust = new SpotEntrust();
        spotEntrust.setUserNo(userNo);
        spotEntrust.setEntrustNo(entrustNo);
        spotEntrust = spotEntrusMapper.selectSpotEntrustByConditionForUpdate(spotEntrust);
        if (spotEntrust == null) {
            return RspUtil.rspError(responseParamsDto.FAIL_TRADE_DELENTRUST_DESC);
        }
        if (spotEntrust.getState() != 0 || (spotEntrust.getEntrustNum()
                .compareTo((spotEntrust.getDealNum().add(spotEntrust.getMatchNum()))) == 0)) {
            return RspUtil.rspError(responseParamsDto.FAIL_TRADE_DELENTRUST_STATE_DESC);
        }
        // 用户资产
        UserWallet userWallet = new UserWallet();
        BigDecimal cancelNum = new BigDecimal("0");// 广告撤销量（剩余未被成交和匹配的）
        BigDecimal unfreezeNum = new BigDecimal("0"); // 要解冻的资产
        if (spotEntrust.getEntrustType() == 0) { // 买
        }
        if (spotEntrust.getEntrustType() == 1) { // 卖
            userWallet.setUserId((long) userNo);
            userWallet.setType(spotEntrust.getEntrustCoin());
            userWallet = userWalletMapper.getUserWalletByConditionForUpdate(userWallet); // 用户交易货币资产信息
            cancelNum = (spotEntrust.getEntrustNum().subtract(spotEntrust.getDealNum())
                    .subtract(spotEntrust.getMatchNum()));
            if (userWallet == null || userWallet.getUnbalance() == null) {
                return RspUtil.rspError(responseParamsDto.EXPORT_FAIL_NULL_ADDRESS);
            }
            // 要解冻的数量
            unfreezeNum = (cancelNum.add(cancelNum.multiply(spotEntrust.getPoundageScale())));
            if (userWallet.getUnbalance().compareTo(unfreezeNum) == -1) {
                logger.warn("已冻结资产少于解冻的资产,撤销委托失败！");
                return RspUtil.rspError(responseParamsDto.FAIL_TRADE_DELENTRUST_STATE_DESC);
            }
        }
        // 撤销委托逻辑
        try {
            delEntrustLogic(spotEntrust, userWallet, cancelNum, unfreezeNum);
            return RspUtil.successMsg(responseParamsDto.SUCCESS_SPOT_DELENTRUST_DESC);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String exception = sw.toString();
            logger.info(exception);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return RspUtil.rspError(responseParamsDto.FAIL_TRADE_DELENTRUST_STATE_DESC);
        }
    }

    /**
     * 撤销广告委托逻辑
     *
     * @param spotEntrustDto
     * @param userBalanceDto
     * @param entrustPrice
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void delEntrustLogic(SpotEntrust spotEntrust, UserWallet userWallet, BigDecimal cancelNum,
                                BigDecimal unfreezeNum) throws Exception {
        SpotEntrust delSpotEntrust = new SpotEntrust();
        delSpotEntrust.setId(spotEntrust.getId());
        delSpotEntrust.setUserNo(spotEntrust.getUserNo());
        delSpotEntrust.setState(2);// 已撤销
        delSpotEntrust.setCancelNum(cancelNum);// 撤销时剩余数量
        // 修改广告
        int flag = spotEntrusMapper.updateSpotEntrust(delSpotEntrust);
        if (flag != 1) {
            throw new Exception(" spot delSpotEntrust | editSpotEntrust error !");
        }
        if (spotEntrust.getEntrustType() == 1) {
            // 解冻卖方资产
            int code = tradeService.unfreezeUserWalletBalance(userWallet, unfreezeNum);
            if (code != 1) {
                throw new Exception("spot delSpotEntrust | editBalance error !");
            }

            //返还未交易的eth资产
            Coin coin = new Coin();
            coin.setCoinNo(spotEntrust.getEntrustCoin().longValue());
            coin.setState(1);
            coin = coinMapper.getCoinByCondition(coin);
            if("eth_tokens_api".equals(coin.getApiType())){
                String entrustNo = spotEntrust.getEntrustNo();

                //总冻结ETH-已匹配订单数量*eth的代币每笔C2C交易冻结的资产数量
                SpotDealDetail spotDealDetail = new SpotDealDetail();
                spotDealDetail.setEntrustNo(entrustNo);

                List<SpotDealDetail> spotDealDetails = spotDealDetailMapper.selectSpotDealDetailByConditions(spotDealDetail);
                int count = 0;//已交易的订单
                for (SpotDealDetail dealDetail : spotDealDetails) {
                    if(dealDetail.getState() != 2 && dealDetail.getState() !=5){
                        count = count + 1;
                    }
                }

                //返还之前代币交易时冻结的ETH
                tradeService.freezeAllReturn4ETHTokens(userWallet, entrustNo, count);
            }
        }
    }

    @Override
    public BaseResponse getSpotEntrust(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String coinNo = json.getString("coinNo");
        String entrustType = json.getString("entrustType");
        String pageSize = json.getString("pageSize");
        String lineSize = json.getString("lineSize");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        if (ValidataUtil.isNull(coinNo))
            return RspUtil.rspError(responseParamsDto.COIN_NULL_DESC);
        if (ValidataUtil.isNull(entrustType))
            return RspUtil.rspError(responseParamsDto.TYPE_NULL_DESC);
        if (ValidataUtil.isNull(pageSize))
            return RspUtil.rspError(responseParamsDto.PAGEORLINE_NULL_DESC);
        if (ValidataUtil.isNull(lineSize))
            return RspUtil.rspError(responseParamsDto.PAGEORLINE_NULL_DESC);

        SpotEntrust spotEntrust = new SpotEntrust();
        spotEntrust.setEntrustCoin(Integer.valueOf(coinNo));
        spotEntrust.setEntrustType(Integer.valueOf(entrustType));
        spotEntrust.setState(0); // 状态：委托中
        spotEntrust.setJudgeType("1"); // 数量：>0

        List<SpotEntrust> spotEntrusts = new ArrayList<>();

        if (pageSize.equals("0") && lineSize.equals("0")) {
            spotEntrusts = spotEntrusMapper.selectSpotEntrustByConditions(spotEntrust);
            for (int i = 0, len = spotEntrusts.size(); i < len; i++) {
                if (spotEntrusts.get(i).getUsername().length() >= 8) {
                    spotEntrusts.get(i)
                            .setUsername(spotEntrusts.get(i).getUsername().substring(0, 3) + "****"
                                    + spotEntrusts.get(i).getUsername().substring(
                                    spotEntrusts.get(i).getUsername().length() - 3,
                                    spotEntrusts.get(i).getUsername().length()));
                }
            }
        } else {
            PageHelper.startPage(Integer.valueOf(pageSize), Integer.valueOf(lineSize));
            spotEntrusts = spotEntrusMapper.selectSpotEntrustByConditions(spotEntrust);
            for (int i = 0, len = spotEntrusts.size(); i < len; i++) {
                if (spotEntrusts.get(i).getUsername().length() >= 8) {
                    spotEntrusts.get(i)
                            .setUsername(spotEntrusts.get(i).getUsername().substring(0, 3) + "****"
                                    + spotEntrusts.get(i).getUsername().substring(
                                    spotEntrusts.get(i).getUsername().length() - 3,
                                    spotEntrusts.get(i).getUsername().length()));
                }
            }
        }

        for (SpotEntrust entrust:spotEntrusts) {
            //获取用户信息拿到手机号
            User user=userMapper.getUserInfoById(Long.valueOf(entrust.getUserNo()));
            if (user != null){
                entrust.setPhone(user.getPhone());
                entrust.setAreaCode(user.getAreaCode());
            }
            //获取货币信息拿到货币图片
            Coin coin=coinMapper.getCoinByCoinNo(Long.valueOf(entrust.getEntrustCoin()));
            if (coin !=null && !ValidataUtil.isNull(coin.getCoinImg())){
                entrust.setCoinImg(coin.getCoinImg());
            }

            BigDecimal scale =entrust.getDealNum().divide(entrust.getEntrustNum(),2, BigDecimal.ROUND_HALF_EVEN);
            logger.info("订单编号: {}，订单完成度: {}%",entrust.getEntrustNo(),scale.multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString());
            entrust.setTradeScale(scale.multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString()+"%");
        }

        return RspUtil.success(JSONFormat.getStr(spotEntrusts));
    }

    @Override
    public BaseResponse getSpotEntrustByUserNo(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String coinNo = json.getString("coinNo");
        String token = json.getString("token");
        String entrustState = json.getString("entrustState"); // 0:委托中,1：已完成，2：已撤销，3：匹配中 | 0:进行中，1：已完成
        String entrustType = json.getString("entrustType");//委托类型0.买1.卖。 非0非1为混合列表
        String pageSize = json.getString("pageSize");
        String lineSize = json.getString("lineSize");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(coinNo))
            return RspUtil.rspError(responseParamsDto.COIN_NULL_DESC);
        if (ValidataUtil.isNull(entrustState))
            return RspUtil.rspError(responseParamsDto.STATE_NULL_DESC);
        if (ValidataUtil.isNull(entrustType))
            return RspUtil.rspError(responseParamsDto.TYPE_NULL_DESC);
        if (ValidataUtil.isNull(pageSize))
            return RspUtil.rspError(responseParamsDto.PAGEORLINE_NULL_DESC);
        if (ValidataUtil.isNull(lineSize))
            return RspUtil.rspError(responseParamsDto.PAGEORLINE_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        SpotEntrust spotEntrust = new SpotEntrust();
        spotEntrust.setUserNo(Integer.valueOf(user.getId().toString()));
        spotEntrust.setEntrustCoin(Integer.valueOf(coinNo));
        //entrustType委托类型0.买1.卖  非0非1为混合
        if(entrustType.equals("0") ||entrustType.equals("1") ){
            spotEntrust.setEntrustType(Integer.valueOf(entrustType));
        }

        if (entrustState.equals("0")) { // 进行中
            spotEntrust.setConductState(1);
        }
        if (entrustState.equals("1")) { // 已完成
            spotEntrust.setCompleteState(1);
        }

        List<SpotEntrust> spotEntrusts = new ArrayList<>();

        if (pageSize.equals("0") && lineSize.equals("0")) {
            spotEntrusts = spotEntrusMapper.selectSpotEntrustByConditions(spotEntrust);
        } else {
            PageHelper.startPage(Integer.valueOf(pageSize), Integer.valueOf(lineSize));
            spotEntrusts = spotEntrusMapper.selectSpotEntrustByConditions(spotEntrust);
        }
        for (SpotEntrust entrust:spotEntrusts) {
            //获取用户信息拿到手机号
            User us=userMapper.getUserInfoById(Long.valueOf(entrust.getUserNo()));
            if (us != null && !ValidataUtil.isNull(us.getPhone())){
                entrust.setPhone(us.getPhone());
                entrust.setAreaCode(user.getAreaCode());
            }
            //获取货币信息拿到货币图片
            Coin coin=coinMapper.getCoinByCoinNo(Long.valueOf(entrust.getEntrustCoin()));
            if (coin !=null && !ValidataUtil.isNull(coin.getCoinImg())){
                entrust.setCoinImg(coin.getCoinImg());
            }
        }

        return RspUtil.success(JSONFormat.getStr(spotEntrusts));

    }

    @Override
    public BaseResponse getSpotEntrustById(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String id = json.getString("id");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        if (ValidataUtil.isNull(id))
            return RspUtil.rspError(responseParamsDto.ID_NULL_DESC);

        SpotEntrust spotEntrust = new SpotEntrust();
        spotEntrust.setId(Long.valueOf(id));
        spotEntrust = spotEntrusMapper.selectSpotEntrustByCondition(spotEntrust);

        //获取用户信息拿到手机号
        User us=userMapper.getUserInfoById(Long.valueOf(spotEntrust.getUserNo()));
        if (us != null && !ValidataUtil.isNull(us.getPhone())){
            spotEntrust.setPhone(us.getPhone());
            spotEntrust.setAreaCode(us.getAreaCode());
        }
        //获取货币信息拿到货币图片
        Coin coin=coinMapper.getCoinByCoinNo(Long.valueOf(spotEntrust.getEntrustCoin()));
        if (coin !=null && !ValidataUtil.isNull(coin.getCoinImg())){
            spotEntrust.setCoinImg(coin.getCoinImg());
        }

        return RspUtil.success(JSONFormat.getStr(spotEntrust));
    }

    /**
     * C2C交易前置判断
     * @param userNo 用户id
     * @param tradePrice 交易价格
     * @param tradeNum 交易数量
     * @param receivablesType 接收方式
     * @param minPrice
     * @param tradePassword 交易密码
     * @return
     */
    private BaseResponse<Map<String, Object>> conditionJudge(Long userNo, BigDecimal tradePrice,
                                                             BigDecimal tradeNum, Integer receivablesType,
                                                             BigDecimal minTradeNum, String tradePassword,
                                                             ResponseParamsDto responseParamsDto) {
        //判断交易数量(数据/格式)是否合理
        if (tradeNum.doubleValue() < 0.0001) {
            return RspUtil.rspError(responseParamsDto.FAIL_TRADE_DATA_FORMAT_DESC);
        }
        if (tradeNum.toString().contains(".")) {
            if (tradeNum.toString().split("\\.")[1].length() > 4) {
                return RspUtil.rspError(responseParamsDto.FAIL_TRADE_DATA_FORMAT_DESC);
            }
        }
        // 判断交易价格(数据/格式)是否合理
        if (tradePrice.doubleValue() < 0.0001) {
            return RspUtil.rspError(responseParamsDto.FAIL_TRADE_PRICE_DATA_FORMAT_DESC);
        }
        if (tradePrice.toString().contains(".")) {
            if (tradePrice.toString().split("\\.")[1].length() > 4) {
                return RspUtil.rspError(responseParamsDto.FAIL_TRADE_PRICE_DATA_FORMAT_DESC);
            }
        }
        // 判断最小限额(数据/格式)是否合理
        if (minTradeNum.doubleValue() < 0.0001 || minTradeNum.doubleValue() > tradeNum.doubleValue()) {
            return RspUtil.rspError(responseParamsDto.FAIL_TRADE_MIN_DATA_FORMAT_DESC);
        }
        if (minTradeNum.toString().contains(".")) {
            if (minTradeNum.toString().split("\\.")[1].length() > 4) {
                return RspUtil.rspError(responseParamsDto.FAIL_TRADE_MIN_DATA_FORMAT_DESC);
            }
        }

//        //判断是否通过了实名认证
//        IDCard idCard = new IDCard();
//        idCard.setUserId(userNo.intValue());
//        idCard.setState(1);
//        List<IDCard> idCardList = idCardMapper.getByConditions(idCard);
//        if(idCardList.size() <=0 ){
//            return RspUtil.rspError(responseParamsDto.AUTH_FAIL);
//        }

        // 判断用户是否为null
        User userDto = userMapper.getUserInfoById(userNo);
        if (userDto == null || userDto.getTradePassword() == null) {
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        }
        // 判断交易密码是否为null
        if (StringUtils.isBlank(userDto.getTradePassword())) {
            return RspUtil.error(responseParamsDto.NO_TRADE_PASSWORD_DESC, 300);
        }
        // 判断交易密码是否一致
        if (!tradePassword.equals(userDto.getTradePassword())) {
            return RspUtil.rspError(responseParamsDto.FAIL_TRADE_PASSWORD_DESC);
        }

        // 支付方式绑定验证
        if (receivablesType == 2 && StringUtils.isBlank(userDto.getApay())) {
            return RspUtil.rspError(responseParamsDto.FAIL_ALIPAY_NULL_DESC);
        }
        if (receivablesType == 3 && StringUtils.isBlank(userDto.getWeChat())) {
            return RspUtil.rspError(responseParamsDto.FAIL_WEIXIN_NULL_DESC);
        }
        if (receivablesType == 5 && StringUtils.isBlank(userDto.getImToken())) {
            return RspUtil.rspError(responseParamsDto.FAIL_IMTOKEN_NULL_DESC);
        }
        return null;
    }

    @Override
    public BaseResponse getOpenTransCoins() throws Exception {
        List<Coin> coins= coinMapper.getOpenTransCoinList();
        return RspUtil.success(coins);
    }


    public static void main(String[] args) {
        BigDecimal bigDecimal1=new BigDecimal("120");//总数量
        BigDecimal bigDecimal2=new BigDecimal("70");//成交数量

        BigDecimal bigDecima3=bigDecimal2.divide(bigDecimal1,1,BigDecimal.ROUND_HALF_UP);

        System.out.println("成交百分比: "+bigDecima3.multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString()+"%");

    }
}
