package com.inesv.service;

import java.util.List;

import com.inesv.model.CycleTrade;

public interface CycleTradeService {
	// 定时处理周期转账,根据定时任务处理
	public boolean doCycleTrade(CycleTrade cycleTrade) throws Exception;

	// 获取所有的未完成的定时任务
	public List<CycleTrade> getAllNeedDo() throws Exception;
}
