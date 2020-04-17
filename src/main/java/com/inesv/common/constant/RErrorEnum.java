package com.inesv.common.constant;

import lombok.Getter;
import lombok.Setter;

/**
 * 返回业务的错误信息
 * message只用于备注，国际化请在i18n文件夹中添加对应的国际化信息
 */
public enum RErrorEnum {

    TRANSFER_ERROR(1001, "转账失败"),
    TRADEPASSWD_ERROR(1002, "交易密码错误"),
    PARAM_ERROR(1003, "参数错误"),
    AMOUNT_ERROR(1004, "金额不合理"),
    USERNODE_NOEXIST(1005, "用户不存在任何社区"),
    NODEAMOUNT_FULL(1006, "投资金额超出设定值"),
    INVITECODE_INVALID(1007,"推荐码不存在"),
    TRANSFERRED_ACCOUNT(1008, "已转过账"),
    GET_MAIN_ADDRESS_FAIL(1009,"获取主账户地址失败"),
    SUSPEND_DRAW(1010,"暂停提取"),
    ENTER_CORRECT_AMOUNT(1011,"请输入100的倍数"),
    NO_RECOMMEND_OWN(1012,"不能推荐自己，请重新输入推荐码"),
    RECOMMEND_CODE_NO_OWN(1013,"当前推荐码不属于本社区,请重新输入"),
    RECOMMEND_CODE_NO_ANY(1014,"当前推荐码不属于任何社区,请重新输入"),
    ZAN_SWITCH(1015,"暂时不能使用"),
    MAX_MONEY(1016,"总投资超过最大金额10000，请重新输入投资金额"),
    INSUFFICIENT_AMOUNT(1017,"金额不足"),
    EXCHANGE_FAIL(1018,"网络原因，获取数据失败"),
    NO_TRADE_COIN(1019,"你的账户没有该币种"),
    PARAMETER_EMPTY(1020,"传入的参数为空"),
    CHECK_FAIL(1021,"验签失败"),
    ORDER_ALREADY_PAY(1022,"订单已支付，请勿重复支付"),
    PAY_FAIL(1023,"网络原因，支付失败"),
    MONEY_MARK_ERROR(1024,"货币标识错误"),
    USER_ADDRESS_NO_EXIST(1025,"钱包地址有误"),
    POINT_NOT_ENOUGH(1026,"积分不足"),
    COIN_TYPE_DIFFERENT(1027,"支付的币种与订单中的币种不一致"),
    ORDER_ABNORMAL(1028,"订单异常"),
    COMMUNITY_CLOSE_INSERT(1029,"社区关闭暂时不能委托"),
    COMMUNITY_CLOSE_DRAW(1030,"社区关闭暂时不能赎回"),
    AUTHORIZED(1031,"已授权"),
    UPLOAD_FAILURE(1032,"图片上传失败")
    ;

    @Getter
    @Setter
    private int code;

    @Getter
    @Setter
    private String message;

    private RErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
