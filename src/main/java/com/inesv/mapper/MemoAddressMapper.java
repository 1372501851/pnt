package com.inesv.mapper;

import java.util.List;

import com.inesv.model.MemoAddress;

public interface MemoAddressMapper {
	int addMemoAddress(MemoAddress memoAddress);

	MemoAddress getMemoAddressByCondition(MemoAddress memoAddress);
}
