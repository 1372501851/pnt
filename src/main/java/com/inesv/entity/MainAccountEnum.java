package com.inesv.entity;



public enum MainAccountEnum {

    MAIN_ACCOUNT_1(1,"MAIN_ACCOUNT_1"),
    MAIN_ACCOUNT_2(2,"MAIN_ACCOUNT_2"),
    MAIN_ACCOUNT_3(3,"MAIN_ACCOUNT_3"),
    MAIN_ACCOUNT_4(4,"MAIN_ACCOUNT_4"),
    MAIN_ACCOUNT_5(5,"MAIN_ACCOUNT_5"),
    MAIN_ACCOUNT_6(6,"MAIN_ACCOUNT_6"),
    MAIN_ACCOUNT_7(7,"MAIN_ACCOUNT_7")
    ;


    private Integer tag;


    private String type;

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    MainAccountEnum(Integer tag, String type) {
        this.tag = tag;
        this.type = type;
    }
}
