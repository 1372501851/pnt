package com.inesv.service;

import java.util.Map;

public interface AuthorizeService {
    Integer isAuthorize(Long id, String programId);

    void register(String programId, String programName, String picture);

    Map<String,String> info(String programId);

    void insertRecord(Long id, String programToken,String programId);
}
