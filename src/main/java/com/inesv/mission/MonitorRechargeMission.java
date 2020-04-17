package com.inesv.mission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 根据邀请注册记录发放奖励
 */
//@Component
public class MonitorRechargeMission {
	private Logger logger = LoggerFactory.getLogger(MonitorRechargeMission.class);

	@Autowired
	private RechargeMission rechargeMission;

	//@Scheduled(cron = "0 0 2 * * ?")
	public void reward() {
		//logger.info("监控定时充值接口是否存活");
		//rechargeMission.restartIfCancelled();
		rechargeMission.restart();
	}


}
