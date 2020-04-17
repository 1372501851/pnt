package com.inesv.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import com.inesv.util.ValidataUtil;
import com.alibaba.fastjson.JSONObject;
import com.inesv.annotation.UnLogin;
import com.inesv.service.WhitePaperService;

@RestController
public class WhitePaperController {

	private static final Logger log = LoggerFactory
			.getLogger(WhitePaperController.class);
	@Resource
	WhitePaperService whitePaperService;
	@PostMapping("/uploadWhitePaper")
	public BaseResponse uploadWhitePaper(
			@RequestParam("file") MultipartFile file,
			@RequestParam("data") String data) {
		log.info("上传白皮书"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("上传白皮书接口参数:" + data);
		log.info("上传白皮书文件名:" + file.getOriginalFilename());
		if (ValidataUtil.isNull(data) || file == null)
			return RspUtil.rspError("参数不能为空");
		try {
			BaseResponse result =whitePaperService.uploadFile(file, data);
			log.info("上传白皮书接口返回参数:"+JSONObject.toJSONString(result));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return RspUtil.error();
		}
	}
}
