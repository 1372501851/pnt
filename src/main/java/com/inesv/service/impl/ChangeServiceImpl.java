package com.inesv.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.ChangeService;
import com.inesv.util.*;
import com.inesv.util.CoinAPI.EthcoinAPI;
import com.inesv.util.CoinAPI.PNTCoinApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Service
public class ChangeServiceImpl implements ChangeService {

    @Resource
    private ChangeMapper changeMapper;

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

    @Override
    public BaseResponse change(String data) {
        JSONObject json = JSON.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        String token = json.getString("token");
        if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        User user = userMapper.getUserInfoByToken(token);
        if (user == null) return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        Coin outCoin = coinMapper.queryCoinByCoinNo(json.getLong("outCoinNo"));
        Coin inCoin = coinMapper.queryCoinByCoinNo(json.getLong("inCoinNo"));

        Address PTNaddress = addressMapper.queryAddressInfo(coinMapper.queryByCoinName("PTN").getCoinNo());
        Address elseAddress = null;
        Address PTNCNYaddress = addressMapper.queryAddressInfo(coinMapper.queryByCoinName("PTNCNY").getCoinNo());
        //查询中间账户金额是否足够
        String TransferStation = paramsMapper.queryByKey("PTN_Transfer_Station").getParamValue();
        BigDecimal TransferStationBalance = new BigDecimal(PNTCoinApi.getApi(PTNaddress).getBalance(TransferStation,"moc"));
        if (json.getBigDecimal("num").multiply(outCoin.getPntRatio()).setScale(4,BigDecimal.ROUND_HALF_UP)
                .compareTo(TransferStationBalance)>0){
            if (json.getInteger("language")==0){
                return RspUtil.error("转换失败！",100);
            }else {
                return RspUtil.error("error！",200);
            }
        }

        /**
         * 统计目前以转换多少
         * */
        Map<String,Object> changeParams = new HashMap<String,Object>();
        BigDecimal price = new BigDecimal("0");
        changeParams.put("userId",json.getLong("userId"));
        changeParams.put("outCoin",json.getLong("outCoinNo"));
        List<Change> maxChanges = changeMapper.queryMax(changeParams);
        BigDecimal maxNum = new BigDecimal(0);
        if (maxChanges!=null){
            for (Change change:maxChanges
                    ) {
                maxNum = maxNum.add(change.getCoinNum());
            }
        }
        price = outCoin.getQuota().subtract(maxNum);
        BigDecimal theMaxNum = json.getBigDecimal("num").add(maxNum);
        if (outCoin.getQuota().longValue()<theMaxNum.longValue()){
            if (price.compareTo(new BigDecimal("0"))==0){
                return RspUtil.rspError(responseParamsDto.CHANGE_MAX_LIMIT_DESC+"0"+outCoin.getCoinName());
            }
             return RspUtil.rspError(responseParamsDto.CHANGE_MAX_LIMIT_DESC+price.toString()+outCoin.getCoinName());
        }

        //获取公钥
        String result = PNTCoinApi.getApi(PTNaddress).getAddressInfos(TransferStation,1,1,"moc");
        JSONObject resultJson = JSONObject.parseObject(result);

        //转换后的货币
        Map<String,Object> inParams = new HashMap<String,Object>();
        inParams.put("userId",json.getLong("userId"));
        inParams.put("coinNo",json.getLong("inCoinNo"));
        UserWallet inWallet = userWalletMapper.queryByUserState(inParams);

        //转换前的货币
        Map<String,Object> outParams  = new HashMap<String,Object>();
        outParams.put("userId",json.getLong("userId"));
        outParams.put("coinNo",json.getLong("outCoinNo"));
        UserWallet outWallet = userWalletMapper.queryByUserState(outParams);

        if (user.getTradePassword()==null||user.getTradePassword().equals("")){
            if (json.getInteger("language")==0){
                return RspUtil.error("请设置支付密码再进行操作",400);
            }else {
                return RspUtil.error("Please set the payment password",400);
            }
        }
        if (!user.getTradePassword().equals(json.getString("tradePassword")))return RspUtil.rspError(responseParamsDto.DEALPWD_FAIL_DESC);
        if (json.getBigDecimal("num").compareTo(new BigDecimal(0))<=0)return RspUtil.rspError(responseParamsDto.CHANGE_PRICE_ERRO_DESC);
        if (outCoin.getChangeState()==1)return RspUtil.rspError(responseParamsDto.CHANGE_TARGET_OFF_DESC);
        if (inCoin.getChangeState()==1)return RspUtil.rspError(responseParamsDto.CHANGE_STATE_OFF_DESC);
        if (inWallet.getState()==0) return RspUtil.rspError(responseParamsDto.CHANGE_IN_WALLET_STATE_OFF);
        if (outWallet.getState()==0)return RspUtil.rspError(responseParamsDto.CHANGE_OUT_WALLET_STATE_OFF);
        if (outCoin.getQuota().longValue()<json.getBigDecimal("num").longValue()){
            if (price.compareTo(new BigDecimal("0"))==0){
                return RspUtil.rspError(responseParamsDto.CHANGE_MAX_LIMIT_DESC+"0"+outCoin.getCoinName());
            }
            return RspUtil.rspError(responseParamsDto.CHANGE_MAX_LIMIT_DESC+price.toString()+outCoin.getCoinName());
        }

        //计算手续费
        BigDecimal ratio = outCoin.getChangeFee().multiply(json.getBigDecimal("num")).setScale(4,BigDecimal.ROUND_HALF_UP);
        /*//转成16进制
        String ratioPrice = new BigInteger(ratio.toString(),10).toString(16);*/

        //转换后所得
        BigDecimal changeNum = json.getBigDecimal("num").multiply(outCoin.getPntRatio()).setScale(4,BigDecimal.ROUND_HALF_UP);
        BigDecimal changeNumFree = changeNum.multiply(outCoin.getFreePrice()).setScale(4,BigDecimal.ROUND_HALF_UP);
       /* //转成16进制
        String changeNumPirce = new BigInteger(changeNum.toString(),10).toString(16);*/

        //使用ETH时需统计两个费用
        BigDecimal ETHPrice = json.getBigDecimal("num").add(ratio);
        String ETHPriceInteger = new BigInteger(ETHPrice.multiply(EthcoinAPI.wei).setScale(0, BigDecimal.ROUND_DOWN).toString(),10).toString(16);

        //目前不允许PTNCNY转换成光子
        if (outCoin.getCoinName().equals("PTNCNY"))return RspUtil.rspError(responseParamsDto.CHANGE_STATE_OFF_DESC);
        if (!inCoin.getCoinName().equals("PTN")) return RspUtil.rspError(responseParamsDto.CHANGE_COIN_BAN);
        //用户可用资产
        BigDecimal userBlance = new BigDecimal("0");
        //目前BTC和LTC是操作数据库，须先判断
        if (outCoin.getCoinName().equals("PTNCNY")||outCoin.getCoinName().equals("PTN")||outCoin.getCoinName().equals("ETH")){
            if (outCoin.getCoinName().equals("ETH")){
                elseAddress = addressMapper.queryAddressInfo(coinMapper.queryByCoinName("ETH").getCoinNo());
                //获取转换的ETH地址的总资产
                EthcoinAPI ethcoinAPI = new EthcoinAPI(elseAddress.getAddress(),elseAddress.getPort(),elseAddress.getName(),
                        elseAddress.getPassword(),"");
               BigDecimal ETHBlance = ValidataUtil.Progressive(ethcoinAPI.getBalance(outWallet.getAddress())).divide(EthcoinAPI.wei);
               userBlance = ETHBlance.subtract(outWallet.getUnbalance());
               //ETH小于等于都不允许转出，只有总资产大于需要转出的数量才允许转出
               if (userBlance.compareTo(ETHPrice)<=0){
                   if (json.getInteger("language")==0){
                       return RspUtil.error("转换失败！",100);
                   }else {
                       return RspUtil.error("error！",200);
                   }
               }else {
                   String ETHTransferStation = paramsMapper.queryByKey("ETH_Transfer_Station").getParamValue();
                  String result2 =  new EthcoinAPI(elseAddress.getAddress(),elseAddress.getPort(),elseAddress.getName(),
                          elseAddress.getPassword(),elseAddress.getLockPassword()).sendTransaction(outWallet.getAddress(),ETHTransferStation,"0x"+ETHPriceInteger);
                   TradeInfo tradeInfo = new TradeInfo();
                   tradeInfo.setUserId(0L);
                   tradeInfo.setCoinNo(Integer.valueOf(outCoin.getCoinNo().toString()));
                   tradeInfo.setType(0);
                   tradeInfo.setOrderNo(ValidataUtil.generateUUID() + "0000");
                   tradeInfo.setTradeNum(json.getBigDecimal("num"));
                   tradeInfo.setRatio(ratio);
                   tradeInfo.setOutAddress(outWallet.getAddress());
                   tradeInfo.setInAddress(inWallet.getAddress());
                   tradeInfo.setRemark("给中间ETH账户转账");
                   tradeInfo.setState(1);
                   tradeInfoMapper.insertTradeInfo(tradeInfo);
               }
            }else if (outCoin.getCoinName().equals("PTN")){
                   //目前PTN不允许转换
            }else if (outCoin.getCoinName().equals("PTNCNY")){
                //目前PTNCNY不允许转换
            }else if (outCoin.getCoinName().equals("ETC")){
                elseAddress = addressMapper.queryAddressInfo(coinMapper.queryByCoinName("ETC").getCoinNo());
                //获取转换的ETH地址的总资产
                EthcoinAPI ethcoinAPI = new EthcoinAPI(elseAddress.getAddress(),elseAddress.getPort(),elseAddress.getName(),
                        elseAddress.getPassword(),"");
                BigDecimal ETHBlance = ValidataUtil.Progressive(ethcoinAPI.getBalance(outWallet.getAddress())).divide(EthcoinAPI.wei);
                userBlance = ETHBlance.subtract(outWallet.getUnbalance());
                //ETH小于等于都不允许转出，只有总资产大于需要转出的数量才允许转出
                if (userBlance.compareTo(ETHPrice)<=0){
                    if (json.getInteger("language")==0){
                        return RspUtil.error("转换失败！",100);
                    }else {
                        return RspUtil.error("error！",200);
                    }
                }else {
                    String ETHTransferStation = paramsMapper.queryByKey("ETC_Transfer_Station").getParamValue();
                    String result2 =  new EthcoinAPI(elseAddress.getAddress(),elseAddress.getPort(),elseAddress.getName(),
                            elseAddress.getPassword(),elseAddress.getLockPassword()).sendTransaction(outWallet.getAddress(),ETHTransferStation,"0x"+ETHPriceInteger);
                    TradeInfo tradeInfo = new TradeInfo();
                    tradeInfo.setUserId(0L);
                    tradeInfo.setCoinNo(Integer.valueOf(outCoin.getCoinNo().toString()));
                    tradeInfo.setType(0);
                    tradeInfo.setOrderNo(ValidataUtil.generateUUID() + "0000");
                    tradeInfo.setTradeNum(json.getBigDecimal("num"));
                    tradeInfo.setRatio(ratio);
                    tradeInfo.setOutAddress(outWallet.getAddress());
                    tradeInfo.setInAddress(inWallet.getAddress());
                    tradeInfo.setRemark("给中间ETC账户转账");
                    tradeInfo.setState(1);
                    tradeInfoMapper.insertTradeInfo(tradeInfo);
                }
            }else if (outCoin.getCoinName().equals("HSR")){
            }
        }else {
            if (outWallet.getBalance().compareTo(json.getBigDecimal("num"))<0)return RspUtil.rspError(responseParamsDto.CHANGE_NUM_LOW);
            //防止数据库操作失误，先加行级锁
            UserWallet outUserWalletLock = new UserWallet();
            outUserWalletLock.setId(outCoin.getId());
            userWalletMapper.getUserWalletByConditionForUpdate(outUserWalletLock);
            outWallet.setBalance(outWallet.getBalance().subtract(json.getBigDecimal("num").add(ratio).setScale(4,BigDecimal.ROUND_HALF_UP)));
            if (userWalletMapper.updateBalance(outWallet)==0){
                if (json.getInteger("language")==0){
                    return RspUtil.error("转换失败！",100);
                }else {
                    return RspUtil.error("error！",200);
                }
            }
        }
        if (outCoin.getUnlockState()==1){
            //由中间账户帮忙转出到的PTN账户
            PNTCoinApi.getApi(PTNaddress).sendTransaction(TransferStation,inWallet.getAddress(),changeNumFree.toString(),resultJson.getString("pubKey"),"中间账户转账","0.0001","moc");

            TradeInfo tradeInfo = new TradeInfo();
            tradeInfo.setUserId(0L);
            tradeInfo.setCoinNo(Integer.valueOf(inCoin.getCoinNo().toString()));
            tradeInfo.setType(0);
            tradeInfo.setOrderNo(ValidataUtil.generateUUID() + "0000");
            tradeInfo.setTradeNum(changeNumFree);
            tradeInfo.setRatio(new BigDecimal("0"));
            tradeInfo.setOutAddress(TransferStation);
            tradeInfo.setInAddress(inWallet.getAddress());
            tradeInfo.setRemark("中间账户转账");
            tradeInfo.setState(1);
            tradeInfoMapper.insertTradeInfo(tradeInfo);
        }else {
            //由中间账户帮忙转出到的PTN账户
            PNTCoinApi.getApi(PTNaddress).sendTransaction(TransferStation,inWallet.getAddress(),changeNum.toString(),resultJson.getString("pubKey"),"中间账户转账","0.0001","moc");

            TradeInfo tradeInfo = new TradeInfo();
            tradeInfo.setUserId(0L);
            tradeInfo.setCoinNo(Integer.valueOf(inCoin.getCoinNo().toString()));
            tradeInfo.setType(0);
            tradeInfo.setOrderNo(ValidataUtil.generateUUID() + "0000");
            tradeInfo.setTradeNum(json.getBigDecimal("num"));
            tradeInfo.setRatio(new BigDecimal("0"));
            tradeInfo.setOutAddress(TransferStation);
            tradeInfo.setInAddress(inWallet.getAddress());
            tradeInfo.setRemark("中间账户转账");
            tradeInfo.setState(1);
            tradeInfoMapper.insertTradeInfo(tradeInfo);
        }

            Change change = new Change();
            change.setUserId(json.getLong("userId"));
            change.setAddress(outWallet.getAddress());
            change.setCoinName(outCoin.getCoinName());
            change.setCoinNum(json.getBigDecimal("num"));
            Date date = new Date();
            change.setLastChangeTime(date);
            change.setDate(date);
            change.setOrderNo(ValidataUtil.generateUUID() + "0000");
            change.setPnt(changeNum);
            if (outCoin.getUnlockState()==1){
                change.setFrozenAssets(changeNum.subtract(changeNumFree));
            }else {
                change.setFrozenAssets(new BigDecimal("0"));
            }
            change.setRatio(ratio);
            change.setRemark(json.getString("remark"));
            change.setInCoin(json.getLong("inCoinNo"));
            change.setOutCoin(json.getLong("outCoinNo"));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = format.format(date);
            change.setDateString(dateString);
            if (changeMapper.add(change)==1){
            Map<String,Object> map = new HashMap<String, Object>();
            if (json.getInteger("language")==0){
                return RspUtil.success(200,"转换成功！",change);
            }else {
                return RspUtil.success(200,"success！",change);
            }
            }else {
        //}catch (Exception e){
            if (json.getInteger("language")==0){
                return RspUtil.error("转换失败！",100);
            }else {
                return RspUtil.error("error！",200);
            }
        //}
            }
    }

    @Override
    public BaseResponse changeNum(String data) {
        JSONObject json = JSON.parseObject(data);
        Map<String,Object> map = new HashMap<String,Object>();
        Coin outCoin = coinMapper.queryCoinByCoinNo(json.getLong("outCoinNo"));
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        BigDecimal ratio = outCoin.getChangeFee().multiply(json.getBigDecimal("num")).setScale(4,BigDecimal.ROUND_HALF_UP);
        if (json.getBigDecimal("num").compareTo(new BigDecimal(0))<=0)return RspUtil.rspError(responseParamsDto.CHANGE_PRICE_ERRO_DESC);
        BigDecimal changeNum = json.getBigDecimal("num").multiply(outCoin.getPntRatio()).setScale(4,BigDecimal.ROUND_HALF_UP);
        map.put("changeNum",changeNum);
        map.put("ratio",ratio);
        return RspUtil.success(map);
    }

    @Override
    public BaseResponse changeListByUser(String data) {
        JSONObject json = JSON.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        String token = json.getString("token");
        if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        User user = userMapper.getUserInfoByToken(token);
        if (user == null) return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        Map<String,Object> outParams = new HashMap<String,Object>();
        Map<String,Object> inParams = new HashMap<String,Object>();
        Map<String,Object> map = new HashMap<String, Object>();
        outParams.put("userId",json.getLong("userId"));
        outParams.put("outCoin",json.getLong("outCoinNo"));
        inParams.put("userId",json.getLong("userId"));
        inParams.put("inCoin",json.getLong("inCoinNo"));
        if (json.getLong("outCoinNo")!=null){
            PageHelper.startPage(json.getInteger("pageNum"),json.getInteger("pageSize"));
            List<Change> outChanges = changeMapper.queryRecordByUserAndCoin(outParams);
            for (Change change:outChanges
                    ) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = format.format(change.getDate());
                change.setDateString(dateString);
                change.setCoinNum(change.getCoinNum().setScale(4,BigDecimal.ROUND_HALF_EVEN));
                change.setPnt(change.getPnt().setScale(4,BigDecimal.ROUND_HALF_EVEN));
                change.setRatio(change.getRatio().setScale(4,BigDecimal.ROUND_HALF_EVEN));
                change.setChangeFlag(0);
                Coin inCoin = coinMapper.queryCoinByCoinNo(change.getInCoin());
                Coin outCoin = coinMapper.queryCoinByCoinNo(change.getOutCoin());
                change.setOutCoinName(outCoin.getCoinName());
                change.setInCoinName(inCoin.getCoinName());
                if (change.getFrozenAssets().compareTo(new BigDecimal(0))>0){
                    change.setFlag(0);//释放进行中
                }else {
                    change.setFlag(1);//释放完成
                }
                if (change.getFrozenAssets().compareTo(new BigDecimal(0))>0){
                    BigDecimal proportion =change.getFrozenAssets().divide(change.getPnt()).setScale(4,BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal percentage = new BigDecimal(1).subtract(proportion);
                    change.setPercentage(percentage.multiply(new BigDecimal(100)));
                }else {
                    change.setPercentage(new BigDecimal(100));
                }
                BigDecimal available = change.getPnt().subtract(change.getFrozenAssets());
                change.setAvailable(available);
            }
            PageInfo<Change> changePageInfo = new PageInfo<Change>(outChanges);
            map.put("change",changePageInfo.getList());
            map.put("pageNum",changePageInfo.getPageNum());
            map.put("pages",changePageInfo.getPages());
            map.put("total",changePageInfo.getTotal());
        }else if (json.getLong("inCoinNo")!=null){
            PageHelper.startPage(json.getInteger("pageNum"),json.getInteger("pageSize"));
            List<Change>  inChanges= changeMapper.queryRecordByUserAndCoin(inParams);
            for (Change change:inChanges
                    ) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = format.format(change.getDate());
                change.setDateString(dateString);
                change.setCoinNum(change.getCoinNum().setScale(4,BigDecimal.ROUND_HALF_EVEN));
                change.setPnt(change.getPnt().setScale(4,BigDecimal.ROUND_HALF_EVEN));
                change.setRatio(change.getRatio().setScale(4,BigDecimal.ROUND_HALF_EVEN));
                change.setChangeFlag(1);
                Coin inCoin = coinMapper.queryCoinByCoinNo(change.getInCoin());
                Coin outCoin = coinMapper.queryCoinByCoinNo(change.getOutCoin());
                change.setOutCoinName(outCoin.getCoinName());
                change.setInCoinName(inCoin.getCoinName());
                if (change.getFrozenAssets().compareTo(new BigDecimal(0))==1){
                    change.setFlag(0);//释放进行中
                }else {
                    change.setFlag(1);//释放完成
                }
                if (change.getFrozenAssets().compareTo(new BigDecimal(0))>0){
                    BigDecimal proportion =change.getFrozenAssets().divide(change.getPnt()).setScale(4,BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal percentage = new BigDecimal(1).subtract(proportion);
                    change.setPercentage(percentage.multiply(new BigDecimal(100)));
                }else {
                    change.setPercentage(new BigDecimal(100));
                }

                BigDecimal available = change.getPnt().subtract(change.getFrozenAssets());
                change.setAvailable(available);
            }
            PageInfo<Change> changePageInfo = new PageInfo<Change>(inChanges);
            map.put("change",changePageInfo.getList());
            map.put("pageNum",changePageInfo.getPageNum());
            map.put("pages",changePageInfo.getPages());
            map.put("total",changePageInfo.getTotal());
        }else {
            PageHelper.startPage(json.getInteger("pageNum"),json.getInteger("pageSize"));
            List<Change> changes = changeMapper.queryRecordByUserAndCoin(outParams);
            for (Change change:changes
                    ) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = format.format(change.getDate());
                change.setDateString(dateString);
                change.setCoinNum(change.getCoinNum().setScale(4,BigDecimal.ROUND_HALF_EVEN));
                change.setPnt(change.getPnt().setScale(4,BigDecimal.ROUND_HALF_EVEN));
                change.setRatio(change.getRatio().setScale(4,BigDecimal.ROUND_HALF_EVEN));
                change.setChangeFlag(0);
                Coin inCoin = coinMapper.queryCoinByCoinNo(change.getInCoin());
                Coin outCoin = coinMapper.queryCoinByCoinNo(change.getOutCoin());
                change.setOutCoinName(outCoin.getCoinName());
                change.setInCoinName(inCoin.getCoinName());
                if (change.getFrozenAssets().compareTo(new BigDecimal(0))>0){
                    change.setFlag(0);//释放进行中
                }else {
                    change.setFlag(1);//释放完成
                }
                if (change.getFrozenAssets().compareTo(new BigDecimal(0))>0){
                    BigDecimal proportion =change.getFrozenAssets().divide(change.getPnt()).setScale(4,BigDecimal.ROUND_HALF_EVEN);
                    BigDecimal percentage = new BigDecimal(1).subtract(proportion);
                    change.setPercentage(percentage.multiply(new BigDecimal(100)));
                }else {
                    change.setPercentage(new BigDecimal(100));
                }
                BigDecimal available = change.getPnt().subtract(change.getFrozenAssets());
                change.setAvailable(available);
            }
            PageInfo<Change> changePageInfo = new PageInfo<Change>(changes);
            map.put("change",changePageInfo.getList());
            map.put("pageNum",changePageInfo.getPageNum());
            map.put("pages",changePageInfo.getPages());
            map.put("total",changePageInfo.getTotal());
        }
        return RspUtil.success(200,"转换列表",map);
    }

    @Override
    public BaseResponse queryDetail(String data) {
        JSONObject json = JSON.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        String token = json.getString("token");
        if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        User user = userMapper.getUserInfoByToken(token);
        if (user == null) return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        Change change = changeMapper.queryDetail(json.getLong("id"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(change.getDate());
        change.setDateString(dateString);
        change.setCoinNum(change.getCoinNum().setScale(4,BigDecimal.ROUND_HALF_EVEN));
        change.setPnt(change.getPnt().setScale(4,BigDecimal.ROUND_HALF_EVEN));
        change.setRatio(change.getRatio().setScale(4,BigDecimal.ROUND_HALF_EVEN));
        Coin inCoin = coinMapper.queryCoinByCoinNo(change.getInCoin());
        Coin outCoin = coinMapper.queryCoinByCoinNo(change.getOutCoin());
        change.setOutCoinName(outCoin.getCoinName());
        change.setInCoinName(inCoin.getCoinName());
        if (change!=null){
            return RspUtil.success(change);
        }else {
            return RspUtil.rspError(responseParamsDto.CHANGE_DETAIL_IS_NULL);
        }
    }
    
}
