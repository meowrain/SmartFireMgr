package com.fire.common.router.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO: 待实现 ，这个方法主要是用来标记一个类为RESTful风格的控制器。
 * 这个注解的作用是标记一个类为RESTful风格的控制器，
 * 通常用于在Web框架中自动识别和处理HTTP请求。
 * 就不用给每个方法添加 @ResponseBody 注解了，
 */
@Target(ElementType.TYPE) // 应用于类
@Retention(RetentionPolicy.RUNTIME)
public @interface RestController {

}
