<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>Stellar Admin</title>
    <!-- plugins:css -->
    <link rel="stylesheet" href="../../vendors/simple-line-icons/css/simple-line-icons.css">
    <link rel="stylesheet" href="../../vendors/flag-icon-css/css/flag-icon.min.css">
    <link rel="stylesheet" href="../../vendors/css/vendor.bundle.base.css">
    <!-- endinject -->
    <!-- Plugin css for this page -->
    <!-- End plugin css for this page -->
    <!-- inject:css -->
    <!-- endinject -->
    <!-- Layout styles -->
    <link rel="stylesheet" href="../../css/style.css"  /><!-- End layout styles -->
    <link rel="shortcut icon" href="../../images/favicon.png"/>
</head>
<body>
<div class="container-scroller">
    <div class="container-fluid page-body-wrapper full-page-wrapper">
        <div class="content-wrapper d-flex align-items-center auth">
            <div class="row flex-grow">
                <div class="col-lg-4 mx-auto">
                    <div class="auth-form-dark text-left p-5">
                        <div class="brand-logo">
                            <img src="../../images/img.png">
                        </div>
                        <h4>你好同学，欢迎登入防火管理系统！</h4>

                        <form class="pt-3">
                            <div class="form-group">
                                <input type="email" class="form-control form-control-lg" id="exampleInputEmail1"
                                       placeholder="用户名">
                            </div>
                            <div class="form-group">
                                <input type="password" class="form-control form-control-lg" id="exampleInputPassword1"
                                       placeholder="密码">
                            </div>
                            <div class="mt-3">
                                <%--                                <a class="btn btn-block btn-primary btn-lg font-weight-medium auth-form-btn"--%>
                                <%--                                   href="../../index.jsp">登入</a>--%>
                                <button type="button" id="loginBtn"
                                        class="btn btn-block btn-primary btn-lg font-weight-medium auth-form-btn">
                                    登入
                                </button>
                                <span id="respMessage"></span>
                            </div>
                            <div class="my-2 d-flex justify-content-between align-items-center">
                                <div class="form-check">
                                    <label class="form-check-label text-muted">
                                        <input type="checkbox" class="form-check-input"> 保持登录 </label>
                                </div>
                                <a href="#" class="auth-link text-white">忘记密码?</a>
                            </div>
                            <div class="text-center mt-4 font-weight-light"> 还没有账号？ <a
                                    href="register.jsp" class="text-primary">注册</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <!-- content-wrapper ends -->
    </div>
    <!-- page-body-wrapper ends -->
</div>
<script>
    let loginBtn = document.getElementById("loginBtn");
    let respMessage = document.getElementById("respMessage");
    loginBtn.addEventListener("click", function () {
        const username = document.getElementById("exampleInputEmail1").value;
        const password = document.getElementById("exampleInputPassword1").value;
        const payload = {
            username: username,
            password: password
        };

        fetch("/api/admin/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
            .then(response => response.json())
            .then(data => {
                console.log("后端返回：", data);
                console.log(data.code)
                if (data.code === "0") {
                    // 例如跳转主页
                    window.location.href = "../../index.jsp";
                } else {
                    respMessage.innerHTML = "<span class='text-danger'>登录失败：" + (data.message || "未知错误") + "</span>";
                    <%--respMessage.setHTMLUnsafe("${data.message}");--%>
                    // alert("登录失败：" + (data.message || "未知错误"));
                }
            })
            .catch(error => {
                console.error("请求失败：", error);
                alert("请求失败，请检查网络或后端服务。");
            });
    });

</script>
<!-- container-scroller -->
<!-- plugins:js -->
<script src="../../vendors/js/vendor.bundle.base.js"></script>
<!-- endinject -->
<!-- Plugin js for this page -->
<!-- End plugin js for this page -->
<!-- inject:js -->
<script src="../../js/off-canvas.js"></script>
<script src="../../js/misc.js"></script>
<!-- endinject -->
</body>
</html>