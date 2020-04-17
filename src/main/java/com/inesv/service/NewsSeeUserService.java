package com.inesv.service;


import com.inesv.util.BaseResponse;

public interface NewsSeeUserService {
	

	
	/**
	 * 资讯详情
	 * @param data
	 * @return
	 */
	BaseResponse  updateNewsId(String data);


	/**
	 *获取未读资讯id
	 * @param data
	 * @return
	 */
	BaseResponse getUnreadNewsIdList(String data);

}
