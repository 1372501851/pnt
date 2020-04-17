package com.inesv.service;


import com.inesv.util.BaseResponse;

public interface NewsService {
	
	/**
	 *资讯信息集合
	 * @param data
	 * @return
	 */
	BaseResponse getNewsList(String data);
	
	
	/**
	 * 资讯详情
	 * @param data
	 * @return
	 */
	BaseResponse  getNewsDetail(String data);

}
