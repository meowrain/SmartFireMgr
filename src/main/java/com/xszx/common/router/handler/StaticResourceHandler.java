package com.xszx.common.router.handler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticResourceHandler {

    public static boolean handleStaticResource(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();

        // 去掉上下文路径，获取资源路径
        String resourcePath = requestURI.substring(contextPath.length());

        // 检查是否是静态资源（通过文件扩展名判断）
        if (isStaticResource(resourcePath)) {

            // 设置正确的Content-Type
            setContentType(response, resourcePath);

            // 从webapp目录读取静态资源
            // 注意：如果路径以/api/开头，需要去掉/api前缀来找真实的文件路径
            String actualResourcePath = resourcePath;
            if (resourcePath.startsWith("/api/") && !resourcePath.startsWith("/api/admin")) {
                // 对于/api/vendors/...这样的路径，去掉/api前缀
                actualResourcePath = resourcePath.substring(4); // 去掉"/api"
            }

            InputStream inputStream = request.getServletContext().getResourceAsStream(actualResourcePath);
            if (inputStream != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                OutputStream outputStream = response.getOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
                return true; // 表示已处理
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Static resource not found: " + actualResourcePath);
                return true;
            }
        }

        return false; // 不是静态资源，继续其他处理
    }

    private static boolean isStaticResource(String path) {
        return path.endsWith(".css") || path.endsWith(".js") ||
                path.endsWith(".png") || path.endsWith(".jpg") ||
                path.endsWith(".gif") || path.endsWith(".ico") ||
                path.endsWith(".html") || path.endsWith(".htm") ||
                path.endsWith(".woff") || path.endsWith(".woff2") ||
                path.endsWith(".ttf") || path.endsWith(".eot") ||
                path.endsWith(".svg") || path.endsWith(".map");
    }

    private static void setContentType(HttpServletResponse response, String fileName) {
        if (fileName.endsWith(".css")) {
            response.setContentType("text/css; charset=UTF-8");
        } else if (fileName.endsWith(".js")) {
            response.setContentType("application/javascript; charset=UTF-8");
        } else if (fileName.endsWith(".png")) {
            response.setContentType("image/png");
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            response.setContentType("image/jpeg");
        } else if (fileName.endsWith(".gif")) {
            response.setContentType("image/gif");
        } else if (fileName.endsWith(".ico")) {
            response.setContentType("image/x-icon");
        } else if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            response.setContentType("text/html; charset=UTF-8");
        } else if (fileName.endsWith(".woff") || fileName.endsWith(".woff2")) {
            response.setContentType("font/woff");
        } else if (fileName.endsWith(".ttf")) {
            response.setContentType("font/ttf");
        } else if (fileName.endsWith(".svg")) {
            response.setContentType("image/svg+xml");
        }
    }
}