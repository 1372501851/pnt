/**   
 * Copyright © 2018  
 * @Package: com.inesv.task   
 * @date: 2018年3月21日 下午5:45:13 
 */
package com.inesv.task;

import com.inesv.mapper.SpotDealDetailMapper;
import com.inesv.model.SpotDealDetail;
import com.inesv.service.SpotDisputeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * @Description:TODO
 * @author: cmk
 * @date: 2018年3月21日 下午5:45:13
 */

//@Component
public class CheckDisputeTask {

	private final static Logger log = LoggerFactory
			.getLogger(CheckDisputeTask.class);

	@Autowired
	private SpotDealDetailMapper spotDealDetailMapper;

	@Autowired
	private SpotDisputeService spotDisputeService;

	/**
	 * 定时检查已到纠纷时间的订单
	 */
//	@Scheduled(fixedRate = 1000 * 60 * 5)
	public void editOrderState() {

		SpotDealDetail condition = new SpotDealDetail();

		// 达到纠纷的时长
		int minutes = 30;
		condition.setState(4);
		condition.setDateType(minutes);
		List<SpotDealDetail> spotDealDetails = spotDealDetailMapper
				.selectAutoSpute(condition);
		log.info("定时器检查已到纠纷时间的订单 | 要处理订单数 : " + spotDealDetails.size());
		for (int i = 0; i < spotDealDetails.size(); i++) {

			String orderNo = spotDealDetails.get(i).getOrderNo();

			try {
				spotDisputeService.autoSpute(spotDealDetails.get(i).getId());
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw, true));
				String exception = sw.toString();
				log.error("待卖方确认单--" + orderNo + "--自动转纠纷发生异常：" + exception);
			}

		}

	}

}
