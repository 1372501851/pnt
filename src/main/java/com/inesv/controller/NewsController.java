package com.inesv.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.NewsSeeUserMapper;
import com.inesv.model.News;
import com.inesv.model.NewsSeeUser;
import com.inesv.service.NewsSeeUserService;
import com.inesv.util.RspUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.inesv.annotation.UnLogin;
import com.inesv.service.NewsService;
import com.inesv.util.BaseResponse;

import java.util.Arrays;
import java.util.List;

@RestController
public class NewsController {

    private static final Logger log = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;
    @Autowired
    private NewsSeeUserService newsSeeUseService;

    /**
     * 获取资讯集合
     *
     * @param data
     * @return
     */
    @GetMapping("/getNewses")
    @UnLogin
    public BaseResponse getNewses(@RequestParam("data") String data) {
        return newsService.getNewsList(data);
    }


    @RequestMapping("getNewsDetail")
    @UnLogin
    public BaseResponse getNewsDetail(@RequestParam("data") String data) {
        return newsService.getNewsDetail(data);
    }


    /**
     * 修改公告状态（将公告id添加到用户已读公告id文本中）
     *
     * @param data
     * @return
     */
    @PostMapping("/updateNewsState")
    @UnLogin
    public BaseResponse addNewsId(@RequestParam("data") String data) {
        return newsSeeUseService.updateNewsId(data);
    }

    /**
     * 判断用户是否有未读消息
     *
     * @param data
     * @return
     */
    @PostMapping("/isExistUnreadNews")
    @UnLogin
    public BaseResponse getUnreadNews(@RequestParam("data") String data) {
        return newsSeeUseService.getUnreadNewsIdList(data);
    }



}
