package com.fire.common.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fire.common.result.Result;
import com.fire.util.JacksonHolderSingleton;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;

public abstract class BaseJsonServlet extends HttpServlet {
    // 获取单例的 ObjectMapper 实例
    private static final ObjectMapper MAPPER = JacksonHolderSingleton.getObjectMapper();

    protected Result<Object> get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 默认实现为不支持该方法
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method not supported.");
        return null;
    }

    protected Result<Object> post(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 默认实现为不支持该方法
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method not supported.");
        return null;
    }
    
    protected Result<Object> put(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 默认实现为不支持该方法
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "PUT method not supported.");
        return null;
    }
    
    protected Result<Object> delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 默认实现为不支持该方法
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "DELETE method not supported.");
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Result<Object> result = get(req, resp);
        writeJsonResponse(resp, result);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Result<Object> result = post(req, resp);
        writeJsonResponse(resp, result);
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Result<Object> result = put(req, resp);
        writeJsonResponse(resp, result);
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Result<Object> result = delete(req, resp);
        writeJsonResponse(resp, result);
    }

    /**
     * 从请求体中读取JSON并转换为指定类型的对象
     *
     * @param req HttpServletRequest 对象
     * @param clazz 目标类型
     * @return 转换后的对象
     */
    protected <T> T readJsonRequest(HttpServletRequest req, Class<T> clazz) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return MAPPER.readValue(sb.toString(), clazz);
    }

    /**
     * 这是一个工具方法，负责将任意对象转换为JSON并写入响应。
     *
     * @param resp HttpServletResponse 对象
     * @param data 要转换的业务对象
     */
    protected void writeJsonResponse(HttpServletResponse resp, Object data) throws IOException {
        if (data == null) {
            // 如果数据为null，可以返回一个空的成功结果
            data = new Result<>(Result.SUCCESS_CODE, "Success", null);
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(resp.getWriter(), data);
    }
}
