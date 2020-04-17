package com.inesv.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.SpotDealDetailService;
import com.inesv.service.SpotDisputeService;
import com.inesv.service.UserWalletService;
import com.inesv.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Transactional
@Service
public class SpotDisputeServiceImpl implements SpotDisputeService {

    private static Logger logger = LoggerFactory.getLogger(SpotDisputeServiceImpl.class);

   /* @Resource
    private UserMapper userMapper;

    @Resource
    private SpotDisputeMapper spotDisputeMapper;

    @Resource
    private SpotDealDetailMapper spotDealDetailMapper;*/

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CoinMapper coinMapper;
    @Autowired
    private UserWalletMapper userWalletMapper;
    @Autowired
    private SpotEntrustMapper spotEntrustMapper;
    @Autowired
    private SpotDisputeMapper spotDisputeMapper;
    @Autowired
    private SpotDealDetailMapper spotDealDetailMapper;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private SpotDealDetailService spotDealDetailService;
    @Autowired
    private ParamsMapper paramsMapper;


    /**
     * 提交纠纷申请
     */
    @Override
    public BaseResponse Submission(String data) throws Exception {
        JSONObject json = JSON.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));

        /**交易时间段限制**/
        Params timeParams = paramsMapper.getParams("C2C_TRADE_TIME");
        String time=timeParams.getParamValue();
        String[] split = time.split("-");
        List<String> list = new ArrayList<>();
        Collections.addAll(list, split);
        String beginTime=list.get(0);
        String endTime=list.get(1);
        boolean timeFlag=DateTools.isEffectiveDate(beginTime,endTime);
        if (timeFlag==false){
            return RspUtil.rspError(responseParamsDto.C2C_TRADE_TIME_ERROR+"("+time+")");
        }


        String token = json.getString("token");
        if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        User user = userMapper.getUserInfoByToken(token);
        if (user == null) return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        SpotDealDetail spotDealDetail = spotDealDetailMapper.queryByOrderNo(json.getString("orderNo"));

        String remark = json.getString("remark");
        if (StringUtils.isBlank(remark))
            return RspUtil.rspError(responseParamsDto.PARAMS_NULL_DESC);

        /**拿到订单原本的状态*/
        Integer originalState=spotDealDetail.getState();


        //确保该订单的状态可被提交纠纷申请 （2019年10月11日 修改：考虑到 地址填写错误 ）
        boolean stateFlag = (spotDealDetail.getState() == 4 || spotDealDetail.getState() == 1 || spotDealDetail.getState() == 3);
        if (!stateFlag){
            return RspUtil.error();
        }

        //确保该订单与该用户有关联
        boolean userFlag = (user.getId().intValue() == spotDealDetail.getSellUserNo() || user.getId().intValue() == spotDealDetail.getBuyUserNo());
        if (!userFlag) return RspUtil.error();

        SpotDealDetail updateSpotDealDetail = new SpotDealDetail();
        updateSpotDealDetail.setId(spotDealDetail.getId());
        updateSpotDealDetail.setState(6);//纠纷中
        int code = spotDealDetailMapper.updateSpotDealDetail(updateSpotDealDetail);
        if (code != 1) {
            logger.warn("订单--" + spotDealDetail.getOrderNo() + "--手动转至纠纷状态失败！");
            return RspUtil.error();
        }

        //纠纷记录
        Integer type = json.getInteger("type");
		/*if(spotDealDetail.getBuyUserNo() ==user.getId().intValue()) type=0;
		if(spotDealDetail.getSellUserNo() ==user.getId().intValue()) type=1;*/



        // 1.买方实际未付款，请求强制取消订单 2.买方一直未付款，请求强制取消订单 3.卖方一直未放行，请求强制放行订单 4.支付账号错误，请求强制取消订单

       /* //如果申请人为出售方
		if(spotDealDetail.getSellUserNo() == user.getId().intValue()){
		    //只能选择124
		    if(type!=1 || type!=2 || type!=4){
                return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
            }
        }
        //如果申请人为购买方
        if(spotDealDetail.getBuyUserNo() == user.getId().intValue()){
            //只能选择3
            if(type!=3){
                return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
            }
        }*/


        SpotDispute spotDispute = new SpotDispute();
        spotDispute.setBuyUserNo(spotDealDetail.getBuyUserNo());
        spotDispute.setSellUserNo(spotDealDetail.getSellUserNo());
        spotDispute.setDealNo(spotDealDetail.getId());
        spotDispute.setOrderNo(spotDealDetail.getOrderNo());
        spotDispute.setUserNo(user.getId().intValue());
        if(ValidataUtil.isNull(json.getString("photo"))){
            spotDispute.setDisputePhone("");
        }else{
            spotDispute.setDisputePhone(json.getString("photo"));
        }
        spotDispute.setState(0);
        spotDispute.setType(type);
        spotDispute.setRemark(remark);
        spotDispute.setOriginalState(originalState);

        spotDisputeMapper.insertSpotDispute(spotDispute);
        if (json.getInteger("language") == 0) {
            return RspUtil.successMsg("成功提交申请！");
        } else {
            return RspUtil.successMsg("success！");
        }
    }

    @Override
    public BaseResponse disputeList(String data) {
        JSONObject json = JSON.parseObject(data);
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        String token = json.getString("token");
        if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        User user = userMapper.getUserInfoByToken(token);
        if (user == null) return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PageHelper.startPage(json.getInteger("pageNum"), json.getInteger("pageSize"));
        List<SpotDispute> disputes = spotDisputeMapper.queryByUser(json.getInteger("userId"));
        for (SpotDispute spotDispute : disputes
        ) {
            String dateString = format.format(spotDispute.getDate());
            spotDispute.setDateStr(dateString);
        }
        PageInfo<SpotDispute> spotDisputePageInfo = new PageInfo<SpotDispute>(disputes);
        return RspUtil.success(spotDisputePageInfo);
    }


    /*旧*/
//	@Override
//	public BaseResponse disputeImg(String data,MultipartFile photo) throws IOException {
//		JSONObject json = JSONObject.parseObject(data);
//		String token = json.getString("token");
//		ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
//				.getString("language"));
//		if (ValidataUtil.isNull(token))
//			return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
//
//		User user = userMapper.getUserInfoByToken(token);
//		if (user == null)
//			return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
//		if (photo != null) {
//			String fileName = QiniuUploadUtil.createFileName("userHead");
//			try {
//				fileName = QiniuUploadUtil.upLoadImage(photo.getInputStream(),
//						fileName);
//				return RspUtil.success("http://opd74myxl.bkt.clouddn.com/"
//						+ fileName);
//						} catch (Exception e) {
//				return RspUtil.error();
//			}
//		}
//		return RspUtil.error("请选择正确的图片格式",100);
//	}

    @Override
    public BaseResponse disputeImg(String data, MultipartFile photo) throws IOException {
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        if (photo != null) {
            try {
                //上传图片到服务器
                String urlPath = executeUpload(photo, responseParamsDto);
                return RspUtil.success(urlPath);
            } catch (Exception e) {
                return RspUtil.error();
            }
        }
        return RspUtil.error("请选择正确的图片格式", 100);
    }

    @Override
    public BaseResponse disputeDetail(String data) {
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        if (ValidataUtil.isNull(token))
            return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null)
            return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        SpotDispute spotDispute = spotDisputeMapper.queryById(json.getLong("disputeId"));
        if (!"".equals(spotDispute.getDisputePhone()) && null != spotDispute.getDisputePhone()) {
            String[] disputeImg = spotDispute.getDisputePhone().split(",");
            for (int i = 0; i < disputeImg.length; i++) {
                if (i == 0) {
                    spotDispute.setImg1(disputeImg[0]);
                }
                if (i == 1) {
                    spotDispute.setImg2(disputeImg[1]);
                }
                if (i == 2) {
                    spotDispute.setImg3(disputeImg[2]);
                }
            }
        }
        if (spotDispute != null) {
            return RspUtil.success(spotDispute);
        } else {
            return RspUtil.error("记录或已被删除", 100);
        }

    }


    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void autoSpute(long orderId) throws Exception {

        //更新订单状态至纠纷状态
        SpotDealDetail condition = new SpotDealDetail();
        condition.setId(orderId);
        condition.setState(4);//等待卖方确认
        SpotDealDetail spotDealDetail = spotDealDetailMapper.selectSpotDealDetailByPrimaryKey(condition);
        SpotDealDetail updateSpotDealDetail = new SpotDealDetail();
        updateSpotDealDetail.setId(spotDealDetail.getId());
        updateSpotDealDetail.setState(6);//纠纷中
        int code = spotDealDetailMapper.updateSpotDealDetail(updateSpotDealDetail);
        if (code != 1) {
            logger.warn("订单--" + spotDealDetail.getOrderNo() + "--自动转至纠纷状态失败！");
            return;
        }

        //纠纷记录
        SpotDispute spotDispute = new SpotDispute();
        spotDispute.setDealNo(spotDealDetail.getId());
        spotDispute.setOrderNo(spotDealDetail.getOrderNo());
        spotDispute.setBuyUserNo(spotDealDetail.getBuyUserNo());
        spotDispute.setSellUserNo(spotDealDetail.getSellUserNo());
        spotDispute.setUserNo(0);//无申请人
        spotDispute.setRemark("自动转纠纷");
        spotDispute.setType(2);//自动纠纷
        spotDispute.setState(0);//申请中
        int result = spotDisputeMapper.insertSpotDispute(spotDispute);
        if (result != 1) {
            logger.warn("订单--" + spotDealDetail.getOrderNo() + "--添加纠纷记录失败！");
            throw new Exception("添加纠纷记录失败");
        }


    }

    
	/*@Override
	public Map<String, Object> disputeDealDetail(Integer buyUserNo, Integer sellUserNo, Long dealNo, Integer userNo,
			Integer type, String remark, MultipartFile disputePhone, ResponseParamsDto responseParamsDto)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getSpotDisputByUserNo(Integer userNo, Integer type, Integer state, Integer pageSize,
			Integer lineSize, ResponseParamsDto responseParamsDto) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> agreeDisput(Integer disputNo, String reason, ResponseParamsDto responseParamsDto)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> refuseDisput(Integer disputNo, String reason, ResponseParamsDto responseParamsDto)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}*/


    /**
     * 单个文件服务器上传
     *
     * @param index
     * @param file
     * @param responseParamsDto
     * @param idCard
     */
    @Value("${dispute.path}")
    public String disputePic;
    @Value("${mapping.pic.path}")
    public String mappingPicPath;

    private String executeUpload(MultipartFile file, ResponseParamsDto responseParamsDto) {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        if (!".jpg".equals(suffix) && !".png".equals(suffix)) {
            throw new RuntimeException(responseParamsDto.ID_PIC_INVALID);
        }

        String filename = UUID.randomUUID() + suffix;
        String filePath = disputePic + filename;//本地存放路径
        String urlPath = mappingPicPath + filename;//url访问路径

        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            log.error("保存文件到本地失败", e);
            throw new RuntimeException(responseParamsDto.FAIL);
        }
        return urlPath;
    }
























/************ C2C管理员纠纷相关   ****/
    /**
     * 管理人员同意纠纷接口
     * @param id
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = { Exception.class, RuntimeException.class })
    @Override
    public BaseResponse agreeDispute(Long id, ResponseParamsDto responseParamsDto) throws Exception {
        // 纠纷信息
        SpotDispute spotDispute = new SpotDispute();
        spotDispute.setId(id);
        spotDispute.setState(0);
        spotDispute = spotDisputeMapper.getSpotDisputeByCondition(spotDispute);
        if (spotDispute == null){
            return RspUtil.rspError(responseParamsDto.FAIL);
        }

        // 订单信息
        SpotDealDetail spotDealDetail = new SpotDealDetail();
        spotDealDetail.setId(spotDispute.getDealNo());
        spotDealDetail.setBuyUserNo(spotDispute.getBuyUserNo());
        spotDealDetail.setSellUserNo(spotDispute.getSellUserNo());
        spotDealDetail.setState(6);
        spotDealDetail = spotDealDetailMapper.selectSpotDealDetailByCondition(spotDealDetail);
        if (spotDealDetail == null) {
            return RspUtil.rspError(responseParamsDto.FAIL_MATCH_FIND_DESC);
        }

        if ((spotDealDetail.getBuyEntrust() == null || spotDealDetail.getBuyEntrust() == 0)
                && (spotDealDetail.getSellEntrust() == null || spotDealDetail.getSellEntrust() == 0)){
            return RspUtil.rspError(responseParamsDto.FAIL);
        }

        if (spotDispute.getUserNo().toString().equals(spotDispute.getBuyUserNo().toString())){		// 买家提起纠纷，管理员同意
            buyAgree(id, spotDealDetail, spotDispute.getBuyUserNo(), spotDispute.getSellUserNo(), spotDispute.getDealNo(), responseParamsDto);
        }

        if (spotDispute.getUserNo().toString().equals(spotDispute.getSellUserNo().toString())){		// 卖家提起纠纷，管理员同意
            sellAgree(id, spotDealDetail, spotDispute.getBuyUserNo(), spotDispute.getSellUserNo(), spotDispute.getDealNo(), responseParamsDto);
        }

        return RspUtil.successMsg(responseParamsDto.SUCCESS);
    }



    /**买家提起纠纷*/
    @Transactional(rollbackFor = { Exception.class, RuntimeException.class })
    public BaseResponse buyAgree(Long id, SpotDealDetail spotDealDetail, Integer buyUserNo, Integer sellUserNo, Long dealNo, ResponseParamsDto responseParamsDto) throws Exception{
        // 买方钱包
        UserWallet buyUserWallet = new UserWallet();
        buyUserWallet.setUserId(Long.valueOf(buyUserNo));
        buyUserWallet.setType(spotDealDetail.getCoinNo());
        buyUserWallet = userWalletMapper.getUserWalletByCondition(buyUserWallet);
        if (buyUserWallet == null || ValidataUtil.isNull(buyUserWallet.getBalance())
                || buyUserWallet.getBalance().compareTo(new BigDecimal("0")) == -1) {
            return RspUtil.rspError(responseParamsDto.FAIL_TRADE_BALANCE_DESC);
        }

        // 卖方钱包
        UserWallet sellUserWallet = new UserWallet();
        sellUserWallet.setUserId((long) spotDealDetail.getSellUserNo());
        sellUserWallet.setType(spotDealDetail.getCoinNo());
        sellUserWallet = userWalletMapper.getUserWalletByCondition(sellUserWallet);
        if (sellUserWallet == null || ValidataUtil.isNull(sellUserWallet.getUnbalance())
                || buyUserWallet.getUnbalance().compareTo(new BigDecimal("0")) == -1) {
            return RspUtil.rspError(responseParamsDto.FAIL_TRADE_BALANCE_DESC);
        }

        Coin coin = coinMapper.getCoinByCoinNo((long)spotDealDetail.getCoinNo());
        if (coin == null || ValidataUtil.isNull(coin.getApiType())){
            return RspUtil.rspError(responseParamsDto.COIN_FAIL_DESC);
        }

        BigDecimal sellUserUnBalance = sellUserWallet.getUnbalance();	// 卖方已冻结资产
        BigDecimal sellPoundage = spotDealDetail.getPoundage();	// 手续费
        BigDecimal tradeNum = spotDealDetail.getDealNum();	// 交易量
        BigDecimal sumNum = tradeNum.add(sellPoundage);
        if (sellUserUnBalance.compareTo(sumNum) == -1) {
            return RspUtil.rspError(responseParamsDto.BALANCE_INSUFFICIENT_DESC);
        }

        // 管理员强制拨款
        try {
            // 修改纠纷记录
            editDisputeState(id, dealNo);

            /*BaseResponse baseResponse = spotDealDetailService.confirmMatchLogic(spotDealDetail, buyUserWallet, sellUserWallet, coin, spotDealDetail.getDealNum(),
                    sellPoundage, sumNum, 1, responseParamsDto);

            if (baseResponse.getCode() != 200){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return baseResponse;
            }*/
        } catch (Exception e) {
            logger.error("纠纷 ： 确认拨款失败！", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return RspUtil.rspError(responseParamsDto.FAIL);
        }
        return RspUtil.successMsg(responseParamsDto.SUCCESS);
    }

    /**卖家提起纠纷*/
    @Transactional(rollbackFor = { Exception.class, RuntimeException.class })
    public BaseResponse sellAgree(Long id, SpotDealDetail spotDealDetail, Integer buyUserNo, Integer sellUserNo, Long dealNo, ResponseParamsDto responseParamsDto) throws Exception{
        // 修改纠纷记录
        editDisputeState(id, dealNo);

        // 修改订单记录
        SpotDealDetail spotDealDetailUpdate = new SpotDealDetail();
        spotDealDetailUpdate.setId(dealNo);
        spotDealDetailUpdate.setState(8);
        int code = spotDealDetailMapper.updateSpotDealDetail(spotDealDetailUpdate);
        if (code == 0){
            throw new Exception("纠纷 : 管理员强制取消订单，修改订单异常！");
        }

        // 委托广告信息
        Long spotEntrustNo = 0L;
        if (spotDealDetail.getBuyEntrust() != null && spotDealDetail.getBuyEntrust() != 0) {        // 修改委托记录-买
            spotEntrustNo = spotDealDetail.getBuyEntrust();
        }else{
            spotEntrustNo = spotDealDetail.getSellEntrust();
        }

        SpotEntrust spotEntrust = spotEntrustMapper.selectSpotEntrustByPrimaryKey(spotEntrustNo);
        if (spotEntrust.getMatchNum().compareTo(spotDealDetail.getDealNum()) == -1) {
            throw new Exception("纠纷 : 管理员强制取消订单，订单成交数量与广告匹配数量不一致！");
        }

        if (spotEntrust.getState() != 0 && spotEntrust.getState() != 3){
            throw new Exception("纠纷 : 管理员强制取消订单，广告状态异常！");
        }

        spotEntrust.setMatchNum(spotEntrust.getMatchNum().subtract(spotDealDetail.getDealNum()));

        if (spotEntrust.getState() == 0 || spotEntrust.getState() == 3) {
            spotEntrust.setState(0);
            spotEntrust.setEntrustMaxPrice(spotEntrust.getEntrustMaxPrice().add(spotDealDetail.getDealNum()));	// 最大交易限额
            BigDecimal residueNum = spotEntrust.getEntrustNum().subtract(spotEntrust.getDealNum())
                    .subtract(spotEntrust.getMatchNum()).subtract(spotEntrust.getCancelNum());		// 剩余量

            if (residueNum.compareTo(spotEntrust.getRecordMinPrice()) >= 0) {
                spotEntrust.setEntrustMinPrice(spotEntrust.getRecordMinPrice());
            } else {
                spotEntrust.setEntrustMinPrice(residueNum);
            }
        }
        code = spotEntrustMapper.updateSpotEntrust(spotEntrust);
        if (code == 0){
            throw new Exception("纠纷 : 管理员强制取消订单，修改委托广告异常！");
        }

        if (spotDealDetail.getBuyEntrust() != null && spotDealDetail.getBuyEntrust() != 0) {		// 委托广告类型-买
            // 判断交易货币状态及钱包API
            Coin coin = coinMapper.getCoinByCoinNo((long)spotDealDetail.getCoinNo());
            if (coin == null || ValidataUtil.isNull(coin.getApiType())) {
                return RspUtil.rspError(responseParamsDto.FAIL_COIN_TYPE_STATE_DESC);
            }

            // 用户交易货币资产
            UserWallet userWallet = new UserWallet();
            userWallet.setUserId((long) spotDealDetail.getSellUserNo());
            userWallet.setType(spotDealDetail.getCoinNo());
            userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
            if (userWallet == null || ValidataUtil.isNull(userWallet.getUnbalance())
                    || userWallet.getUnbalance().compareTo(new BigDecimal("0")) == -1){
                throw new Exception("纠纷 : 管理员强制取消订单，卖方用户资产异常，单号：" + spotDealDetail.getId());
            }

            BigDecimal sellPoundage = spotDealDetail.getPoundage();
            BigDecimal tradeNum = spotDealDetail.getDealNum();
            BigDecimal unFreezeNum = sellPoundage.add(tradeNum);
            userWalletService.editUserWalletBalance(userWallet, unFreezeNum, 3, "纠纷 : 管理员强制取消订单，解冻资金，单号：" + spotDealDetail.getId());	// 解冻卖方资产
        }

        return RspUtil.successMsg(responseParamsDto.SUCCESS);
    }

    @Transactional(rollbackFor = { Exception.class, RuntimeException.class })
    public void editDisputeState(Long id, Long dealNo) throws Exception{
        // 修改纠纷状态
        SpotDispute spotDispute = new SpotDispute();
        spotDispute.setId(id);
        spotDispute.setState(1);
        int code = spotDisputeMapper.updateSpotDispute(spotDispute);
        if (code != 1){
            throw new Exception("纠纷 - 修改纠纷记录状态异常");
        }

        // 修改同一订单的纠纷记录
        spotDispute = new SpotDispute();
        spotDispute.setDealNo(dealNo);
        spotDispute.setState(2);
        spotDispute.setReason("平台管理人员已同意另一笔纠纷记录");
        spotDisputeMapper.updateSpotDisputeOther(spotDispute);

    }
}
