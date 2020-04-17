package com.inesv.mapper;
import com.inesv.model.UserMemo;

/**
 * UserMemo的Dao接口
 * 
 * @author 
 *
 */
public interface UserMemoMapper {

	int addUserMemo(UserMemo userMemo);

	UserMemo getUserMemoByCondition(UserMemo userMemo);
}