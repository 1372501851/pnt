package com.inesv.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SpotDealDetail {
    private Long id;
    private String orderNo;
    private String entrustNo;
    private Integer userNo;
    private Integer buyUserNo;
    private Integer sellUserNo;
    private Integer coinNo;
    private BigDecimal dealPrice;
    private BigDecimal entrustPrice;
    private BigDecimal dealNum;
    private BigDecimal sumPrice;
    private BigDecimal poundageScale;
    private Integer poundageCoin;
    private BigDecimal poundage;
    private BigDecimal minerFee;
    private Long buyEntrust;
    private Long sellEntrust;
    private Long matchNo;
    private Integer state;
    private String remark;
    private Integer receivablesType;
    private String sellAccount;
    private String buyAccount;
    private Date date;
    private Date updateTime;
    private String attr1;
    private String attr2;
    private String buyUserName;     //非表字段
    private String buyUserPhoto;    //非表字段
    private String sellUserName;    //非表字段
    private String sellUserPhoto;   //非表字段
    private String username;    //用户账号（非表字段）
    private String photo;       //用户头像（非表字段）
    private String coinCore;    //非表字段
    private Integer dateType;   //非表字段
    private String dateFormatDate;  //非表字段
    private Integer conductState;    //进行中状态（非表字段）
    private Integer completeState;   //已完成状态（非表字段）
    private Integer dealType;   //买卖类型（非表字段）
    private String coinImg;   //货币图片（非表字段）
    private String phone;   //手机号（非表字段）
    private String sellAccountPhone;     //出售方联系方式（非表字段）
    private String buyAccountPhone;     //购买方联系方式（非表字段）


    public Integer getUserNo() {
        return userNo;
    }

    public void setUserNo(Integer userNo) {
        this.userNo = userNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
	public String getEntrustNo() {
		return entrustNo;
	}

	public void setEntrustNo(String entrustNo) {
		this.entrustNo = entrustNo;
	}

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getBuyUserNo() {
        return buyUserNo;
    }

    public void setBuyUserNo(Integer buyUserNo) {
        this.buyUserNo = buyUserNo;
    }

    public Integer getSellUserNo() {
        return sellUserNo;
    }

    public void setSellUserNo(Integer sellUserNo) {
        this.sellUserNo = sellUserNo;
    }

    public Integer getCoinNo() {
        return coinNo;
    }

    public BigDecimal getMinerFee() {
		return minerFee;
	}

	public void setMinerFee(BigDecimal minerFee) {
		this.minerFee = minerFee;
	}

	public void setCoinNo(Integer coinNo) {
        this.coinNo = coinNo;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public BigDecimal getEntrustPrice() {
        return entrustPrice;
    }

    public void setEntrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    public BigDecimal getDealNum() {
        return dealNum;
    }

    public void setDealNum(BigDecimal dealNum) {
        this.dealNum = dealNum;
    }

    public BigDecimal getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(BigDecimal sumPrice) {
        this.sumPrice = sumPrice;
    }

    public BigDecimal getPoundageScale() {
        return poundageScale;
    }

    public void setPoundageScale(BigDecimal poundageScale) {
        this.poundageScale = poundageScale;
    }

    public Integer getPoundageCoin() {
        return poundageCoin;
    }

    public void setPoundageCoin(Integer poundageCoin) {
        this.poundageCoin = poundageCoin;
    }

    public BigDecimal getPoundage() {
        return poundage;
    }

    public void setPoundage(BigDecimal poundage) {
        this.poundage = poundage;
    }

    public Long getBuyEntrust() {
        return buyEntrust;
    }

    public void setBuyEntrust(Long buyEntrust) {
        this.buyEntrust = buyEntrust;
    }

    public Long getSellEntrust() {
        return sellEntrust;
    }

    public void setSellEntrust(Long sellEntrust) {
        this.sellEntrust = sellEntrust;
    }

    public Long getMatchNo() {
        return matchNo;
    }

    public void setMatchNo(Long matchNo) {
        this.matchNo = matchNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getReceivablesType() {
        return receivablesType;
    }

    public void setReceivablesType(Integer receivablesType) {
        this.receivablesType = receivablesType;
    }

    public String getSellAccount() {
        return sellAccount;
    }

    public void setSellAccount(String sellAccount) {
        this.sellAccount = sellAccount;
    }

    public String getBuyAccount() {
        return buyAccount;
    }

    public void setBuyAccount(String buyAccount) {
        this.buyAccount = buyAccount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    public String getAttr2() {
        return attr2;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }

    public String getDateFormatDate() {
        return dateFormatDate;
    }

    public void setDateFormatDate(String dateFormatDate) {
        this.dateFormatDate = dateFormatDate;
    }

    public String getCoinCore() {
        return coinCore;
    }

    public void setCoinCore(String coinCore) {
        this.coinCore = coinCore;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getDateType() {
        return dateType;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

    public String getBuyUserName() {
        return buyUserName;
    }

    public void setBuyUserName(String buyUserName) {
        this.buyUserName = buyUserName;
    }

    public String getSellUserName() {
        return sellUserName;
    }

    public void setSellUserName(String sellUserName) {
        this.sellUserName = sellUserName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getConductState() {
        return conductState;
    }

    public void setConductState(Integer conductState) {
        this.conductState = conductState;
    }

    public Integer getCompleteState() {
        return completeState;
    }

    public void setCompleteState(Integer completeState) {
        this.completeState = completeState;
    }

    public String getBuyUserPhoto() {
        return buyUserPhoto;
    }

    public void setBuyUserPhoto(String buyUserPhoto) {
        this.buyUserPhoto = buyUserPhoto;
    }

    public String getSellUserPhoto() {
        return sellUserPhoto;
    }

    public void setSellUserPhoto(String sellUserPhoto) {
        this.sellUserPhoto = sellUserPhoto;
    }

    public Integer getDealType() {
        return dealType;
    }

    public void setDealType(Integer dealType) {
        this.dealType = dealType;
    }

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public SpotDealDetail() {
    }

    public static class Builder{
        private Long id;
        private String orderNo;
        private String entrustNo;
        private Integer buyUserNo;
        private Integer sellUserNo;
        private Integer coinNo;
        private BigDecimal dealPrice;
        private BigDecimal entrustPrice;
        private BigDecimal dealNum;
        private BigDecimal sumPrice;
        private BigDecimal poundageScale;
        private Integer poundageCoin;
        private BigDecimal poundage;
        private Long buyEntrust;
        private Long sellEntrust;
        private Long matchNo;
        private Integer state;
        private BigDecimal minerFee;
        private String remark;
        private Integer receivablesType;
        private String buyAccount;
        private String sellAccount;
        private Date date;
        public Builder setId(Long id) {
            this.id = id;
            return this;
        }
        public Builder setOrderNo(String orderNo) {
            this.orderNo = orderNo;
            return this;
        }
        public Builder setEntrustNo(String entrustNo) {
            this.entrustNo = entrustNo;
            return this;
        }
        public Builder setBuyUserNo(Integer buyUserNo) {
            this.buyUserNo = buyUserNo;
            return this;
        }
        public Builder setSellUserNo(Integer sellUserNo) {
            this.sellUserNo = sellUserNo;
            return this;
        }
        public Builder setCoinNo(Integer coinNo) {
            this.coinNo = coinNo;
            return this;
        }
        public Builder setDealPrice(BigDecimal dealPrice) {
            this.dealPrice = dealPrice;
            return this;
        }
        public Builder setEntrustPrice(BigDecimal entrustPrice) {
            this.entrustPrice = entrustPrice;
            return this;
        }
        public Builder setDealNum(BigDecimal dealNum) {
            this.dealNum = dealNum;
            return this;
        }
        public Builder setSumPrice(BigDecimal sumPrice) {
            this.sumPrice = sumPrice;
            return this;
        }
        public Builder setPoundageScale(BigDecimal poundageScale) {
            this.poundageScale = poundageScale;
            return this;
        }
        public Builder setPoundageCoin(Integer poundageCoin) {
            this.poundageCoin = poundageCoin;
            return this;
        }
        public Builder setPoundage(BigDecimal poundage) {
            this.poundage = poundage;
            return this;
        }
        public Builder setBuyEntrust(Long buyEntrust) {
            this.buyEntrust = buyEntrust;
            return this;
        }
        public Builder setSellEntrust(Long sellEntrust) {
            this.sellEntrust = sellEntrust;
            return this;
        }
        public Builder setMatchNo(Long matchNo) {
            this.matchNo = matchNo;
            return this;
        }
        public Builder setState(Integer state) {
            this.state = state;
            return this;
        }
        public Builder setMinerFee(BigDecimal minerFee) {
            this.minerFee = minerFee;
            return this;
        }
        public Builder setRemark(String remark) {
            this.remark = remark;
            return this;
        }
        public Builder setReceivablesType(Integer receivablesType) {
            this.receivablesType = receivablesType;
            return this;
        }
        public Builder setBuyAccount(String buyAccount) {
            this.buyAccount = buyAccount;
            return this;
        }
        public Builder setSellAccount(String sellAccount) {
            this.sellAccount = sellAccount;
            return this;
        }
        public Builder setDate(Date date) {
            this.date = date;
            return this;
        }
        public SpotDealDetail build(){
            return new SpotDealDetail(this);
        }
    }

    private SpotDealDetail(Builder builder){
        this.id = builder.id;
        this.orderNo = builder.orderNo;
        this.entrustNo = builder.entrustNo;
        this.buyUserNo = builder.buyUserNo;
        this.sellUserNo = builder.sellUserNo;
        this.coinNo = builder.coinNo;
        this.dealPrice = builder.dealPrice;
        this.entrustPrice = builder.entrustPrice;
        this.dealNum = builder.dealNum;
        this.sumPrice = builder.sumPrice;
        this.poundageScale = builder.poundageScale;
        this.poundageCoin = builder.poundageCoin;
        this.poundage = builder.poundage;
        this.buyEntrust = builder.buyEntrust;
        this.sellEntrust = builder.sellEntrust;
        this.matchNo = builder.matchNo;
        this.state = builder.state;
        this.minerFee=builder.minerFee;
        this.remark = builder.remark;
        this.receivablesType=builder.receivablesType;
        this.buyAccount=builder.buyAccount;
        this.sellAccount=builder.sellAccount;
        this.date = builder.date;
    }

    public SpotDealDetail(Long id,String orderNo,String entrustNo, Integer buyUserNo, Integer sellUserNo, Integer coinNo, BigDecimal dealPrice, BigDecimal entrustPrice, BigDecimal dealNum, BigDecimal sumPrice, BigDecimal poundageScale, Integer poundageCoin, BigDecimal poundage, Long buyEntrust, Long sellEntrust, Long matchNo, Integer state,BigDecimal minerFee,String remark,Integer receivablesType,String buyAccount,String sellAccount, Date date) {
        this.id = id;
        this.orderNo = orderNo;
        this.entrustNo = entrustNo;
        this.buyUserNo = buyUserNo;
        this.sellUserNo = sellUserNo;
        this.coinNo = coinNo;
        this.dealPrice = dealPrice;
        this.entrustPrice = entrustPrice;
        this.dealNum = dealNum;
        this.sumPrice = sumPrice;
        this.poundageScale = poundageScale;
        this.poundageCoin = poundageCoin;
        this.poundage = poundage;
        this.buyEntrust = buyEntrust;
        this.sellEntrust = sellEntrust;
        this.matchNo = matchNo;
        this.state = state;
        this.minerFee=minerFee;
        this.remark = remark;
        this.receivablesType=receivablesType;
        this.buyAccount=buyAccount;
        this.sellAccount=sellAccount;
        this.date = date;
    }
}
