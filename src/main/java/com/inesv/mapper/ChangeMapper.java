package com.inesv.mapper;

import com.inesv.model.Change;

import java.util.List;
import java.util.Map;

public interface ChangeMapper {

    int add(Change record);

    List<Change> queryMax(Map<String, Object> params);

    List<Change> queryRecordByUserAndCoin(Map<String,Object> params);

    Change queryDetail(Long id);

    List<Change> queryByInCoin(Long coinNo);

    int updateFrozenAssets(Change change);

    List<Change> queryFrozenAssets();
}