package com.inesv.service.impl;

import com.inesv.mapper.PicVerifyCodeRecordMapper;
import com.inesv.model.PicVerifyCodeRecord;
import com.inesv.service.PicVerifyCodeRecordService;
import com.inesv.util.BaseResponse;
import com.inesv.util.DateTools;
import com.inesv.util.ResponseParamsDto;
import com.inesv.util.RspUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: xujianfeng
 * @create: 2018-06-09 11:24
 **/
@Service
public class PicVerifyCodeRecordServiceImpl implements PicVerifyCodeRecordService {
    @Autowired
    private PicVerifyCodeRecordMapper verifyCodeRecordMapper;

    private final static Integer IP_DAILY_LIMIT = 20;//IP发送请求日限制
    private final static Integer MOBILE_DAILY_LIMIT = 15;//手机号码发送请求日限制

    private Lock lock = new ReentrantLock();

    @Override
    public BaseResponse checkIfIllegal(String phone, String ip, ResponseParamsDto responseParamsDto) throws Exception {
        try {
            lock.lock();
            String dateFormat = DateTools.formatDateTime(new Date(), "yyyy-MM-dd");

            //查询IP请求是否超过日限制
            PicVerifyCodeRecord recordIP = new PicVerifyCodeRecord();
            recordIP.setIp(ip);
            recordIP.setDateFormat(dateFormat);
            int count = verifyCodeRecordMapper.getCountByConditions(recordIP);
            if(count >= IP_DAILY_LIMIT){
                return RspUtil.rspError(responseParamsDto.ILLEGAL_REQUEST);
            }

            //查询手机号码请求是否超过日限制
            PicVerifyCodeRecord recordMobile = new PicVerifyCodeRecord();
            recordMobile.setMobile(phone);
            recordMobile.setDateFormat(dateFormat);
            count = verifyCodeRecordMapper.getCountByConditions(recordMobile);
            if(count >= MOBILE_DAILY_LIMIT){
                return RspUtil.rspError(responseParamsDto.ILLEGAL_REQUEST);
            }
        } finally {
            lock.unlock();
        }
        return RspUtil.success();
    }

    @Override
    public PicVerifyCodeRecord getValidVerifyCode4LastOne(PicVerifyCodeRecord record) {
        return verifyCodeRecordMapper.getValidVerifyCode4LastOne(record);
    }

    @Override
    public Integer update(PicVerifyCodeRecord codeRecord) {
        return verifyCodeRecordMapper.update(codeRecord);
    }
}
