package com.inesv.service;

import com.inesv.model.SpotDealDetail;
import com.inesv.util.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

import com.inesv.util.ResponseParamsDto;

import java.io.IOException;
import java.util.Map;

public interface SpotDisputeService {

    /*    *//**
     * 纠纷申请提交
     * @param buyUserNo
     * @param sellUserNo
     * @param dealNo
     * @param userNo
     * @param remark
     * @param disputePhone
     * @param responseParamsDto
     * @return
     * @throws Exception
     *//*
    Map<String,Object> disputeDealDetail(Integer buyUserNo, Integer sellUserNo, Long dealNo,
                                          Integer userNo, Integer type, String remark, MultipartFile disputePhone, ResponseParamsDto responseParamsDto) throws Exception;

    *//**
     * 获取用户纠纷申请
     * @param userNo
     * @param pageSize
     * @param lineSize
     * @param responseParamsDto
     * @return
     * @throws Exception
     *//*
    Map<String,Object> getSpotDisputByUserNo(Integer userNo, Integer type,Integer state,Integer pageSize, Integer lineSize, ResponseParamsDto responseParamsDto) throws Exception;

    *//**
     * 管理员通过用户的纠纷申请
     * @param disputNo
     * @return
     * @throws Exception
     *//*
    Map<String,Object> agreeDisput(Integer disputNo, String reason, ResponseParamsDto responseParamsDto) throws Exception;

    *//**
     * 管理员拒绝用户的纠纷申请
     * @param disputNo
     * @return
     * @throws Exception
     *//*
    Map<String,Object> refuseDisput(Integer disputNo, String reason, ResponseParamsDto responseParamsDto) throws Exception;*/

    /**
     * 提交纠纷申请
     */
    BaseResponse Submission(String data) throws Exception;

    /**
     * 纠纷列表
     */
    BaseResponse disputeList(String data);

    /**
     * 上传纠纷图片
     */
    BaseResponse disputeImg(String data, MultipartFile photo) throws IOException;

    BaseResponse disputeDetail(String data);

    //自动纠纷检查
    void autoSpute(long orderId) throws Exception;





    /************ C2C管理员纠纷相关   ****/
    /**
     * 管理员通过用户的纠纷申请
     *
     * @return
     * @throws Exception
     */
    BaseResponse agreeDispute(Long id, ResponseParamsDto responseParamsDto) throws Exception;

}
