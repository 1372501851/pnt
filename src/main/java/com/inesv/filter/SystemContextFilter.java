package com.inesv.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Dreyer
 * @date 2017/1/13 15:02
 * @description 系统过滤器
 */
public class SystemContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 设置允许跨域调用
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        response.setHeader("Access-Control-Allow-Origin", "*");
        String[] allowDomain = {"http://192.168.10.36"};
        Set<String> allowedOrigins= new HashSet<String>(Arrays.asList(allowDomain));  
        String originHeader=((HttpServletRequest) servletRequest).getHeader("Origin");
        if(allowedOrigins.contains(originHeader)){
        	response.setHeader("Access-Control-Allow-Origin", originHeader);
        	response.setHeader("Access-Control-Allow-Methods", "POST");
        	response.setHeader("Access-Control-Allow-Credentials", "true");
        	response.setHeader("Access-Control-Allow-Headers", "username,token,activityId,activityType");
        	response.setHeader("Access-Control-Expose-Headers", "*");
        }

        // 请求相关信息打印输出
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        StringBuffer url = new StringBuffer("")
                .append(request.getScheme())
                .append(":")
                .append(request.getServerName())
                .append(":")
                .append(request.getServerPort())
                .append(request.getContextPath())
                .append(request.getServletPath());

        Enumeration<String> parameters = request.getParameterNames();
        String element = "";
        StringBuffer params = new StringBuffer("");
        // 用户模块的参数信息不进行打印输出
        while (parameters.hasMoreElements()) {
            element = parameters.nextElement();
            params.append("||key=")
                    .append(element)
                    .append(",value=")
                    .append(request.getParameter(element));
        }

        // 计算请求耗时
        long start = System.currentTimeMillis();
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}