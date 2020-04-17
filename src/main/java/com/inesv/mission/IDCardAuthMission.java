package com.inesv.mission;

import com.inesv.entity.IDCards;
import com.inesv.mapper.IDCardMapper;
import com.inesv.model.IDCard;
import com.inesv.util.GsonUtils;
import com.inesv.util.HttpUtils;
import com.inesv.util.ValidataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用外部api识别身份证图片
 */
@Component
public class IDCardAuthMission {
	private Logger logger = LoggerFactory.getLogger(IDCardAuthMission.class);

	private final static String OCR_URL = "https://api-cn.faceplusplus.com/cardpp/v1/ocridcard";

	@Autowired
	private IDCardMapper idCardMapper;

	@Scheduled(cron = "0 0/10 * * * ?")
	public void auth() throws Exception{
		Map<String, Object> map = new HashMap<>();
		map.put("api_key", "G8_3lO8UX7MKhLitL77qb6Wrmq9xNbAM");
		map.put("api_secret", "n1W54MNbMSgKvbU7kN1aTtBG24LcQSCD");

		IDCard idCard = new IDCard();
		idCard.setState(0);
		idCard.setLimitNum(100);
		idCard.setOrderByCondition("ORDER BY id ASC");
		List<IDCard> idCardList = idCardMapper.getByConditionsWithLimit(idCard);
		for (IDCard card : idCardList) {
			String frontFilePath = card.getFrontFilePath();
			String backFilePath = card.getBackFilePath();
			String name = card.getName();//姓名
			String idCardNumber = card.getIdCardNumber();//身份证号码

			//身份证正面校验
			boolean flag = false;
			String result = HttpUtils.doPostWithFile(OCR_URL, map, frontFilePath);
			Thread.sleep(500);//控制一秒只处理两条记录
			IDCards idCardsFront = GsonUtils.fromJson(result, IDCards.class);
			logger.info("正在处理实名认证身份证正面，name：{}，idCardNumber：{}，返回结果：{}", name, idCardNumber, GsonUtils.toJson(idCardsFront));
			if(idCardsFront == null){
				continue;
			}
			if(idCardsFront.getCards().size() > 0){
				IDCards.Cards cardsFront = idCardsFront.getCards().get(0);
				if(cardsFront != null){
					if(name.equals(cardsFront.getName()) && idCardNumber.equals(cardsFront.getId_card_number())){
						flag = true;
					}
				}
			}

			//身份证正面校验通过后再校验背面
			if(flag){
				result = HttpUtils.doPostWithFile(OCR_URL, map, backFilePath);
				Thread.sleep(500);//控制一秒只处理两条记录
				IDCards idCardsBack = GsonUtils.fromJson(result, IDCards.class);
				logger.info("正在处理实名认证身份证背面，name：{}，idCardNumber：{}，返回结果：{}", name, idCardNumber, GsonUtils.toJson(idCardsBack));
				if(idCardsBack == null){
					continue;
				}
				if(idCardsBack.getCards().size() > 0){
					IDCards.Cards cardsBack = idCardsBack.getCards().get(0);
					if(cardsBack != null){
						if("back".equals(cardsBack.getSide())){
							//判断身份证是否过期
							String validDates = cardsBack.getValid_date();
							if(validDates != null){
								String[] array = validDates.split("[-]]");
								if(array.length == 2){
									String endDate = array[1];
									if(ValidataUtil.ifNowDateBeforeParamDate(endDate, "yyyy.MM.dd")){
										flag = true;
									}
								}
							}
						}
					}
				}
			}

			//更新状态
			if(flag){
				card.setState(1);
			}else{
				card.setState(2);
			}

			int count = idCardMapper.update(card);
			logger.info("处理实名认证身份证结束，name：{}，idCardNumber：{}，返回结果：{}，更新记录结果：{}", name, idCardNumber, flag, count);
		}
	}
}
