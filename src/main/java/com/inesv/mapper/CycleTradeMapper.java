package com.inesv.mapper;

import java.util.List;

import com.inesv.model.CycleTrade;

public interface CycleTradeMapper {
	public int insertCycleTrade(CycleTrade cycleTrade);

	public List<CycleTrade> getAllNeedToDo();

	public int modifyCycleTrade(CycleTrade cycleTrade);

	public CycleTrade findByUserAddress(String address);
}
