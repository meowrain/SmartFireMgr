package com.xszx.common.router.handler.resolver.returnvalue;

import com.xszx.common.router.core.ModelAndView;
import com.xszx.common.router.handler.resolver.ReturnValueHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * ModelAndView返回值处理器
 * 处理返回ModelAndView类型的方法，设置模型数据到request并转发到JSP
 */
public class ModelAndViewReturnValueHandler implements ReturnValueHandler {

    @Override
    public boolean supports(Method method) {
        // 支持返回类型为ModelAndView的方法
        return ModelAndView.class.isAssignableFrom(method.getReturnType());
    }

    @Override
    public void handleReturnValue(Object returnValue, Method method,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (returnValue == null) {
            return;
        }

        ModelAndView modelAndView = (ModelAndView) returnValue;

        // 将模型数据设置到request attribute中
        Map<String, Object> model = modelAndView.getModel();
        if (model != null && !model.isEmpty()) {
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        // 获取视图名称并转发到JSP
        String viewName = modelAndView.getViewName();
        if (viewName != null && !viewName.isEmpty()) {
            // 如果视图名称不包含扩展名，默认添加.jsp
            if (!viewName.contains(".")) {
                viewName = viewName + ".jsp";
            }

            // 如果不是以/开头，添加/
            if (!viewName.startsWith("/")) {
                viewName = "/" + viewName;
            }

            // 转发到JSP页面
            RequestDispatcher dispatcher = request.getRequestDispatcher(viewName);
            dispatcher.forward(request, response);
        }
    }
}
