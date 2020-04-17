package com.inesv.service.impl;

import com.inesv.mapper.AuthorizationMapper;
import com.inesv.service.AuthorizeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Autowired
    private AuthorizationMapper authorizationMapper;

    @Override
    public Integer isAuthorize(Long id, String programId) {
        Integer count=authorizationMapper.isAuthorize(id,programId);
        return count;
    }

    @Override
    public void register(String programId, String programName, String picture) {
        authorizationMapper.register(programId,programName,picture);
    }

    @Override
    public Map<String, String> info(String programId) {
        Map<String,String> map=authorizationMapper.info(programId);
        return map;
    }

    @Override
    public void insertRecord(Long id, String programToken,String programId) {
        authorizationMapper.insertRecord(id,programToken,programId);
    }
}
