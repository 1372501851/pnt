package com.inesv.common.exception;

import com.inesv.util.BaseResponse;
import com.inesv.util.LocaleMessageUtils;
import com.inesv.util.RspUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LocaleMessageUtils messageUtils;

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(RRException.class)
    public BaseResponse handleRRException(RRException e){
        int code = e.getCode();
        String message = e.getMsg();
        if(StringUtils.isBlank(message)) {
            message = messageUtils.getMessage(String.valueOf(code));
        }
        if (code == -1) {
            code = -1;
        } else if (code == -2) {
            code = -2;
        } else {
            code = 400;
        }
        return RspUtil.error(message, code);
    }

    /**
     * 未知异常捕获
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse handleException(Exception e){
        logger.error(e.getMessage(), e);
        //发送通知（邮件等）
        return RspUtil.error();
    }


//    @ExceptionHandler(DuplicateKeyException.class)
//    public BaseResponse handleDKException(Exception e) {
//        logger.error(e.getMessage(), e);
//        return R.error("数据已存在");
//    }

}
