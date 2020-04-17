package com.inesv.common.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Slf4j
public class MyLocaleInterceptor extends HandlerInterceptorAdapter {

    private String paramName = "lang";
    private boolean ignoreInvalidLocale = false;
    private boolean languageTagCompliant = false;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String newLocale = request.getHeader(this.getParamName());
        //如果请求头之中没有，就从请求参数中拿取
        if(newLocale == null) {
            newLocale = request.getParameter(this.getParamName());
        }
        if (StringUtils.isNotBlank(newLocale)) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }
            localeResolver.setLocale(request, response, org.springframework.util.StringUtils.parseLocaleString(newLocale));

            try {
                localeResolver.setLocale(request, response, this.parseLocaleValue(newLocale));
            } catch (IllegalArgumentException e) {
                if (!this.isIgnoreInvalidLocale()) {
                    throw e;
                }
                log.debug("Ignoring invalid locale value [" + newLocale + "]: " + e.getMessage());
            }
        }
        return true;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public boolean isIgnoreInvalidLocale() {
        return ignoreInvalidLocale;
    }

    public void setIgnoreInvalidLocale(boolean ignoreInvalidLocale) {
        this.ignoreInvalidLocale = ignoreInvalidLocale;
    }

    public boolean isLanguageTagCompliant() {
        return languageTagCompliant;
    }

    public void setLanguageTagCompliant(boolean languageTagCompliant) {
        this.languageTagCompliant = languageTagCompliant;
    }

    protected Locale parseLocaleValue(String locale) {
        return this.isLanguageTagCompliant() ? Locale.forLanguageTag(locale) : org.springframework.util.StringUtils.parseLocaleString(locale);
    }
}
