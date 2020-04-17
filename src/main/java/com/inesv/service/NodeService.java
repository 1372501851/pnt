package com.inesv.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.inesv.model.*;
import com.inesv.util.BaseResponse;

public interface NodeService {
//    BaseResponse bet(String token, String language, Long amount);
//
      void withdrawBet(User user,String password);
//
//    BaseResponse pageJudge(String token, String language);



    /**
     * 查询用户到他parid的路径
     * @param userId
     * @param parId
     * @return
     */
    Set<Long> getParListByPid(Long userId, Long parId);


    /**
     * 判断用户是否在所有节点中
     * @param userId
     * @return
     */
    boolean isExistNode(Long userId);

    void userBuyNode(Long userId, BigDecimal amount);
    BaseResponse pageJudge(String token, String language);

    Node nodeRecord(Long id);

    List<NodeUserRecord> nodeUserPetRecord(Long id);

    List<NodeUserDraw> nodeUserDrawRecord(Long id);

    List<NodeUserProfit> nodeUserProfitRecord(Long id);

    Map<String,String> getData();

    List<NodeMineProfit> digMineProfitRecord(Long nodeId);

    List<NodeUserTotal> drawShowRecord(Long id);

    List<Node> getAllNode();

    Node getDetails(Long nodeId);

    boolean getNodeIsRight(Long nodeId,Long userId);

    Node userBelongNode(Long userId);
}
