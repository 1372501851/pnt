/**
 * 
 */
package com.inesv.model;

import lombok.Data;

import java.util.Date;

/**
 * @author Admin
 * 资讯
 */
@Data
public class News {
	
	private Long id;
	
	private String  newsTitle;
	
	private String newsSummary;
	
	private String newsText;
	
	private Date createTime ;
	
	private String adminId ;
	
	private String newsUrl ;
	
	private String newsImg;
	
	private Integer newsLangue;

	/**已读未读 ，非表字段*/
	private Integer isRead;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNewsTitle() {
		return newsTitle;
	}

	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}

	public String getNewsText() {
		return newsText;
	}

	public void setNewsText(String newsText) {
		this.newsText = newsText;
	}

   

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getNewsUrl() {
		return newsUrl;
	}

	public void setNewsUrl(String newsUrl) {
		this.newsUrl = newsUrl;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getNewsImg() {
		return newsImg;
	}

	public void setNewsImg(String newsImg) {
		this.newsImg = newsImg;
	}

	public Integer getNewsLangue() {
		return newsLangue;
	}

	public void setNewsLangue(Integer newsLangue) {
		this.newsLangue = newsLangue;
	}

	public String getNewsSummary() {
		return newsSummary;
	}

	public void setNewsSummary(String newsSummary) {
		this.newsSummary = newsSummary;
	}
   
	
	

}
