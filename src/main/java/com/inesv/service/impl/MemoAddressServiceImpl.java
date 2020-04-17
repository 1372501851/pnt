package com.inesv.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inesv.mapper.MemoAddressMapper;
import com.inesv.model.Memo;
import com.inesv.model.MemoAddress;
import com.inesv.service.MemoAddressService;
import com.inesv.service.MemoService;
import com.inesv.util.BaseResponse;

@Service
@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
public class MemoAddressServiceImpl implements MemoAddressService {
	@Resource
	MemoAddressMapper memoAddressMapper;

	@Override
	public int addMemoAddress(MemoAddress memoAddress) {
		// TODO Auto-generated method stub
		int k = memoAddressMapper.addMemoAddress(memoAddress);
		return k;
	}

	@Override
	public MemoAddress getMemoAddressByCondition(MemoAddress memoAddress) {
		// TODO Auto-generated method stub
		return memoAddressMapper.getMemoAddressByCondition(memoAddress);
	}

}
