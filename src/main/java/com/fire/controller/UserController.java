package com.fire.controller;

import com.fire.common.router.interfaces.Controller;
import com.fire.common.router.interfaces.RequestMapping;
import com.fire.common.router.interfaces.PathVariable;
import com.fire.common.router.interfaces.RequestParam;
import com.fire.common.router.interfaces.RequestBody;
import com.fire.common.router.enums.HttpMethod;
import com.fire.common.result.Result;
import com.fire.common.result.ResultBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器示例
 */
@Controller
public class UserController {

    @RequestMapping(path = "/api/users", method = HttpMethod.GET)
    public void getUsers(@RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            HttpServletResponse response) throws Exception {
        Map<String, Object> users = new HashMap<>();
        users.put("users", new String[] { "张三", "李四", "王五" });
        users.put("page", page);
        users.put("size", size);
        users.put("total", 100);

        Result<Map<String, Object>> result = ResultBuilder.success(users);
        writeJsonResponse(response, result);
    }

    @RequestMapping(path = "/api/users/{id}", method = HttpMethod.GET)
    public void getUserById(@PathVariable("id") Long id, HttpServletResponse response) throws Exception {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("name", "用户" + id);
        user.put("email", "user" + id + "@example.com");

        Result<Map<String, Object>> result = ResultBuilder.success(user);
        writeJsonResponse(response, result);
    }

    @RequestMapping(path = "/api/users", method = HttpMethod.POST)
    public void createUser(@RequestBody Map<String, Object> userData, HttpServletResponse response) throws Exception {
        // 模拟创建用户
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("id", System.currentTimeMillis()); // 模拟生成ID
        newUser.put("name", userData.get("name"));
        newUser.put("email", userData.get("email"));
        newUser.put("created", new java.util.Date());

        Result<Map<String, Object>> result = ResultBuilder.success(newUser);
        writeJsonResponse(response, result);
    }

    @RequestMapping(path = "/api/users/{id}", method = HttpMethod.PUT)
    public void updateUser(@PathVariable("id") Long id,
            @RequestBody Map<String, Object> userData,
            HttpServletResponse response) throws Exception {
        // 模拟更新用户
        Map<String, Object> updatedUser = new HashMap<>();
        updatedUser.put("id", id);
        updatedUser.put("name", userData.get("name"));
        updatedUser.put("email", userData.get("email"));
        updatedUser.put("updated", new java.util.Date());

        Result<Map<String, Object>> result = ResultBuilder.success(updatedUser);
        writeJsonResponse(response, result);
    }

    @RequestMapping(path = "/api/users/{id}", method = HttpMethod.DELETE)
    public void deleteUser(@PathVariable("id") Long id, HttpServletResponse response) throws Exception {
        // 模拟删除操作
        Map<String, String> result = new HashMap<>();
        result.put("message", "用户 " + id + " 已删除");
        result.put("timestamp", String.valueOf(System.currentTimeMillis()));

        Result<Map<String, String>> successResult = ResultBuilder.success(result);
        writeJsonResponse(response, successResult);
    }

    @RequestMapping(path = "/api/hello/{name}", method = HttpMethod.GET)
    public void sayHello(@PathVariable("name") String name,
            @RequestParam(value = "lang", defaultValue = "zh") String lang,
            HttpServletResponse response) throws Exception {
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

        Result<Map<String, String>> result = ResultBuilder.success(greeting);
        writeJsonResponse(response, result);
    }

    @RequestMapping(path = "/api/search", method = HttpMethod.GET)
    public void search(@RequestParam("q") String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit,
            HttpServletResponse response) throws Exception {
        Map<String, Object> searchResult = new HashMap<>();
        searchResult.put("query", query);
        searchResult.put("category", category);
        searchResult.put("limit", limit);
        searchResult.put("results", new String[] { "结果1", "结果2", "结果3" });
        searchResult.put("total", 123);

        Result<Map<String, Object>> result = ResultBuilder.success(searchResult);
        writeJsonResponse(response, result);
    }

    private void writeJsonResponse(HttpServletResponse response, Object data) throws Exception {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 这里简化处理，实际应该使用Jackson
        String json = com.fire.util.JacksonHolderSingleton.getObjectMapper().writeValueAsString(data);
        response.getWriter().write(json);
    }
}
