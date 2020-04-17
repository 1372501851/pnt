package com.inesv.service;

import com.inesv.model.MemoAddress;

public interface MemoAddressService {
	int addMemoAddress(MemoAddress memoAddress);

	MemoAddress getMemoAddressByCondition(MemoAddress memoAddress);
}
