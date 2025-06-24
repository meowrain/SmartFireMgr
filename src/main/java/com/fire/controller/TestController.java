package com.fire.controller;

import com.fire.common.router.interfaces.Controller;
import com.fire.common.router.interfaces.RequestMapping;
import com.fire.common.router.interfaces.PathVariable;
import com.fire.common.router.interfaces.RequestParam;
import com.fire.common.router.interfaces.RequestBody;
import com.fire.common.router.interfaces.ResponseBody;
import com.fire.common.router.enums.HttpMethod;
import com.fire.common.errorcode.BaseErrorCode;
import com.fire.common.exceptions.ClientException;
import com.fire.common.result.Result;
import com.fire.common.result.ResultBuilder;
import com.fire.pojo.User;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 测试控制器 - 演示@ResponseBody注解的完整用法
 */
@Controller
public class TestController {

    // 1. 基本用法 - 返回简单对象
    @RequestMapping(path = "/test/simple", method = HttpMethod.GET)
    @ResponseBody
    public Map<String, String> getSimpleData() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "Hello from @ResponseBody!");
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return data;
    } // 2. 返回POJO对象

    @RequestMapping(path = "/test/user", method = HttpMethod.GET)
    @ResponseBody
    public User getUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("123456");
        return user;
    }

    // 3. 返回Result包装的数据
    @RequestMapping(path = "/test/result", method = HttpMethod.GET)
    @ResponseBody
    public Result<Map<String, Object>> getResultData() {
        Map<String, Object> data = new HashMap<>();
        data.put("server", "智慧消防系统");
        data.put("version", "1.0.0");
        data.put("status", "running");
        return ResultBuilder.success(data);
    }

    // 4. 返回列表数据
    @RequestMapping(path = "/test/users", method = HttpMethod.GET)
    @ResponseBody
    public Result<List<Map<String, Object>>> getUserList() {
        List<Map<String, Object>> users = new ArrayList<>();

        Map<String, Object> user1 = new HashMap<>();
        user1.put("id", 1);
        user1.put("name", "张三");
        user1.put("email", "zhangsan@example.com");
        users.add(user1);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("id", 2);
        user2.put("name", "李四");
        user2.put("email", "lisi@example.com");
        users.add(user2);

        return ResultBuilder.success(users);
    }

    // 5. 带路径参数
    @RequestMapping(path = "/test/user/{id}", method = HttpMethod.GET)
    @ResponseBody
    public Result<Map<String, Object>> getUserById(@PathVariable("id") Long id) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("name", "测试用户" + id);
        user.put("email", "user" + id + "@test.com");
        return ResultBuilder.success(user);
    }

    // 6. 带查询参数
    @RequestMapping(path = "/test/search", method = HttpMethod.GET)
    @ResponseBody
    public Result<Map<String, Object>> search(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        Map<String, Object> result = new HashMap<>();
        result.put("keyword", keyword);
        result.put("page", page);
        result.put("size", size);
        result.put("total", 50);
        result.put("data", Arrays.asList("结果1", "结果2", "结果3"));

        return ResultBuilder.success(result);
    }

    @RequestMapping(path = "/test/exception", method = HttpMethod.GET)
    @ResponseBody
    public Result<String> throwException() {
        // 模拟抛出异常
        throw new ClientException("主动错误", BaseErrorCode.CLIENT_ERROR);
    }

    // 7. POST请求处理JSON数据
    @RequestMapping(path = "/test/user", method = HttpMethod.POST)
    @ResponseBody
    public Result<Map<String, Object>> createUser(@RequestBody Map<String, Object> userData) {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("id", System.currentTimeMillis());
        newUser.put("name", userData.get("name"));
        newUser.put("email", userData.get("email"));
        newUser.put("created", new Date());

        return ResultBuilder.success(newUser);
    }

    // 8. 返回null值测试
    @RequestMapping(path = "/test/null", method = HttpMethod.GET)
    @ResponseBody
    public Object getNullValue() {
        return null;
    } // 9. 错误处理示例

    @RequestMapping(path = "/test/error/{type}", method = HttpMethod.GET)
    @ResponseBody
    public Result<Object> testError(@PathVariable("type") String type) {
        switch (type) {
            case "404":
                return new Result<Object>().setCode("404").setMessage("资源不存在");
            case "500":
                return new Result<Object>().setCode("500").setMessage("服务器内部错误");
            case "400":
                return new Result<Object>().setCode("400").setMessage("请求参数错误");
            default:
                return ResultBuilder.success("正常响应");
        }
    }

    // 10. 复杂对象测试
    @RequestMapping(path = "/test/complex", method = HttpMethod.GET)
    @ResponseBody
    public Map<String, Object> getComplexData() {
        Map<String, Object> data = new HashMap<>();

        // 基本数据
        data.put("string", "文本内容");
        data.put("number", 42);
        data.put("boolean", true);
        data.put("date", new Date());

        // 嵌套对象
        Map<String, Object> nested = new HashMap<>();
        nested.put("level", 2);
        nested.put("description", "嵌套对象");
        data.put("nested", nested);

        // 数组
        data.put("array", new String[] { "item1", "item2", "item3" });

        // 列表
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        data.put("list", numbers);

        return data;
    }

    // 11. 对比：不使用@ResponseBody的方法
    @RequestMapping(path = "/test/html", method = HttpMethod.GET)
    public String getHtmlResponse() {
        return "<html><body><h1>这是HTML响应</h1><p>没有使用@ResponseBody注解</p></body></html>";
    }

    // 12. 对比：手动处理JSON的方法
    @RequestMapping(path = "/test/manual-json", method = HttpMethod.GET)
    public void getManualJsonResponse(HttpServletResponse response) throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("message", "手动处理JSON");
        data.put("method", "manual");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = com.fire.util.JacksonHolderSingleton.getObjectMapper().writeValueAsString(data);
        response.getWriter().write(json);
    }

    // 13. 性能测试接口
    @RequestMapping(path = "/test/performance", method = HttpMethod.GET)
    @ResponseBody
    public Map<String, Object> performanceTest() {
        long startTime = System.currentTimeMillis();

        // 模拟一些处理
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            result.put("key" + i, "value" + i);
        }

        long endTime = System.currentTimeMillis();
        result.put("processingTime", endTime - startTime);
        result.put("itemCount", 100);

        return result;
    }
}
