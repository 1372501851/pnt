package com.inesv.mapper;

import java.util.List;

import com.inesv.model.Memo;

public interface MemoMapper {
	List<Memo> getAllMemo();

	int addMemo(String memo);

	Memo findByMemo(String memo);

	int delMemo(String id);
}
