package com.xszx.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 白名单工具类(线程安全的)
 * 用于存储不需要认证的路径
 * 可以在此类中添加或修改白名单路径
 *
 */
public final class WhiteList {
    private static final Logger log = LoggerFactory.getLogger(WhiteList.class);
    private static volatile List<String> whiteList = null;

    /**
     * 加载白名单
     *
     * @return
     */
    private static List<String> loadWhiteList() {
        try (InputStream inputStream = WhiteList.class.getClassLoader().getResourceAsStream("white_list.json")) {
            if (inputStream == null) {
                throw new RuntimeException("白名单文件未找到");
            }
            ObjectMapper objectMapper = JacksonHolderSingleton.getObjectMapper();
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode whitelistNode = rootNode.get("whitelist");

            if (whitelistNode != null && whitelistNode.isArray()) {
                List<String> tempList = new ArrayList<>();
                for (JsonNode pathNode : whitelistNode) {
                    tempList.add(pathNode.asText());
                }
                // 返回一个不可修改的列表
                return Collections.unmodifiableList(tempList);
            } else {
                throw new RuntimeException("白名单格式错误");
            }

        } catch (Exception e) {
            log.error("加载白名单失败", e);
            throw new RuntimeException("加载白名单失败", e); // 抛异常，防止服务启动异常
        }
    }

    /**
     * 判断路径是否在白名单中
     *
     * @param path
     * @return
     */
    public static boolean isInWhiteList(String path) {
        // 双重检测，防止重复加载
        /**
         * 如果只检测一次
         * 比如线程A和B同时进来，A先加载了白名单，判断whitelist为null，进入第一层，现在操作系统把cpu时间片让给B，B也判断whitelist为null，进入第一层，
         * 这样的话如果不加锁进行二次判断直接loadWhiteList()，就会导致白名单被加载两次，
         *
         * 现在双重检测，A检测到whiteList为null，然后进入同步块，B也检测到whiteList为null
         * 但是b没有获取到锁，会被同步锁阻塞，直到A加载完白名单并释放锁，
         * 这个时候B进来，需要再次判断whiteList是否为null，
         * 因为whiteList是volatile的，所以B会看到A已经加载完白名单，此时B就不会再次调用loadWhiteList()方法，
         */
        if (whiteList == null) {
            synchronized (WhiteList.class) {
                if (whiteList == null) {
                    whiteList = loadWhiteList();
                }
            }
        }
        if (path == null || path.isEmpty()) {
            return false;
        }
        for (String pattern : whiteList) {
            if (pathMatches(path, pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 路径匹配方法，支持通配符
     *
     * @param path    实际路径
     * @param pattern 匹配模式
     * @return 是否匹配
     */
    private static boolean pathMatches(String path, String pattern) {
        // 支持 /** 通配符
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }

        // 支持 /* 通配符
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return path.startsWith(prefix) && !path.substring(prefix.length()).contains("/");
        }

        // 精确匹配
        return path.equals(pattern);
    }
}
