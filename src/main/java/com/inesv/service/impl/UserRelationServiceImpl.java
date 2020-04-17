package com.inesv.service.impl;

import com.inesv.mapper.UserRelationMapper;
import com.inesv.model.UserRelation;
import com.inesv.service.UserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRelationServiceImpl implements UserRelationService {

    @Autowired
    private UserRelationMapper userRelationMapper;


    public UserRelation getUserRelation(Long userId) {
        UserRelation query = new UserRelation();
        query.setUserId(userId);
        UserRelation userRelation = userRelationMapper.getUserRelationByCondition(query);
        return userRelation;
    }

    @Override
    public boolean userHasParID(Long userId) {
        UserRelation userRelation = getUserRelation(userId);
        if(userRelation == null) {
            return false;
        } else {
            return true;
        }
    }
}
