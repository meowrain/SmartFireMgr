package com.xszx.controller;

import javax.servlet.http.HttpServletRequest;

import com.xszx.common.result.Result;
import com.xszx.common.result.ResultBuilder;
import com.xszx.common.router.core.ModelAndView;
import com.xszx.common.router.enums.HttpMethod;
import com.xszx.common.router.interfaces.*;
import com.xszx.dto.req.admin.LoginRequestDTO;
import com.xszx.dto.req.admin.RegisterRequestDTO;
import com.xszx.dto.resp.admin.LoginResponseDTO;
import com.xszx.dto.resp.admin.RegisterResponseDTO;
import com.xszx.service.AdminService;
import com.xszx.service.impl.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AdminController - 管理员控制器 目前有登录、注册接口
 */
@Controller
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    AdminService adminService = new AdminServiceImpl();

    @RequestMapping(path = "/api/admin", method = HttpMethod.GET)
    public ModelAndView adminPage(HttpServletRequest req) {
        ModelAndView mv = new ModelAndView();
        String username = (String) req.getSession().getAttribute("username");
        log.info("username: {}", username);
        if (username != null) {
            mv.setViewName("/index.jsp");
            mv.addObject(username, username);
        } else {
            mv.setViewName("/login.jsp");
        }
        return mv;
    }

    @RequestMapping(path = "/api/admin/login", method = HttpMethod.POST)
    @ResponseBody
    public Result login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest req) {
        // 这里可以添加登录逻辑，例如验证用户名和密码
        // 如果验证成功，返回一个成功的消息或令牌
        LoginResponseDTO responseDTO = adminService.login(loginRequestDTO);
        if (responseDTO != null) {
            // 使用 Session 存储，而不是 Request Attribute
            req.getSession().setAttribute("username", responseDTO.getUsername());
            req.getSession().setAttribute("token", responseDTO.getToken());
        }
        Result<?> result;
        result = ResultBuilder.success(responseDTO);
        return result;
    }

    @RequestMapping(path = "/api/admin/register", method = HttpMethod.POST)
    @ResponseBody
    public Result register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        RegisterResponseDTO responseDTO = adminService.register(registerRequestDTO);
        return ResultBuilder.success(responseDTO);
    }


}
