package com.xszx.controller;

import com.xszx.common.router.interfaces.Controller;
import com.xszx.common.router.interfaces.RequestMapping;
import com.xszx.common.router.interfaces.PathVariable;
import com.xszx.common.router.interfaces.RequestParam;
import com.xszx.common.router.interfaces.RequestBody;
import com.xszx.common.router.interfaces.ResponseBody;
import com.xszx.common.router.enums.HttpMethod;
import com.xszx.common.result.Result;
import com.xszx.common.result.ResultBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * API控制器示例 - 演示@ResponseBody注解的使用
 */
@Controller
public class ApiController {

    @RequestMapping(path = "/api/users", method = HttpMethod.GET)
    @ResponseBody
    public Result<Map<String, Object>> getUsers(@RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Map<String, Object> users = new HashMap<>();
        users.put("users", new String[] { "张三", "李四", "王五" });
        users.put("page", page);
        users.put("size", size);
        users.put("total", 100);

        return ResultBuilder.success(users);
    }

    @RequestMapping(path = "/api/users/{id}", method = HttpMethod.GET)
    @ResponseBody
    public Result<Map<String, Object>> getUserById(@PathVariable("id") Long id) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("name", "用户" + id);
        user.put("email", "user" + id + "@example.com");

        return ResultBuilder.success(user);
    }

    @RequestMapping(path = "/api/users", method = HttpMethod.POST)
    @ResponseBody
    public Result<Map<String, Object>> createUser(@RequestBody Map<String, Object> userData) {
        // 模拟创建用户
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("id", System.currentTimeMillis()); // 模拟生成ID
        newUser.put("name", userData.get("name"));
        newUser.put("email", userData.get("email"));
        newUser.put("created", new java.util.Date());

        return ResultBuilder.success(newUser);
    }

    @RequestMapping(path = "/api/users/{id}", method = HttpMethod.PUT)
    @ResponseBody
    public Result<Map<String, Object>> updateUser(@PathVariable("id") Long id,
            @RequestBody Map<String, Object> userData) {
        // 模拟更新用户
        Map<String, Object> updatedUser = new HashMap<>();
        updatedUser.put("id", id);
        updatedUser.put("name", userData.get("name"));
        updatedUser.put("email", userData.get("email"));
        updatedUser.put("updated", new java.util.Date());

        return ResultBuilder.success(updatedUser);
    }

    @RequestMapping(path = "/api/users/{id}", method = HttpMethod.DELETE)
    @ResponseBody
    public Result<Map<String, String>> deleteUser(@PathVariable("id") Long id) {
        // 模拟删除操作
        Map<String, String> result = new HashMap<>();
        result.put("message", "用户 " + id + " 已删除");
        result.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return ResultBuilder.success(result);
    }

    @RequestMapping(path = "/api/hello/{name}", method = HttpMethod.GET)
    @ResponseBody
    public Result<Map<String, String>> sayHello(@PathVariable("name") String name,
            @RequestParam(value = "lang", defaultValue = "zh") String lang) {
        Map<String, String> greeting = new HashMap<>();

        String message;
        switch (lang) {
            case "en":
                message = "Hello, " + name + "!";
                break;
            case "zh":
            default:
                message = "你好, " + name + "!";
                break;
        }

        greeting.put("message", message);
        greeting.put("language", lang);
        greeting.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return ResultBuilder.success(greeting);
    }

    @RequestMapping(path = "/api/search", method = HttpMethod.GET)
    @ResponseBody
    public Result<Map<String, Object>> search(@RequestParam("q") String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        Map<String, Object> searchResult = new HashMap<>();
        searchResult.put("query", query);
        searchResult.put("category", category);
        searchResult.put("limit", limit);
        searchResult.put("results", new String[] { "结果1", "结果2", "结果3" });
        searchResult.put("total", 123);

        return ResultBuilder.success(searchResult);
    }

    // 演示非@ResponseBody方法（返回字符串）
    @RequestMapping(path = "/api/simple", method = HttpMethod.GET)
    public String getSimpleMessage() {
        return "<h1>这是一个简单的HTML响应</h1><p>没有使用@ResponseBody注解</p>";
    }

    // 演示返回纯JSON对象
    @RequestMapping(path = "/api/status", method = HttpMethod.GET)
    @ResponseBody
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("server", "智慧消防系统");
        status.put("version", "1.0.0");
        status.put("timestamp", System.currentTimeMillis());
        status.put("status", "running");
        return status;
    }
}
