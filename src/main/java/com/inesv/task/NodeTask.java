package com.inesv.task;

import com.inesv.common.constant.RErrorEnum;
import com.inesv.common.exception.RRException;
import com.inesv.entity.CoinParams;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.NodeService;
import com.inesv.service.TradeInfoService;
import com.inesv.util.CoinAPI.PNTCoinApi;
import com.inesv.util.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class NodeTask {

   /* @Autowired
    private NodeMapperTDelete nodeMapper;*/

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private ParamsMapper paramsMapper;

    @Autowired
    private TradeInfoService tradeInfoService;

    /*@Autowired
    private PNTCoinApi pntCoinApi;*/

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private UserRelationMapper userRelationMapper;

    @Autowired
    private NodeProfitRecordMapper nodeProfitRecordMapper;

    @Autowired
    private NodeUserRecordMapper nodeUserRecordMapper;

    @Autowired
    private NodeUserProfitMapper nodeUserProfitMapper;

    @Autowired
    private NodeUserTotalMapper nodeUserTotalMapper;

    @Autowired
    private NodeService nodeService;



    @Value("${spring.datasource.driver-class-name}")
    private String jdbcDriver;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    //@Transactional
    //@Scheduled(cron = "0 0 01 * * ?")
    public void NodeProfit() {
        //动态开关：0：关闭，1：开启
        Params paramsS = paramsMapper.getParams(CoinParams.MOC_DYNAMIC_SWITCH);
        String paramValue = paramsS.getParamValue();
        if (paramValue.equals("1")){
            //JxAmount();
            //sysSum();
        }
    }

    public void  getStaticAmount(){
        List<Map<String,Object>> lists=nodeMapper.getAllUserStaticAmount();
        LocalDate now = LocalDate.now();
        String s = now.format(DateTimeFormatter.BASIC_ISO_DATE);
        String s1 = "t_node_user_profit_" + s;
        if (CollectionUtils.isNotEmpty(lists)){
            for (Map<String,Object> map : lists){
                //BigDecimal sum=nodeUserRecordMapper.getSumByuseId(Long.valueOf(map.get("userId").toString()));
                BigDecimal sum= nodeUserTotalMapper.getSumByUserId(Long.valueOf(map.get("userId").toString())).getAmount();
                Map<String, Object> flowRate = nodeMapper.getFlowRate(sum);
                map.put("fromId",map.get("userId"));
                map.put("userId",map.get("userId"));
                map.put("type", 1);
                map.put("recordTable",s1);
                map.put("profit",sum.multiply(new BigDecimal(flowRate.get("staticRate").toString())));
                map.put("level",flowRate.get("level"));
                nodeMapper.addNodeProfitRecord(map);
                //userMapper.insertRecord(map);
            }
        }
    }


    public void JxAmount(){
        log.info("开始结算全部静态收益");
        //getStaticAmount();
        log.info("静态收益结算完成");

        log.info("开始结算动态收益");
        //获取最大等级数
        Integer maxTreeGrade = userRelationMapper.getMaxTreeGrade();
        for (int i = maxTreeGrade; i > 1; i--) {
            List<Long>  userIds= userRelationMapper.getMaxTreeGradeList(i);
            if (CollectionUtils.isNotEmpty(userIds)){
                for (Long userId : userIds){
                    Node node = nodeService.userBelongNode(userId);
                    if (node==null){
                        continue;
                    }
                    log.info("当前用户属于那个社区{}",node.getId());
                    if (node.getState()==1){
                        List<Long>  userIdss=new ArrayList<>();
                        userIdss.add(userId);
                        getRecommendIncome1(new HashSet<>(userIdss),Integer.valueOf(node.getId().toString()));
                        log.info("当前树形层级{},用户id={},number={}",i, userIdss, userIds.size());
                    }
                }
            }
        }
        log.info("动态收益结算完成");


    }


    public void getRecommendIncome1(Set<Long> ids,Integer nodeId) {
        LocalDate now = LocalDate.now();
        String s = now.format(DateTimeFormatter.BASIC_ISO_DATE);
        String s1 = "t_node_user_profit_" + s;
        //动态比率
        Params params = paramsMapper.getParams(CoinParams.MOC_NODE_DIG_MINE_DYNAMIC_RATE);
        String value = params.getParamValue();
        BigDecimal rate = new BigDecimal(value);
        //获取烧伤等级
        Integer burnGrade=nodeMapper.getBurnGrade();
        for (Long uId : ids) {
            log.info("正在结算用户{}",uId);

                UserRelation userRelation = new UserRelation();
                userRelation.setUserId(uId);
                UserRelation relation = userRelationMapper.getUserRelationByCondition(userRelation);
                HashMap<String, Object> insertModel = new HashMap<>();
                if (relation != null) {
                    Long recId = relation.getRecId();

                    //查询当前已结算的累计
                    List<NodeProfitRecord> userAmount = nodeProfitRecordMapper.queryAmountByUser(Long.valueOf(uId),s1,nodeId);
                    //查询当前用户的总金额
                    NodeUserTotal nodeUserTotal1 = nodeUserTotalMapper.getSumByUserId(uId);
                    //给当前用户赋值为最低等级的最小数
                    int sumTag1=0;
                    BigDecimal sum1=BigDecimal.ZERO;
                    if (nodeUserTotal1==null){
                        sum1=new BigDecimal(500);
                        insertModel.put("tag",0);
                        if (!(CollectionUtils.isNotEmpty(userAmount))){
                            continue;
                        }
                        sumTag1=1;
                    }else {
                        sum1=nodeUserTotal1.getAmount();
                    }
                    BigDecimal maxAmountMin=nodeMapper.getMaxAmountMin();
                    Map<String, Object> map1=new HashMap<>();
                    if (sum1.compareTo(maxAmountMin)>=0){
                        map1=nodeMapper.getFlowRate2(maxAmountMin);
                    }else {
                        map1 = nodeMapper.getFlowRate(sum1);
                    }
                    //判断推荐人是否投注了
                    NodeUserTotal nodeUserTotal = nodeUserTotalMapper.getSumByUserId(recId);
                    BigDecimal sum2=BigDecimal.ZERO;
                    int sumTag2=0;
                    if (nodeUserTotal!=null){
                        sum2= nodeUserTotal.getAmount();
                        insertModel.put("tag",1);
                    }else {
                        sum2=new BigDecimal(500);
                        insertModel.put("tag",0);
                        sumTag2=1;
                    }
                    log.info("sum1={},总数1,sum2={}，总数2",sum1,sum2);
                    Map<String, Object> map2 =new HashMap<>();
                    if (sum2.compareTo(maxAmountMin)>=0){
                        map2=nodeMapper.getFlowRate2(maxAmountMin);
                    }else {
                        map2 = nodeMapper.getFlowRate(sum2);
                    }
                    log.info("表格table :{}",s1);
                    BigDecimal userAmountSum = BigDecimal.ZERO;
                    if (CollectionUtils.isNotEmpty(userAmount)){
                        for (NodeProfitRecord userIncomeTemp : userAmount) {

                                userAmountSum=userAmountSum.add(userIncomeTemp.getAmount());

                        }
                    }

                    BigDecimal gxdt =BigDecimal.ZERO;

                    Integer  level2 = (Integer) map2.get("level");
                    BigDecimal  staticRate = new BigDecimal(map2.get("staticRate").toString());

                        if (level2.compareTo(burnGrade) == 0){

                            if (sumTag1==1){
                                sum1=BigDecimal.ZERO;
                            }
                            BigDecimal add = userAmountSum.add(sum1.multiply(staticRate));
                            gxdt = gxdt.add(add.multiply(rate));

                        }else {
                            Integer  level1= (Integer) map1.get("level");

                            if (level2.compareTo(level1) >=0){

                                if (sumTag1==1){
                                    sum1=BigDecimal.ZERO;
                                }
                                BigDecimal add = userAmountSum.add(sum1.multiply(staticRate));
                                gxdt = gxdt.add(add.multiply(rate));
                            }else {

                                //获取当前用户的动态
                                UserRelation userRelation1 = new UserRelation();
                                userRelation1.setUserId(recId);
                                UserRelation relation2 = userRelationMapper.getUserRelationByCondition(userRelation1);
                                BigDecimal sum=BigDecimal.ZERO;
                                //依据recId是否有推荐人来确定取用户的动态还是推荐人的静态
                                int tag=1;
                                if (relation2 !=null){
                                    sum=nodeProfitRecordMapper.getSumByRecordUserId(s1,Integer.valueOf(uId.toString()),nodeId);
                                }else {
                                    sum=sum2;
                                    tag=0;
                                }

                                if (sum == null){
                                    BigDecimal decimal = sum2.multiply(staticRate).add(new BigDecimal("0"));
                                    gxdt = (gxdt.add(decimal)).multiply(rate);
                                }else {
                                    //tag=0是没有推荐人的
                                    if (tag==0){
                                        BigDecimal sum3 = nodeProfitRecordMapper.getSumByRecordUserId(s1, Integer.valueOf(uId.toString()), nodeId);
                                        if (sum3==null){
                                            sum3=BigDecimal.ZERO;
                                        }
                                        BigDecimal decimal = sum2.multiply(staticRate);
                                        gxdt = (gxdt.add(decimal).add(sum3)).multiply(rate);
                                    }else {
                                        //有推荐人且动态不为零
                                        BigDecimal decimal = sum2.multiply(staticRate).add(sum);
                                        gxdt = (gxdt.add(decimal)).multiply(rate);
                                    }
                                }
                            }
                        }
                    //}
                    log.info("uid={},父亲级别对应的分红是{},recId={}",uId,gxdt,recId);
                    //HashMap<String, Object> insertModel = new HashMap<>();
                    insertModel.put("userId", recId);
                    insertModel.put("type", 2);
                    insertModel.put("amount", gxdt);
                    insertModel.put("fromId", uId);
                    insertModel.put("recordTable",s1);
                    insertModel.put("nodeId",nodeId);
                    nodeProfitRecordMapper.insertRecord(insertModel);
                }


        }
    }

    public void sysSum(){
        LocalDate now = LocalDate.now();
        String s = now.format(DateTimeFormatter.BASIC_ISO_DATE);
        String dateS = "t_node_user_profit_" + s;
       /* List<Map<String,Object>> lists= userIncomeTempMapper.getSum(s1);
        for (Map<String,Object> map : lists){
            BigDecimal sum = new BigDecimal(map.get("sum").toString());
            Integer userId = Integer.valueOf(map.get("userId").toString());
            userMapper.addExtraMoney(userId,sum);
            userMapper.insertIncomeSum(userId,sum);

        }*/
        List<Map<String,Object>> userIds = nodeUserRecordMapper.getUserIds();
        if (CollectionUtils.isNotEmpty(userIds)){
            for (Map<String,Object> mapS :userIds){
                Integer userId = Integer.valueOf(mapS.get("userId").toString());
                Integer nodeId =Integer.valueOf(mapS.get("nodeId").toString()) ;
                Integer state=nodeMapper.getStateByNodeId(nodeId);
                if (state == 1){
                    BigDecimal sum=nodeProfitRecordMapper.getSumByRecordUserId(dateS,userId,nodeId);
                    //获取静态的
                    //BigDecimal sum1=nodeProfitRecordMapper.getSumByRecordUserId2(dateS,userId);
                    Map<String, Object> map1 =new HashMap<>();
                    BigDecimal sum1= nodeUserTotalMapper.getSumByUserId(Long.valueOf(userId)).getAmount();
                    BigDecimal maxAmountMin=nodeMapper.getMaxAmountMin();
                    if (sum1.compareTo(maxAmountMin)>=0){
                        map1=nodeMapper.getFlowRate2(maxAmountMin);
                    }else {
                        map1 = nodeMapper.getFlowRate(sum1);
                    }
                    BigDecimal staticRate1 = new BigDecimal(map1.get("staticRate").toString()) ;
                    //判断是否有动态的收益
                    if (sum !=null){
                        //2表示动态的，1表示静态的，0表示未转账
                        //nodeUserProfitMapper.insertSumRecord(userId,sum,2);
                        //nodeUserProfitMapper.insertSumRecord(userId,sum1.multiply(staticRate1),1);
                        NodeUserProfit nodeUserProfit = new NodeUserProfit();
                        nodeUserProfit.setAmount(sum);
                        nodeUserProfit.setUserId(userId.longValue());
                        nodeUserProfit.setType(2);
                        nodeUserProfit.setCreatetime(LocalDateTime.now());
                        nodeUserProfit.setUpdatetime(LocalDateTime.now());
                        nodeUserProfitMapper.insertNodeUserProfit(nodeUserProfit);
//                    nodeUserProfitMapper.insertSumRecord(userId,sum,2);

                        NodeUserProfit nodeUserProfit2 = new NodeUserProfit();
                        nodeUserProfit2.setAmount(sum1.multiply(staticRate1));
                        nodeUserProfit2.setUserId(userId.longValue());
                        nodeUserProfit2.setType(1);
                        nodeUserProfit2.setCreatetime(LocalDateTime.now());
                        nodeUserProfit2.setUpdatetime(LocalDateTime.now());
                        nodeUserProfitMapper.insertNodeUserProfit(nodeUserProfit2);
//                    nodeUserProfitMapper.insertSumRecord(userId,sum1,1);
                        log.info("动态的同步插入累计收益表成功");
                    }else {
                        NodeUserProfit nodeUserProfit2 = new NodeUserProfit();
                        nodeUserProfit2.setAmount(sum1.multiply(staticRate1));
                        nodeUserProfit2.setUserId(userId.longValue());
                        nodeUserProfit2.setType(1);
                        nodeUserProfit2.setCreatetime(LocalDateTime.now());
                        nodeUserProfit2.setUpdatetime(LocalDateTime.now());
                        nodeUserProfitMapper.insertNodeUserProfit(nodeUserProfit2);
                        //nodeUserProfitMapper.insertSumRecord(userId,sum1.multiply(staticRate1),1);
                        log.info("静态的同步插入累计收益表成功");
                    }
                }
            }
        }
        log.info("动态收益结算同步完成");
    }

    //当天结算完之后，进行转账
    @Scheduled(cron = "0 15 01 ? * *")
    public void trade(){
        /*//摩云主账户地址
        Params paramsA = paramsMapper.getParams(CoinParams.MOC_ADDRESS);
        String mAddress = paramsA.getParamValue();
        //摩云主账户密码
        Params paramsP = paramsMapper.getParams(CoinParams.MOC_PASSWORD);
        String password = paramsP.getParamValue();*/
        //动态开关：0：关闭，1：开启
        Params paramsS = paramsMapper.getParams(CoinParams.MOC_DYNAMIC_SWITCH);
        String paramValue = paramsS.getParamValue();
        if (paramValue.equals("1")){
            //获取用户
            List<Long> lists=nodeUserProfitMapper.getUserIds();
            log.info("动静态转账的用户lists={}",lists);
            //转账
            /*Address address=new Address();
            address.setCoinNo(Long.valueOf(40));
            address.setStatus(1);
            Address getAddress = addressMapper.getAddressByCondition(address);
            address.setValue(getAddress);
            PNTCoinApi api = PNTCoinApi.getApi(address);*/
            Coin coin=new Coin();
            coin.setCoinNo(Long.valueOf(40));
            coin.setState(1);
            Coin getCoin = coinMapper.getCoinByCondition(coin);
            coin.setValue(getCoin);
            Params param2 = paramsMapper.getParams(CoinParams.MOC_TRADE_FEE);
            String fee=param2.getParamValue();
            String tokenName = coin.getCoinName().toUpperCase();
            String pubKey="";
            String remark="社区主账户给用户转静态动态收益";
            //去总收益记录表获取用户id和总金额
            //List<Map<String,Object>>  lists=nodeMapper.getTotalSum();
            if (CollectionUtils.isNotEmpty(lists)){
                for (Long userId : lists){
                    //BigDecimal  profit = new BigDecimal(map.get("profit").toString());
                    //Integer userId = Integer.valueOf(map.get("userId").toString());
                    BigDecimal sum1=nodeUserProfitMapper.getSumByUserId(userId);
                    BigDecimal sum=sum1.setScale(6);
                    Long nodeId=nodeUserRecordMapper.getNodeId(userId);
                    Node node=new Node();
                    node.setId(nodeId);
                    node=nodeMapper.queryNodeLimit1(node);
                    String mAddress=node.getAddress();
                    List<UserWallet> wallets = userWalletMapper.getuserWallet(userId);
                    if (CollectionUtils.isNotEmpty(wallets)){
                        String hash = tradeInfoService.systemToUserTrade(mAddress, userId, sum.toString(), pubKey,remark, fee, tokenName);
                        //String hash = api.sendTransaction(mAddress, inAddress, sum.toString(), pubKey, remark, fee, tokenName);
                        log.info("主账户静态动态转账给用户之后返回的hash={},userId={}",hash,userId);
                        if (StringUtils.isNotBlank(hash)){
                            //更新状态
                            nodeUserProfitMapper.updateStatus(userId,hash);
                            log.info("主账户静态动态转账成功");
                        }else{
                            log.info("主账户静态动态转账失败");
                            throw new RRException(RErrorEnum.TRANSFER_ERROR);
                        }
                    }
                }
            }
        }
    }
    /**
     * 每天创建一张新的社区节点投注记录表
     */

    @Scheduled(cron = "0 00 00 * * ?")
    public void crateTable() {
        LocalDate now = LocalDate.now();
        String s = now.format(DateTimeFormatter.BASIC_ISO_DATE);
        String s1 = "t_node_user_profit_" + s;
        String creatSql = "CREATE TABLE" + " " + s1 + " " + "(" +
                "`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id'," +
                "`user_id` bigint(20) NOT NULL COMMENT '用户id'," +
                "`profit` decimal(30,6) NOT NULL COMMENT '用户给推荐人带来的收益'," +
                "`type` int(2) NOT NULL COMMENT '类型（1.静态，2.动态）'," +
                "`from_id` bigint(20) NOT NULL COMMENT '来源id'," +
                "`create_time` datetime NOT NULL COMMENT '创建时间'," +
                "`node_id` int(10) DEFAULT NULL COMMENT '节点id'," +
                "`tag` int(2) DEFAULT NULL COMMENT '1代表是真实的，0代表是不真实的'," +
                " PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB AUTO_INCREMENT=339 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci";
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(jdbcDriver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            long l = stmt.executeLargeUpdate(creatSql);
            if (l == 0) {
                log.info("创建表成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}