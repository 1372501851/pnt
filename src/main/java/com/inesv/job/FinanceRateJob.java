/**
 * 
 */
package com.inesv.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.ParamsMapper;
import com.inesv.model.Params;
import com.inesv.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Admin
 * 获取汇率定时job
 */

@Component
public class FinanceRateJob {
	
	@Resource
	ParamsMapper paramsMapper;
	
	private static final Logger log = LoggerFactory.getLogger(FinanceRateJob.class);
	
	//每三十分钟执行一次
	@Scheduled(cron="0 0/5 * * * ?")
    public void cronJob(){
		try{
			String financeRate =  HttpUtil.getUsdtQcMarket("usdt_qc");
			JSONObject json = JSON.parseObject(financeRate);
			String ticker =json.getString("ticker");
			JSONObject json1 = JSON.parseObject(ticker);
			String paramValue = json1.getString("last");
			String paramKey="usd_cny_rate";
			Params editParam =new Params();
			editParam.setParamKey(paramKey);
			editParam.setParamValue(paramValue);
			paramsMapper.updateParams(editParam);
		}catch (Exception e){
			return;
		}
    }

}
