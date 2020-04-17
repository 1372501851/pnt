package com.inesv.mission;

import com.inesv.model.CycleTrade;
import com.inesv.service.CycleTradeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CycleTradeMission {
	@Autowired
	CycleTradeService cycleTradeService;
	private Logger logger = Logger.getLogger(CycleTradeMission.class);

	// 周期解冻
	@Scheduled(cron = "0 0 6 ? * *")
	// 每天早上六点执行
	// @Scheduled(cron = "0 0/5 * * * ?")
	// 每三分钟执行一次
	public void unfeezed() throws Exception {
		logger.info("周期解冻=============开始");
		List<CycleTrade> cycles = cycleTradeService.getAllNeedDo();
		for (CycleTrade cycle : cycles) {
			try {
				boolean ok = cycleTradeService.doCycleTrade(cycle);
				logger.info("周期解冻===" + cycle.getAddress() + "========" + ok);
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("error===" + e.getMessage());
			}
		}
	}
	/*
	 * @Scheduled(cron = "0 0 6 ? * *") // 每天早上六点钟执行 周期转账 public void recharge()
	 * { logger.info("定时处理周期到账==========开始"); try { List<CycleTrade> needToDo =
	 * cycleTradeService.getAllNeedDo(); if (needToDo != null && needToDo.size()
	 * > 0) { for (CycleTrade cycleTrade : needToDo) { try { boolean ok =
	 * cycleTradeService.doCycleTrade(cycleTrade);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } } } } catch (Exception e)
	 * { e.printStackTrace(); } }
	 */
}
