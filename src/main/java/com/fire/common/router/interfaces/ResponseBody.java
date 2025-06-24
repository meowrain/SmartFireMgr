package com.fire.common.router.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这个注解的作用是标记一个方法的返回值，将它作为响应体返回。
 * 通常用于在Controller方法中，指示返回的对象应该被序列化为JSON或其他格式，
 * 并直接写入HTTP响应的主体部分。
 */
@Target(ElementType.METHOD) // 应用于方法
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBody {

}
