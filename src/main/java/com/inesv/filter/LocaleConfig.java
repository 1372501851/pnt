package com.inesv.filter;

import com.inesv.common.interceptors.MyLocaleInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class LocaleConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private LoginUserHandlerMethodArgumentResolver loginUserHandlerMethodArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 国际化拦截器
        registry.addInterceptor(myLocaleInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginUserHandlerMethodArgumentResolver);
    }

    /**
     * 配置国际化，默认中文
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
//        localeResolver.setDefaultLocale(Locale.US);
        Locale locale = new Locale("zh", "CN");
        localeResolver.setDefaultLocale(locale);
        return localeResolver;
    }

    /**
     * 根据参数上的lang变化请求类型
     * lang=zh_CN 中文
     * lang=en_US 英文
     * lang=ko_KR 韩语
     * @return
     */
    @Bean
    @Deprecated
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }


    /**
     * 语言切换拦截器，根据请求head中的lang判断
     * lang=zh_CN 中文
     * lang=en_US 英文
     * lang=ko_KR 韩语
     * @return
     */
    @Bean
    public MyLocaleInterceptor myLocaleInterceptor() {
        MyLocaleInterceptor myLocale = new MyLocaleInterceptor();
        myLocale.setParamName("lang");
        return myLocale;
    }
}
