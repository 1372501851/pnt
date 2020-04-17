package com.inesv.controller;

import com.inesv.annotation.Login;
import com.inesv.annotation.LoginUser;
import com.inesv.annotation.UnLogin;
import com.inesv.common.constant.RErrorEnum;
import com.inesv.common.exception.RRException;
import com.inesv.entity.MainAccountEnum;
import com.inesv.mapper.*;
import com.inesv.model.Node;
import com.inesv.model.NodeUserTotal;
import com.inesv.model.User;
import com.inesv.service.NodeService;
import com.inesv.service.TeamService;
import com.inesv.task.PayTask;
import com.inesv.util.BaseResponse;
import com.inesv.util.LocaleMessageUtils;
import com.inesv.util.RspUtil;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import  com.inesv.task.NodeTask;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("test")
@Profile({"dev", "test"})
public class TestController {

    @Autowired
    private LocaleMessageUtils localeMessageUtils;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private NodeTask nodeTask;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private NodeUserProfitMapper nodeUserProfitMapper;

    @Autowired
    private NodeProfitRecordMapper nodeProfitRecordMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PayTask payTask;

    @Autowired
    private PayMapper payMapper;

    @Autowired
    private NodeUserTotalMapper nodeUserTotalMapper;

    @GetMapping("exception")
    @UnLogin
    public BaseResponse exception(@RequestParam("exception") String exception) {
        String message = localeMessageUtils.getMessage("1001");
        System.out.println("message:" + message);
        if (StringUtils.isNotBlank(exception)) {
            throw new RRException(RErrorEnum.TRANSFER_ERROR);
        }
        return RspUtil.success();
    }

    @GetMapping("user")
    @Login
    @UnLogin
    public BaseResponse user(@LoginUser User user) {
        System.out.println(user);
        return RspUtil.success(user);
    }

    @RequestMapping("/test")
    @UnLogin
    public void test() {
        //NodeUserTotal nodeUserTotal= nodeUserTotalMapper.getSumByUserId(userId);
        /*String string="2019-09-18 11:11:10";
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //加上时间
        //必须捕获异常
        try {
            Date date=sDateFormat.parse(string);
            date.getTime()
            (date.getSecond()+30*24*60*60)- (LocalTime.now().getSecond())
            System.out.println(date);
        } catch(ParseException px) {
            px.printStackTrace();
        }*/
        //nodeTask.NodeProfit();
        //nodeTask.crateTable();
        //payTask.pay();
        //teamService.teamInfo(3L);

    }
    //nodeTask.trade();
    @RequestMapping("/test2")
    @UnLogin
    public void tes2t() {
      List<Long>  list=teamMapper.getUserIds();
        for (Long list1 : list){
            String path =teamMapper.getPath(list1);
            String[] split = path.split(",");
            String s="";
            for (int i = split.length-1; i >=0 ; i--) {
                 s= s+split[i] + ",";
            }
            String s1 = s.substring(0, s.length() - 1);
            int length = split.length;
            teamMapper.insert(list1,s1,length);
        }
    }

}
