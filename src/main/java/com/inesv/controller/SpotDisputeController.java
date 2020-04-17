package com.inesv.controller;

import com.inesv.annotation.UnLogin;
import com.inesv.service.SpotDisputeService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/spotDispute")
public class SpotDisputeController {

    private static Logger logger = LoggerFactory.getLogger(SpotDisputeController.class);

    @Autowired
    private SpotDisputeService spotDisputeService;

/*    *//**
     * 用户纠纷申请订单
     *//*
    @ResponseBody
    @RequestMapping(value = "/addSpotDispute", method = RequestMethod.POST)
    public Map<String,Object> addSpotDispute(HttpServletRequest request, @RequestParam Integer buyUserNo, @RequestParam Integer sellUserNo, @RequestParam Long dealNo,
                                             @RequestParam Integer usreNo, @RequestParam Integer type, @RequestParam String remark, MultipartFile disputePhone) {
        Map<String,Object> resultMap=new HashMap<>();
        return resultMap;
    }

    *//**
     * 获取用户纠纷申请
     *//*
    @ResponseBody
    @RequestMapping(value = "/getSpotDispute", method = RequestMethod.POST)
    public Map<String,Object> getSpotDispute(HttpServletRequest request, @RequestParam Integer userNo, Integer type, Integer state, @RequestParam Integer pageSize,
    		@RequestParam Integer lineSize) {
       
    	Map<String,Object> resultMap=new HashMap<>();
        return resultMap;
    }*/

    /**
     * 新增纠纷
     * */
    @PostMapping("/submission")
    @ResponseBody
    public BaseResponse submitDispute(String data){
        logger.info("纠纷提交接口"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        logger.info("纠纷提交接口参数"+data);
        try {
			return spotDisputeService.Submission(data);
		} catch (Exception e) {
			e.printStackTrace();
			return RspUtil.error();
		}
    }

    /**
     * 纠纷列表
     * */
    @UnLogin
    @GetMapping("/disputeList")
    public BaseResponse disputeList(String data){
        logger.info("纠纷提交接口"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        logger.info("纠纷提交接口参数"+data);
        return spotDisputeService.disputeList(data);
    }

    @PostMapping("/uploadImg")
    public BaseResponse uploadImg(String data,MultipartFile photo) throws IOException {
        logger.info("纠纷截图上传接口"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        logger.info("纠纷截图上传接口参数"+data);
        return spotDisputeService.disputeImg(data,photo);
    }

    @UnLogin
    @GetMapping("/disputeDetail")
    public BaseResponse disputeDetail(String data){
        logger.info("纠纷详情接口"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        logger.info("纠纷纠纷详情接口参数"+data);
        return spotDisputeService.disputeDetail(data);
    }
}
