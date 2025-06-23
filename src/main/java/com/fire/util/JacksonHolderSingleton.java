package com.fire.util;

import com.fasterxml.jackson.databind.ObjectMapper;

// java effective 单例模式
public class JacksonHolderSingleton {
    // 确保构造器无法调用
    private JacksonHolderSingleton() {
    }

    private static class ObjectMapperHolder {
        private static final ObjectMapper INSTANCE = new ObjectMapper();
    }

    public static ObjectMapper getObjectMapper() {
        return ObjectMapperHolder.INSTANCE;
    }
}
