package com.inesv.mission;

import com.inesv.mapper.*;
import com.inesv.mapper.base.NodeBaseMapper;
import com.inesv.model.*;
import com.inesv.service.UserWalletService;
import com.inesv.util.CastUtils;
import com.inesv.util.CoinAPI.PNTCoinApi;
import com.inesv.util.ValidataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * PTN币种外部转入本节点账号处理
 */
@Component
public class PTNExternalChargeMission {
    private Logger logger = LoggerFactory.getLogger(PTNExternalChargeMission.class);

    private final static int BLOCKNUM_PERTIME = 10;//一次同步的区块数量
    private final static String COIN_NAME = "MOC";

    @Autowired
    private ParamsMapper paramsMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private TradeInfoMapper tradeInfoMapper;
    @Autowired
    private UserWalletMapper userWalletMapper;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private NodeMapper nodeMapper;

    @Scheduled(initialDelay = 5 * 1000, fixedRate = 120 * 1000)
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void reward() throws Exception {
        //上次同步的PTN区块高度，这里是闭区间[LastSyncPTNBlockNumber,N),因此LastSyncPTNBlockNumber+1
        Params params = paramsMapper.getParams("LastSyncPTNBlockNumber");
        if (ValidataUtil.isNull(params.getParamValue())) {
            return;
        }

        //PTN接口信息
        Address address = getAddressByCoinNo();
        if (address == null) return;

        PNTCoinApi api = PNTCoinApi.getApi(address);
        int lastBlockHeight = CastUtils.castInt(params.getParamValue());
        int validBlockNum = BLOCKNUM_PERTIME;//有效的同步数量，因为不能超出当前最大高度


        //获取代币集合
        Coin coin = new Coin();
        coin.setApiType("ptn_api");
        List<Coin> coinList = coinMapper.getCoinByConditions(coin);

        for (int i = 1; i <= BLOCKNUM_PERTIME; i++) {
            int height = lastBlockHeight + i;
            logger.info("查询区块高度[{}]的流水", height);
            List<TradeInfo> tradeInfos = api.getTransactionsByBlockHeight(height);
            if (tradeInfos.size() == 0) {
                validBlockNum = height - 1;
                break;
            }
            ;

            String nodeAddress;
            for (TradeInfo tradeInfo : tradeInfos) {

                /**判断是哪个社区的分红*/
                List<Node> nodes = nodeMapper.queryNode(null);
                for (Node node:nodes) {
                    nodeAddress=node.getAddress();
                    //如果节点地址与交易支出方地址相等
                    if(nodeAddress.equalsIgnoreCase(tradeInfo.getOutAddress())){
                        //加上备注
                        tradeInfo.setRemark(node.getRemark()+"["+tradeInfo.getOutAddress()+"]"+"进行分红");
                    }
                }


                for (Coin coin1 : coinList) {
                    if (coin1.getCoinName().equalsIgnoreCase(tradeInfo.getTradeTokenName())) {
                        tradeInfo.setCoinNo(Math.toIntExact(coin1.getCoinNo()));
                    }
                }
                //如果此条记录的tokenName查不到对应的货币编号，直接执行下一条
                if(ValidataUtil.isNull(tradeInfo.getCoinNo())){
                    continue;
                }

//                String hash = MD5Util.GetMD5Code(COIN_NAME + tradeInfo.getHash());
                String hash = tradeInfo.getHash();
                //hash已存在则不处理
                if (ifHashExists4TradeInfo(hash)) continue;

                //处理同步代币流水
                if (!tradeInfo.getTradeTokenName().equalsIgnoreCase("moc")) {
                    //收款方(先查询主币地址是否存在，如果存在则进行创建代币地址，不存在代币地址则创建，反之，则不创建)
                    UserWallet getMainCoinInfo = new UserWallet();
                    getMainCoinInfo.setAddress(tradeInfo.getInAddress());
                    getMainCoinInfo.setType(40);
                    getMainCoinInfo = userWalletMapper.getUserWalletByConditionForUpdate(getMainCoinInfo);
                    if (!ValidataUtil.isNull(getMainCoinInfo)) {
                        //判断代币存不存在
                        UserWallet getCoinInfo = new UserWallet();
                        getCoinInfo.setAddress(tradeInfo.getInAddress());
                        getCoinInfo.setType(tradeInfo.getCoinNo());
                        getCoinInfo = userWalletMapper.getUserWalletByConditionForUpdate(getCoinInfo);
                        if (ValidataUtil.isNull(getCoinInfo)) {
                            //创建货币地址
                            UserWallet coinInWallet = new UserWallet();
                            coinInWallet.setUserId(getMainCoinInfo.getUserId());
                            coinInWallet.setAddress(getMainCoinInfo.getAddress());
                            coinInWallet.setType(tradeInfo.getCoinNo());
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


                //收款地址不是本节点不处理
                UserWallet userWallet = getUserWalletByAddress(tradeInfo);
                if (userWallet == null) continue;

                //插入流水、增加用户资产
                addTradeInfoAndUpdateBalance(address, tradeInfo, hash, userWallet);
            }
            validBlockNum = height;
        }

        params.setParamValue(String.valueOf(validBlockNum));
        paramsMapper.updateParams(params);
    }

    private Address getAddressByCoinNo() {
        Address address = new Address();
        address.setCoinNo(40L);
        address.setStatus(1);
        address = addressMapper.getAddressByCondition(address);
        return address;
    }

    private void addTradeInfoAndUpdateBalance(Address address, TradeInfo tradeInfo, String hash, UserWallet userWallet) throws Exception {
        tradeInfo.setHash(hash);
        tradeInfo.setCoinNo(tradeInfo.getCoinNo());
        tradeInfo.setUserId(0l);
        // 0为转入1为转出
        tradeInfo.setType(0);
        tradeInfo.setOrderNo(ValidataUtil.generateUUID());
        tradeInfo.setOutAddress(tradeInfo.getOutAddress());
        // 1转账成功
        tradeInfo.setState(1);
        tradeInfo.setRatio(tradeInfo.getRatio());
        tradeInfo.setRemark(tradeInfo.getRemark());
        tradeInfo.setDate(tradeInfo.getDate());
        userWalletService.rechargeAddress(userWallet.getId(), tradeInfo, tradeInfo.getTradeTokenName(), address);
    }

    private UserWallet getUserWalletByAddress(TradeInfo tradeInfo) {
        UserWallet userWallet = new UserWallet();
//        userWallet.setType(40);
        userWallet.setType(tradeInfo.getCoinNo());
        userWallet.setAddress(tradeInfo.getInAddress());
        userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
        return userWallet;
    }

    private boolean ifHashExists4TradeInfo(String hash) {
        TradeInfo tInfo = new TradeInfo();
        tInfo.setHash(hash);
        tInfo = tradeInfoMapper.getTradeInfoByCondition(tInfo);
        if (tInfo != null) {
            return true;
        }
        return false;
    }
}
