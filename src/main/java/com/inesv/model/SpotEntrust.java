package com.inesv.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class SpotEntrust {
    private Long id;
    private String entrustNo;
    private Integer userNo;
    private Integer entrustCoin;
    private Integer entrustType;
    private BigDecimal entrustPrice;
    private BigDecimal entrustRange;
    private BigDecimal entrustMinPrice;
    private BigDecimal entrustMaxPrice;
    private BigDecimal recordMinPrice;
    private BigDecimal recordMaxPrice;
    private BigDecimal entrustNum;
    private BigDecimal dealNum;
    private BigDecimal matchNum;
    private BigDecimal cancelNum;
    private BigDecimal poundage;
    private Integer poundageCoin;
    private BigDecimal poundageScale;
    private BigDecimal minerFee;
    private BigDecimal minerScale;
    private Integer receivablesType;
    private Integer bankcardId;
    private Integer matchingType;
    private Integer securityState;
    private Integer state;
    private String remark;
    private Date date;
    private String dateFormatDate;  //时间（非表字段）
    private BigDecimal minPrice;    //非表字段
    private BigDecimal maxPrice;    //非表字段
    private String coinCore;        //非表字段
    private BigDecimal RemainNum;   //剩余数量（非表字段）
    private BigDecimal tradeSize;
    private BigDecimal rangeMinPrice;   //溢价最低价
    private BigDecimal rangeMaxPrice;   //溢价最高价
    private String judgeType;   //判断可用数量是否要大于（1：大于）（非表字段）
    private BigDecimal APIDealNumber;   //API接口修改数量（非表字段）
    private String APIDealEditType;     //API接口修改类型（非表字段）
    private BigDecimal APIMatchNumber;  //API接口修改数量（非表字段）
    private String APIMatchEditType;    //API接口修改类型（非表字段）
    private String APIType; //修改类型（非表字段）
    private Integer conductState;    //进行中状态（非表字段）
    private Integer completeState;   //已完成状态（非表字段）
    private String nickname;    //用户昵称（非表字段）
    private String username;    //用户账号（非表字段）
    private String photo;       //用户头像（非表字段）
    private String coinImg;   //货币图片（非表字段）
    private String phone;   //手机号（非表字段）
    private String areaCode;//区号（非表字段）
    private String tradeScale;//比例（非表字段）

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

    public Integer getUserNo() {
        return userNo;
    }

    public void setUserNo(Integer userNo) {
        this.userNo = userNo;
    }

    public Integer getEntrustCoin() {
        return entrustCoin;
    }

    public void setEntrustCoin(Integer entrustCoin) {
        this.entrustCoin = entrustCoin;
    }

    public Integer getEntrustType() {
        return entrustType;
    }

    public void setEntrustType(Integer entrustType) {
        this.entrustType = entrustType;
    }

    public BigDecimal getEntrustPrice() {
        return entrustPrice;
    }

    public void setEntrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    public BigDecimal getEntrustRange() {
        return entrustRange;
    }

    public void setEntrustRange(BigDecimal entrustRange) {
        this.entrustRange = entrustRange;
    }

    public BigDecimal getEntrustMinPrice() {
        return entrustMinPrice;
    }

    public void setEntrustMinPrice(BigDecimal entrustMinPrice) {
        this.entrustMinPrice = entrustMinPrice;
    }

    public BigDecimal getEntrustMaxPrice() {
        return entrustMaxPrice;
    }

    public void setEntrustMaxPrice(BigDecimal entrustMaxPrice) {
        this.entrustMaxPrice = entrustMaxPrice;
    }

    public BigDecimal getRecordMinPrice() {
        return recordMinPrice;
    }

    public void setRecordMinPrice(BigDecimal recordMinPrice) {
        this.recordMinPrice = recordMinPrice;
    }

    public BigDecimal getRecordMaxPrice() {
        return recordMaxPrice;
    }

    public void setRecordMaxPrice(BigDecimal recordMaxPrice) {
        this.recordMaxPrice = recordMaxPrice;
    }

    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    public BigDecimal getDealNum() {
        return dealNum;
    }

	public void setDealNum(BigDecimal dealNum) {
		this.dealNum = dealNum;
	}

    public BigDecimal getMatchNum() {
        return matchNum;
    }

    public void setMatchNum(BigDecimal matchNum) {
        this.matchNum = matchNum;
    }

    public BigDecimal getPoundage() {
        return poundage;
    }

    public void setPoundage(BigDecimal poundage) {
        this.poundage = poundage;
    }

    public Integer getPoundageCoin() {
        return poundageCoin;
    }

    public void setPoundageCoin(Integer poundageCoin) {
        this.poundageCoin = poundageCoin;
    }

    public BigDecimal getPoundageScale() {
        return poundageScale;
    }

    public void setPoundageScale(BigDecimal poundageScale) {
        this.poundageScale = poundageScale;
    }

    public Integer getReceivablesType() {
        return receivablesType;
    }

    public void setReceivablesType(Integer receivablesType) {
        this.receivablesType = receivablesType;
    }

    public Integer getMatchingType() {
        return matchingType;
    }

    public void setMatchingType(Integer matchingType) {
        this.matchingType = matchingType;
    }

    public Integer getSecurityState() {
        return securityState;
    }

    public void setSecurityState(Integer securityState) {
        this.securityState = securityState;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateFormatDate() {
        return dateFormatDate;
    }

    public void setDateFormatDate(String dateFormatDate) {
        this.dateFormatDate = dateFormatDate;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getCoinCore() {
        return coinCore;
    }

    public void setCoinCore(String coinCore) {
        this.coinCore = coinCore;
    }

    public BigDecimal getRemainNum() {
        return RemainNum;
    }

    public void setRemainNum(BigDecimal remainNum) {
        RemainNum = remainNum;
    }

    public BigDecimal getTradeSize() {
        return tradeSize;
    }

    public void setTradeSize(BigDecimal tradeSize) {
        this.tradeSize = tradeSize;
    }

    public BigDecimal getRangeMinPrice() {
        return rangeMinPrice;
    }

    public void setRangeMinPrice(BigDecimal rangeMinPrice) {
        this.rangeMinPrice = rangeMinPrice;
    }

    public BigDecimal getRangeMaxPrice() {
        return rangeMaxPrice;
    }

    public void setRangeMaxPrice(BigDecimal rangeMaxPrice) {
        this.rangeMaxPrice = rangeMaxPrice;
    }

    public String getJudgeType() {
        return judgeType;
    }

    public void setJudgeType(String judgeType) {
        this.judgeType = judgeType;
    }

    public BigDecimal getAPIDealNumber() {
        return APIDealNumber;
    }

    public void setAPIDealNumber(BigDecimal aPIDealNumber) {
        APIDealNumber = aPIDealNumber;
    }

    public String getAPIDealEditType() {
        return APIDealEditType;
    }

    public void setAPIDealEditType(String aPIDealEditType) {
        APIDealEditType = aPIDealEditType;
    }

    public BigDecimal getAPIMatchNumber() {
        return APIMatchNumber;
    }

    public void setAPIMatchNumber(BigDecimal aPIMatchNumber) {
        APIMatchNumber = aPIMatchNumber;
    }

    public String getAPIMatchEditType() {
        return APIMatchEditType;
    }

    public void setAPIMatchEditType(String aPIMatchEditType) {
        APIMatchEditType = aPIMatchEditType;
    }

    public String getAPIType() {
        return APIType;
    }

    public void setAPIType(String aPIType) {
        APIType = aPIType;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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


    public BigDecimal getMinerFee() {
        return minerFee;
    }

    public void setMinerFee(BigDecimal minerFee) {
        this.minerFee = minerFee;
    }

    public BigDecimal getMinerScale() {
        return minerScale;
    }

    public void setMinerScale(BigDecimal minerScale) {
        this.minerScale = minerScale;
    }


    public Integer getBankcardId() {
        return bankcardId;
    }

    public void setBankcardId(Integer bankcardId) {
        this.bankcardId = bankcardId;
    }

    public BigDecimal getCancelNum() {
        return cancelNum;
    }

    public void setCancelNum(BigDecimal cancelNum) {
        this.cancelNum = cancelNum;
    }

    public SpotEntrust() {
    }


    public static class Builder {
        private Long id;
        private String entrustNo;
        private Integer userNo;
        private Integer entrustCoin;
        private Integer entrustType;
        private BigDecimal entrustPrice;
        private BigDecimal entrustMinPrice;
        private BigDecimal entrustMaxPrice;
        private BigDecimal recordMinPrice;
        private BigDecimal recordMaxPrice;
        private BigDecimal entrustNum;
        private BigDecimal dealNum;
        private BigDecimal matchNum;
        private BigDecimal poundage;
        private Integer poundageCoin;
        private BigDecimal poundageScale;
        private Integer receivablesType;
        private Integer bankcardId;
        private Integer matchingType;
        private Integer state;
        private String remark;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }
        public Builder setEntrustNo(String entrustNo) {
            this.entrustNo = entrustNo;
            return this;
        }
        public Builder setUserNo(Integer userNo) {
            this.userNo = userNo;
            return this;
        }
        public Builder setEntrustCoin(Integer entrustCoin) {
            this.entrustCoin = entrustCoin;
            return this;
        }
        public Builder setEntrustType(Integer entrustType) {
            this.entrustType = entrustType;
            return this;
        }
        public Builder setEntrustPrice(BigDecimal entrustPrice) {
            this.entrustPrice = entrustPrice;
            return this;
        }
        public Builder setEntrustMinPrice(BigDecimal entrustMinPrice) {
            this.entrustMinPrice = entrustMinPrice;
            return this;
        }
        public Builder setEntrustMaxPrice(BigDecimal entrustMaxPrice) {
            this.entrustMaxPrice = entrustMaxPrice;
            return this;
        }
        public Builder setRecordMinPrice(BigDecimal recordMinPrice) {
            this.recordMinPrice = recordMinPrice;
            return this;
        }
        public Builder setRecordMaxPrice(BigDecimal recordMaxPrice) {
            this.recordMaxPrice = recordMaxPrice;
            return this;
        }
        public Builder setEntrustNum(BigDecimal entrustNum) {
            this.entrustNum = entrustNum;
            return this;
        }
        public Builder setDealNum(BigDecimal dealNum) {
            this.dealNum = dealNum;
            return this;
        }
        public Builder setMatchNum(BigDecimal matchNum) {
            this.matchNum = matchNum;
            return this;
        }
        public Builder setPoundage(BigDecimal poundage) {
            this.poundage = poundage;
            return this;
        }
        public Builder setPoundageCoin(Integer poundageCoin) {
            this.poundageCoin = poundageCoin;
            return this;
        }
        public Builder setPoundageScale(BigDecimal poundageScale) {
            this.poundageScale = poundageScale;
            return this;
        }
        public Builder setReceivablesType(Integer receivablesType) {
            this.receivablesType = receivablesType;
            return this;
        }
        public Builder setBankcardId(Integer bankcardId) {
            this.bankcardId = bankcardId;
            return this;
        }
        public Builder setMatchingType(Integer matchingType) {
            this.matchingType = matchingType;
            return this;
        }
        public Builder setState(Integer state) {
            this.state = state;
            return this;
        }
        public Builder setRemark(String remark) {
            this.remark = remark;
            return this;
        }
        public SpotEntrust build(){
            return new SpotEntrust(this);

        }
    }

    private SpotEntrust(Builder builder) {
        this.id = builder.id;
        this.entrustNo = builder.entrustNo;
        this.userNo = builder.userNo;
        this.entrustCoin = builder.entrustCoin;
        this.entrustType = builder.entrustType;
        this.entrustPrice = builder.entrustPrice;
        this.entrustMinPrice = builder.entrustMinPrice;
        this.entrustMaxPrice = builder.entrustMaxPrice;
        this.recordMinPrice = builder.recordMinPrice;
        this.recordMaxPrice = builder.recordMaxPrice;
        this.entrustNum = builder.entrustNum;
        this.dealNum = builder.dealNum;
        this.matchNum = builder.matchNum;
        this.poundage = builder.poundage;
        this.poundageCoin = builder.poundageCoin;
        this.poundageScale = builder.poundageScale;
        this.receivablesType = builder.receivablesType;
        this.bankcardId = builder.bankcardId;
        this.matchingType = builder.matchingType;
        this.state = builder.state;
        this.remark = builder.remark;
    }
}
