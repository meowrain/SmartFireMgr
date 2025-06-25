package com.xszx.common.result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xszx.util.JacksonHolderSingleton;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private static final long serialVersionUID = 5679018624309023727L;
    public static final String SUCCESS_CODE = "0";
    public static final String ERROR_CODE = "-1";

    private String code;
    private T data;
    private String message;

    // 构造方法
    public Result() {
    }

    public Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public Result<T> setCode(String code) {
        this.code = code;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

    public String toJsoString() throws JsonProcessingException {
        return JacksonHolderSingleton.getObjectMapper().writeValueAsString(this);
    }
}
