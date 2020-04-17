package com.inesv.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.inesv.common.constant.RErrorEnum;
import com.inesv.common.exception.RRException;
import com.google.common.collect.Maps;
import com.inesv.entity.CoinParams;
import com.inesv.entity.MainAccountEnum;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.NodeLevelService;
import com.inesv.service.NodeService;
import com.inesv.service.TradeInfoService;
import com.inesv.service.UserService;
import com.inesv.util.BaseResponse;
import com.inesv.util.CoinAPI.PNTCoinApi;
import com.inesv.util.LanguageUtil;
import com.inesv.util.ResponseParamsDto;
import com.inesv.util.RspUtil;
import com.inesv.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.sun.tools.doclint.Entity.sum;

@Slf4j
@Service
public class NodeServiceImpl implements NodeService {

    private static Lock tradeLock = new ReentrantLock();    // 锁对象

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private TradeInfoService tradeInfoService;

    @Autowired
    private NodeUserRecordMapper nodeUserRecordMapper;

    @Autowired
    private NodeUserTotalMapper nodeUserTotalMapper;

    @Autowired
    private NodeUserDrawMapper nodeUserDrawMapper;

    @Autowired
    private NodeUserProfitMapper nodeUserProfitMapper;

    @Autowired
    private ParamsMapper paramsMapper;

    @Autowired
    private NodeLevelService nodeLevelService;

    @Autowired
    private NodeMineProfitMapper nodeMineProfitMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private UserService userService;

    @Override
    public Set<Long> getParListByPid(Long userId, Long parId) {
        String parListByPid = nodeMapper.getParListByPid(userId, parId);
        if (StringUtils.isNotBlank(parListByPid)) {
            String[] strArr = parListByPid.split(",");
            Long[] strArrNum = (Long[]) ConvertUtils.convert(strArr, Long.class);
            List<Long> longList = Arrays.asList(strArrNum);
            Set<Long> set = new HashSet<>(longList);
            return set;
        } else {
            return null;
        }
    }


    @Override
    public boolean isExistNode(Long userId) {
        List<Node> nodes = nodeMapper.queryNode(null);
        for (Node node : nodes) {
            Long parId = node.getUserId();
            Set<Long> set = getParListByPid(userId, parId);
            if (CollectionUtils.isNotEmpty(set)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void userBuyNode(Long userId, BigDecimal amount) {
        //用户购买节点记录
        Node node = getUserNode(userId);
        if (node == null) {
            throw new RRException(RErrorEnum.USERNODE_NOEXIST);
        }
        if (node.getState()==2){
            throw new RRException(RErrorEnum.COMMUNITY_CLOSE_INSERT);
        }
        Long nodeId = node.getId();
        Long nodeUserId = node.getUserId();
        String toAddress = node.getAddress();
        //判断节点总额
        BigDecimal totalAmount = node.getTotalAmount();
        BigDecimal intoAmount = node.getIntoAmount();
        BigDecimal lastAmount = intoAmount.add(amount);
        if(lastAmount.compareTo(totalAmount) > 0) {
            throw new RRException(RErrorEnum.NODEAMOUNT_FULL);
        }
        String txHash = tradeInfoService.tradeMocToSystem(userId, amount, new BigDecimal("0.01"),
                toAddress, "用户社区挖矿投入");
        if (StringUtils.isNotBlank(txHash)) {
            NodeUserRecord nodeUserRecord = new NodeUserRecord();
            nodeUserRecord.setAmount(amount);
            nodeUserRecord.setUserId(userId);
            nodeUserRecord.setNodeId(nodeId);
            nodeUserRecord.setHash(txHash);
            nodeUserRecord.setState(1);
            nodeUserRecord.setCreatetime(LocalDateTime.now());
            nodeUserRecord.setUpdatetime(LocalDateTime.now());
            nodeUserRecordMapper.insertNodeUserRecord(nodeUserRecord);

            //查询社区总投资额
            BigDecimal userTotalAmount = nodeUserRecordMapper.getUserNodeAllAmount(userId, nodeId);

            //查询用户正在投注的汇总记录
            NodeUserTotal queryCondition = new NodeUserTotal();
            queryCondition.setUserId(userId);
            queryCondition.setState(1);
            NodeUserTotal nodeUserTotal = nodeUserTotalMapper.queryNodeUserTotalLimit1(queryCondition);
            if (nodeUserTotal == null) {
                NodeUserTotal insertModel = new NodeUserTotal();
                insertModel.setUserId(userId);
                insertModel.setState(1);
                insertModel.setAmount(userTotalAmount);
                insertModel.setLasttime(LocalDateTime.now());
                insertModel.setNodeId(nodeId);
                insertModel.setCreatetime(LocalDateTime.now());
                insertModel.setUpdatetime(LocalDateTime.now());
                String path = nodeMapper.getParListByPid(userId, nodeUserId);
                if(StringUtils.isNotBlank(path)) {
                    insertModel.setPath(path);
                    String[] split = path.split(",");
                    insertModel.setTreeGrade(split.length);
                } else {
                    log.error("查询用户层级失败,userId={},nodeId={}", userId, nodeId);
                }
                nodeUserTotalMapper.insertNodeUserTotal(insertModel);
            } else {
                Long id = nodeUserTotal.getId();
                NodeUserTotal updateModel = new NodeUserTotal();
                updateModel.setId(id);
                updateModel.setAmount(userTotalAmount);
                updateModel.setLasttime(LocalDateTime.now());
                updateModel.setUpdatetime(LocalDateTime.now());
                nodeUserTotalMapper.updateNodeUserTotal(updateModel);
            }
            //更新节点总金额
            BigDecimal nodeAllAmount = nodeUserRecordMapper.getNodeAllAmount(nodeId);
            Node updateNode = new Node();
            updateNode.setId(nodeId);
            updateNode.setIntoAmount(nodeAllAmount);
            updateNode.setUpdatetime(LocalDateTime.now());
            nodeMapper.updateNode(updateNode);
        } else {
            log.error("转账失败");
            throw new RRException(RErrorEnum.TRANSFER_ERROR);
        }
    }

    //判断用户是哪个社区的
    public Node getUserNode(Long userId) {
        NodeUserRecord nodeUserRecord=new NodeUserRecord();
        nodeUserRecord.setUserId(userId);
        //去记录表查看是否有记录，有的话直接获取节点id
        NodeUserRecord nodeU=nodeUserRecordMapper.queryNodeUserRecordLimit1(nodeUserRecord);
        if (nodeU !=null){
            Node node= new Node();
            node.setId(nodeU.getNodeId());
            //获取对应节点的信息
            Node node1 = nodeMapper.queryNodeLimit1(node);
            return node1;
        }else {
            List<Node> nodes = nodeMapper.queryNode(null);
            for (Node node : nodes) {
                Long parID = node.getUserId();
                Set<Long> parListByPid = getParListByPid(userId, parID);
                if(CollectionUtils.isNotEmpty(parListByPid)) {
                    return node;
                }
            }
        }
        return null;
    }



    @Override
    public BaseResponse pageJudge(String token, String language) {
        return null;
    }

    @Override
    public Node nodeRecord(Long id) {
        Node node = getUserNode(id);
        if (node !=null){
            node.setIntoAmount(node.getIntoAmount().setScale(6));
            node.setAddressAmount(node.getAddressAmount().setScale(6));
            node.setTotalAmount(node.getTotalAmount().setScale(6));
        }
        return node;
    }

    @Override
    public List<NodeUserRecord> nodeUserPetRecord(Long id) {
        List<NodeUserRecord> list=nodeUserRecordMapper.getUserPetRecord(id);
        if (CollectionUtils.isNotEmpty(list)){
            for (NodeUserRecord nodeUserRecord : list){
                nodeUserRecord.setAmount(nodeUserRecord.getAmount().setScale(6));
            }
        }
        return list;
    }

    @Override
    public List<NodeUserDraw> nodeUserDrawRecord(Long id) {
        List<NodeUserTotal> nodeUserTotals=nodeUserTotalMapper.getDrawRecord(id);
        List<NodeUserDraw> list=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(nodeUserTotals)){
            for (NodeUserTotal nodeUserTotal : nodeUserTotals ){
                NodeUserDraw nodeUserDraw=nodeUserDrawMapper.getUserDrawRecord(nodeUserTotal.getId());
                if (nodeUserDraw != null){
                    nodeUserDraw.setAmount(nodeUserDraw.getAmount().setScale(6));
                    list.add(nodeUserDraw);
                }
            }
            return list;
        }else {
            return list;
        }
    }

    @Override
    public List<NodeUserProfit> nodeUserProfitRecord(Long id) {
        List<NodeUserProfit> list=nodeUserProfitMapper.getUserProfitRecord(id);
        if (CollectionUtils.isNotEmpty(list)){
            for (NodeUserProfit nodeUserProfit : list){
                nodeUserProfit.setAmount(nodeUserProfit.getAmount().setScale(6));
            }
        }
        return list;
    }

    @Override
    public Map<String, String> getData() {
        HashMap<String, String> map = Maps.newHashMap();
        //5%
        Params param1 = paramsMapper.getParams(CoinParams.MOC_NO_START_EXIT);
        map.put("maxRate",param1.getParamValue());
        //1%
        Params param2 = paramsMapper.getParams(CoinParams.MOC_START_EXIT);
        map.put("minRate",param2.getParamValue());
        //获取天数30
        Params param3 = paramsMapper.getParams(CoinParams.MOC_SET_DAY);
        map.put("day",param3.getParamValue());
        //获取moc值范围的最小值
        BigDecimal amountMin = nodeLevelService.getNodeBuyMin();
        map.put("minAmount",amountMin.setScale(0).toString());
        //获取moc值范围的最大值
        Params param4 = paramsMapper.getParams(CoinParams.MOC_BET_AMOUNT_MAX);
        map.put("maxAmount",param4.getParamValue());
        return map;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void withdrawBet(User user,String password) {
        //加开关 0是禁止，1是开启
        Params params1 = paramsMapper.getParams(CoinParams.MOC_DRAW_SWITH);
        if (params1.getParamValue().equals("1")){
            Long userId = user.getId();
            try {
                tradeLock.lock();
                boolean isPasswd = userService.isTradePaswd(userId, password);
                if (!isPasswd) {
                    throw new RRException(RErrorEnum.TRADEPASSWD_ERROR);
                }
                //根据userId获取总额
                NodeUserTotal nodeUserTotal= nodeUserTotalMapper.getSumByUserId(userId);
                if (nodeUserTotal != null){
                    Node node = nodeMapper.getNodeByNodeId(nodeUserTotal.getNodeId());
                    //0:是初始状态，1：是开启中，2：是关闭中  MainAccountEnum
                    //转账
                    /*Address address=new Address();
                    address.setCoinNo(Long.valueOf(40));
                    address.setStatus(1);
                    Address getAddress = addressMapper.getAddressByCondition(address);
                    address.setValue(getAddress);
                    PNTCoinApi api = PNTCoinApi.getApi(address);*/
                    //获取对应的主账户
                    String mAddress=node.getAddress();
                    /*MainAccountEnum[] values = MainAccountEnum.values();
                    for (int i = 0; i <values.length ; i++) {
                        if (values[i].getTag().compareTo(node.getTag())==0) {
                            Params paramsA = paramsMapper.getParams(values[i].getType());
                            mAddress= paramsA.getParamValue();
                            break;
                        }
                    }*/
                    if (StringUtils.isBlank(mAddress)){
                        throw new RRException(RErrorEnum.TRANSFER_ERROR);
                    }
                    List<UserWallet> wallets = userWalletMapper.getuserWallet(user.getId());
                    String inAddress=null;
                    String passW="";
                    for (UserWallet userWallet : wallets){
                        if (userWallet.getType()==40){
                            inAddress=userWallet.getAddress();
                            break;
                        }
                    }
                    //社区未开启退出、开启不够一个月退出扣除的手续费
                    Params params = paramsMapper.getParams(CoinParams.MOC_NO_START_EXIT);
                    Coin coin=new Coin();
                    coin.setCoinNo(Long.valueOf(40));
                    coin.setState(1);
                    Coin getCoin = coinMapper.getCoinByCondition(coin);
                    coin.setValue(getCoin);
                    Params param2 = paramsMapper.getParams(CoinParams.MOC_TRADE_FEE);
                    String fee=param2.getParamValue();
                    String tokenName = coin.getCoinName().toUpperCase();
                    String pubKey="";

                    //String tradeAmountStr = tradeAmount.toString();
                    //log.info("社区提取，转账金额={}, 转出地址={},转入地址={},手续费{}", tradeAmountStr, mAddress, inAddress, fee);
                    //判断社区是否关闭的
                    if (node.getState() == 0 && node.getState() !=2){
                        BigDecimal decimal = nodeUserTotal.getAmount().multiply(new BigDecimal(params.getParamValue()).divide(new BigDecimal("100")));
                        BigDecimal tradeAmount = nodeUserTotal.getAmount().subtract(decimal).setScale(6);

                        String hash = tradeInfoService.systemToUserTrade(mAddress, userId, tradeAmount.toString(), pubKey, "社区未开启，用户退出社区退账的交易", fee, tokenName);
                        //String hash = api.sendTransaction(mAddress, inAddress, tradeAmount.toString() , pubKey, "社区未开启，用户退出社区退账的交易", fee, tokenName);
                        if (StringUtils.isNotBlank(hash)){
                            //更新state
                            nodeUserTotalMapper.updateByUserId(userId);
                            //插入提取记录表
                            NodeUserDraw nodeUserDraw=new NodeUserDraw();
                            nodeUserDraw.setTotalId(nodeUserTotal.getId());
                            nodeUserDraw.setTotalAmount(nodeUserTotal.getAmount());
                            nodeUserDraw.setAmount(nodeUserTotal.getAmount().subtract(decimal));
                            nodeUserDraw.setFee(decimal);
                            nodeUserDraw.setHash(hash);
                            nodeUserDraw.setCreatetime(LocalDateTime.now());
                            nodeUserDraw.setUpdatetime(LocalDateTime.now());
                            nodeUserDrawMapper.insertNodeUserDraw(nodeUserDraw);
                            //扣除节点投入的金额
                            nodeMapper.updateIntoAmount(nodeUserTotal.getNodeId(),nodeUserTotal.getAmount());
                            //更新投注记录表所有记录为退出状态
                            nodeUserRecordMapper.updateStateByUserId(userId);
                            log.info("给用户退款成功");
                            log.info("交易之后返回的hash={}",hash);
                        }else {
                            log.info("给用户退款失败");
                            throw new RRException(RErrorEnum.TRANSFER_ERROR);
                        }
                    }else {
                        //判断社区是否关闭的
                        if (node.getState() == 1){
                            BigDecimal decimal = nodeUserTotal.getAmount().multiply(new BigDecimal(params.getParamValue()).divide(new BigDecimal("100")));
                            BigDecimal tradeAmount = nodeUserTotal.getAmount().subtract(decimal).setScale(6);
                            //社区开启一个月后退出扣除的手续费
                            if ((nodeUserTotal.getLasttime().toEpochSecond(ZoneOffset.of("+8"))+ 30 * 24 * 60 * 60)- (LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))) > 0){
                                //String hash = api.sendTransaction(mAddress, inAddress, tradeAmount.toString(), pubKey, "社区开启，不足一个月，用户退出社区退账的交易", fee, tokenName);
                                String hash = tradeInfoService.systemToUserTrade(mAddress, userId, tradeAmount.toString(), pubKey, "社区开启，不足一个月，用户退出社区退账的交易", fee, tokenName);
                                if (StringUtils.isNotBlank(hash)){
                                    //更新state
                                    nodeUserTotalMapper.updateByUserId(userId);
                                    //插入提取记录表
                                    //.multiply(new BigDecimal(params.getParamValue()))
                                    NodeUserDraw nodeUserDraw=new NodeUserDraw();
                                    nodeUserDraw.setTotalId(nodeUserTotal.getId());
                                    nodeUserDraw.setTotalAmount(nodeUserTotal.getAmount());
                                    //BigDecimal decimal = nodeUserTotal.getAmount().multiply(new BigDecimal(params.getParamValue()).divide(new BigDecimal("100")));
                                    nodeUserDraw.setAmount(nodeUserTotal.getAmount().subtract(decimal));
                                    nodeUserDraw.setFee(decimal);
                                    nodeUserDraw.setHash(hash);
                                    nodeUserDraw.setCreatetime(LocalDateTime.now());
                                    nodeUserDraw.setUpdatetime(LocalDateTime.now());
                                    nodeUserDrawMapper.insertNodeUserDraw(nodeUserDraw);
                                    //扣除节点投入的金额
                                    nodeMapper.updateIntoAmount(nodeUserTotal.getNodeId(),nodeUserTotal.getAmount());
                                    //更新投注记录表所有记录为退出状态
                                    nodeUserRecordMapper.updateStateByUserId(userId);
                                    log.info("给用户退款成功");
                                    log.info("交易之后返回的userId={},hash={},",userId,hash);
                                }else {
                                    log.info("给用户退款失败");
                                    throw new RRException(RErrorEnum.TRANSFER_ERROR);
                                }
                            }else {

                                Params param = paramsMapper.getParams(CoinParams.MOC_START_EXIT);
                                BigDecimal decimal2 = nodeUserTotal.getAmount().multiply(new BigDecimal(param.getParamValue()).divide(new BigDecimal("100")));
                                BigDecimal tradeAmount2 = nodeUserTotal.getAmount().subtract(decimal2).setScale(6);

                                //String hash = api.sendTransaction(mAddress, inAddress, tradeAmount2.toString(), pubKey, "社区开启，超一个月，用户退出社区退账的交易", fee, tokenName);
                                String hash = tradeInfoService.systemToUserTrade(mAddress, userId, tradeAmount2.toString(), pubKey, "社区开启，超一个月，用户退出社区退账的交易", fee, tokenName);

                                if (StringUtils.isNotBlank(hash)){
                                    //更新state
                                    nodeUserTotalMapper.updateByUserId(userId);
                                    //插入提取记录表
                                    //.multiply(new BigDecimal(param.getParamValue()))
                                    NodeUserDraw nodeUserDraw=new NodeUserDraw();
                                    nodeUserDraw.setTotalId(nodeUserTotal.getId());
                                    //BigDecimal decimal = nodeUserTotal.getAmount().multiply(new BigDecimal(param.getParamValue()).divide(new BigDecimal("100")));
                                    nodeUserDraw.setTotalAmount(nodeUserTotal.getAmount());
                                    nodeUserDraw.setAmount(nodeUserTotal.getAmount().subtract(decimal2));
                                    nodeUserDraw.setFee(decimal2);
                                    nodeUserDraw.setHash(hash);
                                    nodeUserDraw.setCreatetime(LocalDateTime.now());
                                    nodeUserDraw.setUpdatetime(LocalDateTime.now());
                                    nodeUserDrawMapper.insertNodeUserDraw(nodeUserDraw);
                                    //扣除节点投入的金额
                                    nodeMapper.updateIntoAmount(nodeUserTotal.getNodeId(),nodeUserTotal.getAmount());
                                    //更新投注记录表所有记录为退出状态
                                    nodeUserRecordMapper.updateStateByUserId(userId);
                                    log.info("给用户退款成功");
                                    log.info("交易之后返回的hash={},userId={}",hash,userId);
                                }else {
                                    log.info("给用户退款失败");
                                    throw new RRException(RErrorEnum.TRANSFER_ERROR);
                                }
                            }
                        }else {
                            throw new RRException(RErrorEnum.COMMUNITY_CLOSE_DRAW);
                        }

                    }

                }else {
                    //已转过账
                    throw new RRException(RErrorEnum.TRANSFERRED_ACCOUNT);
                }
            }finally {
                tradeLock.unlock();
            }
        }else {
            throw new RRException(RErrorEnum.SUSPEND_DRAW);
        }
    }

    @Override
    public List<NodeMineProfit> digMineProfitRecord(Long nodeId) {
        //Node node = getUserNode(id);
        List<NodeMineProfit> nodeMineProfits=nodeMineProfitMapper.digMineProfitRecord(nodeId);
        if (CollectionUtils.isNotEmpty(nodeMineProfits)){
            for (NodeMineProfit nodeUserProfit : nodeMineProfits){
                nodeUserProfit.setAmount(nodeUserProfit.getAmount().setScale(6));
            }
        }
        return nodeMineProfits;
    }

    @Override
    public List<NodeUserTotal> drawShowRecord(Long id) {
        List<NodeUserTotal> list=nodeUserTotalMapper.drawShowRecord(id);
        return list;
    }

    @Override
    public List<Node> getAllNode() {
        List<Node> list=nodeMapper.getAllNode();
        return list;
    }

    @Override
    public Node getDetails(Long nodeId) {
        Node node=nodeMapper.getDetails(nodeId);
        return node;
    }

    @Override
    public boolean getNodeIsRight(Long nodeId,Long userId) {
        Node node = getUserNode(userId);
        if (node.getId().compareTo(nodeId)==0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Node userBelongNode(Long userId) {
        Node node = getUserNode(userId);
        return node;
    }
}
