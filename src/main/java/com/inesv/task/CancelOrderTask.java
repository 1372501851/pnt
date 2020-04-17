package com.inesv.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.inesv.mapper.SpotDealDetailMapper;
import com.inesv.model.SpotDealDetail;
import com.inesv.service.SpotDealDetailService;
import com.inesv.util.ResponseParamsDto;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

//@Component
public class CancelOrderTask {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SpotDealDetailMapper spotDealDetailMapper;

	@Autowired
	private SpotDealDetailService spotDealDetailService;

	/**
	 * 定时修改未支付的订单（半小时前的订单）
	 */
	@Scheduled(fixedRate = 1000 * 60 * 5)
	public void editOrderState() {
		try {
			ResponseParamsDto responseParams = ResponseParamsDto.responseParamsCNDto;
			SpotDealDetail spotDealDetail = new SpotDealDetail();
			spotDealDetail.setState(3);
			spotDealDetail.setDateType(2);
			List<SpotDealDetail> spotDealDetails = spotDealDetailMapper
					.selectSpotDealDetailByConditionsAndDate(spotDealDetail);
			logger.info(" editOrderState timer | order num : "
					+ spotDealDetails.size());
			for (int i = 0; i < spotDealDetails.size(); i++) {
				spotDealDetailService.cancel(spotDealDetails.get(i)
						.getOrderNo(), (long) spotDealDetails.get(i)
						.getBuyUserNo(), 2, responseParams);
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String exception = sw.toString();
			logger.error(exception);
		}
	}

}
