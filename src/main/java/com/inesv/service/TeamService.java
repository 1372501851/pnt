package com.inesv.service;

import com.github.pagehelper.PageInfo;
import com.inesv.model.Team;

import java.util.List;
import java.util.Map;

public interface TeamService {
    Map<String,Object> teamInfo(Long id, Integer pageNum, Integer pageSize);

    List<Team> search(Long id, String username);
}
