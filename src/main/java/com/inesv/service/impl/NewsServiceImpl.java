package com.inesv.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import com.inesv.mapper.NewsSeeUserMapper;
import com.inesv.model.NewsSeeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.inesv.mapper.NewsMapper;
import com.inesv.model.News;
import com.inesv.service.NewsService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import com.inesv.util.ValidataUtil;

@Service
public class NewsServiceImpl implements NewsService{

    @Autowired
    private NewsMapper  newsMapper;
    @Autowired
    private NewsSeeUserMapper  newsSeeUserMapper;

    private static final Logger log = LoggerFactory
            .getLogger(NewsServiceImpl.class);

    /**
     * 获得资讯信息集合
     */
    @Override
    public BaseResponse getNewsList(String data) {

        log.info("进入资讯"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("进入资讯参数:"+ data);
        Integer  langueCode = 0;
        if(ValidataUtil.isNull(data)) return RspUtil.rspError("params: data is null");

        Map<String,Object> map =new HashMap<String,Object>();
        try{

            JSONObject  jsonObject = JSONObject.parseObject(data);
            Integer userId =jsonObject.getInteger("userId");
            Integer pageNum =jsonObject.getInteger("pageNum");
            Integer pageSize =jsonObject.getInteger("pageSize");
            Integer newsLangue =jsonObject.getInteger("language");
            if (userId == null) {
                return RspUtil.rspError("userId is null");
            }

            if (newsLangue == null) newsLangue =  0;
            langueCode = newsLangue;
            //分页查询
            if(pageNum==null ||pageNum <=0)pageNum=1;
            if(pageSize==null ||pageSize <=0)pageSize=5;
            PageHelper.startPage(pageNum, pageSize);
            List<News> entitys = newsMapper.getNewsListByLangue(newsLangue);
            //根据前端传来的用户id，查询用户已读的公告列表
            NewsSeeUser newsSeeUser = newsSeeUserMapper.getSeeNewsListByUserId(userId);
            if(newsSeeUser!=null){
                String newsList=newsSeeUser.getNewsIdList();
                //对公告id文本进行List处理
                List<String> strList = Arrays.asList(newsList.split(","));
                for (News news:entitys) {
                    //遍历时默认是已读
                    news.setIsRead(1);
                    boolean isExist=strList.contains( news.getId().toString());
                    //如果不存在公告id则改为未读
                    if(isExist!=true){
                        //修改为未读
                        news.setIsRead(0);
                    }
                }
            }else{
                for (News news:entitys) {
                    //遍历时默认是未读
                    news.setIsRead(0);
                }
            }


            PageInfo<News> pageInfo = new PageInfo<News>(entitys);

	       /*List<News> newsList =pageInfo.getList();
	       List<News> addUrlnewsList =new ArrayList<News>();
	       if(!newsList.isEmpty()) {
	    	   for(News news :newsList) {
	    		   Long id= news.getId();
	    		   Integer langue =news.getNewsLangue();
	    		   StringBuffer sb =new StringBuffer();
	    		   //getNewsDetail?data={"language":"1","id":"4"}
	    		   sb.append("getNewsDetail?data={");
	    		   sb.append("'language':");
	    		   sb.append(news.getNewsLangue());
	    		   sb.append(",");
	    		   sb.append("'id':");
	    		   sb.append(news.getId());
	    		   sb.append("}");
	    		   String url =sb.toString();
	    		   news.setNewsUrl(url);
	    		   addUrlnewsList.add(news);
	    	   }
	    	   pageInfo.setList(addUrlnewsList);
	       }*/


            map.put("pageInfo", pageInfo);
        }catch(Exception e){
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
        log.info("返回资讯参数："+JSON.toJSONString(baseResponse));
        return baseResponse;
    }

    @Override
    public BaseResponse getNewsDetail(String data) {
        log.info("进入资讯详情"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("进入资讯详情参数:"+ data);
        Integer  langueCode = 0;
        if(ValidataUtil.isNull(data)) return RspUtil.rspError("params: data is null");

        Map<String,Object> map =new HashMap<String,Object>();
        try{

            JSONObject  jsonObject = JSONObject.parseObject(data);

            Long id =jsonObject.getLong("id");
            Integer newsLangue =jsonObject.getInteger("language");

            if (newsLangue == null) newsLangue =  0;
            langueCode = newsLangue;
            News news = newsMapper.getNewsDetail(id);
            Map<String,Object> resultNews =new HashMap<String ,Object>();
            if(news !=null) {
                resultNews.put("id", news.getId());
                resultNews.put("newsTitle", news.getNewsTitle());
                resultNews.put("newsSummary", news.getNewsSummary());
                resultNews.put("newsText", news.getNewsText());
                resultNews.put("createTime", news.getCreateTime());
            }
            map.put("news", resultNews);
        }catch(Exception e){
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
        log.info("资讯详情出参："+JSON.toJSONString(baseResponse));
        return baseResponse;
    }

}
