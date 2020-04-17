package com.inesv.filter;

import com.github.pagehelper.PageHelper;
import com.inesv.common.interceptors.AuthorizationInterceptor;
import com.inesv.common.interceptors.LoginInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.Filter;
import java.util.Properties;


@Configuration
public class CorsConfig extends WebMvcConfigurerAdapter{
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }
    /**
     * 配置过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(systemContextFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("sessionFilter");
        return registration;
    }

    /**
     * 创建一个bean
     * @return
     */
    @Bean(name = "systemContextFilter")
    public Filter systemContextFilter() { 
        return new SystemContextFilter();
    }
    /**
     * 注册 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor());
        registry.addInterceptor(getAuthorizationInterceptor());
    }
    //redis
    /*@Bean
    public RedisConnectionFactory redisConnectionFactory(){
    	return new JedisConnectionFactory();
    }
    
    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate()throws UnknownHostException{
    	RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
    	template.setConnectionFactory(redisConnectionFactory());
    	return template;
    }*/

    // 配置mybatis的分页插件pageHelper
    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "false");  //超过最大页数后是否返回数据
        properties.setProperty("dialect", "mysql"); // 配置mysql数据库的方言
        pageHelper.setProperties(properties);
        return pageHelper;
    }

    @Bean
    public HandlerInterceptorAdapter getAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }
}
