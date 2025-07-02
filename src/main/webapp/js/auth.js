/**
 * 认证相关的 JavaScript 工具函数
 */

// 获取保存的 token（优先 localStorage）
function getToken() {
    return localStorage.getItem("token") || sessionStorage.getItem("token");
}

// 保存 token（统一使用 localStorage，可改为 sessionStorage 视场景需求）
function saveToken(token) {
    localStorage.setItem("token", token);
}

// 删除 token
function removeToken() {
    localStorage.removeItem("token");
    sessionStorage.removeItem("token");
}

// 判断是否已登录（仅判断是否存在 token，不校验有效性）
function isLoggedIn() {
    return getToken() !== null;
}

// 判断是否过期
function isTokenExpired(token) {
    try {
        const payload = JSON.parse(atob(token.split(".")[1]));
        return payload.exp * 1000 < Date.now();
    } catch {
        return true; // 无法解析时视为过期
    }
}

// 封装 fetch：自动注入 Authorization 头部
const originalFetch = window.fetch;
window.fetch = function (url, options = {}) {
    const token = getToken();

    if (token) {
        if (options.headers instanceof Headers) {
            options.headers.set("Authorization", token);
        } else {
            options.headers = {
                ...options.headers,
                Authorization: token,
            };
        }
    }

    return originalFetch(url, options)
        .then((response) => {
            if (response.status === 401) {
                console.warn("Token 无效，跳转到登录页");
                removeToken();
                window.location.href = "/login.jsp";
                return;
            }
            return response;
        })
        .catch((error) => {
            console.error("请求失败:", error);
            throw error;
        });
};

// 封装 XMLHttpRequest：自动注入 Authorization
(function () {
    const originalOpen = XMLHttpRequest.prototype.open;
    const originalSend = XMLHttpRequest.prototype.send;

    XMLHttpRequest.prototype.open = function (method, url, async, user, password) {
        this._method = method;
        this._url = url;
        originalOpen.call(this, method, url, async, user, password);
    };

    XMLHttpRequest.prototype.send = function (data) {
        const token = getToken();
        if (token) {
            this.setRequestHeader("Authorization", token);
        }
        originalSend.call(this, data);
    };
})();

// 页面加载时检查认证状态
document.addEventListener("DOMContentLoaded", function () {
    const currentPath = window.location.pathname;

    const whiteList = ["/login.jsp", "/register.jsp", "/"];
    const staticExtensions = [
        ".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".ico",
        ".woff", ".woff2", ".ttf", ".eot"
    ];

    const isStaticResource =
        staticExtensions.some((ext) => currentPath.endsWith(ext)) ||
        currentPath.startsWith("/css/") ||
        currentPath.startsWith("/js/") ||
        currentPath.startsWith("/images/") ||
        currentPath.startsWith("/fonts/") ||
        currentPath.startsWith("/vendors/");

    if (whiteList.includes(currentPath) || isStaticResource) {
        console.log("页面在白名单中或为静态资源，跳过认证检查:", currentPath);

        // 登录页重定向
        if ((currentPath === "/login.jsp" || currentPath === "/") && isLoggedIn()) {
            console.log("用户已登录，从登录页跳转到首页");
            window.location.href = "/index.jsp";
        }
        return;
    }

    if (!isLoggedIn()) {
        console.warn("用户未登录，跳转到登录页。当前页面:", currentPath);
        window.location.href = "/login.jsp";
    } else {
        const token = getToken();
        if (isTokenExpired(token)) {
            console.warn("Token 已过期，跳转到登录页");
            removeToken();
            window.location.href = "/login.jsp";
        } else {
            console.log("用户已登录，token:", token);
        }
    }
});

// 退出登录
function logout() {
    if (isLoggedIn()) {
        removeToken();
    }
    window.location.href = "/login.jsp";
}

// 手动调用检查认证状态（适合异步或特定页面调用）
function checkAuthentication() {
    const currentPath = window.location.pathname;
    const whiteList = ["/login.jsp", "/register.jsp", "/"];

    if (whiteList.includes(currentPath)) {
        if ((currentPath === "/login.jsp" || currentPath === "/") && isLoggedIn()) {
            console.log("用户已登录，从登录页跳转到首页");
            window.location.href = "/index.jsp";
        }
        return true;
    }

    if (!isLoggedIn()) {
        console.warn("用户未登录，跳转到登录页。当前页面:", currentPath);
        window.location.href = "/login.jsp";
        return false;
    }

    const token = getToken();
    if (isTokenExpired(token)) {
        console.warn("Token 已过期，跳转到登录页");
        removeToken();
        window.location.href = "/login.jsp";
        return false;
    }

    console.log("用户已登录，token:", token);
    return true;
}
