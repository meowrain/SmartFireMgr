package com.xszx.dto.req.admin;

public class RegisterRequestDTO {
    private String name;
    private String password;
    public RegisterRequestDTO() {

    }
    public RegisterRequestDTO(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
