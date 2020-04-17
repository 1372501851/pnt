package com.inesv.model;


import lombok.Data;

/**
 * t_user_memo实体类
 * 
 * @author 
 *
 */

@Data
public class UserMemo {
	/**自增id*/
	private Integer id; 
	/**用户id*/
	private Integer userId; 
	/**用户助记词*/
	private String userMemo; 

}
