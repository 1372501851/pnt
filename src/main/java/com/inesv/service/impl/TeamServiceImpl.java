package com.inesv.service.impl;


import com.inesv.mapper.TeamMapper;
import com.inesv.model.Team;
import com.inesv.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamMapper teamMapper;


    @Override
    public Map<String,Object> teamInfo(Long id, Integer pageNum, Integer pageSize) {
        //获取当前用户下的团队
        //根据当前用户是否是最上级
        Integer count=teamMapper.isFirstUserId(id);
        List<Long> list;
        List<Long> list2;
        List<Long> list3;
        List<Team> lists=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        if (count==null || count==0){
            String userId=id+",";
            list2=teamMapper.getLowerUserId(userId);
            String totalAmount=teamMapper.getTotalAmount(userId);
            map.put("totalAmount",totalAmount);
        }else {
            String userId=","+id+",";
            list2=teamMapper.getLowerUserId2(userId);
            //lists=teamMapper.teamInfo(userId);
            String totalAmount=teamMapper.getTotalAmount2(userId);
            map.put("totalAmount",totalAmount);
        }
        list=teamMapper.getRecId(id);
        if (CollectionUtils.isNotEmpty(list)){
            for (Long userId : list){
                Team team=teamMapper.teamInfo(userId);
                Integer count1=teamMapper.isFirstUserId(userId);
                if (count1==null || count1==0){
                    String userId1=userId+",";
                    list3=teamMapper.getLowerUserId(userId1);
                    String totalAmount=teamMapper.getTotalAmount(userId1);
                    team.setUserRecAmount(totalAmount);
                }else {
                    String userId1=","+userId+",";
                    list3=teamMapper.getLowerUserId2(userId1);
                    //lists=teamMapper.teamInfo(userId);
                    String totalAmount=teamMapper.getTotalAmount2(userId1);
                    team.setUserRecAmount(totalAmount);
                }
                if (CollectionUtils.isNotEmpty(list3)){
                    team.setUserRecCount(list3.size());
                }else {
                    team.setUserRecCount(0);
                }
                if (team.getPhoto()==null){
                    team.setPhoto("");
                }
                lists.add(team);
            }
        }
        //PageHelper.startPage(pageNum, pageSize);
        //PageInfo<Team> tradePageInfo = new PageInfo<Team>(lists);
        /*int count1;
        List<Team> pageList=new ArrayList<>();
        if(lists != null && lists.size() > 0) {
            count1 = lists.size();
            int fromIndex = (pageNum-1) * pageSize;
            int toIndex = pageNum  * pageSize;
            if(toIndex > count1) {
                toIndex = count1;
            }
            if (fromIndex>=0&&fromIndex<=lists.size()&&toIndex>=0&&toIndex<=lists.size()){
                pageList= lists.subList(fromIndex, toIndex);
            }
        }*/
        map.put("list",lists);
        //获取直推人数
        Integer recCount=teamMapper.getRecCount(id);
        if (recCount==null){
            recCount=0;
        }
        map.put("recCount",recCount);
        if (CollectionUtils.isNotEmpty(list2)){
            map.put("count",list2.size());
        }else {
            map.put("count",0);
        }
        return map;
    }

    @Override
    public  List<Team> search(Long id, String username) {
        Integer count=teamMapper.isFirstUserId(id);
        List<Long> list;
        if (count==null || count==0){
            String userId=id+",";
            list=teamMapper.getSearch(userId,username);
        }else {
            String userId=","+id+",";
            list=teamMapper.getSearch(userId,username);
        }
        List<Team> lists=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)){
            int count1=0;
            for (Long userId : list){
                count1=count1+1;
                //限制一百条
                if (count1>100){
                    break;
                }
                Team team=teamMapper.teamInfo(userId);
                if (team.getPhoto()==null){
                    team.setPhoto("");
                }
                lists.add(team);
            }
        }
        return lists;
    }
}
