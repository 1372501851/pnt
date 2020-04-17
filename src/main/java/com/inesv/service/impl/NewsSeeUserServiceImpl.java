package com.inesv.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.inesv.mapper.NewsMapper;
import com.inesv.mapper.NewsSeeUserMapper;
import com.inesv.model.News;
import com.inesv.model.NewsSeeUser;
import com.inesv.service.NewsSeeUserService;
import com.inesv.service.NewsService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import com.inesv.util.ValidataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

@Service
public class NewsSeeUserServiceImpl implements NewsSeeUserService {

    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private NewsSeeUserMapper newsSeeUserMapper;

    private static final Logger log = LoggerFactory
            .getLogger(NewsSeeUserServiceImpl.class);

    @Override
    public BaseResponse updateNewsId(String data) {
        log.info("修改公告状态" + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("修改公告状态:" + data);
        if (ValidataUtil.isNull(data)) {
            return RspUtil.rspError("params: data is null");
        }
        JSONObject jsonObject = JSONObject.parseObject(data);

        Integer userId = jsonObject.getInteger("userId");
        Integer newsLangue = jsonObject.getInteger("language");
        String newId = jsonObject.getString("newId");
        if (ValidataUtil.isNull(userId)) {
            return RspUtil.rspError("userId is null");
        }
        if (ValidataUtil.isNull(newId)) {
            return RspUtil.rspError("newId is null");
        }
        if (newsLangue == null) newsLangue = 0;
        //查询是否存在此广告id
        News entitys = newsMapper.getNewsDetail(Long.valueOf(newId));
        if (entitys == null) {
            return RspUtil.success(200, "公告id不存在", null);
        }

        BaseResponse baseResponse = RspUtil.success();
        ;
        try {
            //根据前端传来的用户id，查询用户已读的公告列表
            NewsSeeUser newsSeeUser = newsSeeUserMapper.getSeeNewsListByUserId(userId);
            if (newsSeeUser != null) {
                String newsList = newsSeeUser.getNewsIdList();
                //对公告id文本进行List处理
                List<String> strList = Arrays.asList(newsList.split(","));
                boolean isExist = strList.contains(newId);
                //如果不存在公告id则添加id到已读公告id中
                String newsIdList;
                if (isExist != true) {
                    if (newsList.equals("")) {
                        newsIdList = newId;
                    } else {
                        newsIdList = newsList + "," + newId;
                        List<String> sortList = Arrays.asList(newsIdList.split(","));
                        Collections.sort(sortList);
                        String str = String.join(",", sortList);
                        newsIdList = str;
                    }
                    Integer num = newsSeeUserMapper.updateNewsId(userId, newsIdList);
                    if (num > 0) {
                        return RspUtil.success(200, "公告状态修改成功", null);
                    }
                } else {
                    return RspUtil.success(200, "公告状态为已读", null);
                }
            } else {
                //新添加
                NewsSeeUser newsSeeUser1 = new NewsSeeUser();
                newsSeeUser1.setUserId(Integer.valueOf(userId));
                newsSeeUser1.setNewsIdList(newId);
                Integer num = newsSeeUserMapper.addNewsSeeUser(newsSeeUser1);
                if (num > 0) {
                    return RspUtil.success(200, "新添加用户到已读公告列表，状态修改成功", null);
                }
            }
        } catch (Exception e) {
            throw e;
        }
        log.info("资讯详情出参：" + JSON.toJSONString(baseResponse));
        return baseResponse;
    }

    @Override
    public BaseResponse getUnreadNewsIdList(String data) {
        log.info("根据用户id获取未读公告id列表:" + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("根据用户id获取未读公告id列表:" + data);
        if (ValidataUtil.isNull(data)) {
            return RspUtil.rspError("params: data is null");
        }
        JSONObject jsonObject = JSONObject.parseObject(data);

        Integer userId = jsonObject.getInteger("userId");
        if (ValidataUtil.isNull(userId)) {
            return RspUtil.rspError("userId is null");
        }
        //获取所有公告id
        List<News> newsList = newsMapper.getNewsListByLangue(0);
        Map map = new HashMap();
        //用于保存未读id的列表
        List<Long> unReadIdList = new ArrayList<>();

        //根据id获取已读公告id
        NewsSeeUser newsSeeUser = newsSeeUserMapper.getSeeNewsListByUserId(userId);
        //判断公告id是否存在于已读公告中
        if (newsSeeUser != null) {
            for (News n : newsList) {
                String newsIdList = newsSeeUser.getNewsIdList();
                //对公告id文本进行List处理
                List<String> strList = Arrays.asList(newsIdList.split(","));
                //若包含某元素，返回结果为true, 若不包含该元素，返回结果为false
                boolean isExist = strList.contains(n.getId().toString());
                //如果不存在
                if (isExist != true) {
                    unReadIdList.add(n.getId());
                }
            }
        } else {
            for (News n : newsList) {
                unReadIdList.add(n.getId());
            }
            map.put("isExist", true);
            map.put("unReadNewsIdList", unReadIdList);
            return RspUtil.success(map);
        }

        //如果未读列表大于0，那就是存在未读公告，返回true，并展示未读公告id
        if (unReadIdList.size() > 0) {
            map.put("isExist", true);
            map.put("unReadNewsIdList", unReadIdList);
            return RspUtil.success(map);
        }
        map.put("isExist", false);
        map.put("unReadNewsIdList", unReadIdList);
        return RspUtil.success(map);
    }
}
