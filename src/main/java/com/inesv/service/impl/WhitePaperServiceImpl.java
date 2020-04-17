package com.inesv.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.UserMapper;
import com.inesv.mapper.WhitePaperMapper;
import com.inesv.model.User;
import com.inesv.model.WhitePaper;
import com.inesv.service.WhitePaperService;
import com.inesv.util.BaseResponse;
import com.inesv.util.LanguageUtil;
import com.inesv.util.MyTaskUtil;
import com.inesv.util.QiniuUploadUtil;
import com.inesv.util.ResponseParamsDto;
import com.inesv.util.RspUtil;
import com.inesv.util.ThreadUtil;
import com.inesv.util.ValidataUtil;

@Service
@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
public class WhitePaperServiceImpl implements WhitePaperService {

	private static final Logger log = LoggerFactory.getLogger(WhitePaperServiceImpl.class);

	@Resource
	WhitePaperMapper whitePaperMapper;

	@Resource
	UserMapper userMapper;

	public BaseResponse uploadFile(final MultipartFile file, String data) throws Exception {
		JSONObject json = JSONObject.parseObject(data);
		// 中英文翻译资源
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));

		final Long userId = json.getLong("userId");
		final String remark = json.getString("remark");
		final String company = json.getString("company");

		// 用户校验
		User user = userMapper.getUserInfoById(userId);
		if (user == null)
			return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

		// 必填参数不可为空
		if ( StringUtils.isBlank(company))
			return RspUtil.rspError(responseParamsDto.PRIMARY_PARAMS_NOT_NULL);

		// 异步上传文件，添加白皮书记录
		try {
			ThreadUtil.execute(new MyTaskUtil() {
				@Override
				public void run() {
					try {
						String originalFileName = file.getOriginalFilename();
						String lastName = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);

						String name = QiniuUploadUtil.upLoadImage(file.getInputStream(),
								UUID.randomUUID().toString() + "." + lastName);
						if (!ValidataUtil.isNull(name)) {
							// 上传成功，并获取到文件的下载url
							String url = QiniuUploadUtil.getStartStaff() + name;
							WhitePaper paper = new WhitePaper();
							paper.setUrl(url);
							paper.setUserId(userId.intValue());
							paper.setRemark(remark);
							paper.setState(0);
							paper.setCompany(company);
							int count = whitePaperMapper.insertPaper(paper);
							if (count > 0) {
								log.error("白皮书数据记录成功");
							} else {
								log.error("白皮书数据记录失败");
							}
						} else {
							// 上传失败
							log.error("白皮书文件上传失败");
						}

					} catch (Exception e) {
					}
				}
			}, 2, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RspUtil.successMsg("提交申请成功");

	}
}
