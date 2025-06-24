package com.fire.common.router.core;

import com.fire.common.router.enums.HttpMethod;
import com.fire.common.router.handler.HandlerMethod;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Router {
    // 为每种 HTTP 方法维护一个独立的 Trie 树
    private final Map<HttpMethod, Node> roots = new EnumMap<>(HttpMethod.class);

    /**
     * Trie 树节点
     */
    private static class Node {
        // 静态路径子节点，如 /users, /orders
        Map<String, Node> staticChildren = new HashMap<>();
        // 动态路径子节点（路径变量），如 /{id}
        Node dynamicChild;
        String paramName;
        // 如果该节点是某个路由的终点，则存储其 HandlerMethod
        HandlerMethod handler;
    }

    public void addRoute(HttpMethod httpMethod, String path, HandlerMethod handler) {
        if (httpMethod == HttpMethod.ALL) {
            for (HttpMethod m : HttpMethod.values()) {
                if (m != HttpMethod.ALL) addRoute(m, path, handler);
            }
            return;
        }

        Node root = roots.computeIfAbsent(httpMethod, k -> new Node());
        String[] segments = path.split("/");

        Node currentNode = root;
        for (String segment : segments) {
            if (segment.isEmpty()) continue;

            if (segment.startsWith("{") && segment.endsWith("}")) {
                // 动态路径段
                if (currentNode.dynamicChild == null) {
                    currentNode.dynamicChild = new Node();
                }
                currentNode = currentNode.dynamicChild;
                currentNode.paramName = segment.substring(1, segment.length() - 1);
            } else {
                // 静态路径段
                currentNode = currentNode.staticChildren.computeIfAbsent(segment, k -> new Node());
            }
        }
        currentNode.handler = handler;
    }

    public Optional<RouteMatch> findRoute(HttpMethod httpMethod, String path) {
        Node root = roots.get(httpMethod);
        if (root == null) {
            return Optional.empty();
        }

        String[] segments = path.split("/");
        Node currentNode = root;
        Map<String, String> pathVariables = new HashMap<>();

        for (String segment : segments) {
            if (segment.isEmpty()) continue;

            Node nextNode = currentNode.staticChildren.get(segment);
            if (nextNode != null) {
                // 优先匹配静态路径
                currentNode = nextNode;
            } else if (currentNode.dynamicChild != null) {
                // 匹配动态路径
                currentNode = currentNode.dynamicChild;
                pathVariables.put(currentNode.paramName, segment);
            } else {
                // 未找到匹配
                return Optional.empty();
            }
        }

        if (currentNode.handler != null) {
            return Optional.of(new RouteMatch(currentNode.handler, pathVariables));
        }

        return Optional.empty();
    }
}
