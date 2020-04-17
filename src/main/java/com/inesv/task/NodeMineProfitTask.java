package com.inesv.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.AddressMapper;
import com.inesv.mapper.NodeMapper;
import com.inesv.mapper.NodeMineProfitMapper;
import com.inesv.model.Address;
import com.inesv.model.Node;
import com.inesv.model.NodeMineProfit;
import com.inesv.util.CoinAPI.PNTCoinApi;
import com.inesv.util.DateTools;
import com.inesv.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 每天获取昨天的挖矿产出
 */
@Slf4j
@Component
public class NodeMineProfitTask {


    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private NodeMineProfitMapper nodeMineProfitMapper;

    @Scheduled(cron = "0 5 0 ? * *")
    public void getMineProfit()  {
        Address address = addressMapper.queryAddressInfo(40L);
        String ipAddr = address.getAddress();
        String port = address.getPort();

        String url = "http://" + ipAddr + ":" + port + "/WalletController/getTransactionByPubkey";
        //获取昨天
        LocalDate localDate = LocalDate.now().plusDays(-1);

        List<Node> nodes = nodeMapper.queryNode(null);
        for (Node node : nodes) {
            if (node.getState() == 1) {
                String pubkey = node.getPubkey();
                BigDecimal amount = getTransaction(url, pubkey, DateTools.localDateToUdate(localDate));
                Long nodeId = node.getId();
                NodeMineProfit nodeMineProfit = new NodeMineProfit();
                nodeMineProfit.setNodeId(nodeId);
                nodeMineProfit.setAmount(amount);

                nodeMineProfit.setCreatedate(localDate);
                nodeMineProfit.setCreatetime(LocalDateTime.now());
                nodeMineProfit.setUpdatetime(LocalDateTime.now());
                nodeMineProfitMapper.insertNodeMineProfit(nodeMineProfit);
            }
        }

    }


    public BigDecimal getTransaction(String url, String pubkey, Date afterDate) {
        BigDecimal amount = BigDecimal.ZERO;
        Map<String, Object> map = new HashMap<>(10);
        map.put("ieType", 1);
        map.put("pubKey", pubkey);
        map.put("tokenName", "moc");
        map.put("pageNumber", 1);
        map.put("pageSize", 10000);

        String s = HttpUtils.doGet(url, map);
        JSONObject jsonObject = JSON.parseObject(s);
        int code = jsonObject.getIntValue("code");
       if (code == 100) {
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("transactionList");
            for(int i=0; i < jsonArray.size(); i++) {
                JSONObject transaction = jsonArray.getJSONObject(i);
                Integer type = transaction.getInteger("type");
                Date date = transaction.getDate("date");

                long number = DateTools.getDateDiff(date, afterDate);
                if (number == 0 && type == 2) {
                    //累加金额
                    amount = amount.add(transaction.getBigDecimal("amount"));
                }
                if (number == 1) {
                    log.info("数组大小:{},记录时间：{}", i, date);
                    break;
                }
            }
        } else {
            log.error("查询账户余额");
        }
        return amount;
    }


    @Scheduled(fixedDelay = 1000*60)
    public void saveAddressBalance() {

        Address address = addressMapper.queryAddressInfo(40L);
        List<Node> nodes = nodeMapper.queryNode(null);
        for (Node node : nodes) {
            String nodeAddress = node.getAddress();
            PNTCoinApi api = PNTCoinApi.getApi(address);
            String moc = api.getBalance(nodeAddress, "moc");

            Node updateModel = new Node();
            updateModel.setId(node.getId());
            updateModel.setAddressAmount(new BigDecimal(moc));
            updateModel.setUpdatetime(LocalDateTime.now());
            nodeMapper.updateNode(updateModel);
        }
    }

}
