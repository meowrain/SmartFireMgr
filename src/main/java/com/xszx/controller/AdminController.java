package com.xszx.controller;

import com.xszx.common.result.Result;
import com.xszx.common.result.ResultBuilder;
import com.xszx.common.router.enums.HttpMethod;
import com.xszx.common.router.interfaces.Controller;
import com.xszx.common.router.interfaces.RequestBody;
import com.xszx.common.router.interfaces.RequestMapping;
import com.xszx.common.router.interfaces.ResponseBody;
import com.xszx.dto.req.admin.LoginRequestDTO;
import com.xszx.dto.resp.LoginResponseDTO;
import com.xszx.service.AdminService;
import com.xszx.service.impl.AdminServiceImpl;

@Controller
public class AdminController {
    AdminService adminService = new AdminServiceImpl();

    @RequestMapping(path = "/api/admin/login", method = HttpMethod.POST)
    @ResponseBody
    public Result login(@RequestBody LoginRequestDTO loginRequestDTO) {
        // 这里可以添加登录逻辑，例如验证用户名和密码
        // 如果验证成功，返回一个成功的消息或令牌
        LoginResponseDTO responseDTO = adminService.login(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
        Result<?> result;
        if (responseDTO == null) {
            return ResultBuilder.failure("503", "登录失败，用户名或密码错误");
        }
        result = ResultBuilder.success(responseDTO);
        return result;
    }

//    @RequestMapping(path = "/api/admin/register",method = HttpMethod.POST)
//    @ResponseBody
//    public Result<String> register() {
//
//    }

    @RequestMapping(path = "/api/admin/greeting", method = HttpMethod.GET)
    @ResponseBody
    public String greeting() {
        return "hello";
    }
}
