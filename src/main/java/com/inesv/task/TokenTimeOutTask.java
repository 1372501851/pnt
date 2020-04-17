package com.inesv.task;

import com.inesv.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TokenTimeOutTask {

    private static Logger logger = LoggerFactory.getLogger(TokenTimeOutTask.class);

    @Autowired
    private UserService userService;

    //@Scheduled(cron = "* */30 * * * ?")
    //@Scheduled(cron = "0/5 * * * * ?")
    public void updateUserToken() {
        logger.info("start TokenTimeOutTask work");
        /*List<User> userList = userService.getUserTokenIsNotNull();
        for(User user : userList){
            if(timeCompare(user.getTimeout()) > new Long(2*60*60)){
                user.setTimeout(null);
                user.setToken(null);
                userService.updateUser(user);
                logger.info("token失效");
            }
            logger.info("更新token");
        }
*/
        logger.info("end TokenTimeOutTask work");
    }

    public Long timeCompare(Date date) {
        long between=(new Date().getTime()-date.getTime())/1000;//除以1000是为了转换成秒
        return between;
    }
}
