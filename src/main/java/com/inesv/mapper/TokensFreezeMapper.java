package com.inesv.mapper;

import com.inesv.model.TokensFreeze;

import java.util.List;

public interface TokensFreezeMapper {

    int add(TokensFreeze tokensFreeze);

    int update(TokensFreeze tokensFreeze);

    List<TokensFreeze> getByConditions(TokensFreeze tokensFreeze);
}
