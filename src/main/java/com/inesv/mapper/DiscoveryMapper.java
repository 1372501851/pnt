package com.inesv.mapper;

import com.inesv.model.Discovery;

import java.util.List;

public interface DiscoveryMapper {

    List<Discovery> discoveryList();


    Discovery queryDiscoveryByCondition(Discovery discovery);

}

