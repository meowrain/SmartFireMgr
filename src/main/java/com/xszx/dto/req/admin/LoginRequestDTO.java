package com.xszx.dto.req.admin;

public class LoginRequestDTO {
    private String username;
    private String password;

    public LoginRequestDTO(String name, String password) {
        this.username = name;
        this.password = password;
    }

    public LoginRequestDTO() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
