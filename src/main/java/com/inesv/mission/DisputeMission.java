package com.inesv.mission;

import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.SpotDealDetailService;
import com.inesv.service.SpotDisputeService;
import com.inesv.service.UserWalletService;
import com.inesv.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author RLY
 */
@Slf4j
@Component
public class DisputeMission {

    /*管理员同意*/
    private static Lock agreeDisputeLock = new ReentrantLock();    // 锁对象

    @Autowired
    private SpotDisputeService spotDisputeService;
    @Autowired
    private SpotDisputeMapper spotDisputeMapper;
    @Autowired
    private SpotDealDetailMapper spotDealDetailMapper;
    @Autowired
    private UserWalletMapper userWalletMapper;
    @Autowired
    private CoinMapper coinMapper;
    @Autowired
    private SpotDealDetailService spotDealDetailService;


    @Scheduled(initialDelay = 5 * 1000, fixedRate = 120 * 1000)
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void doTask() throws Exception {
        //获取管理员同意的列表
        SpotDispute spotDispute = new SpotDispute();
        //状态,0：申请中，1：已通过 2：拒绝！3：放行中 4放行失败
        spotDispute.setState(3);
        /**
         * 类型：
         * 1.买方实际未付款，请求强制取消订单
         * 2.买方一直未付款，请求强制取消订单
         * 3.卖方一直未放行，请求强制放行订单
         * 4.支付账号错误，请求强制取消订单
         */
        spotDispute.setType(3);
        List<SpotDispute> spotDisputeList = spotDisputeMapper.getSpotDisputeByConditions(spotDispute);
        if(spotDisputeList.size()!=0){
            log.info("定时器开始处理放行数据！！");
            for (SpotDispute dispute : spotDisputeList) {
                log.info("本次处理的OrderNo单号: {}",dispute.getOrderNo());
                agreeDispute(dispute.getId());
            }
        }else{
            log.info("暂无需要处理的数据");
        }

    }



    /**
     * 管理人员同意放行
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void agreeDispute(Long id) throws Exception {
        // 纠纷信息
        SpotDispute spotDispute1 = new SpotDispute();
        spotDispute1.setId(id);
        spotDispute1 = spotDisputeMapper.getSpotDisputeByCondition(spotDispute1);
        if (spotDispute1 == null) {
            log.info("纠纷记录不存在！！");
        }

        // 订单信息
        SpotDealDetail spotDealDetail = new SpotDealDetail();
        spotDealDetail.setId(spotDispute1.getDealNo());
        spotDealDetail.setBuyUserNo(spotDispute1.getBuyUserNo());
        spotDealDetail.setSellUserNo(spotDispute1.getSellUserNo());
        spotDealDetail.setState(6);
        spotDealDetail = spotDealDetailMapper.selectSpotDealDetailByCondition(spotDealDetail);
        if (spotDealDetail == null) {
            log.info("匹配记录不存在！！");
        }
        if ((spotDealDetail.getBuyEntrust() == null || spotDealDetail.getBuyEntrust() == 0)
                && (spotDealDetail.getSellEntrust() == null || spotDealDetail.getSellEntrust() == 0)) {
            log.info("匹配记录不存在！！");
        }

//        log.info("买家提起纠纷，管理员同意！！");
//        // 买家提起纠纷，管理员同意
//        if (spotDispute1.getUserNo().toString().equals(spotDispute1.getBuyUserNo().toString())) {
//
//        }
        log.info("管理员同意放行！！");
        buyAgree(id, spotDealDetail, spotDispute1.getBuyUserNo(), spotDispute1.getSellUserNo(), spotDispute1.getDealNo());

    }


    /**
     * 买家請求放行
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void buyAgree(Long id, SpotDealDetail spotDealDetail, Integer buyUserNo, Integer sellUserNo, Long dealNo) throws Exception {
        log.info("买家請求放行！！");
        // 买方钱包
        UserWallet buyUserWallet = new UserWallet();
        buyUserWallet.setUserId(Long.valueOf(buyUserNo));
        buyUserWallet.setType(spotDealDetail.getCoinNo());
        buyUserWallet = userWalletMapper.getUserWalletByCondition(buyUserWallet);
        if (buyUserWallet == null || ValidataUtil.isNull(buyUserWallet.getBalance())
                || buyUserWallet.getBalance().compareTo(new BigDecimal("0")) == -1) {
            log.info("买方钱包用户资产异常，失败!");
        }

        // 卖方钱包
        UserWallet sellUserWallet = new UserWallet();
        sellUserWallet.setUserId(Long.valueOf(sellUserNo));
        sellUserWallet.setType(spotDealDetail.getCoinNo());
        sellUserWallet = userWalletMapper.getUserWalletByCondition(sellUserWallet);
        if (sellUserWallet == null || ValidataUtil.isNull(sellUserWallet.getUnbalance())
                || buyUserWallet.getUnbalance().compareTo(new BigDecimal("0")) == -1) {
            log.info("卖方钱包用户资产异常，失败!");
        }

        Coin coin = coinMapper.getCoinByCoinNo((long) spotDealDetail.getCoinNo());
        if (coin == null || ValidataUtil.isNull(coin.getApiType())) {
            log.info("货币详情异常");
        }

        BigDecimal sellUserUnBalance = sellUserWallet.getUnbalance();    // 卖方已冻结资产
        BigDecimal sellPoundage = spotDealDetail.getPoundage();    // 手续费
        BigDecimal tradeNum = spotDealDetail.getDealNum();    // 交易量
        BigDecimal sumNum = tradeNum.add(sellPoundage);
        if (sellUserUnBalance.compareTo(sumNum) == -1) {
            log.info("主币余额不足");
        }

        // 管理员强制拨款
        try {
            // 修改纠纷记录为通过
            editDisputeState(id, dealNo);

            spotDealDetailService.confirmMatchLogic(spotDealDetail, buyUserWallet, sellUserWallet, coin, spotDealDetail.getDealNum(),
                    sellPoundage, sumNum, 1, null);
            log.error("纠纷 ： 确认拨款成功！");
        } catch (Exception e) {
            log.error("纠纷 ： 确认拨款失败！", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }


    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void editDisputeState(Long id, Long dealNo) throws Exception {
        // 修改纠纷状态
        SpotDispute spotDispute = new SpotDispute();
        spotDispute.setId(id);
        spotDispute.setState(1);
        int code = spotDisputeMapper.updateSpotDispute(spotDispute);
        if (code != 1) {
            throw new Exception("纠纷 - 修改纠纷记录状态异常");
        }

    }

    public static void main(String[] args) {
        BigDecimal num1 = new BigDecimal(10);
        BigDecimal num2 = new BigDecimal(10);
        if (num2.compareTo(num1) >= 0) {
            log.info("num2 大于等于 num1");
        }

    }

}
