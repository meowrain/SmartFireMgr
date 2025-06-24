package com.fire.common.router.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 它通常在 RESTful API 开发中，用于处理动态变量的路由。
 */
@Target(ElementType.PARAMETER) // 应用于参数
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {
    String value();
}
/**
 * import org.springframework.web.bind.annotation.*;
 *
 * @RestController
 * @RequestMapping("/users")
 * public class UserController {
 *
 *     @GetMapping("/{id}")
 *     public String getUserById(@PathVariable("id") String id) {
 *         return "用户 ID: " + id;
 *     }
 * }
 */