package com.fire.common.router.core;

import com.fire.common.router.enums.HttpMethod;
import com.fire.common.router.handler.HandlerInvoker;
import com.fire.common.router.handler.HandlerMethod;
import com.fire.common.router.interfaces.Controller;
import com.fire.common.router.interfaces.RequestMapping;
import com.fire.common.router.interfaces.ResponseBody;
import com.fire.common.router.interceptor.LoggingInterceptor;
import com.fire.common.router.interceptor.CorsInterceptor;
import com.fire.util.ClasspathScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

/**
 * DispatcherServlet	请求分发器
 */
public class DispatcherServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);
    private Router router;
    private HandlerInvoker handlerInvoker;

    // tomcat启动以后会调用这个方法
    @Override
    public void init(ServletConfig config) throws ServletException {
        // 初始化路由匹配引擎，可以往里面注册路由
        this.router = new Router();
        //初始化方法调用器
        this.handlerInvoker = new HandlerInvoker();

        // 注册拦截器（类似Gin的中间件）
        this.handlerInvoker.addInterceptor(new CorsInterceptor());
        this.handlerInvoker.addInterceptor(new LoggingInterceptor());

        // 从web.xml获取扫描包路径
        String scanPackage = config.getInitParameter("scanPackage");

        try {
            // 使用工具类扫描，更清晰
            Set<Class<?>> classes = ClasspathScanner.scan(scanPackage);
            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(Controller.class)) {
                    // 把标有@Controller的类注册到router中
                    registerController(clazz);
                }
            }
        } catch (Exception e) {
            throw new ServletException("Initialization failed", e);
        }
    }

    private void registerController(Class<?> clazz) throws Exception {
        // 注册器实例，使用反射创建使用@Controller修饰的类对象
        Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
        // 通过反射获取它的所有方法
        for (Method method : clazz.getMethods()) {
            // 查找使用@RequestMapping标注的方法
            if (method.isAnnotationPresent(RequestMapping.class)) {
                // 提取@RequestMapping
                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                //TODO: 一会儿看原理
                HandlerMethod handler = new HandlerMethod(controllerInstance, method);
                // 添加路由到router中。请求方法（get,post,update...）/path 还有handler
                router.addRoute(mapping.method(), mapping.path(), handler);
                // 查看这个类里面的方法有没有被@ResponseBody修饰，如果修饰了，那就返回类型为application/json，否则为text/html
                String responseType = method.isAnnotationPresent(ResponseBody.class) ? "application/json" : "text/html";
                System.out.println("Mapped " + mapping.method() + " " + mapping.path() + " -> " + clazz.getSimpleName()
                        + "." + method.getName() + " (Response Type: " + responseType + ")");
            }
        }
    }

    /**
     * 根据请求的 HTTP 方法类型（GET、POST、PUT、DELETE 等）来调用对应的 doXXX 方法。
     *
     * @param req  the {@link HttpServletRequest} object that
     *             contains the request the client made of
     *             the servlet
     * @param resp the {@link HttpServletResponse} object that
     *             contains the response the servlet returns
     *             to the client
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 从请求中获取 URI，并去掉上下文路径，得到实际请求路径（如 /user/1）
        String path = req.getRequestURI().substring(req.getContextPath().length());
        // 将 HTTP 方法（如 GET、POST）转为自定义的 HttpMethod 枚举类型
        HttpMethod method = HttpMethod.valueOf(req.getMethod().toUpperCase());
        // 使用 Router 根据请求方法和路径查找匹配的路由（Handler 和路径变量）
        Optional<RouteMatch> routeMatch = router.findRoute(method, path);

        if (routeMatch.isPresent()) {
            // 如果找到了匹配的路由
            try {
                // 通过 handlerInvoker 调用实际的处理方法
                handlerInvoker.invoke(req, resp, routeMatch.get());
            } catch (Exception e) {
                log.error("Server error while handling request: {}", e.getMessage(), e);
                handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error: " + e.getMessage());
            }
        } else {
            handleError(resp, HttpServletResponse.SC_NOT_FOUND, "Not Found: " + method + " " + path);
        }
    }

    // 统一错误处理方法
    private void handleError(HttpServletResponse resp, int status, String message) {
        try {
            resp.setStatus(status);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"error\":\"" + message + "\"}");
        } catch (IOException e) {
            log.error("Failed to write error response: {}", e.getMessage(), e);
        }
    }
}
