package com.inesv.mapper;

import com.inesv.model.PicVerifyCodeRecord;


public interface PicVerifyCodeRecordMapper {
    /**
     * 取最后一条有效验证码
     * @param mobile 手机号码
     * @param validTime 验证码有效时间
     * @return
     */
    PicVerifyCodeRecord getValidVerifyCode4LastOne(PicVerifyCodeRecord verifyCodeRecord);

    /**
     * 根据条件查询记录数量
     * @param verifyCodeRecord
     * @return
     */
    Integer getCountByConditions(PicVerifyCodeRecord verifyCodeRecord);

    /**
     * 保存发送记录
     * @param verifycodeSmsRecord
     * @return
     */
    int add(PicVerifyCodeRecord verifycodeSmsRecord);

    int update(PicVerifyCodeRecord verifycodeSmsRecord);
}
