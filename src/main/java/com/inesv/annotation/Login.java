package com.inesv.annotation;

import java.lang.annotation.*;

/**
 * 需要登陆token
 */
//@Target(ElementType.METHOD)
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Login {
}
