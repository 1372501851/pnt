package com.inesv.service;

import org.springframework.web.multipart.MultipartFile;

import com.inesv.util.BaseResponse;

public interface WhitePaperService {
	BaseResponse uploadFile(MultipartFile file, String data) throws Exception;
}
