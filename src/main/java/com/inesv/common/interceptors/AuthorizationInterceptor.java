package com.inesv.common.interceptors;//package com.inesv.common.interceptors;


import com.inesv.annotation.Login;
import com.inesv.common.exception.RRException;
import com.inesv.mapper.UserMapper;
import com.inesv.model.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限(Token)验证
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserMapper userMapper;

    public static final String USER_KEY = "myuserinfo";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Login annotation;
        if(handler instanceof HandlerMethod) {
            //获取类上面的注解
            annotation = ((HandlerMethod) handler).getMethod().getDeclaringClass().getAnnotation(Login.class);
            if(annotation == null) {
                //获取方法上面的注解
                annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
            }
        }else{
            return true;
        }

        if(annotation == null){
            return true;
        }

        //从header中获取token
        String token = request.getHeader("token");
        //如果header中不存在token，则从参数中获取token
        if(StringUtils.isBlank(token)){
            token = request.getParameter("token");
        }

        //token为空
        if(StringUtils.isBlank(token)){
            throw new RRException("token is not blank", -1);
        }

        //查询token信息
        User user = userMapper.getUserInfoByToken(token);
        if(user == null || user.getTimeout().getTime() > System.currentTimeMillis()) {
            throw new RRException("token error", -1);
        }
        request.setAttribute(USER_KEY, user);
        return true;
    }
}
