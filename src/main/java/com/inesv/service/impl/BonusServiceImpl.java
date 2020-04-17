package com.inesv.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.BonusService;
import com.inesv.service.TradeInfoService;
import com.inesv.util.*;
import com.inesv.util.CoinAPI.PNTCoinApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class BonusServiceImpl implements BonusService {
	private static final Logger log = LoggerFactory
			.getLogger(BonusServiceImpl.class);

	@Resource
	private UserRelationMapper userRelationMapper;

	@Resource
	private BonusDetailMapper bonusDetailMapper;

	@Resource
	private UserWalletMapper userWalletMapper;

	@Resource
	private AddressMapper addressMapper;

	@Resource
	private ParamsMapper paramsMapper;

	@Resource
	private BonusMapper bonusMapper;

	@Resource
	private CoinMapper coinMapper;

	@Resource
	private TradeInfoService tradeInfoService;
	@Resource
	private TradeInfoMapper tradeInfoMapper;

	private static final Integer invitationBonusCoinNo = 40;    //PTN

	private static final String invitationBonusKey = "invitation";

	/**
	 * 给推荐人发放奖励
	 * @param userId
	 * @param recId
	 * @throws Exception
	 */
	@Override
	public void invitationBonus(Long userId, Long recId) throws Exception {
		UserRelation userRelation = new UserRelation();
		UserWallet userWallet = new UserWallet();
		Bonus bonus = new Bonus();
		bonus.setBonusKey(invitationBonusKey);
		bonus.setState(1);
		bonus = bonusMapper.getBonusByCondition(bonus);
		if (bonus == null) return;

		Address address = new Address();
		address.setCoinNo(Long.valueOf(invitationBonusCoinNo));
		address = addressMapper.getAddressByCondition(address);
		if (address == null) return;

		Params centerAddress = paramsMapper
				.getParams("PTN_Transfer_Station");
		if (ValidataUtil.isNull(centerAddress.getParamValue())) return;

		String remark = "邀请会员注册-分润";
		if (bonus.getBonusOne() > 0) {
			if(!isOverLimit(recId, "bonusone", "bonusone_limit")){
				Long detailNo = insertBonusDetail(bonus.getBonusKey(), recId, userId, invitationBonusCoinNo, bonus.getBonusOne(), 0, remark, "bonusone");
				log.info("注册用户ID：{}，推荐用户ID：{}，奖励级别：{}，新增奖励记录ID：{}", userId, recId, "一级", detailNo);
			}
		}

		if (bonus.getBonusTwo() > 0) {
			userRelation = new UserRelation();
			userRelation.setUserId(recId);
			userRelation = userRelationMapper.getUserRelationByCondition(userRelation);
			if (userRelation == null) return;
			recId = userRelation.getRecId();

			if(!isOverLimit(recId, "bonustwo", "bonustwo_limit")){
				Long detailNo = insertBonusDetail(bonus.getBonusKey(), userRelation.getRecId(), userRelation.getUserId(), invitationBonusCoinNo, bonus.getBonusTwo(), 0, remark, "bonustwo");
				log.info("注册用户ID：{}，推荐用户ID：{}，奖励级别：{}，新增奖励记录ID：{}", userId, recId, "二级", detailNo);
			}
		}

		if (bonus.getBonusThree() > 0) {
			userRelation = new UserRelation();
			userRelation.setUserId(recId);
			userRelation = userRelationMapper.getUserRelationByCondition(userRelation);
			if (userRelation == null) return;

			if(!isOverLimit(recId, "bonusthree", "bonusthree_limit")){
				Long detailNo = insertBonusDetail(bonus.getBonusKey(), userRelation.getRecId(), userRelation.getUserId(), invitationBonusCoinNo, bonus.getBonusThree(), 0, remark, "bonusthree");
				log.info("注册用户ID：{}，推荐用户ID：{}，奖励级别：{}，新增奖励记录ID：{}", userId, recId, "三级", detailNo);
			}
		}
	}

	/**
	 * 检查邀请奖励是否超过限制次数
	 * @param recId 邀请人用户ID
	 * @param bonusLevel 邀请奖励级别
	 * @param limitName
     * @return
     */
	private boolean isOverLimit(Long recId, String bonusLevel, String limitName){
		BonusDetail bonusDetail = new BonusDetail();
		bonusDetail.setRecId(recId);
		bonusDetail.setBonusLevel(bonusLevel);
		List<BonusDetail> list = bonusDetailMapper.getBonusDetailByCondition(bonusDetail);

		//指定邀请奖励级别查询推荐用户获取奖励的次数限制
		Params bonusoneLimit = paramsMapper.queryByKey(limitName);
		Integer bonusoneLimitValue = CastUtils.castInt(bonusoneLimit.getParamValue(), 10);
		if(list.size() >= bonusoneLimitValue ){
			return true;
		}
		return false;
	}


	/**
	 * 创建钱包后，根据分润记录给推荐人发放奖励
	 * @param address
	 * @param centerAddress
	 * @param userAddress
	 * @param userNo
	 * @throws Exception
	 */
	@Override
	public void invitationBonusByCreateWallet(Address address, Params centerAddress, String userAddress, Long userNo, UserWallet userWallet) throws Exception {
		Coin coin = new Coin();
		coin.setCoinName("MOC");
		coin = coinMapper.getCoinByCondition(coin);
		if (coin != null) {
			if (coin.getCoinNo() == Long.valueOf(invitationBonusCoinNo)) {
				BonusDetail bonusDetail = new BonusDetail();
				bonusDetail.setCoinNo(invitationBonusCoinNo);
				bonusDetail.setRecId(userNo);
				bonusDetail.setState(0);
				bonusDetail.setBonusKey(invitationBonusKey);
				List<BonusDetail> list = bonusDetailMapper.getBonusDetailByCondition(bonusDetail);
				for (BonusDetail detail : list) {
					BonusDetail bDetail = new BonusDetail();
					bDetail.setId(detail.getId());
					bDetail.setState(1);
					bDetail.setRecWallet(userAddress);

					int count = bonusDetailMapper.updateBonusDetail(bDetail);
					if(count > 0 ){
						String price = String.valueOf(detail.getPrice());
						for(int i=0;i<2;i++){
							String result = reward(address, centerAddress.getParamValue(), userAddress, price, "邀请会员注册-分润");
							log.info("邀请注册，奖励用户：{}，{}：个光子币，转账结果：{}", userAddress, price, result);
							if (!ValidataUtil.isNull(result)) {
								String hash = MD5Util.GetMD5Code("MOC" + result);
								TradeInfo tradeInfo = new TradeInfo();
								tradeInfo.setHash(hash);
								List<TradeInfo> tradeInfos = tradeInfoMapper.getTradeInfoByConditions(tradeInfo);
								if(tradeInfos.size() == 0){
									//新增转账记录，并增加用户资产
									tradeInfoService.addTradeInfoAndAddUserWallet(price, "0", userAddress, "邀请注册奖励", result, userWallet);
									break;
								}else{
									Thread.sleep(2000);//转出地址、转入地址、转账数量相同，需要间隔2秒再操作
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 分润记录
	 *
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@Override
	public BaseResponse getBonusDetailByUserNo(String data) throws Exception {
		JSONObject json = JSONObject.parseObject(data);
		String userNo = json.getString("userNo");
		String pageSize = json.getString("pageSize");
		String lineSize = json.getString("lineSize");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
				.getString("language"));
		if (ValidataUtil.isNull(userNo))
			return RspUtil.rspError(responseParamsDto.ID_NULL_DESC);
		if (ValidataUtil.isNull(pageSize))
			return RspUtil.rspError(responseParamsDto.PAGEORLINE_NULL_DESC);
		if (ValidataUtil.isNull(lineSize))
			return RspUtil.rspError(responseParamsDto.PAGEORLINE_NULL_DESC);

		List<BonusDetail> bonusDetails = new ArrayList<>();

		BonusDetail bonusDetail = new BonusDetail();
		bonusDetail.setRecId(Long.valueOf(userNo));
		bonusDetail.setState(1);
		if (pageSize.equals("0") && lineSize.equals("0")) {
			bonusDetails = bonusDetailMapper.getBonudDetailsByRecNo(bonusDetail);
		} else {
			PageHelper.startPage(Integer.valueOf(pageSize), Integer.valueOf(lineSize));
			bonusDetails = bonusDetailMapper.getBonudDetailsByRecNo(bonusDetail);
		}
		return RspUtil.success(JSONFormat.getStr(bonusDetails));
	}

	public Long insertBonusDetail(String bonusKey, Long recId, Long userId, Integer coinNo, Double price, Integer state, String remark, String bonusLevel) throws Exception {
		Long detailNo = 0L;
		BonusDetail bonusDetail = new BonusDetail();
		bonusDetail.setBonusKey(bonusKey);
		bonusDetail.setCoinNo(coinNo);
		bonusDetail.setPrice(price);
		bonusDetail.setUserId(userId);
		bonusDetail.setRecId(recId);
		bonusDetail.setState(state);
		bonusDetail.setRemark(remark);
		bonusDetail.setBonusLevel(bonusLevel);
		bonusDetailMapper.insertBonusDetail(bonusDetail);
		detailNo = bonusDetail.getId();
		return detailNo;
	}

	public String reward(Address address, String centerAddress, String userAddress, String price, String remark) throws Exception {
		PNTCoinApi api = PNTCoinApi.getApi(address);
		String result = api.sendTransaction(
				centerAddress, userAddress, price, "", remark, "0.01", "moc");
		return result;
	}
}
