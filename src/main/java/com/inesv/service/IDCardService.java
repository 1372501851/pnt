package com.inesv.service;

import com.inesv.model.IDCard;
import com.inesv.util.ResponseParamsDto;
import org.springframework.web.multipart.MultipartFile;

public interface IDCardService {
	void uploadIDCard(String name, String idCardNumber, MultipartFile[] file, ResponseParamsDto responseParamsDto, String token) throws RuntimeException;

	IDCard getIDCard(String token, ResponseParamsDto responseParamsDto) throws RuntimeException ;
}
