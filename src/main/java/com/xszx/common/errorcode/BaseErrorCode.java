package com.xszx.common.errorcode;

public enum BaseErrorCode implements IErrorCode {
    CLIENT_ERROR("A000001", "用户端错误"),
    // ========== 一级宏观错误码 系统执行出错 ==========
    SERVICE_ERROR("B000001", "系统执行出错"),
    // ================= 二级错误吗，业务错误 ============
    LOGIN_ERROR_USERNAME_PWD("L00001", "用户名或者密码错误"),
    REGISTER_ERROR01("R00001", "用户已经注册,请更换用户名后重试！"),
    REGISTER_ERROR02("R00002", "用户注册失败，原因见日志"),
    AUTH_ERROR("A00001","用户未登录或登录已失效，请重新登录");
    private final String code;
    private final String message;

    BaseErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
