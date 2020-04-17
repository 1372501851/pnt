package com.inesv.util;

public class ResponseParamsDto {

	public static final ResponseParamsENDto responseParamsENDto = new ResponseParamsENDto();
	public static final ResponseParamsCNDto responseParamsCNDto = new ResponseParamsCNDto();
	public static final ResponseParamsKRDto responseParamsKRDto = new ResponseParamsKRDto();


	//不在交易时间段内
	public String C2C_TRADE_TIME_ERROR;
    //申诉类型错误
    public String C2C_DISPUTE_TYPE_ERROR;
	//您的此币种被限制转账
	public String TRADE_COIN_SUSPENDED;
	//格式错误
	public String USDT_FORMAT_ERROR;
	//交易数量不能小于100
	public String C2C_TRADE_NUM_LIMIT;
	//您存在进行中的广告，请完成后重试！
	public String NUM_ENTRUST_LIMIT;
	//您存在委托中的广告，请完成后重试！
	public String CUSTOMER_NUM_ENTRUST_LIMIT;
	//存在三条进行中的订单，请完成交易后重试！
	public String CUSTOMER_NUM_MATCH_LIMIT;
	//该订单存在进行中的订单，请稍后重试！
	public String NUM_MATCH_LIMIT;
	//暂未开放
	public String NOT_YET_OPNE;
	//验证码类短信1小时内同一手机号发送次数不能超过三次
	public String SMS_REQUEST_FAIL;
	//imToken地址不能为空
	public String FAIL_IMTOKEN_NULL_DESC;
	//助记词顺序错误
	public String MEMO_SEQUENTIAL_ERROR;
	//用户名格式不正确
	public String FAIL_USERNAME_DATA_FORMAT_DESC;
	//主币余额不足
    public String MAIN_BALANCE_INSUFFICIENT_DESC;
    //访问过快，请稍后再试
    public String QUICK_ACCESS_FAIL;
    // 对比失败
    public String CONTRAST_FAIL;
	// 推荐用户不存在
	public String INVITE_USER_IS_NOT_EXIST;

	// 成功
	public String SUCCESS;
	// 失败
	public String FAIL;
	// 参数为null
	public String PARAMS_NULL_DESC;
	// ID不能为空
	public String ID_NULL_DESC;
	public String DEAL_PWD_SETTING_NULL;
	public String DECIMAL_6_FAIL;
	// token为null
	public String TOKEN_NULL_DESC;
	// 验证码不能为null
	public String CODE_NULL_DESC;
	// 验证码错误
	public String CODE_FAIL_DESC;
	// 状态不能为null
	public String STATE_NULL_DESC;
	// 类型不能为null
	public String TYPE_NULL_DESC;
	// 类型不能为null
	public String DATE_NULL_DESC;
	// 账号已存在
	public String ACCOUNT_EXIST_DESC;
	// 账号为null
	public String ACCOUNT_NULL_DESC;
	// 密码为null
	public String USERPWD_NULL_DESC;
	// 密码错误
	public String USERPWD_FAIL_DESC;
	// 新密码为null
	public String NEW_USERPWD_NULL_DESC;
	// 旧密码为null
	public String OLD_USERPWD_NULL_DESC;
	// 新旧密码不能一致
	public String NEW_OLD_FAIL_DESC;
	// 交易数据格式异常
	public String FAIL_TRADE_DATA_FORMAT_DESC;
	// 交易价格格式异常
	public String FAIL_TRADE_PRICE_DATA_FORMAT_DESC;
	// 交易密码为null，请设置交易密码
	public String DEALPWD_NOT_SET_DESC;
	// 交易密码为null
	public String DEALPWD_NULL_DESC;
	// 交易密码错误
	public String DEALPWD_FAIL_DESC;
	// 交易密码已存在
	public String DEALPWD_EXIST_DESC;
	// 用户名不能为null
	public String USERNAME_NULL_DESC;
	// 用户昵称不能为null
	public String NICKNAME_NULL_DESC;
	// 手续费不能为null
	public String FEE_NULL_DESC;
	// 手续费异常
	public String FEE_FAIL_DESC;
	// 详情不能为null
	public String REMARK_NULL_DESC;
	// 联系方式不能为null
	public String CONTACT_NULL_DESC;
	// 转入账号不能为null
	public String IN_ADDRESS_NULL_DESC;
	// 转入账号异常
	public String IN_ADDRESS_FAIL_DESC;
	// 转出账号不能为null
	public String OUT_ADDRESS_NULL_DESC;
	// 转出账号异常
	public String OUT_ADDRESS_FAIL_DESC;
	// 金额或数量不能为null
	public String PRICE_NUM_NULL_DESC;
	// 金额或数量需要正确格式
	public String PRICE_NUM_FAIL_DESC;
	// 货币编号不能为空
	public String COIN_NULL_DESC;
	// 货币编号异常
	public String COIN_FAIL_DESC;
	// 转换货币个数格式不正确
	public String CHANGE_PRICE_ERRO_DESC;
	// 货币转换状态被关闭
	public String CHANGE_STATE_OFF_DESC;
	// 目标货币已被禁止转换
	public String CHANGE_TARGET_OFF_DESC;
	// 转换个数超过限额
	public String CHANGE_MAX_LIMIT_DESC;
	// 用户资金异常
	public String BALANCE_FAIL_DESC;
	// 用户资金余额不足
	public String BALANCE_INSUFFICIENT_DESC;
	// 用户资金余额不足
	public String BALANCE_INSUFFICIENT_API_DESC;
	// 冻结用户资产失败 后台开启冻结
	public String UNFEEZED_FAIL;
	// 转账异常
	public String BALANCE_LOCKFAIL_DESC;
	// 转入转出地址不能一致
	public String ADDRESS_SAME_DESC;
	// 转入转出钱包货币匹配异常
	public String COIN_MATCH_FAIL_DESC;
	// 暂不支持想外转账
	public String TRANSFER_TYPE_DESC;
	// 不能在指定时间内连续转账
	public String TRANSFER_GAP_DESC1;
	public String TRANSFER_GAP_DESC2;
	// 页数或行数不能为空
	public String PAGEORLINE_NULL_DESC;
	// 获取备忘字成功
	public String GET_MEMO_SUCCESS;
	public String SEND_SELLER_NITY;
	// 获取备忘字失败
	public String GET_MEMO_FAIL;
	// 导入地址缺少参数
	public String IMPORT_ADDRESS_LESS;
	// 导入失败，已存在地址
	public String IMPORT_ADDRESS_HAS;
	// 导入地址成功
	public String IMPORT_ADDRESS_SUCCESS;
	// 导入地址失败
	public String IMPORT_ADDRESS_FAIL;
	// 备份地址缺少参数
	public String LACK_PARAMETERS;
	// 备份地址成功
	public String EXPORT_ADDRESS_SUCCESS;
	// 备份地址失败
	public String EXPORT_ADDRESS_FAIL;
	// 助记词重复
	public String EXPORT_MEMO_REPEAT;
	// 地址已备份
	public String EXPORT_ADDRESS_REPEAT;
	// 用户钱包地址不存在
	public String EXPORT_FAIL_NULL_ADDRESS;
	// 不允许转换成该货币
	public String CHANGE_COIN_BAN;
	// 余额不足
	public String CHANGE_NUM_LOW;
	// 判断比重是否能够启用
	public String ADDRESS_OFF;
	// 添加资产失败
	public String WALLET_ADD_ERROR;
	// 添加资产成功
	public String WALLET_ADD_SUCCESS;
	// 查看转换详情不存在
	public String CHANGE_DETAIL_IS_NULL;
	// 该钱转入包地址已被关闭
	public String CHANGE_IN_WALLET_STATE_OFF;
	// 该钱转出包地址已被关闭
	public String CHANGE_OUT_WALLET_STATE_OFF;
	// 地址公钥错误
	public String PUBKEY_ISFALSE;
	// 上传失败
	public String UPLOAD_FAIL;
	// 中间账号地址异常
	public String CENTER_ADDRESSFAIL;
	// 交易数据格式异常（最小限额，最大限额）
	public String FAIL_TRADE_MIN_DATA_FORMAT_DESC;
	// 交易数据溢价格式异常
	public String FAIL_TRADE_RANGE_DATA_FORMAT_DESC;
	// 交易-资产异常
	public String FAIL_TRADE_USER_DESC;
	// 交易-未设置支付密码
	public String NO_TRADE_PASSWORD_DESC;
	// 交易-资产异常
	public String FAIL_TRADE_BALANCE_DESC;
	// 交易-余额不足
	public String FAIL_TRADE_INSUFFICIENT_DESC;
	// 交易-密码错误
	public String FAIL_TRADE_PASSWORD_DESC;
	// 交易-委托失败
	public String FAIL_TRADE_ADDTRUST_STATE_DESC;
	// 交易-委托成功
	public String SUCCESS_TRADE_ADDTRUST_STATE_DESC;
	// 广告-委托失败
	public String FAIL_SPOT_ADDTRUST_STATE_DESC;
	// 广告-委托成功
	public String SUCCESS_SPOT_TRADE_STATE_DESC;
	// 广告-委托失败
	public String FAIL_SPOT_TRADE_STATE_DESC;
	// 广告-委托成功
	public String SUCCESS_SPOT_ADDTRUST_STATE_DESC;
	// 广告-委托失败
	public String FAIL_SPOT_CANCEL_STATE_DESC;
	// 广告-委托成功
	public String SUCCESS_SPOT_CANCEL_STATE_DESC;
	// 广告-C2C用户匹配失败
	public String FAIL_SPOT_USER_STATE_DESC;
	// 银行卡不可为空
	public String FAIL_SPOT_NO_BANK_DESC;
	// 广告-C2C交易匹配成功
	public String SUCCESS_SPOT_MATCH_STATE_DESC;
	// 广告-C2C交易匹配成功
	public String FAIL_SPOT_MATCH_STATE_DESC;
	// 币币类型错误
	public String FAIL_COIN_TRAN_TYPE_STATE_DESC;
	// 货币类型错误
	public String FAIL_COIN_TYPE_STATE_DESC;
	// 交易数据溢价格式异常
	public String FAIL_TRADE_RANGE_DATA_DESC;
	// 交易密码解密异常
	public String FAIL_TRADE_BASE64_DESC;
	// 委托-撤销委托成功
	public String SUCCESS_TRADE_DELENTRUST_DESC;
	// 广告-撤销委托成功
	public String SUCCESS_SPOT_DELENTRUST_DESC;
	// 委托-撤销委托失败
	public String FAIL_TRADE_DELENTRUST_DESC;
	// 委托-撤销委托
	public String FAIL_TRADE_DELENTRUST_STATE_DESC;
	// 管理员密匙
	public String FAIL_TRADE_DELENTRUST_ADMIN_DESC;
	// 广告-匹配记录查找失败
	public String FAIL_MATCH_FIND_DESC;
	// 广告-确认收款成功
	public String SUCCESS_MATCH_CONFIRM_DESC;
	// 广告-确认收款失败
	public String FAIL_MATCH_CONFIRM_DESC;
	// 广告-确认收款失败：可用资产不足够支付矿工费
	public String FAIL_PAY_MINER_DESC;
	// 重复提交
	public String FAIL_REPEAT_STATE_DESC;
	// 重复数据
	public String FAIL_PUSH_REPEAT_DATA_DESC;
	// 微信异常
	public String FAIL_WEIXIN_NULL_DESC;
	// 微信异常
	public String FAIL_WEIXIN_DATA_DESC;
	// 支付宝异常
	public String FAIL_ALIPAY_NULL_DESC;
	// 支付宝异常
	public String FAIL_ALIPAY_DATA_DESC;
	// 银行卡异常
	public String FAIL_CARD_DATA_DESC;
	// 实名认证
	public String FAIL_IDENTITY_DATA_DESC;
	// 手机号码
	public String FAIL_PHONE_DATA_DESC;
	// 申请纠纷需要一小时后
	public String FAIL_DISPUTE_TIME_DESC;
	// 订单-纠纷
	public String FAIL_DEAL_DISPUTE_DESC;
	// 订单-已有纠纷记录
	public String FAIL_DEAL_DISPUTE_EXIST_DESC;
	// 金额
	public String TRANSACTION_AMOUNT;
	// 手续费
	public String POUNDAGE;
	// 成功绑定支付包
	public String BIND_APAY_SUCCESS;
	// 成功绑定支付包
	public String BIND_APAY_FAIL;
	// 成功绑定微信
	public String BIND_WECHAT_SUCCESS;
	// 成功绑定微信
	public String BIND_WECHAT_FAIL;
	// 成功绑定银行卡
	public String BIND_BANK_SUCCESS;
	// 成功绑定定银行卡
	public String BIND_BANK_FAIL;
	// 用户地址不存在
	public String USER_ADDRESS_FAIL;
	// 用户不存在
	public String USER_EXIST_FAIL;
	// 用户存在
	public String USER_EXIST_SUCCESS;
	// 必填参数不可为空
	public String PRIMARY_PARAMS_NOT_NULL;
	// 可用资产不足
	public String NOT_ENOUGH_ENABLE_BALANCE;
	// 判断交易数量不可小于最小限额,交易数量不可大于最大限额
	public String FAIL_TRADE_MAXMIN_DATA_FORMAT_DESC;
	// 广告可被匹配量不足
	public String NOT_ENOUGH_NUM_TO_MATCH;
	// 名称不能为空
	public String NAME_NOT_NULL;
	// 开户行不能为空
	public String BANK_NOT_NULL;
	// 号码不能为空
	public String CODE_NOT_NULL;
	// 地址不能为空
	public String ADDRESS_NOT_NULL;
	// 记录已存在
	public String BANK_EXISTEST_DESC;
	public String PHONE_REGEX_WRONG;
	public String SMSSEND_SUCCESS;
	public String VERSION_UPDATE_TIP;
	public String ILLEGAL_REQUEST;
	public String NOT_ENOUGH_ETH_ENABLE_BALANCE;
	public String CHINESE_NAME_WRONG;
	public String ID_CARD_NUMBER_WRONG;
	public String ID_PIC_INVALID;
	public String UPLOAD_SUCCESSD;
	public String AUTH_FAIL;
	public String ID_CARD_BINDTOOMUCH;
	public String TRANSFER_SUSPENDED;
	public String NOT_ANY_COMMUNITY;

	public static class ResponseParamsCNDto extends ResponseParamsDto {
		{
			C2C_TRADE_TIME_ERROR="请您在规定的时间段内进行交易！";
            C2C_DISPUTE_TYPE_ERROR="申诉类型错误,请重新选择";
			TRADE_COIN_SUSPENDED="您的此币种被限制转账";
			USDT_FORMAT_ERROR="格式错误，您必须绑定1或3开头的USDT(OMNI)地址！";
			C2C_TRADE_NUM_LIMIT="交易数量不能小于100";
			NUM_ENTRUST_LIMIT="您存在进行中的广告，请完成后重试！";
			CUSTOMER_NUM_ENTRUST_LIMIT="您存在委托中的广告，请完成后重试！";
			CUSTOMER_NUM_MATCH_LIMIT="存在三条进行中的订单，请完成交易后重试！";
			NUM_MATCH_LIMIT="该订单存在进行中的订单，请稍后重试！";
			NOT_YET_OPNE="暂未开放";
			SMS_REQUEST_FAIL="一小时内,手机号请求不能超过三次";
			FAIL_IMTOKEN_NULL_DESC="imToken不能为空";
			MEMO_SEQUENTIAL_ERROR="助记词顺序错误";
			FAIL_USERNAME_DATA_FORMAT_DESC="用户名格式不正确";
            MAIN_BALANCE_INSUFFICIENT_DESC="主币余额不足";
			QUICK_ACCESS_FAIL="访问过快，请稍后再试";
			CONTRAST_FAIL="对比失败";
			INVITE_USER_IS_NOT_EXIST="推荐用户不存在";

			SUCCESS = "成功";
			FAIL = "失败";
			PARAMS_NULL_DESC = "参数不能为空";
			ID_NULL_DESC = "ID不能为空";
			TOKEN_NULL_DESC = "token不能为空";
			CODE_NULL_DESC = "验证码不能为空";
			CODE_FAIL_DESC = "验证码错误";
			STATE_NULL_DESC = "状态码不能为空";
			TYPE_NULL_DESC = "类型不能为空";
			DATE_NULL_DESC = "时间不能为空";
			ACCOUNT_EXIST_DESC = "账号已存在";
			ACCOUNT_NULL_DESC = "账号不存在";
			DECIMAL_6_FAIL = "超过6位，提示格式错误！";
			USERPWD_NULL_DESC = "密码不能为空";
			USERPWD_FAIL_DESC = "密码错误";
			SEND_SELLER_NITY = "已发送过提醒短信给卖方";
			DEAL_PWD_SETTING_NULL = "未设置交易密码，请前往设置";
			NEW_USERPWD_NULL_DESC = "新密码不能为空";
			OLD_USERPWD_NULL_DESC = "旧密码不能为空";
			NEW_OLD_FAIL_DESC = "新旧密码不能一致";
			USER_EXIST_FAIL = "用户不存在";
			DEALPWD_NOT_SET_DESC = "该账号未设置交易密码";
			DEALPWD_NULL_DESC = "交易密码不能为空";
			DEALPWD_FAIL_DESC = "交易密码错误";
			DEALPWD_EXIST_DESC = "交易密码已存在";
			USERNAME_NULL_DESC = "用户账号不能为空";
			USER_EXIST_SUCCESS = "用户存在";
			NICKNAME_NULL_DESC = "用户昵称不能为空";
			ADDRESS_SAME_DESC = "转入转出地址不能一致";
			FEE_NULL_DESC = "手续费不能为空";
			FEE_FAIL_DESC = "手续费异常";
			REMARK_NULL_DESC = "详情不能为空";
			CONTACT_NULL_DESC = "联系方式不能为空";
			IN_ADDRESS_NULL_DESC = "转入地址不能为空";
			IN_ADDRESS_FAIL_DESC = "转入地址异常或不存在";
			OUT_ADDRESS_NULL_DESC = "转出地址不能为空";
			OUT_ADDRESS_FAIL_DESC = "转出地址异常或不存在";
			PRICE_NUM_NULL_DESC = "金额或数量不能为空";
			PRICE_NUM_FAIL_DESC = "金额或数量格式异常";
			COIN_NULL_DESC = "货币编号不能为空";
			COIN_FAIL_DESC = "货币详情异常";
			CHANGE_PRICE_ERRO_DESC = "金额不合法！";
			CHANGE_STATE_OFF_DESC = "该货币不允许转换成其他货币！";
			CHANGE_TARGET_OFF_DESC = "目标货币暂不允许转换";
			CHANGE_MAX_LIMIT_DESC = "超过限额，当前剩余可转换额度为";
			BALANCE_FAIL_DESC = "用户资产异常";
			BALANCE_INSUFFICIENT_DESC = "用户资金不足";
			BALANCE_INSUFFICIENT_API_DESC = "用户钱包资金不足";
			COIN_MATCH_FAIL_DESC = "转出与转入货币匹配异常";
			TRANSFER_TYPE_DESC = "MOC|MOCCNY:暂不支持向外转账";
			TRANSFER_GAP_DESC1 = "不能在";
			TRANSFER_GAP_DESC2 = "分钟内连续转账";
			PAGEORLINE_NULL_DESC = "行数或页数不能为空";
			GET_MEMO_SUCCESS = "获取成功";
			GET_MEMO_FAIL = "获取失败";
			IMPORT_ADDRESS_LESS = "缺少参数";
			IMPORT_ADDRESS_SUCCESS = "导入成功";
			IMPORT_ADDRESS_FAIL = "导入失败";
			LACK_PARAMETERS = "缺少参数";
			EXPORT_ADDRESS_SUCCESS = "备份成功";
			EXPORT_ADDRESS_FAIL = "备份失败";
			EXPORT_FAIL_NULL_ADDRESS = "钱包地址不存在，请先创建";
			IMPORT_ADDRESS_HAS = "用户已存在钱包地址";
			CHANGE_COIN_BAN = "不允许转换成该类货币";
			CHANGE_NUM_LOW = "余额不足";
			ADDRESS_OFF = "该币种通道已关闭";
			WALLET_ADD_ERROR = "添加失败";
			WALLET_ADD_SUCCESS = "添加成功";
			EXPORT_MEMO_REPEAT = "助记词重复，请重新输入";
			EXPORT_ADDRESS_REPEAT = "该地址已备份，无需重复备份";
			BALANCE_LOCKFAIL_DESC = "转账异常";
			CHANGE_DETAIL_IS_NULL = "转换详情不存在或已被删除";
			CHANGE_IN_WALLET_STATE_OFF = "你已关闭目标币种钱包";
			CHANGE_OUT_WALLET_STATE_OFF = "你已关闭转换前币种钱包";
			PUBKEY_ISFALSE = "地址错误或者公钥错误！";
			UPLOAD_FAIL = "上传文件失败";
			CENTER_ADDRESSFAIL = "账户地址异常";
			FAIL_TRADE_USER_DESC = "用户账号或密码异常，失败!";
			FAIL_TRADE_BALANCE_DESC = "用户资产异常，失败!";
			FAIL_TRADE_INSUFFICIENT_DESC = "资产余额不足，失败!";
			FAIL_TRADE_PASSWORD_DESC = "交易密码错误，失败！";
			FAIL_TRADE_ADDTRUST_STATE_DESC = "币币交易委托，失败！";
			SUCCESS_TRADE_ADDTRUST_STATE_DESC = "币币交易委托，成功。";
			FAIL_SPOT_ADDTRUST_STATE_DESC = "广告发布，失败！";
			SUCCESS_SPOT_ADDTRUST_STATE_DESC = "广告发布，成功。";
			SUCCESS_SPOT_TRADE_STATE_DESC = "广告匹配成功，等待确认。";
			FAIL_SPOT_TRADE_STATE_DESC = "抱歉，广告匹配失败!";
			FAIL_SPOT_CANCEL_STATE_DESC = "抱歉，广告订单取消失败！";
			SUCCESS_SPOT_CANCEL_STATE_DESC = "广告订单取消成功。";
			FAIL_SPOT_USER_STATE_DESC = "抱歉，个人广告不能匹配！";
			SUCCESS_SPOT_MATCH_STATE_DESC = "广告匹配成功。";
			FAIL_SPOT_MATCH_STATE_DESC = "广告匹配失败!";
			FAIL_COIN_TRAN_TYPE_STATE_DESC = "抱歉，交易货币与兑换货币类型状态异常！";
			FAIL_COIN_TYPE_STATE_DESC = "抱歉，该货币不可交易！";
			FAIL_TRADE_DATA_FORMAT_DESC = "抱歉，交易数量须大于0.0001，且不得超过4位小数！";
			FAIL_TRADE_PRICE_DATA_FORMAT_DESC = "抱歉，交易价格须大于0.0001，且不得超过小数点后4位";
			FAIL_TRADE_MIN_DATA_FORMAT_DESC = "抱歉，最小限额不得大于4位小数且不得大于委托量";
			FAIL_TRADE_RANGE_DATA_FORMAT_DESC = "抱歉，溢价不能大于100%!";
			FAIL_TRADE_RANGE_DATA_DESC = "抱歉，价格超过了该广告的溢价范围！";
			FAIL_TRADE_BASE64_DESC = "抱歉，密码解密失败，请联系管理人员！";
			SUCCESS_TRADE_DELENTRUST_DESC = "币币交易委托，撤销成功。";
			SUCCESS_SPOT_DELENTRUST_DESC = "广告交易委托，撤销成功";
			FAIL_MATCH_FIND_DESC = "该记录不存在，确认失败！";
			SUCCESS_MATCH_CONFIRM_DESC = "确认成功";
			FAIL_MATCH_CONFIRM_DESC = "C2C-记录，确认失败，请检查数据！";
			FAIL_TRADE_DELENTRUST_DESC = "该广告不存在，撤销失败！";
			FAIL_TRADE_DELENTRUST_STATE_DESC = "该广告不能撤销，撤销失败！";
			FAIL_TRADE_DELENTRUST_ADMIN_DESC = "验证私钥错误，撤销失败！";
			FAIL_REPEAT_STATE_DESC = "请求正在处理中，请勿重复提交！";
			FAIL_PUSH_REPEAT_DATA_DESC = "该货币对应价格提醒已有数据，新增失败！";
			FAIL_WEIXIN_NULL_DESC = "微信号不能为空";
			FAIL_WEIXIN_DATA_DESC = "暂未绑定微信，请前往个人中心！";
			FAIL_ALIPAY_NULL_DESC = "支付宝号不能为空";
			FAIL_ALIPAY_DATA_DESC = "暂未绑定支付宝，请前往个人中心！";
			FAIL_SPOT_NO_BANK_DESC = "银行卡号不可为空";
			FAIL_CARD_DATA_DESC = "暂未绑定银行卡，请前往个人中心！";
			FAIL_IDENTITY_DATA_DESC = "暂未实名认证，请前往个人中心！";
			FAIL_PHONE_DATA_DESC = "抱歉，手机号码不能为空！";
			FAIL_DISPUTE_TIME_DESC = "申请需在创建订单1小时后才可提交！";
			FAIL_DEAL_DISPUTE_DESC = "抱歉，该订单处于纠纷状态！";
			FAIL_DEAL_DISPUTE_EXIST_DESC = "抱歉，该订单已存在纠纷记录，如有疑问，联系管理人员!";
			TRANSACTION_AMOUNT = "交易金额";
			POUNDAGE = "手续费";
			BIND_APAY_SUCCESS = "绑定支付宝成功";
			BIND_APAY_FAIL = "绑定支付宝失败";
			BIND_WECHAT_SUCCESS = "绑定微信成功";
			BIND_WECHAT_FAIL = "绑定微信失败";
			BIND_BANK_SUCCESS = "绑定银行卡成功";
			BIND_BANK_FAIL = "绑定银行卡失败";
			FAIL_PAY_MINER_DESC = "卖方可用资产不够支付矿工费";
			USER_ADDRESS_FAIL = "地址不存在";
			NO_TRADE_PASSWORD_DESC = "支付密码未设置，请前往个人中心进行设置";
			PRIMARY_PARAMS_NOT_NULL = "必填项不可为空";
			NOT_ENOUGH_ENABLE_BALANCE = "抱歉,可用资产不足";
			FAIL_TRADE_MAXMIN_DATA_FORMAT_DESC = "交易数量不可小于最小限额和交易数量不可大于最大限额";
			NOT_ENOUGH_NUM_TO_MATCH = "可被匹配数量不足，请调整交易量";
			UNFEEZED_FAIL = "冻结用户资产失败";
			NAME_NOT_NULL = "名称不能为空";
			BANK_NOT_NULL = "开户行不能为空";
			CODE_NOT_NULL = "号码不能为空";
			ADDRESS_NOT_NULL = "地址不能为空";
			BANK_EXISTEST_DESC = "已有该银行卡账号";
			PHONE_REGEX_WRONG = "手机号码不存在";
			SMSSEND_SUCCESS = "短信发送成功";
			VERSION_UPDATE_TIP ="请更新到最新版本";
			ILLEGAL_REQUEST = "非法请求";
			NOT_ENOUGH_ETH_ENABLE_BALANCE = "ETH可用资产不足";
			CHINESE_NAME_WRONG = "姓名应为2-6个汉字";
			ID_CARD_NUMBER_WRONG = "身份证号错误";
			ID_PIC_INVALID = "请上传jpg，png格式的图片";
			UPLOAD_SUCCESSD = "上传成功，请等待审核";
			AUTH_FAIL = "实名认证未通过";
			ID_CARD_BINDTOOMUCH = "身份证关联账户过多";
			TRANSFER_SUSPENDED = "转账功能暂时关闭";
		}
	}

	public static class ResponseParamsENDto extends ResponseParamsDto {
		{
			C2C_TRADE_TIME_ERROR="Please conduct the transaction within the specified time period.";
            C2C_DISPUTE_TYPE_ERROR="The appeal type is wrong, please re-select";
			TRADE_COIN_SUSPENDED="Your currency is restricted from transferring money";
			USDT_FORMAT_ERROR="The format is wrong, you must bind the USDT (OMNI) address starting with 1 or 3.";
			C2C_TRADE_NUM_LIMIT="The number of transactions cannot be less than 100";
			NUM_ENTRUST_LIMIT="You have an ongoing ad, please try again after you finish!";
			CUSTOMER_NUM_ENTRUST_LIMIT="You have an ad in the commission, please try again after completing!";
			CUSTOMER_NUM_MATCH_LIMIT="There are three in-progress orders, please try again after completing the transaction!";
			NUM_MATCH_LIMIT="There are orders in progress for this order, please try again later!";
			NOT_YET_OPNE="Not yet open!!";
			SMS_REQUEST_FAIL="Within one hour, the phone number request cannot exceed three times.";
			FAIL_IMTOKEN_NULL_DESC="ImToken cannot be empty";
			MEMO_SEQUENTIAL_ERROR=" memo sequential error";
			FAIL_USERNAME_DATA_FORMAT_DESC="Username format is incorrect";
            MAIN_BALANCE_INSUFFICIENT_DESC="Main Coin Insufficient balance";
			QUICK_ACCESS_FAIL="access too fast, please try again later";
			CONTRAST_FAIL="Contrast failure";
			INVITE_USER_IS_NOT_EXIST="Recommended user does not exist";

			SUCCESS = "SUCCESS";
			FAIL = "FAIL";
			PARAMS_NULL_DESC = "Parameter can not be empty";
			ID_NULL_DESC = "Id can not be empty";
			TOKEN_NULL_DESC = "Token can not be empty";
			CODE_NULL_DESC = "verification code must be filled";
			SEND_SELLER_NITY = "A reminder message has been sent to the seller";
			CODE_FAIL_DESC = "Verification code error";
			STATE_NULL_DESC = "Status code can not be empty";
			TYPE_NULL_DESC = "Type can not be empty";
			DATE_NULL_DESC = "Time can not be empty";
			ACCOUNT_EXIST_DESC = "Account already exists";
			ACCOUNT_NULL_DESC = "Account does not exist";
			DEAL_PWD_SETTING_NULL = "No transaction password is set, please go to setup";
			USERPWD_NULL_DESC = "password can not be blank";
			USERPWD_FAIL_DESC = "wrong password";
			NEW_USERPWD_NULL_DESC = "New password can not be blank";
			OLD_USERPWD_NULL_DESC = "Old password can not be blank";
			DECIMAL_6_FAIL = "More than 6, prompting the format error!";
			NEW_OLD_FAIL_DESC = "Old and new passwords can not be the same";
			DEALPWD_NOT_SET_DESC = "The account does not set the transaction password";
			DEALPWD_NULL_DESC = "Trading password can not be empty";
			DEALPWD_FAIL_DESC = "Trading password is wrong";
			DEALPWD_EXIST_DESC = "Transaction password already exists";
			USERNAME_NULL_DESC = "User account can not be empty";
			USERPWD_NULL_DESC = "User password can not be empty";
			NICKNAME_NULL_DESC = "User nickname can not be empty";
			FEE_NULL_DESC = "Fee can not be empty";
			USER_EXIST_FAIL = "user does not exist";
			FEE_FAIL_DESC = "Fee abnormalities";
			USER_EXIST_SUCCESS = "User existence";
			REMARK_NULL_DESC = "Details can not be empty";
			ADDRESS_SAME_DESC = "Transfer out of the address can not be the same";
			CONTACT_NULL_DESC = "Contact information can not be empty";
			IN_ADDRESS_NULL_DESC = "Transfer to the address can not be empty";
			IN_ADDRESS_FAIL_DESC = "Transfer to the address is abnormal or does not exist";
			OUT_ADDRESS_NULL_DESC = "Out of address can not be empty";
			OUT_ADDRESS_FAIL_DESC = "Out of address abnormal or does not exist";
			PRICE_NUM_NULL_DESC = "The amount or quantity can not be empty";
			PRICE_NUM_FAIL_DESC = "The amount or quantity format is abnormal";
			COIN_NULL_DESC = "Currency number can not be empty";
			COIN_FAIL_DESC = "Abnormal currency details";
			CHANGE_PRICE_ERRO_DESC = "Error amount";
			CHANGE_STATE_OFF_DESC = "The currency is not allowed to be converted into other currencies！";
			CHANGE_TARGET_OFF_DESC = "The target currency is temporarily not allowed to convert！";
			CHANGE_MAX_LIMIT_DESC = "Exceed the limit，the current remainder remainder amount is:";
			BALANCE_FAIL_DESC = "User asset is abnormal";
			BALANCE_INSUFFICIENT_DESC = "User funds are not enough";
			BALANCE_INSUFFICIENT_API_DESC = "Lack of money for the user's wallet";
			COIN_MATCH_FAIL_DESC = "Out and into currency mismatch";
			TRANSFER_TYPE_DESC = "MOC|MOCCNY:does not support outward transfer";
			TRANSFER_GAP_DESC1 = "Can not be within";
			TRANSFER_GAP_DESC2 = "minutes continuous transfer";
			PAGEORLINE_NULL_DESC = "The number of pages or rows can not be empty";
			GET_MEMO_SUCCESS = "Gain success";
			GET_MEMO_FAIL = "Gain failure";
			IMPORT_ADDRESS_LESS = "Lack of parameters";
			IMPORT_ADDRESS_SUCCESS = "Import Sussess";
			IMPORT_ADDRESS_FAIL = "Import Fail";
			LACK_PARAMETERS = "Lack of parameters";
			EXPORT_ADDRESS_SUCCESS = "Backup success";
			EXPORT_ADDRESS_FAIL = "Backup failure";
			EXPORT_FAIL_NULL_ADDRESS = "The wallet address does not exist, please create it first";
			IMPORT_ADDRESS_HAS = "The user has a wallet address";
			CHANGE_COIN_BAN = "It is forbidden to convert into this kind of currency";
			CHANGE_NUM_LOW = "Insufficient account balance";
			ADDRESS_OFF = "channel closing";
			WALLET_ADD_ERROR = "add error";
			WALLET_ADD_SUCCESS = "add success";
			EXPORT_MEMO_REPEAT = "Repeat the help word, please reenter it";
			EXPORT_ADDRESS_REPEAT = "This address has been backed up without repeated backup";
			BALANCE_LOCKFAIL_DESC = "Abnormality of transfer";
			CHANGE_DETAIL_IS_NULL = "conversion records do not exist or have been deleted";
			CHANGE_IN_WALLET_STATE_OFF = "you have closed the purse of the target currency";
			CHANGE_OUT_WALLET_STATE_OFF = "you have shut down the currency Purse";
			PUBKEY_ISFALSE = "Address error or public key error";
			UPLOAD_FAIL = "Upload file failure";
			CENTER_ADDRESSFAIL = "Account address abnormality";
			FAIL_TRADE_USER_DESC = "Abnormal user account or password, failure!";
			FAIL_TRADE_BALANCE_DESC = "Abnormal user assets, failure!";
			FAIL_TRADE_INSUFFICIENT_DESC = "Insufficient balance of assets, failure!";
			FAIL_TRADE_PASSWORD_DESC = "Trading password wrong, failed!";
			FAIL_TRADE_ADDTRUST_STATE_DESC = "Currency transaction commission, failure!";
			SUCCESS_TRADE_ADDTRUST_STATE_DESC = "Currency transaction commission, success.";
			FAIL_SPOT_ADDTRUST_STATE_DESC = "Advertising, failed!";
			SUCCESS_SPOT_ADDTRUST_STATE_DESC = "Advertising, success.";
			SUCCESS_SPOT_TRADE_STATE_DESC = "Ads match, waiting for confirmation.";
			FAIL_SPOT_TRADE_STATE_DESC = "Sorry, ad matching failed!";
			FAIL_SPOT_CANCEL_STATE_DESC = "Sorry, ad order cancellation failed!";
			SUCCESS_SPOT_CANCEL_STATE_DESC = "Ad order canceled successfully.";
			FAIL_SPOT_USER_STATE_DESC = "Sorry, personal ads can not match!";
			SUCCESS_SPOT_MATCH_STATE_DESC = "Ads match successfully.";
			FAIL_SPOT_MATCH_STATE_DESC = "Ad failed to match!";
			FAIL_COIN_TRAN_TYPE_STATE_DESC = "Sorry, the transaction currency and currency exchange currency status is abnormal!";
			FAIL_COIN_TYPE_STATE_DESC = "Sorry, the currency can't be traded!";
			FAIL_TRADE_DATA_FORMAT_DESC = "Sorry, the number of transactions must be more than 0.0001, and no more than 4 decimal numbers";
			FAIL_TRADE_PRICE_DATA_FORMAT_DESC = "Sorry, the transaction price must be more than 0.0001, and not more than 4 decimal";
			FAIL_TRADE_MIN_DATA_FORMAT_DESC = "Sorry, the minimum limit should not be greater than 4 decimal and not greater than the Commission";
			FAIL_TRADE_RANGE_DATA_FORMAT_DESC = "Sorry, the premium can not be greater than 100%!";
			FAIL_TRADE_RANGE_DATA_DESC = "Sorry, the price is out of the premium range for this ad!";
			FAIL_TRADE_BASE64_DESC = "Sorry, password decryption failed, please contact management!";
			SUCCESS_TRADE_DELENTRUST_DESC = "Currency transaction commission, the revocation of success.";
			SUCCESS_SPOT_DELENTRUST_DESC = "Advertising transactions commissioned, revoked successfully.";
			FAIL_MATCH_FIND_DESC = "The record does not exist, confirm the failure!";
			SUCCESS_MATCH_CONFIRM_DESC = "Record, confirm success.";
			FAIL_MATCH_CONFIRM_DESC = "C2C-Record, confirm the failure, please check the data!";
			FAIL_TRADE_DELENTRUST_DESC = "The commission does not exist, revocation failed!";
			FAIL_TRADE_DELENTRUST_STATE_DESC = "The commission can not be revoked, revoked failed!";
			FAIL_TRADE_DELENTRUST_ADMIN_DESC = "Verify private key error, revocation failed!";
			FAIL_REPEAT_STATE_DESC = "Request is being processed, please do not repeat the submission!";
			FAIL_PUSH_REPEAT_DATA_DESC = "The currency corresponding to the price reminder has data, the new failed!";
			FAIL_WEIXIN_NULL_DESC = "WeChat account cannot be empty";
			FAIL_WEIXIN_DATA_DESC = "Not yet bound WeChat, please go to the personal center！";
			FAIL_ALIPAY_NULL_DESC = "Alipay account can not be empty";
			FAIL_ALIPAY_DATA_DESC = "Not yet bound Alipay, go to the personal center！";
			FAIL_SPOT_NO_BANK_DESC = "Bank card number can not be empty";
			FAIL_CARD_DATA_DESC = "Not yet bound bank card, go to the personal center！";
			FAIL_IDENTITY_DATA_DESC = "Not yet real-name certification, please go to the personal center!";
			FAIL_PHONE_DATA_DESC = "Sorry, the phone number can not be empty!";
			FAIL_DISPUTE_TIME_DESC = "Application need to be created within 1 hour before submission!";
			FAIL_DEAL_DISPUTE_DESC = "Sorry, the order is in dispute!";
			FAIL_DEAL_DISPUTE_EXIST_DESC = "Sorry, the order already exists dispute records, if in doubt, contact management!";
			TRANSACTION_AMOUNT = "transaction amount";
			POUNDAGE = "poundage";
			BIND_APAY_SUCCESS = "The binding of Alipay";
			BIND_APAY_FAIL = "Alipay failed to bind";
			BIND_WECHAT_SUCCESS = "Binding WeChat success";
			BIND_WECHAT_FAIL = "Bindings of WeChat failure";
			BIND_BANK_SUCCESS = "Bind bank card success";
			BIND_BANK_FAIL = "Bindings of bank cards failed";
			FAIL_PAY_MINER_DESC = "Seller  dose not have enough assets to pay miner's fee";
			USER_ADDRESS_FAIL = "The address does not exist";
			NO_TRADE_PASSWORD_DESC = "The payment password is not set, please go to the personal center to set up";
			PRIMARY_PARAMS_NOT_NULL = "primary params is null";
			NOT_ENOUGH_ENABLE_BALANCE = "Sorry, the assets are not available";
			FAIL_TRADE_MAXMIN_DATA_FORMAT_DESC = "The number of transactions must not be less than the minimum limit and the number of transactions cannot be larger than the maximum limit";
			NOT_ENOUGH_NUM_TO_MATCH = "Can be matched inadequately, please adjust the volume of transactions";
			UNFEEZED_FAIL = "Failure to freeze user assets";
			NAME_NOT_NULL = "Name can not be empty";
			BANK_NOT_NULL = "Open bank can not be empty";
			CODE_NOT_NULL = "The number can not be empty";
			ADDRESS_NOT_NULL = "The address can not be empty";
			BANK_EXISTEST_DESC = "There is a bank card account";
			PHONE_REGEX_WRONG = "mobile does not exists";
			SMSSEND_SUCCESS = "message sent successfully";
			VERSION_UPDATE_TIP = "please update to the latest version";
			ILLEGAL_REQUEST = "illegal request";
			NOT_ENOUGH_ETH_ENABLE_BALANCE = "Sorry, the ETH assets not enough";
			CHINESE_NAME_WRONG = "please fill in 2 to 6 characters";
			ID_CARD_NUMBER_WRONG = "ID error";
			ID_PIC_INVALID = "please upload a JPG, PNG format images";
			UPLOAD_SUCCESSD = "uploaded successfully, please waiting for audit";
			AUTH_FAIL = "real-name authentication failed";
			ID_CARD_BINDTOOMUCH = "ID associated account too much";
			TRANSFER_SUSPENDED = "temporarily closed transfer function\n";
		}
	}

	public static class ResponseParamsKRDto extends ResponseParamsDto {
		{
			C2C_TRADE_TIME_ERROR="지정된 기간 내에 거래를 수행하십시오";
            C2C_DISPUTE_TYPE_ERROR="이의 신청 유형이 잘못되었습니다. 다시 선택하십시오.";
			TRADE_COIN_SUSPENDED="통화가 송금되지 않습니다.";
			USDT_FORMAT_ERROR="형식이 잘못되었으므로 1 또는 3으로 시작하는 USDT (OMNI) 주소를 바인딩해야합니다.";
			C2C_TRADE_NUM_LIMIT="거래 수는 100보다 작을 수 없습니다";
			NUM_ENTRUST_LIMIT="진행중인 광고가 있습니다. 완료 후 다시 시도하십시오!";
			CUSTOMER_NUM_ENTRUST_LIMIT="커미션에 광고가 있습니다. 완료 후 다시 시도하십시오!";
			CUSTOMER_NUM_MATCH_LIMIT="3 개의 진행중인 주문이 있습니다. 거래를 완료 한 후 다시 시도하십시오!";
			NUM_MATCH_LIMIT="이 주문에 대한 주문이 진행 중입니다. 나중에 다시 시도하십시오!";
			NOT_YET_OPNE="아직 열지 않았다.";
			SMS_REQUEST_FAIL="1 시간 이내에 전화 번호 요청은 3 번을 초과 할 수 없습니다.";
			FAIL_IMTOKEN_NULL_DESC="ImToken 은 비워 둘 수 없습니다.";
			MEMO_SEQUENTIAL_ERROR="메모 순차 오류";
			FAIL_USERNAME_DATA_FORMAT_DESC="사용자 이름 형식이 잘못되었습니다.";
            MAIN_BALANCE_INSUFFICIENT_DESC="주화 동전 부족.";
			QUICK_ACCESS_FAIL="액세스가 너무 빠름. 나중에 다시 시도하십시오.";
            CONTRAST_FAIL="대조 실패";
			INVITE_USER_IS_NOT_EXIST="추천 사용자가 존재하지 않습니다.";

			SUCCESS = "성공";
			FAIL = "실패";
			PARAMS_NULL_DESC = "수자입력";
			ID_NULL_DESC = "ID입력";
			TOKEN_NULL_DESC = "token입력";
			CODE_NULL_DESC = "인증번호입력";
			CODE_FAIL_DESC = "인증번호틀림";
			STATE_NULL_DESC = "상태비번입력";
			TYPE_NULL_DESC = "유형입력";
			DATE_NULL_DESC = "미가입력";
			ACCOUNT_EXIST_DESC = "아이디존재함";
			ACCOUNT_NULL_DESC = "아이디존재하지않음";
			DECIMAL_6_FAIL = "6자리초과하지 말고 격식틀림";
			USERPWD_NULL_DESC = "비밀번호입력";
			USERPWD_FAIL_DESC = "비밀번호틀림";
			SEND_SELLER_NITY = "판매자에게 메세지보냄";
			DEAL_PWD_SETTING_NULL = "비번미설정,설정에들어감";
			NEW_USERPWD_NULL_DESC = "새로운비번입력";
			OLD_USERPWD_NULL_DESC = "원래비번입력";
			NEW_OLD_FAIL_DESC = "새로운비번과원래비번일치하지않아야됨";
			USER_EXIST_FAIL = "아이디존재하지않음";
			DEALPWD_NOT_SET_DESC = "아이디거래비번설치않았음";
			DEALPWD_NULL_DESC = "거래비번입력";
			DEALPWD_FAIL_DESC = "거래비번틀림";
			DEALPWD_EXIST_DESC = "거래비번존재함";
			USERNAME_NULL_DESC = "아이디입력요망";
			USER_EXIST_SUCCESS = "아이디존재잠";
			NICKNAME_NULL_DESC = "닉네임입력요망";
			ADDRESS_SAME_DESC = "입출금주소일치하지않아야됨";
			FEE_NULL_DESC = "수수료입력";
			FEE_FAIL_DESC = "수수료오류";
			REMARK_NULL_DESC = "상세내용입력";
			CONTACT_NULL_DESC = "연락처입력";
			IN_ADDRESS_NULL_DESC = "입금주소입력";
			IN_ADDRESS_FAIL_DESC = "입금주소오류혹존재하지않음";
			OUT_ADDRESS_NULL_DESC = "출금주소입력";
			OUT_ADDRESS_FAIL_DESC = "입금주소오류혹존재하지않음";
			PRICE_NUM_NULL_DESC = "금액혹수량입력";
			PRICE_NUM_FAIL_DESC = "금액혹수량격식오류";
			COIN_NULL_DESC = "코인번호입력";
			COIN_FAIL_DESC = "코인상세내용오류";
			CHANGE_PRICE_ERRO_DESC = "금액불합법！";
			CHANGE_STATE_OFF_DESC = "본코인은다른코인으로전환하지못함！";
			CHANGE_TARGET_OFF_DESC = "선택코인잠시전환못함";
			CHANGE_MAX_LIMIT_DESC = "금액초과,현재전환금액";
			BALANCE_FAIL_DESC = "재산오류";
			BALANCE_INSUFFICIENT_DESC = "자금부족";
			BALANCE_INSUFFICIENT_API_DESC = "지갑금액부족";
			COIN_MATCH_FAIL_DESC = "입출코인매칭오류";
			TRANSFER_TYPE_DESC = "MOC|MOCCNY:잠시출그이전금지";
			TRANSFER_GAP_DESC1 = "안됨";
			TRANSFER_GAP_DESC2 = "분내연속이체";
			PAGEORLINE_NULL_DESC = "페이지비여있음안됨";
			GET_MEMO_SUCCESS = "받기성공";
			GET_MEMO_FAIL = "받기실패";
			IMPORT_ADDRESS_LESS = "수량부족";
			IMPORT_ADDRESS_SUCCESS = "가져오기성공";
			IMPORT_ADDRESS_FAIL = "가져오기실패";
			LACK_PARAMETERS = "수량부족";
			EXPORT_ADDRESS_SUCCESS = "백업성공";
			EXPORT_ADDRESS_FAIL = "백업실패";
			EXPORT_FAIL_NULL_ADDRESS = "지갑주소존재하지않음 새로만듬";
			IMPORT_ADDRESS_HAS = "이미존재하는지갑주소";
			CHANGE_COIN_BAN = "코인종류이전안됨";
			CHANGE_NUM_LOW = "잔액부족";
			ADDRESS_OFF = "코인거래닫힘";
			WALLET_ADD_ERROR = "추가실패";
			WALLET_ADD_SUCCESS = "추가성공";
			EXPORT_MEMO_REPEAT = "니모닉중복,새로입력";
			EXPORT_ADDRESS_REPEAT = "주소이미저장됨";
			BALANCE_LOCKFAIL_DESC = "전환오류";
			CHANGE_DETAIL_IS_NULL = "전환상세존재하지않음혹삭제";
			CHANGE_IN_WALLET_STATE_OFF = "코인종류닫힘";
			CHANGE_OUT_WALLET_STATE_OFF = "코인지갑종류닫힘";
			PUBKEY_ISFALSE = "주소오류혹publickey오류！";
			UPLOAD_FAIL = "업그레드실패";
			CENTER_ADDRESSFAIL = "주소오류";
			FAIL_TRADE_USER_DESC = "아이디혹비번오류 실패!";
			FAIL_TRADE_BALANCE_DESC = "재산오류 실패!";
			FAIL_TRADE_INSUFFICIENT_DESC = "재산잔애부족 실패!";
			FAIL_TRADE_PASSWORD_DESC = "거래비번틀림 실패！";
			FAIL_TRADE_ADDTRUST_STATE_DESC = "코인위탁거래 실패！";
			SUCCESS_TRADE_ADDTRUST_STATE_DESC = "코인위탁거래 성공。";
			FAIL_SPOT_ADDTRUST_STATE_DESC = "광고발표실패！";
			SUCCESS_SPOT_ADDTRUST_STATE_DESC = "광고발표성공。";
			SUCCESS_SPOT_TRADE_STATE_DESC = "광고매칭성공 확인중。";
			FAIL_SPOT_TRADE_STATE_DESC = "미안 광고매칭실패!";
			FAIL_SPOT_CANCEL_STATE_DESC = "미안 광고주무취소실패！";
			SUCCESS_SPOT_CANCEL_STATE_DESC = "광고주문취소성공됨。";
			FAIL_SPOT_USER_STATE_DESC = "미안,개인광고매칭못함！";
			SUCCESS_SPOT_MATCH_STATE_DESC = "광고매칭성공。";
			FAIL_SPOT_MATCH_STATE_DESC = "광고매칭실패!";
			FAIL_COIN_TRAN_TYPE_STATE_DESC = "미안 거래코인및코인전환종류상황 오류！";
			FAIL_COIN_TYPE_STATE_DESC = "미안 본코인거래안됨！";
			FAIL_TRADE_DATA_FORMAT_DESC = "미안 ,거래수래수량0.0001이상,그리고4자리수자이하안됨";
			FAIL_TRADE_PRICE_DATA_FORMAT_DESC = "미안,거래금애0.0001이상,그리고4자리수자이하안됨";
			FAIL_TRADE_MIN_DATA_FORMAT_DESC = "미안,최소4자리수자이고 위탁수량초과못함";
			FAIL_TRADE_RANGE_DATA_FORMAT_DESC = "미안,100%액면초과안됨!";
			FAIL_TRADE_RANGE_DATA_DESC = "미안 ,광고액면가격  초과됨！";
			FAIL_TRADE_BASE64_DESC = "미안,비번해제실패,관리원과연락요망！";
			SUCCESS_TRADE_DELENTRUST_DESC = "코인위탁거래 취소성공。";
			SUCCESS_SPOT_DELENTRUST_DESC = "광고위탁거래 취소성공";
			FAIL_MATCH_FIND_DESC = "기록존재하지않음 확인실패！";
			SUCCESS_MATCH_CONFIRM_DESC = "확인성공";
			FAIL_MATCH_CONFIRM_DESC = "기록,확인실패,데이터확인！";
			FAIL_TRADE_DELENTRUST_DESC = "광고존재하지않음,취소실패！";
			FAIL_TRADE_DELENTRUST_STATE_DESC = "광고취소불가,취소실패！";
			FAIL_TRADE_DELENTRUST_ADMIN_DESC = "privatekey인증실패 ,취소실패！";
			FAIL_REPEAT_STATE_DESC = "요청처리중,반복하지마십시요！";
			FAIL_PUSH_REPEAT_DATA_DESC = "본코인대응가격기록이미존재함,새로추가실패！";
			FAIL_WEIXIN_NULL_DESC = "위쳇번호입력";
			FAIL_WEIXIN_DATA_DESC = "위쳇바인딩없음 개인설정에들어가세요！";
			FAIL_ALIPAY_NULL_DESC = "Alipay입력하세요";
			FAIL_ALIPAY_DATA_DESC = "Alipay바인딩없음 개인설정에들어가세요！";
			FAIL_SPOT_NO_BANK_DESC = "은행계좌번호입력";
			FAIL_CARD_DATA_DESC = "은행계좌번호바인딩없음 개인설정에들어가세요！";
			FAIL_IDENTITY_DATA_DESC = "실명인증없음 개인설정에들어가세요！";
			FAIL_PHONE_DATA_DESC = "미안  핸드폰번호입력！";
			FAIL_DISPUTE_TIME_DESC = "신청다음1시간후에 자료제출！";
			FAIL_DEAL_DISPUTE_DESC = "미안 본서류처리중！";
			FAIL_DEAL_DISPUTE_EXIST_DESC = "미안 ,본서류이미존재함!";
			TRANSACTION_AMOUNT = "거래금액";
			POUNDAGE = "수수료";
			BIND_APAY_SUCCESS = "Alipay바인드성공";
			BIND_APAY_FAIL = "Alipay바인드실패";
			BIND_WECHAT_SUCCESS = "위쳇바인드성공";
			BIND_WECHAT_FAIL = "위쳇바인드실패";
			BIND_BANK_SUCCESS = "은행카드바인드성공";
			BIND_BANK_FAIL = "은행카드바인드실패";
			FAIL_PAY_MINER_DESC = "판매자재산부족시채굴비로결제가능";
			USER_ADDRESS_FAIL = "주소존재하지않음";
			NO_TRADE_PASSWORD_DESC = "결제비번미설정,개인설정에들어가십시요";
			PRIMARY_PARAMS_NOT_NULL = "비여있음안됨";
			NOT_ENOUGH_ENABLE_BALANCE = "미안,사용재산부족";
			FAIL_TRADE_MAXMIN_DATA_FORMAT_DESC = "거래수량최소금액과최대금액한도있음";
			NOT_ENOUGH_NUM_TO_MATCH = "매칭수량부족,거래수량조절해야함";
			UNFEEZED_FAIL = "재산정지실패";
			NAME_NOT_NULL = "비여있음안됨";
			BANK_NOT_NULL = "계정번호입력";
			CODE_NOT_NULL = "비여있음안됨";
			ADDRESS_NOT_NULL = "주소입력";
			BANK_EXISTEST_DESC = "계좌번호이미존재함";
			PHONE_REGEX_WRONG = "전화번호존재하지않음";
			SMSSEND_SUCCESS = "메세지발송성공";
			VERSION_UPDATE_TIP ="새로운버전업그레드하세요";
			ILLEGAL_REQUEST = "불법인요청";
			NOT_ENOUGH_ETH_ENABLE_BALANCE = "ETH사용잔액부족";
			CHINESE_NAME_WRONG = "성명한문으로2-6자리입력";
			ID_CARD_NUMBER_WRONG = "주민등록증 오류";
			ID_PIC_INVALID = "jpg업그레드,png파일로";
			UPLOAD_SUCCESSD = "업그레드성공 심사중";
			AUTH_FAIL = "실명인증미통과";
			ID_CARD_BINDTOOMUCH = "주민증사용이많음";
			TRANSFER_SUSPENDED = "이체기능잠시닫힘";
		}

	}
}
