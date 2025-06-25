package com.xszx.controller;

import com.xszx.common.router.interfaces.Controller;
import com.xszx.common.router.interfaces.RequestMapping;
import com.xszx.common.router.interfaces.ResponseBody;
import com.xszx.common.router.enums.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * 诊断控制器 - 用于测试基本路由功能
 */
@Controller
public class DiagnosticController {

    @RequestMapping(path = "/", method = HttpMethod.GET)
    public String home() {
        return "<html><body><h1>智慧消防系统</h1><p>系统正常运行</p><a href='/diagnostic'>诊断页面</a></body></html>";
    }

    @RequestMapping(path = "/diagnostic", method = HttpMethod.GET)
    public String diagnostic() {
        return "<html><body>" +
                "<h1>系统诊断</h1>" +
                "<h2>测试链接</h2>" +
                "<ul>" +
                "<li><a href='/test/simple'>JSON测试</a></li>" +
                "<li><a href='/test/user'>用户对象测试</a></li>" +
                "<li><a href='/api/users'>API用户列表</a></li>" +
                "<li><a href='/test-responsebody.html'>测试页面</a></li>" +
                "</ul>" +
                "</body></html>";
    }

    @RequestMapping(path = "/ping", method = HttpMethod.GET)
    @ResponseBody
    public Map<String, Object> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "服务器正常运行");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
