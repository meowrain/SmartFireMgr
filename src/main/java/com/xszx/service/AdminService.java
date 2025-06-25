package com.xszx.service;

import com.xszx.dao.entity.AdminDao;
import com.xszx.dto.resp.LoginResponseDTO;

public interface AdminService {
    LoginResponseDTO login(String name, String password);
}
