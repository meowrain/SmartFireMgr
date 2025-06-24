package com.fire.common.router.core;

import com.fire.common.router.enums.HttpMethod;
import com.fire.common.router.handler.HandlerMethod;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 一个基于Trie树（前缀树）的高性能路由器。
 * <p>
 * 核心设计思想是为每种HTTP方法（GET, POST等）维护一棵独立的Trie树，
 * 用于存储和快速查找路由规则。这种设计非常适合处理现代Web框架中常见的层级式URL结构。
 *
 * <h3>主要设计特点:</h3>
 * <ol>
 * <li><b>独立Trie树</b>:
 * 通过 {@code Map<HttpMethod, Node> roots} 为每个HTTP方法（GET, POST等）
 * 创建一个专属的Trie树根节点。当查找路由时，可直接定位到对应方法的树，
 * 极大地缩小了搜索空间，提高了匹配效率。
 * </li>
 * <li><b>支持静态与动态路由</b>:
 * Trie树的节点 {@link Node} 设计同时支持静态路径（如 {@code /users/profile}）
 * 和动态路径参数（如 {@code /users/{id}}）。
 * </li>
 * <li><b>静态路由优先</b>:
 * 在路由查找时，总是优先匹配静态路径。只有在当前路径段没有静态匹配项时，
 * 才会尝试匹配动态路径。这是业界标准的、符合直觉的路由行为。
 * </li>
 * </ol>
 */
public class Router {

    /**
     * 存储所有路由树的根节点。
     * Map的键是 {@link HttpMethod}，值是对应方法Trie树的根节点 {@link Node}。
     * 使用 {@link EnumMap} 是因为它针对枚举类型的键有性能优化。
     */
    private final Map<HttpMethod, Node> roots = new EnumMap<>(HttpMethod.class);

    /**
     * 私有静态类，防止被外界访问
     * Trie树的节点，代表URL路径中的一个段 (segment)。
     */
    private static class Node {
        /**
         * 存储静态路径的子节点。
         * Key是路径段字符串（如 "users"），Value是对应的子节点。
         */
        Map<String, Node> staticChildren = new HashMap<>();

        /**
         * 存储动态路径的子节点（例如, 对应于 "/{id}" 的节点）。
         * 每个节点最多只有一个动态子节点。这意味着不支持在同一层级定义如 "/users/{id}" 和 "/users/{name}" 这样的多个动态路由。
         */
        Node dynamicChild;

        /**
         * 如果当前节点是动态节点，此字段存储动态参数的名称。
         * 例如，对于路径段 "{id}"，paramName 的值是 "id"。
         */
        String paramName;

        /**
         * 如果此节点代表一个完整路由的终点，则该字段会存储其对应的处理器方法。
         * 如果为 null，表示这只是一个中间路径节点，而非一个可处理的路由。
         */
        HandlerMethod handler;
    }

    /**
     * 添加一条路由规则到指定HTTP方法的Trie树中。
     *
     * @param httpMethod 路由对应的HTTP方法。如果为 {@link HttpMethod#ALL}，则会为所有具体方法注册该路由。
     * @param path       路由的URL路径，例如 "/users/{id}/profile"。
     * @param handler    当路由匹配时要执行的处理器方法 {@link HandlerMethod}。
     */
    public void addRoute(HttpMethod httpMethod, String path, HandlerMethod handler) {
        // 特殊处理 ALL 方法，为所有其他HTTP方法都添加此路由。
        if (httpMethod == HttpMethod.ALL) {
            for (HttpMethod m : HttpMethod.values()) {
                if (m != HttpMethod.ALL) {
                    addRoute(m, path, handler);
                }
            }
            return;
        }

        // 1. 获取或创建当前HTTP方法对应的Trie树根节点。
        Node root = roots.computeIfAbsent(httpMethod, k -> new Node());
        // 2. 将URL路径按 "/" 分割成多个路径段 (segments)。
        String[] segments = path.split("/");

        // 3. 从根节点开始，遍历路径段，构建或更新Trie树。
        Node currentNode = root;
        for (String segment : segments) {
            if (segment.isEmpty()) {
                // 跳过由前导/、尾随/或双//产生的空段。
                continue;
            }

            // 判断当前路径段是否为动态参数 (例如, "{id}")
            if (segment.startsWith("{") && segment.endsWith("}")) {
                // 如果是动态路径段，则走向 dynamicChild。
                if (currentNode.dynamicChild == null) {
                    currentNode.dynamicChild = new Node();
                }
                currentNode = currentNode.dynamicChild;
                // 提取并存储参数名（例如, 从 "{id}" 中提取 "id"）。
                currentNode.paramName = segment.substring(1, segment.length() - 1);
            } else {
                // 如果是静态路径段，则在 staticChildren 中查找或创建子节点。
                currentNode = currentNode.staticChildren.computeIfAbsent(segment, k -> new Node());
            }
        }
        // 4. 循环结束后，当前节点即为该路由的终点，为其关联处理器方法。
        currentNode.handler = handler;
    }

    /**
     * 根据给定的HTTP方法和路径，查找匹配的路由。
     *
     * @param httpMethod 请求的HTTP方法。
     * @param path       请求的URL路径。
     * @return 如果找到匹配的路由，则返回一个包含处理器和路径参数的 {@link Optional<RouteMatch>}；
     * 否则返回 {@link Optional#empty()}。
     */
    public Optional<RouteMatch> findRoute(HttpMethod httpMethod, String path) {
        // 1. 获取当前请求方法对应的Trie树根节点。
        Node root = roots.get(httpMethod);
        if (root == null) {
            // 如果该方法没有任何已注册的路由，直接返回空。
            return Optional.empty();
        }

        // 2. 准备工作：分割请求路径，并初始化用于存储路径参数的Map。
        String[] segments = path.split("/");
        Node currentNode = root;
        // 用于存储匹配到的动态路径参数键值对，例如 {"id": "123"}
        Map<String, String> pathVariables = new HashMap<>();

        // 3. 遍历请求路径的每个段，在Trie树中进行匹配。
        for (String segment : segments) {
            if (segment.isEmpty()) {
                continue;
            }

            // 优先尝试匹配静态路径。
            Node nextNode = currentNode.staticChildren.get(segment);
            if (nextNode != null) {
                // 找到静态匹配，更新当前节点。
                currentNode = nextNode;
            } else if (currentNode.dynamicChild != null) {
                // 未找到静态匹配，尝试匹配动态路径。
                currentNode = currentNode.dynamicChild;
                // 成功匹配动态路径，捕获路径参数。
                pathVariables.put(currentNode.paramName, segment);
            } else {
                // 既无静态匹配也无动态匹配，宣告路由查找失败。
                return Optional.empty();
            }
        }

        // 4. 路径所有段都匹配完毕后，必须检查最终节点是否关联了一个处理器。
        //    这可以防止将 "/users/123" 错误地匹配到只注册了 "/users/123/profile" 的情况。
        if (currentNode.handler != null) {
            // 只有handler不为null，才是一个完整、有效的路由匹配。
            return Optional.of(new RouteMatch(currentNode.handler, pathVariables));
        }

        // 最终节点没有处理器，意味着路径只是某个更长路由的前缀，匹配失败。
        return Optional.empty();
    }
}