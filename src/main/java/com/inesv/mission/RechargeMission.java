package com.inesv.mission;

import com.inesv.mapper.UserWalletMapper;
import com.inesv.model.UserWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

//@Component
public class RechargeMission {
    private Logger logger = LoggerFactory.getLogger(RechargeMission.class);

	@Autowired
	UserWalletMapper userWalletMapper;
	@Autowired
	private AsyncRechargeTask asyncTask;
	private ThreadPoolTaskScheduler scheduler;

	private ScheduledFuture<?> future;
	private CronTrigger trigger = new CronTrigger("0 0/10 * * * ?");

	//@PostConstruct
	public void start(){
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.initialize();
        future = scheduler.schedule(new RechargeTask(), trigger);
    }

    //重启任务
    public void restart(){
        logger.info("重启定时充值任务");
        stop();
        future = scheduler.schedule(new RechargeTask(), trigger);
    }

    //定时任务停止后重启任务
    public void restartIfCancelled(){
        boolean flag = future.isCancelled();
        logger.info("定时充值任务存活情况future.isCancelled()：{}", flag);
        if(flag){
            future = scheduler.schedule(new RechargeTask(), trigger);
        }
    }

    public void stop(){
        if(future != null){
            boolean flag = future.cancel(true);
            logger.info("取消定时任务结果：{}", flag);
        }
    }

	private class RechargeTask implements Runnable{
        @Override
        public void run() {
            List<UserWallet> wallets = userWalletMapper.getAllUserWalletWithoutPTN();
            logger.info("执行定时充值任务，任务数量：{}"+ wallets.size());
            for (UserWallet userWallet : wallets) {
                try {
                    asyncTask.doTask(userWallet);
                } catch (Exception e) {
                    logger.error("执行定时充值失败", e);
                    continue;
                }
            }
        }
    }
}
