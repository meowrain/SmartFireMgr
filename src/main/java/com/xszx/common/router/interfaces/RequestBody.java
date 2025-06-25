package com.xszx.common.router.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @RequestBody 的作用是从 HTTP 请求体中读取数据，
 * 并根据数据内容，将其自动转换为 Java 对象（或者基本数据类型），并传递到控制器方法的参数中。
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {
}
