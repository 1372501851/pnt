package com.inesv.controller;

import com.github.pagehelper.PageInfo;
import com.inesv.annotation.Login;
import com.inesv.annotation.LoginUser;
import com.inesv.annotation.UnLogin;
import com.inesv.common.constant.RErrorEnum;
import com.inesv.common.exception.RRException;
import com.inesv.entity.CoinParams;
import com.inesv.model.Node;
import com.inesv.model.Params;
import com.inesv.model.Team;
import com.inesv.model.User;
import com.inesv.service.TeamService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    //查看当前用户的下面的团队人数及详情
    @RequestMapping("/teamInfo")
    @Login
    @UnLogin
    public BaseResponse nodeRecord(@LoginUser User user,@RequestParam(required = false, defaultValue = "1") Integer pageNum, @RequestParam(required = false, defaultValue = "8") Integer pageSize) {
        Map<String,Object> map=teamService.teamInfo(user.getId(),pageNum,pageSize);
        return RspUtil.success(map);
    }

    //搜索接口
    @RequestMapping("/search")
    @Login
    @UnLogin
    public BaseResponse search(@LoginUser User user,@RequestParam("username") String username) {
        List<Team> map=teamService.search(user.getId(),username);
        return RspUtil.success(map);
    }
}
