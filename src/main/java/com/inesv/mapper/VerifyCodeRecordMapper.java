package com.inesv.mapper;

import com.inesv.model.VerifyCodeRecord;
import org.apache.ibatis.annotations.Param;


public interface VerifyCodeRecordMapper {
    /**
     * 取最后一条有效验证码
     * @param mobile 手机号码
     * @param validTime 验证码有效时间
     * @return
     */
    VerifyCodeRecord getValidVerifyCode4LastOne(VerifyCodeRecord verifyCodeRecord);

    /**
     * 根据条件查询记录数量
     * @param verifyCodeRecord
     * @return
     */
    Integer getCountByConditions(VerifyCodeRecord verifyCodeRecord);

    /**
     * 保存发送记录
     * @param verifycodeSmsRecord
     * @return
     */
    int add(VerifyCodeRecord verifycodeSmsRecord);

    int update(VerifyCodeRecord verifycodeSmsRecord);
}
