package com.inesv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Dreyer
 * @date: 2017/2/14 14:02
 * @description:在Controller的方法上加上该注解则表示该方法不需要鉴权，默认需要鉴权
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UnLogin {

}