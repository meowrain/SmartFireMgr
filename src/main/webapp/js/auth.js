/**
 * 认证相关的JavaScript工具函数
 */

// 获取保存的token
function getToken() {
  return localStorage.getItem("token") || sessionStorage.getItem("token");
}

// 保存token
function saveToken(token) {
  localStorage.setItem("token", token);
}

// 删除token
function removeToken() {
  localStorage.removeItem("token");
  sessionStorage.removeItem("token");
}

// 检查用户是否已登录
function isLoggedIn() {
  return getToken() !== null;
}

// 重写fetch函数，自动添加Authorization头
const originalFetch = window.fetch;
window.fetch = function (url, options = {}) {
  const token = getToken();

  if (token) {
    options.headers = options.headers || {};
    options.headers["Authorization"] = token;
  }

  return originalFetch(url, options)
    .then((response) => {
      // 如果返回401，说明token无效，跳转到登录页
      if (response.status === 401) {
        console.warn("Token无效，跳转到登录页");
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

// 拦截XMLHttpRequest请求，自动添加Authorization头
const originalXMLHttpRequest = window.XMLHttpRequest;
window.XMLHttpRequest = function () {
  const xhr = new originalXMLHttpRequest();
  const originalOpen = xhr.open;
  const originalSend = xhr.send;

  xhr.open = function (method, url, async, user, password) {
    originalOpen.call(this, method, url, async, user, password);
  };

  xhr.send = function (data) {
    const token = getToken();
    if (token) {
      this.setRequestHeader("Authorization", token);
    }
    originalSend.call(this, data);
  };

  return xhr;
};

// 页面加载时检查认证状态
document.addEventListener("DOMContentLoaded", function () {
  const currentPath = window.location.pathname;

  // 白名单页面，不需要检查认证
  const whiteList = ["/login.jsp", "/register.jsp", "/"];

  // 检查是否为静态资源
  const staticExtensions = [
    ".css",
    ".js",
    ".png",
    ".jpg",
    ".jpeg",
    ".gif",
    ".ico",
    ".woff",
    ".woff2",
    ".ttf",
    ".eot",
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

    // 特殊处理：如果用户已登录且在登录页，跳转到首页
    if (currentPath === "/login.jsp" && isLoggedIn()) {
      console.log("用户已登录，从登录页跳转到首页");
      window.location.href = "/index.jsp";
    }
    return;
  }

  // 检查是否需要认证
  if (!isLoggedIn()) {
    console.warn("用户未登录，跳转到登录页。当前页面:", currentPath);
    window.location.href = "/login.jsp";
  } else {
    console.log("用户已登录，token:", getToken());
  }
});

// 退出登录函数
function logout() {
  removeToken();
  window.location.href = "/login.jsp";
}

// 检查认证状态的函数，供页面手动调用
function checkAuthentication() {
  const currentPath = window.location.pathname;

  // 白名单页面，不需要检查认证
  const whiteList = ["/login.jsp", "/register.jsp", "/"];

  if (whiteList.includes(currentPath)) {
    // 特殊处理：如果用户已登录且在登录页，跳转到首页
    if (currentPath === "/login.jsp" && isLoggedIn()) {
      console.log("用户已登录，从登录页跳转到首页");
      window.location.href = "/index.jsp";
    }
    return true;
  }

  // 检查是否需要认证
  if (!isLoggedIn()) {
    console.warn("用户未登录，跳转到登录页。当前页面:", currentPath);
    window.location.href = "/login.jsp";
    return false;
  } else {
    console.log("用户已登录，token:", getToken());
    return true;
  }
}
