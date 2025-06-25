package com.xszx.dto.req.admin;

public class RegisterRequestDTO {
    private String username;
    private String password;
    public RegisterRequestDTO() {

    }
    public RegisterRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
