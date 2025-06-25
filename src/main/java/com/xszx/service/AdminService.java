package com.xszx.service;

import com.xszx.dto.req.admin.LoginRequestDTO;
import com.xszx.dto.req.admin.RegisterRequestDTO;
import com.xszx.dto.resp.LoginResponseDTO;
import com.xszx.dto.resp.RegisterResponseDTO;

public interface AdminService {
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
    RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO);
}
