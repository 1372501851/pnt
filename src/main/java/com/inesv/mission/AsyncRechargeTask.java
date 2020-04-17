package com.inesv.mission;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.AddressMapper;
import com.inesv.mapper.CoinMapper;
import com.inesv.mapper.TradeInfoMapper;
import com.inesv.mapper.UserWalletMapper;
import com.inesv.model.Address;
import com.inesv.model.Coin;
import com.inesv.model.TradeInfo;
import com.inesv.model.UserWallet;
import com.inesv.service.UserWalletService;
import com.inesv.util.*;
import com.inesv.util.ZbApi.BlockchainBrowserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: xujianfeng
 * @create: 2018-04-20 23:33
 **/
//@Component
public class AsyncRechargeTask {
    private Logger logger = LoggerFactory.getLogger(AsyncRechargeTask.class);

    @Autowired
    UserWalletService userWalletService;
    @Autowired
    UserWalletMapper userWalletMapper;
    @Autowired
    CoinMapper coinMapper;
    @Autowired
    AddressMapper addressMapper;
    @Autowired
    TradeInfoMapper tradeInfoMapper;

    //使用类TaskThreadPool中配置的自定义线程池
    //@Async("taskAsyncPool")
    public void doTask(UserWallet wallet) throws Exception {
        try{
            Coin coin = new Coin();
            coin.setCoinNo(Long.parseLong(wallet.getType().toString()));
            coin.setState(1);
            coin = coinMapper.getCoinByCondition(coin);
            if(coin == null || coin.getState() == 0){
                return;
            }

            Address address = new Address();
            address.setCoinNo(Long.parseLong(wallet.getType().toString()));
            address = addressMapper.getAddressByCondition(address);
            if(address == null || address.getStatus() == 0){
                //logger.info("钱包服务器状态为不可用，address："+address.getAddress()+",port："+address.getPort());
                return;
            }

            String coinName = coin.getCoinName();
            String errorMsg = coinName + "交易记录数量小于等于0";
            String apiType = coin.getApiType();
            String userAddress = wallet.getAddress();

            if ("ptn_api".equalsIgnoreCase(apiType)) {
            /*String tokenName = "PTN".equalsIgnoreCase(coinName) ? "ptn" : "ptncny";

            PNTCoinApi api = PNTCoinApi.getApi(address);
            //1.取得pubkey,用于调用交易记录接口
            String resultJson = api.getAddressInfos(wallet.getAddress(), -1, -1, tokenName);
            JSONObject dataJson = new JSONObject();
            try {
                dataJson = JSONObject.parseObject(resultJson).getJSONObject("data");
            } catch (Exception e) {
                logger.error("userId："+ wallet.getUserId()+",钱包地址："+wallet.getAddress()+"，解析接口返回数据失败，解析的数据是："+resultJson);
                return;
            }
            String pubKey = dataJson.getJSONObject("accountMap").getString("pubKey");

            //2.取得交易记录数量
            String jsonStr = api.getAddressTradeInfos(pubKey, 1, 1, tokenName);
            JSONObject job = JSONObject.parseObject(jsonStr);
            int count = job.getJSONObject("data").getInteger("count");
            if (count <= 0) {
                logger.info("userId：{},钱包地址：{}，错误信息：{}", wallet.getUserId(), wallet.getAddress(), errorMsg);
                return;
            }

            //3.取得交易记录列表
            jsonStr = api.getAddressTradeInfos(pubKey, 1, count, tokenName);
            job = JSONObject.parseObject(jsonStr);
            JSONArray jsonArray = job.getJSONObject("data").getJSONArray("transactionList");

            //处理交易记录
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject tranJson = jsonArray.getJSONObject(i);
                String hash = tranJson.getString("hash");
                BigDecimal amount = tranJson.getBigDecimal("amount");//交易数量
                int blockHeight = tranJson.getIntValue("blockHeight");//区块高度
                // 处理充值
                boolean flag = reacher(
                        hash,
                        blockHeight,
                        wallet.getAddress().equals(tranJson.getString("to")) ? "receive" : "send",
                        wallet,
                        amount,
                        coinName,
                        address);
                logger.info("货币名称:{}，userId:{},钱包地址:{},处理结果:{}，单条交易记录:{}", coinName, wallet.getUserId(), wallet.getAddress(), flag, tranJson.toString());
            }*/
            } else if ("btc_api".equalsIgnoreCase(apiType)) {
                TransactionResult transactionResult = new TransactionResult(
                        address.getAddress(), address.getPort(),
                        address.getName(), address.getPassword(),
                        address.getLockPassword());
                if (transactionResult.creatBitcoin().getinfo() == null || transactionResult.creatBitcoin().getinfo().equals("")) {
                    logger.info("userId：{},钱包地址：{}，错误信息：{}", wallet.getUserId(), userAddress, errorMsg);
                    return;
                }

                String transactionResultList = transactionResult.getTrans("*");
                if (!"none".equals(transactionResultList)) {
                    List<Map<String, Object>> list = new ArrayList<>();
                    try {
                        list = JSON.parseObject(transactionResultList, List.class);
                    } catch (Exception e) {
                        logger.info("userId：{},钱包地址：{}，错误信息：{}", wallet.getUserId(), userAddress, errorMsg);
                        return;
                    }

                    for (Map<String, Object> listMap : list) {
                        String account = (String) listMap.get("account");
                        String category = (String) listMap.get("category");
                        String _address = (String) listMap.get("address");
                        String tixId = (String) listMap.get("txid");
                        Double price = transactionResult
                                .getResultCoin(listMap.get("amount"));
                        if(!"receive".equals(category)){
                            continue;
                        }

                        if (userAddress.equals(_address)) {
                            boolean ok = reacher(tixId, 6, category,
                                    wallet, new BigDecimal(price),
                                    coinName, address);
                            logger.info("处理充值====ok:" + ok + "   userId:"
                                    + wallet.getUserId() + "  coinNo:"
                                    + wallet.getType() + "   address:"
                                    + userAddress);
                        }
                    }
                }
            } else if ("eth_api".equalsIgnoreCase(apiType)) {
                JSONArray transRecords = BlockchainBrowserUtil.geTransByEtherscan(userAddress);
                if(transRecords == null || transRecords.size() == 0){
                    logger.info("userId：{},钱包地址：{}，错误信息：{}", wallet.getUserId(), userAddress, errorMsg);
                    return;
                }
                for (int i = 0; i < transRecords.size(); i++) {
                    JSONObject transRecord = transRecords.getJSONObject(i);
                    String confirmationsStr = transRecord.getString("confirmations");
                    String from_address = transRecord.getString("from");//打款方钱包地址
                    String to_address = transRecord.getString("to");// 收款方钱包地址
                    String txid = transRecord.getString("hash");
                    String valueStr = transRecord.getString("value");//交易金额
                    Integer confirmations = new Integer(confirmationsStr);
                    BigDecimal value = new BigDecimal(valueStr);
                    BigDecimal wei = new BigDecimal("1000000000000000000");
                    BigDecimal bigPrice = value.divide(wei); // 交易金额

                    if (!to_address.equals(userAddress)) { // 只处理充值方
                        continue;
                    }
                    boolean ok = reacher(txid, confirmations, "receive",
                            wallet, bigPrice, coinName, address);
                    logger.info("处理充值====ok:" + ok + "   userId:"
                            + wallet.getUserId() + "  coinNo:"
                            + wallet.getType() + "   address:"
                            + userAddress);
                }
            } else if ("eth_tokens_api".equalsIgnoreCase(apiType)) {
                logger.info("处理充值TOKEN");
            }
        }catch (Exception e){
            logger.error("执行充值失败："+ GsonUtils.toJson(wallet), e);
        }
    }

    protected boolean reacher(String hash, int confirmations, String type,
                              UserWallet wallet, BigDecimal money, String coinName,
                              Address address) throws Exception {
        //处理交易记录中to（接收地址）和当前遍历的用户钱包地址相等的记录
        if(!"receive".equals(type) || confirmations <= 0){
            logger.info("userId：{}，钱包地址：{}，只处理充值记录，转账不处理且区块高度必须大于0", wallet.getUserId() + address.getAddress());
            return false;
        }

        hash = MD5Util.GetMD5Code(coinName + hash);
        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setHash(hash);
        List<TradeInfo> tradeInfos = tradeInfoMapper.getTradeInfoByConditions(tradeInfo);
        if (tradeInfos == null || tradeInfos.size() == 0) {
            // 没有充值过 进行充值
            tradeInfo.setAddress(wallet.getAddress());
            tradeInfo.setCoinNo(wallet.getType());
            tradeInfo.setDate(new Date(System.currentTimeMillis()));
            tradeInfo.setUserId(0l);
            tradeInfo.setType(0);// 0为转入1为转出
            tradeInfo.setOrderNo(ValidataUtil.generateUUID());
            tradeInfo.setTradeNum(money);
            tradeInfo.setOutAddress("");
            tradeInfo.setInAddress(wallet.getAddress());
            tradeInfo.setState(1);// 1转账成功
            tradeInfo.setRatio(new BigDecimal("0"));
            tradeInfo.setRemark("转入");
            boolean ok = false;
            // if ("PTN".equalsIgnoreCase(coinName)
            // || "PTNCNY".equalsIgnoreCase(coinName)) {
            ok = userWalletService.rechargeAddress(wallet.getId(), tradeInfo,
                    coinName, address);
            // }
            if (ok) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
