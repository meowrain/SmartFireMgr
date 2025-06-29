package com.xszx.dto.resp.admin;

public class LoginResponseDTO {

    private String username;

    /**
     * jwt token
     */
    private String token;

    public LoginResponseDTO() {

    }
    public LoginResponseDTO(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
