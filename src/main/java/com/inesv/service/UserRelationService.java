package com.inesv.service;

public interface UserRelationService {

    /**
     * 判断用户id是否有推荐人
     * @param userId
     * @return
     */
    boolean userHasParID(Long userId);
}
