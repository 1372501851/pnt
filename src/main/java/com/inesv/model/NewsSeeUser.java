/**
 * 
 */
package com.inesv.model;

import lombok.Data;

import java.util.Date;

/**
 * @author Admin
 * 用户已读公告列表
 */
@Data
public class NewsSeeUser {
	private Integer id;
	
	private Integer  userId;
	
	private String newsIdList;

	private Date createTime ;



}
