package com.fire.common.router.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 应用于参数
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {
    String value();
}
