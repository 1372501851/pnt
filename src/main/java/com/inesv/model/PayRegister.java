package com.inesv.model;


import lombok.Data;

@Data
public class PayRegister {

    //id
    private Long id;

    //用户名
    private String platformName;

    //平台包名
    private String platformPackageName;

    //moc地址
    private String mocAddress;

    //交易所用户Id
    private String tradeUserId;

    //交易所交易密码
    private String password;

    //注册加密用的
    private String encode;

    //备注
    private String remark;


}
