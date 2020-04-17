package com.inesv.controller;


import com.inesv.annotation.Login;
import com.inesv.annotation.LoginUser;
import com.inesv.annotation.UnLogin;
import com.inesv.common.constant.RErrorEnum;
import com.inesv.common.exception.RRException;
import com.inesv.entity.CoinParams;
import com.inesv.entity.UserBuyNodeForm;
import com.inesv.entity.UserPassword;
import com.inesv.mapper.*;
import com.inesv.model.User;
import com.inesv.service.*;
import com.inesv.model.*;
import com.inesv.util.BaseResponse;
import com.inesv.util.LanguageUtil;
import com.inesv.util.ResponseParamsDto;
import com.inesv.util.RspUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.inesv.entity.CoinParams.MOC_BET_AMOUNT_MAX;
/*import org.springframework.web.bind.annotation.*;*/

@Slf4j
@RestController
@RequestMapping("/node")
public class NodeController {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private UserRelationService userRelationService;

    @Autowired
    private NodeLevelService nodeLevelService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NodeUserTotalMapper nodeUserTotalMapper;

    @Autowired
    private UserRelationMapper userRelationMapper;

    @Autowired
    private ParamsService paramsService;

    @Autowired
    private ParamsMapper paramsMapper;

    @Autowired
    private UserWalletMapper userWalletMapper;

    /**
     * 判断用户打开社区挖矿的资格
     * @param user
     * @return
     */
    @GetMapping("qualification")
    @Login
    @UnLogin
    public BaseResponse userNode(@LoginUser User user) {
        /**
         * status
         * 1.允许进入
         * 2.没有推荐人
         * 3.推荐人不在节点
         */
        Long userId = user.getId();
        Integer status = null;
        boolean isHasRefer = userRelationService.userHasParID(userId);
        if (!isHasRefer) {
            status = 2;
        } else {
            boolean existNode = nodeService.isExistNode(userId);
            if (existNode) {
                status = 1;
            } else {
                status = 3;
            }
        }
        Map<String, Object> hash = new HashMap<>();
        hash.put("status", status);
        return RspUtil.success(hash);
    }

    @PostMapping("buyNode")
    @Login
    @UnLogin
    public BaseResponse userBuy(@RequestBody UserBuyNodeForm userBuyNodeForm, @LoginUser User user) {
        /**
         * 用户购买节点逻辑（传参：tradePasswd,amount）
         * 1.判断交易密码是否正确
         * 2.判断金额是否符合要求
         * 3.找到用户的社区，拿到社区id
         * 4.发送转账扣除金额；
         * 5.得到hash值，添加用户购买记录到用户表(t_node_user_record)
         * 6.判断是否是第一次购买
         *      是：t_node_user_total新添加一条记录，更新t_node中into_amount的总量
         *      否：t_node_user_total更新总投入量，更新t_node中into_amount的总量
         *
         */
        String tradePasswd = userBuyNodeForm.getTradePasswd();
        BigDecimal amount = userBuyNodeForm.getAmount();
        Long userId = user.getId();

        UserWallet qeryCond = new UserWallet();
        qeryCond.setUserId(userId);
        qeryCond.setType(40);  //  此处写死 40 moc
        UserWallet userWallet = userWalletMapper.getUserWalletByCondition(qeryCond);
        //19-10-10判断币种状态  0开启，1关闭
        ResponseParamsDto responseParamsDto= LanguageUtil.proving("0");
        if(userWallet.getWalletState()==1){
            throw new RRException(responseParamsDto.TRADE_COIN_SUSPENDED);
        }
        
        if (checkUserBuyNodeForm(tradePasswd, amount,userId)) {
            boolean isPasswd = userService.isTradePaswd(userId, tradePasswd);
            if (isPasswd) {
                nodeService.userBuyNode(userId, amount);
            } else {
                throw new RRException(RErrorEnum.TRADEPASSWD_ERROR);
            }
        }
        return RspUtil.success();
    }

    public boolean checkUserBuyNodeForm(String tradePasswd, BigDecimal amount,Long userId) {
        if (StringUtils.isNotBlank(tradePasswd)) {
            if (amount != null) {
                Params params = paramsService.queryByKey(MOC_BET_AMOUNT_MAX);
                Integer count=nodeUserTotalMapper.getIsSecondPet(userId);
                BigDecimal maxAmount = new BigDecimal(params.getParamValue());
                if (count==null || count==0){
                    BigDecimal amountMin = nodeLevelService.getNodeBuyMin();
                    if (amount.compareTo(amountMin) >= 0 && amount.compareTo(maxAmount) <= 0) {
                        return true;
                    } else {
                        throw new RRException(RErrorEnum.AMOUNT_ERROR);
                    }
                }else {
                    Params pSecondPet = paramsService.queryByKey(CoinParams.MOC_SECOND_PET);
                    String secondMin = pSecondPet.getParamValue();
                    BigDecimal decimal = new BigDecimal(secondMin);
                    //获取当前用户总共投的金额
                    NodeUserTotal total= nodeUserTotalMapper.getSumByUserId(userId);
                    if (amount.divideAndRemainder(decimal)[1].compareTo(BigDecimal.ZERO)==0){
                        if (amount.compareTo(decimal) >= 0 && amount.compareTo(maxAmount) <= 0) {
                            if (total.getAmount().add(amount).compareTo(maxAmount)<=0){
                                return true;
                            }else {
                                throw new RRException(RErrorEnum.MAX_MONEY);
                            }
                        } else {
                            throw new RRException(RErrorEnum.AMOUNT_ERROR);
                        }
                    }else {
                        throw new RRException(RErrorEnum.ENTER_CORRECT_AMOUNT);
                    }
                }
            } else {
                throw new RRException(RErrorEnum.PARAM_ERROR);
            }
        } else {
            throw new RRException(RErrorEnum.TRADEPASSWD_ERROR);
        }
    }

    //节点记录信息接口
    @RequestMapping("/nodeRecord")
    @Login
    @UnLogin
    public BaseResponse nodeRecord(@LoginUser User user) {
        Params params = paramsMapper.getParams(CoinParams.MOC_ZHI_SWITCH);
        if (params.getParamValue().equals("0")){
            throw new RRException(RErrorEnum.ZAN_SWITCH);
        }
        boolean isHasRefer = userRelationService.userHasParID(user.getId());
        List<Node> list = new ArrayList<>();
        if (!isHasRefer) {
            List<Node> list1= nodeService.getAllNode();
            for (Node node:list1){
                node.setIntoAmount(node.getIntoAmount().setScale(6));
                node.setAddressAmount(node.getAddressAmount().setScale(6));
                node.setTotalAmount(node.getTotalAmount().setScale(6));
            }
            return RspUtil.success(list1);
        }else {
            Node node=nodeService.nodeRecord(user.getId());
            if (node != null) {
                list.add(node);
            }
        }

        if (list.size()==0){
            throw new RRException(RErrorEnum.USERNODE_NOEXIST);
        }
        return RspUtil.success(list);
}


    //节点记录信息接口
    @RequestMapping("/nodeDetails")
    @Login
    @UnLogin
    public BaseResponse nodeDetails(@LoginUser User user,@RequestParam("nodeId") Long nodeId) {
        //boolean isHasRefer = userRelationService.userHasParID(user.getId());
        Node node=nodeService.getDetails(nodeId);
        //for (Node node:list1){
        if (node != null){
            node.setIntoAmount(node.getIntoAmount().setScale(6));
            node.setAddressAmount(node.getAddressAmount().setScale(6));
            node.setTotalAmount(node.getTotalAmount().setScale(6));
        }
        //}
        return RspUtil.success(node);
    }


    //节点用户投注记录接口
    @RequestMapping("/userPetRecord")
    @Login
    @UnLogin
    public BaseResponse nodeUserPetRecord(@LoginUser User user) {
        List<NodeUserRecord> list=nodeService.nodeUserPetRecord(user.getId());
        return RspUtil.success(list);
    }

    //节点用户取出记录接口
    @RequestMapping("/userDrawRecord")
    @Login
    @UnLogin
    public BaseResponse nodeUserDrawRecord(@LoginUser User user) {
        List<NodeUserDraw> nodeUserDraws=nodeService.nodeUserDrawRecord(user.getId());
        return RspUtil.success(nodeUserDraws);
    }

    //节点用户收益记录接口
    @RequestMapping("/userProfitRecord")
    @Login
    @UnLogin
    public BaseResponse nodeUserProfitRecord(@LoginUser User user) {
        List<NodeUserProfit> nodeUserProfits=nodeService.nodeUserProfitRecord(user.getId());
        return RspUtil.success(nodeUserProfits);
    }

    //返回提示窗中的所需要的数据
    @RequestMapping("/getData")
    @Login
    @UnLogin
    public BaseResponse getData(@LoginUser User user) {
        Map<String,String> map=nodeService.getData();
        return RspUtil.success(map);
    }

    //用户退出(提取)投注接口
    @RequestMapping("/withdrawBet")
    @Login
    @UnLogin
    public BaseResponse withdrawBet(@LoginUser User user, @RequestBody UserPassword userPassword){
        nodeService.withdrawBet(user,userPassword.getPassword());
       return RspUtil.success();
   }

   //邀请码接口
    @RequestMapping("/getInviteCode")
    @Login
    @UnLogin
    public BaseResponse getInviteCode(@LoginUser User user,@RequestParam("inviteCode") String inviteCode,@RequestParam("nodeId") String nodeId){
        //进行推荐人查询，不存在的话返回推荐手机号不存在
        User userRec = new User();
        //set推荐人id
        userRec.setInvitationCode(inviteCode);
        //根据推荐人username查到推荐人信息
        //userRec.setUsername(invitationCode);
        userRec = userMapper.getUserInfoByCondition(userRec);
        if (userRec == null){
            throw new RRException(RErrorEnum.INVITECODE_INVALID);
        }
        UserRelation userRelation2=new UserRelation();
        userRelation2.setUserId(user.getId());
        //先去关系表查询是否有关系了
        UserRelation relation2 = userRelationMapper.getUserRelationByCondition(userRelation2);
        if (relation2==null){
            if (user.getId().compareTo(userRec.getId())==0){
                throw new RRException(RErrorEnum.NO_RECOMMEND_OWN);
            }
            boolean existNode = nodeService.isExistNode(userRec.getId());
            if (existNode){
                boolean isRight=nodeService.getNodeIsRight(Long.valueOf(nodeId),userRec.getId());
                if (isRight){
                    //创建关系
                    /*UserRelation userRelation=new UserRelation();
                    userRelation.setUserId(user.getId());
                    userRelation.setRecId(userRec.getId());
                    userRelationMapper.insertUserRelation(userRelation);*/
                    UserRelation userRelation1 = new UserRelation();
                    userRelation1.setUserId(userRec.getId());
                    UserRelation userRelation3 = userRelationMapper.getUserRelationByCondition(userRelation1);
                    UserRelation userRelation4 = new UserRelation();
                    userRelation4.setUserId(user.getId());
                    userRelation4.setRecId(userRec.getId());
                    if (userRelation3 != null){
                        userRelation4.setTreeTrade(userRelation3.getTreeTrade()+1);
                        userRelation4.setPath(userRelation3.getPath()+","+user.getId().toString());
                    }else {
                        userRelation4.setTreeTrade(2);
                        userRelation4.setPath(userRec.getId()+","+user.getId().toString());
                    }
                    userRelationMapper.insertUserRelation(userRelation4);

                }else {
                    throw new RRException(RErrorEnum.RECOMMEND_CODE_NO_OWN);
                }
            }else {
                //创建关系
                /*UserRelation userRelation=new UserRelation();
                userRelation.setUserId(user.getId());
                userRelation.setRecId(userRec.getId());
                userRelationMapper.insertUserRelation(userRelation);*/
                throw new RRException(RErrorEnum.RECOMMEND_CODE_NO_ANY);
            }
        }
        //1.允许进入
        //3.推荐人不在节点
        Integer status=null;
        Map<String, Object> hash = new HashMap<>();
        boolean existNode = nodeService.isExistNode(user.getId());
        if (existNode) {
            status = 1;
        } else {
            status = 3;
        }
        hash.put("status", status);
        return RspUtil.success(hash);
    }


    //节点挖矿产出记录
    @RequestMapping("/digMineProfitRecord")
    @Login
    @UnLogin
    public BaseResponse digMineProfitRecord(@LoginUser User user,@RequestParam("nodeId") String nodeId) {
        List<NodeMineProfit> profits=nodeService.digMineProfitRecord(Long.valueOf(nodeId));
        return RspUtil.success(profits);
    }

    //提取接口展示记录
    @RequestMapping("/drawShowRecord")
    @Login
    @UnLogin
    public BaseResponse drawShowRecord(@LoginUser User user) {
        List<NodeUserTotal> profits=nodeService.drawShowRecord(user.getId());
        if (CollectionUtils.isNotEmpty(profits)){
            for (NodeUserTotal nodeUserTotal : profits){
                nodeUserTotal.setAmount(nodeUserTotal.getAmount().setScale(6));
            }
        }
        return RspUtil.success(profits);
    }

}
