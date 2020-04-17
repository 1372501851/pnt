
package com.inesv.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inesv.entity.Kline;
import com.inesv.mapper.*;
import com.inesv.model.Coin;
import com.inesv.model.Params;
import com.inesv.model.User;
import com.inesv.model.UserWallet;
import com.inesv.service.CoinService;
import com.inesv.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Service
public class CoinServiceImpl implements CoinService {

    @Resource
    private CoinMapper coinMapper;

    @Resource
    private UserWalletMapper userWalletMapper;

    @Resource
    private ParamsMapper paramsMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private KlineMapper klineMapper;

    private static final Logger log = LoggerFactory
            .getLogger(CoinServiceImpl.class);

    @Override
    public BaseResponse queryRoutineList(String data) {
        JSONObject json = JSON.parseObject(data);
        String token = json.getString("token");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        User user = userMapper.getUserInfoByToken(token);
        if (user == null) return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        List<Coin> coins = coinMapper.queryOpenRoutineCoin();
        List<Coin> returnCoins = new ArrayList<Coin>();
        for (Coin coin : coins
        ) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userId", json.getLong("userId"));
            params.put("coinNo", coin.getCoinNo());
            UserWallet userWallet = userWalletMapper.queryByUserState(params);
            if (userWallet != null && userWallet.getState() == 1) {
                String name = "";
                if (coin.getCoinName().equals("BTC")) {
                    name = "btc_usdt";
                } else if (coin.getCoinName().equals("LTC")) {
                    name = "ltc_usdt";
                } else if (coin.getCoinName().equals("ETH")) {
                    name = "eth_usdt";
                } else if (coin.getCoinName().equals("ETC")) {
                    name = "etc_usdt";
                } else if (coin.getCoinName().equals("HSR")) {
                    name = "hsr_usdt";
                } else if (coin.getCoinName().equals("QTUM")) {
                    name = "qtum_usdt";
                } else if (coin.getCoinName().equals("PTN")) {
                    name = "ptn_usdt";
                }
                BigDecimal price = new BigDecimal(0);
                if (coin.getCoinName().equals("PTNCNY")) {
                    Params coinParams = paramsMapper.queryByKey(coin.getCoinName());
                    if (null == coinParams) {
                        price = new BigDecimal(0);
                    } else {
                        price = new BigDecimal(coinParams.getParamValue());
                    }
                } else {
                    price = new BigDecimal(paramsMapper.queryByKey(name).getParamValue());
                }
                BigDecimal rate = new BigDecimal(paramsMapper.queryByKey("usd_cny_rate").getParamValue()).setScale(4);
                if (json.getInteger("language") == 0) {

                    coin.setCoinPriceBySys(price.multiply(rate));
                    coin.setCoinPrice(price);
                    returnCoins.add(coin);
                } else {

                    coin.setCoinPrice(price.multiply(rate));
                    coin.setCoinPriceBySys(price);
                    returnCoins.add(coin);
                }
            }
        }
        return RspUtil.success(returnCoins);
    }

    @Override
    public BaseResponse queryOpenPntCoin(String data) {
        JSONObject json = JSON.parseObject(data);
        List<Coin> coins = coinMapper.queryOpenPntCoin();
        List<Coin> returnCoins = new ArrayList<Coin>();
        String token = json.getString("token");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        User user = userMapper.getUserInfoByToken(token);
        if (user == null) return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        for (Coin coin : coins
        ) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userId", json.getLong("userId"));
            params.put("coinNo", coin.getCoinNo());
            UserWallet userWallet = userWalletMapper.queryByUserState(params);

            if (userWallet != null && userWallet.getState() == 1) {
                BigDecimal price = new BigDecimal(0);

                Params coinParams = paramsMapper.queryByKey(coin.getCoinName());
                if (null == coinParams) {
                    price = new BigDecimal(0);
                } else {
                    price = new BigDecimal(coinParams.getParamValue());
                }

                BigDecimal rate = new BigDecimal(paramsMapper.queryByKey("usd_cny_rate").getParamValue()).setScale(4);
                if (json.getInteger("language") == 0) {
                    coin.setCoinPrice(price.divide(rate, 5, BigDecimal.ROUND_HALF_EVEN));
                    coin.setCoinPriceBySys(price);
                    returnCoins.add(coin);
                } else {
                    coin.setCoinPriceBySys(price.divide(rate, 5, BigDecimal.ROUND_HALF_EVEN));
                    coin.setCoinPrice(price);
                    returnCoins.add(coin);
                }
            }
        }
        return RspUtil.success(returnCoins);
    }

    @Override
    public BaseResponse queryList(String data) {
        return null;
    }


    /**
     * 查询货币列表
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse getCoin(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String state = json.getString("state");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        if (ValidataUtil.isNull(state)) return RspUtil.rspError(responseParamsDto.STATE_NULL_DESC);

        Coin coin = new Coin();
        if (!state.equals("3")) {    //所有货币
            coin.setState(Integer.valueOf(state));
        } else {
            coin.setState(1);
        }

        List<Coin> coinList = coinMapper.getCoinByConditions(coin);

        if (!ValidataUtil.isNull(json.getString("type"))) {    //不为null，只返回id和coinName
            List<Object> coins = new ArrayList<>();
            for (int i = 0, len = coinList.size(); i < len; i++) {
                String coinName = coinList.get(i).getCoinName();
                if ("PTN".equals(coinName) || "PTNCNY".equals(coinName) || "ENSA".equals(coinName)) {
                    continue;
                }
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("coinName", ValidataUtil.isNull(coinList.get(i).getCoinName()) ? "" : coinList.get(i).getCoinName());
                resultMap.put("id", ValidataUtil.isNull(coinList.get(i).getCoinNo().toString()) ? "" : coinList.get(i).getCoinNo());
                coins.add(resultMap);
            }
            return RspUtil.success(coins);
        }

        return RspUtil.success(coinList);
    }

    /**
     * 查询指定货币详情
     *
     * @param data
     * @return
     */
    @Override
    public BaseResponse getCoinByNo(String data) {
        JSONObject json = JSONObject.parseObject(data);
        String coinNo = json.getString("coinNo");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        if (ValidataUtil.isNull(coinNo)) return RspUtil.rspError(responseParamsDto.COIN_NULL_DESC);

        Coin coin = new Coin();
        coin.setCoinNo(Long.valueOf(coinNo));

        coin = coinMapper.getCoinByCondition(coin);

        return RspUtil.success(coin);

    }

    /**
     * 代币折线图数据
     */
    @Override
    public BaseResponse coinKchartsData(String data) {
        Map<String, Object> map = new HashMap<String, Object>();
        Long langueCode = (long) 0;

        log.info("获取代币折线图数据请求" + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取代币折线图数据入参:" + data);

        if (ValidataUtil.isNull(data)) {
            log.info("参数data为空");
            return RspUtil.rspError("params: data is null");
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(data);
            String coinName = jsonObject.getString("coinName");//币名 BTC  ETH  LTC  PTN  PTNCNY
            Long langue = jsonObject.getLong("language");
            Long size = jsonObject.getLong("size");//折线数据数量，至多1000条
            String type = jsonObject.getString("interval");//间隔时间，格式为1min 3min 5min 15min 30min 1day 3day 1week 1hour 2hour 4hour 6hour 12hour

            if (langue == null)
                langue = (long) 0;
            langueCode = langue;

            if (StringUtils.isBlank(coinName)) {
                log.info("币名为空");
                return RspUtil.rspError("params: coinName is null");
            }

            if (size == null) {
                log.info("请求折线数据为空，使用默认值");
                size = (long) 100;
            }
            if (type == null) {
                log.info("时间间隔参数为空，使用默认值");
                type = "15min";
            }
            //转换为min
            Integer minutes = convertMinute(type);
            Calendar calendar = Calendar.getInstance();
            int timeNum = (int) -(size * minutes);  //设定获取多少分钟前的数据
            calendar.add(Calendar.MINUTE, timeNum);
            Long since = calendar.getTimeInMillis();

            /**
             * 根据代币名称，获取折线图json数据,转换为map集合
             */
            //K线图json数据
            String KchartsJson = null;
            JSONArray kData = null;
            Map<String, Object> echartDatas = null;
            if ("BTC".equalsIgnoreCase(coinName)) {
                String market = "btc_usdt";
                KchartsJson = HttpUtil.getcoinKcharts(market, since, size, type);
                if (StringUtils.isNotBlank(KchartsJson)) {
                    //图表数据
                    echartDatas = echartData(KchartsJson);
                }
            }
            if ("ETH".equalsIgnoreCase(coinName)) {
                String market = "eth_usdt";
                KchartsJson = HttpUtil.getcoinKcharts(market, since, size, type);
                if (StringUtils.isNotBlank(KchartsJson)) {
                    //图表数据
                    echartDatas = echartData(KchartsJson);
                }
            }
            if ("LTC".equalsIgnoreCase(coinName)) {
                String market = "ltc_usdt";
                KchartsJson = HttpUtil.getcoinKcharts(market, since, size, type);
                if (StringUtils.isNotBlank(KchartsJson)) {
                    //图表数据
                    echartDatas = echartData(KchartsJson);
                }
            }
            if ("PTN".equalsIgnoreCase(coinName)) {

            }
            if ("PTNCNY".equalsIgnoreCase(coinName)) {

            }

            map.put("echartData", echartDatas);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            String exception = sw.toString();
            log.error(exception);
            if (langueCode == 0) {
                return RspUtil.error();
            }
            return RspUtil.error("error", 500);
        }
        BaseResponse baseResponse = RspUtil.success(map);
        log.info("代币详情请求出参：" + JSON.toJSONString(baseResponse));
        return baseResponse;
    }


    /**
     * 换算为分钟
     * 1min 3min 5min 15min 30min 1day 3day 1week 1hour 2hour 4hour 6hour 12hour
     *
     * @param type
     * @return
     */
    Integer convertMinute(String type) {
        Integer resultMinute = 0;
        switch (type) {
            case "1min":
                resultMinute = 1;
                break;
            case "3min":
                resultMinute = 3;
                break;
            case "5min":
                resultMinute = 5;
                break;
            case "15min":
                resultMinute = 15;
                break;
            case "30min":
                resultMinute = 30;
                break;
            case "1hour":
                resultMinute = 60;
                break;
            case "2hour":
                resultMinute = 120;
                break;
            case "4hour":
                resultMinute = 240;
                break;
            case "6hour":
                resultMinute = 360;
                break;
            case "12hour":
                resultMinute = 720;
                break;
            case "1day":
                resultMinute = 1440;
                break;
            case "3day":
                resultMinute = 4320;
                break;
            case "1week":
                resultMinute = 10080;
                break;
            default:
                resultMinute = 15;
        }
        return resultMinute;
    }

    /**
     * 折线图数据
     *
     * @param stringJson
     * @return
     */
    Map echartData(String stringJson) {
        Map<String, Object> objectMap = new HashMap<>();
        JSONObject kchartsObject = JSONObject.parseObject(stringJson);
        JSONArray kArray = kchartsObject.getJSONArray("data");
        Integer sizeNum = kArray.size();
        if (sizeNum != null && sizeNum > 0) {
            ArrayList<Long> priceTimeList = new ArrayList<Long>();
            ArrayList<BigDecimal> lastPriceList = new ArrayList<BigDecimal>();
            for (int i = 0; i < sizeNum; i++) {
                JSONArray eachArray = (JSONArray) kArray.get(i);
                //时间戳
                Long priceTime = eachArray.getLong(0);
                //收盘价
                BigDecimal lastPrice = eachArray.getBigDecimal(4);
                priceTimeList.add(priceTime);
                lastPriceList.add(lastPrice);

            }
            Object[] objectTime = priceTimeList.toArray();
            Object[] objectPrice = lastPriceList.toArray();
            objectMap.put("time", objectTime);
            objectMap.put("prices", objectPrice);
            return objectMap;
        }
        return null;
    }

    @Override
    public BaseResponse coinMarket(String data) {
        JSONObject json = JSON.parseObject(data);
        Coin coin = coinMapper.queryCoinByCoinNo(json.getLong("coinNo"));
        String name = "";
        String openName = "";
        if (coin.getCoinName().equals("BTC")) {
            name = "btcusdt";
            openName = "btc_last";
        } else if (coin.getCoinName().equals("LTC")) {
            name = "ltcusdt";
            openName = "ltc_last";
        } else if (coin.getCoinName().equals("ETH")) {
            name = "ethusdt";
            openName = "eth_last";
        } else if (coin.getCoinName().equals("ETC")) {
            name = "etcusdt";
            openName = "etc_last";
        } else if (coin.getCoinName().equals("HSR")) {
            name = "hsrusdt";
            openName = "eth_last";
        } else if (coin.getCoinName().equals("QTUM")) {
            name = "qtumusdt";
            openName = "qtum_last";
        }
        String result = klineMapper.queryKlineByCoinNo(coin.getCoinNo()).getMarket();

        //获取美金换人民币汇率
        BigDecimal rate = new BigDecimal(paramsMapper.queryByKey("usd_cny_rate").getParamValue());
        JSONObject marketJson = JSONObject.parseObject(result);
        JSONObject marketJson1 = JSONObject.parseObject(marketJson.getString("ticker"));
        BigDecimal last = new BigDecimal(0);
        BigDecimal open = new BigDecimal(0);
        BigDecimal high = new BigDecimal(0);
        BigDecimal low = new BigDecimal(0);
        BigDecimal buy = new BigDecimal(0);
        BigDecimal sell = new BigDecimal(0);
        BigDecimal vol = new BigDecimal(0);
        BigDecimal range = new BigDecimal(0);
        BigDecimal quota = new BigDecimal(0);
        BigDecimal PTN = new BigDecimal(paramsMapper.getParams("PTN").getParamValue());
        Map<String, Object> marketData = new HashMap<String, Object>();
        //如果是PTN与PTNCNY需要去数据库中的价格
        if (coin.getCoinName().equals("PTN") || coin.getCoinName().equals("PTNCNY")) {
            //判断当前语言版本
            if (json.getInteger("language") == 0) {
                last = PTN;
                marketData.put("priceBySys", last);
                marketData.put("price", last.divide(rate, 4, BigDecimal.ROUND_HALF_EVEN));
            } else {
                last = PTN.divide(rate, 4, BigDecimal.ROUND_HALF_EVEN);
                marketData.put("price", last);
                marketData.put("priceBySys", last.divide(rate, 4, BigDecimal.ROUND_HALF_EVEN));
            }

            open = PTN;
            marketData.put("state", 2);
            marketData.put("range", "0%");
        } else {
            //判断当前语言版本
            if (json.getInteger("language") == 0) {
                open = new BigDecimal(paramsMapper.queryByKey(openName).getParamValue()).multiply(rate).setScale(4, BigDecimal.ROUND_HALF_EVEN);
                last = marketJson1.getBigDecimal("last").multiply(rate).setScale(4, BigDecimal.ROUND_HALF_EVEN);
                high = marketJson1.getBigDecimal("high").multiply(rate).setScale(4, BigDecimal.ROUND_HALF_EVEN);
                low = marketJson1.getBigDecimal("low").multiply(rate).setScale(4, BigDecimal.ROUND_HALF_EVEN);
                buy = marketJson1.getBigDecimal("buy").multiply(rate).setScale(4, BigDecimal.ROUND_HALF_EVEN);
                sell = marketJson1.getBigDecimal("sell").multiply(rate).setScale(4, BigDecimal.ROUND_HALF_EVEN);
                vol = marketJson1.getBigDecimal("vol").multiply(rate).setScale(4, BigDecimal.ROUND_HALF_EVEN);
                marketData.put("priceBySys", last);
                marketData.put("price", last.divide(rate, 4, BigDecimal.ROUND_HALF_EVEN));
            } else {
                open = new BigDecimal(paramsMapper.queryByKey(openName).getParamValue());
                last = marketJson1.getBigDecimal("last");
                high = marketJson1.getBigDecimal("high");
                low = marketJson1.getBigDecimal("low");
                buy = marketJson1.getBigDecimal("buy");
                sell = marketJson1.getBigDecimal("sell");
                vol = marketJson1.getBigDecimal("vol");
                marketData.put("price", last);
                marketData.put("priceBySys", last.divide(rate, 4, BigDecimal.ROUND_HALF_EVEN));
            }

            if (last.longValue() > open.longValue()) {
                quota = last.subtract(open);
                //表示上涨
                marketData.put("state", 0);
                range = quota.divide(open, 3, BigDecimal.ROUND_HALF_EVEN);
                marketData.put("range", range.multiply(new BigDecimal(100)).toString() + "%");
            } else {
                quota = open.subtract(last);

                if (last.longValue() == open.longValue()) {
                    //表示没有变化
                    marketData.put("state", 2);
                } else {
                    //表示下跌
                    marketData.put("state", 1);
                }
                range = quota.divide(open, 3, BigDecimal.ROUND_HALF_EVEN);
                marketData.put("range", range.multiply(new BigDecimal(100)).toString() + "%");
            }
        }


        marketData.put("high", high);
        marketData.put("low", low);
        marketData.put("buy", buy);
        marketData.put("sell", sell);
        marketData.put("last", last);
        marketData.put("vol", vol);
        marketData.put("coinIcon", coin.getCoinImg());
        marketData.put("coinName", coin.getCoinName());
        return RspUtil.success(marketData);
    }


    //K线图
    @Override
    public BaseResponse Kline(String data) {
        JSONObject json = JSON.parseObject(data);
        Coin coin = coinMapper.queryCoinByCoinNo(json.getLong("coinNo"));
        String name = "";
        if (coin.getCoinName().equals("BTC")) {
            name = "btcusdt";
        } else if (coin.getCoinName().equals("LTC")) {
            name = "ltcusdt";
        } else if (coin.getCoinName().equals("ETH")) {
            name = "ethusdt";
        } else if (coin.getCoinName().equals("ETC")) {
            name = "etcusdt";
        } else if (coin.getCoinName().equals("HSR")) {
            name = "hsrusdt";
        } else if (coin.getCoinName().equals("QTUM")) {
            name = "qtumusdt";
        }
        String result = null;
        result = klineMapper.queryKlineByCoinNo(coin.getCoinNo()).getKline();
        JSONObject resultJson = JSONObject.parseObject(result);
        JSONArray jsonData = resultJson.getJSONArray("data");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Kline data1 = new Kline();
        List<BigDecimal[]> dataList = new ArrayList<BigDecimal[]>();
        List<String> dates = new ArrayList<String>();
        List<BigDecimal> volumes = new ArrayList<BigDecimal>();
        BigDecimal[] kline = null;
        for (int i = 0; i < jsonData.size(); i++) {
            String[] str = jsonData.get(i).toString().split(",");
            String dateStr = str[0].split("\\[")[1];
            String date = sdf.format(new Date(Long.parseLong(dateStr)));
            kline = new BigDecimal[5];
            kline[0] = new BigDecimal(str[2]);
            kline[1] = new BigDecimal(str[1]);
            kline[2] = new BigDecimal(str[4]);
            kline[3] = new BigDecimal(str[5].split("]")[0]);
            kline[4] = new BigDecimal(str[3]);
            dataList.add(kline);
            dates.add(date);
            volumes.add(new BigDecimal(str[5].split("]")[0]));
        }
        data1.setVolumes(volumes);
        data1.setData(dataList);
        data1.setDates(dates);
        return RspUtil.success(data1);
    }

    @Override
    public BaseResponse queryOpenCoinList(String data) {
        JSONObject json = JSON.parseObject(data);
        String token = json.getString("token");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        User user = userMapper.getUserInfoByToken(token);
        if (user == null) return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);
        List<Coin> coins = coinMapper.queryOpenCoinList();
        List<Coin> returnCoins = new ArrayList<Coin>();
        for (Coin coin : coins
        ) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userId", json.getLong("userId"));
            params.put("coinNo", coin.getCoinNo());
            params.put("coinRemark", coin.getCoinRemark());
            UserWallet userWallet = userWalletMapper.queryByUserState(params);
//			if (userWallet!=null&&userWallet.getState()==1){
            String name = "";
            if (coin.getCoinName().equals("BTC")) {
                name = "btc_usdt";
            } else if (coin.getCoinName().equals("LTC")) {
                name = "ltc_usdt";
            } else if (coin.getCoinName().equals("ETH")) {
                name = "eth_usdt";
            } else if (coin.getCoinName().equals("ENSA")) {
                name = "ensa_usdt";
            } else if (coin.getCoinName().equals("ETC")) {
                name = "etc_usdt";
            } else if (coin.getCoinName().equals("HSR")) {
                name = "hsr_usdt";
            } else if (coin.getCoinName().equals("QTUM")) {
                name = "qtum_usdt";
            } else if (coin.getCoinName().equalsIgnoreCase("MOC")) {
                name = "moc_usdt";
            }

            BigDecimal price = new BigDecimal(0);
            if (coin.getCoinName().equals("PTNCNY")) {
                Params coinParams = paramsMapper.queryByKey(coin.getCoinName());
                if (null == coinParams) {
                    price = new BigDecimal(0);
                } else {
                    price = new BigDecimal(coinParams.getParamValue());
                }
            } else {
                price = new BigDecimal(paramsMapper.queryByKey(name).getParamValue());
            }
            BigDecimal rate = new BigDecimal(paramsMapper.queryByKey("usd_cny_rate").getParamValue()).setScale(4);
            if (json.getInteger("language") == 0) {

                coin.setCoinPriceBySys(price.multiply(rate));
                coin.setCoinPrice(price);
                returnCoins.add(coin);
            } else {

                coin.setCoinPrice(price.multiply(rate));
                coin.setCoinPriceBySys(price);
                returnCoins.add(coin);
            }
//			}
        }
        return RspUtil.success(returnCoins);
    }

    /**
     * 查询所有货币行情
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse queryAllCoinList(String data) throws Exception {
        JSONObject json = JSON.parseObject(data);

        List<Coin> coins = coinMapper.queryAllCoinList();
        List<Coin> returnCoins = new ArrayList<Coin>();
        for (Coin coin : coins) {
            String name = "";
            BigDecimal price = new BigDecimal(0);
            BigDecimal rate = new BigDecimal(1);

            Params params = paramsMapper.queryByKey("usd_cny_rate");
            String rateStr = params.getParamValue();
            if (params != null && !ValidataUtil.isNull(rateStr) && Double.valueOf(rateStr) != 0){
                rate = new BigDecimal(params.getParamValue()).setScale(4);
            }
            name = coin.getCoinName() + "_usdt";
            params = paramsMapper.queryByKey(name);
            if (params != null && !ValidataUtil.isNull(params.getParamValue())){
                price = new BigDecimal(params.getParamValue());
            }
            String Sys= String.valueOf(price.multiply(rate));
            if (json.getInteger("language") == 0) {

                coin.setCoinPriceBySys(new BigDecimal(Sys).setScale(5,BigDecimal.ROUND_HALF_EVEN));
                coin.setCoinPrice(price);
                returnCoins.add(coin);
            } else {

                coin.setCoinPrice(new BigDecimal(Sys).setScale(5,BigDecimal.ROUND_HALF_EVEN));
                coin.setCoinPriceBySys(price);
                returnCoins.add(coin);
            }
            coin.setWalletState(0);
        }

        return RspUtil.success(returnCoins);
    }
}
