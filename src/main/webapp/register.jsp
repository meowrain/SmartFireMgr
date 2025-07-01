<%--
  Created by IntelliJ IDEA.
  User: meowr
  Date: 2025/6/26
  Time: 15:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
  <link rel="stylesheet" href="../../css/style.css" /> <!-- End layout styles -->
  <link rel="shortcut icon" href="../../images/favicon.png" />
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
            <h4>智慧消防系统注册页</h4>
<%--            <h6 class="font-weight-light">Signing up is easy. It only takes a few steps</h6>--%>
            <form class="pt-3">
              <div class="form-group">
                <input type="text" class="form-control form-control-lg" id="exampleInputUsername1" placeholder="用户名">
              </div>
<%--              <div class="form-group">--%>
<%--                <input type="email" class="form-control form-control-lg" id="exampleInputEmail1" placeholder="Email">--%>
<%--              </div>--%>
<%--              <div class="form-group">--%>
<%--                <select class="form-control form-control-lg" id="exampleFormControlSelect2">--%>
<%--                  <option>Country</option>--%>
<%--                  <option>United States of America</option>--%>
<%--                  <option>United Kingdom</option>--%>
<%--                  <option>India</option>--%>
<%--                  <option>Germany</option>--%>
<%--                  <option>Argentina</option>--%>
<%--                </select>--%>
<%--              </div>--%>
              <div class="form-group">
                <input type="password" class="form-control form-control-lg" id="exampleInputPassword1" placeholder="密码">
              </div>
<%--              <div class="mb-4">--%>
<%--                <div class="form-check">--%>
<%--                  <label class="form-check-label text-muted">--%>
<%--                    <input type="checkbox" class="form-check-input"> I agree to all Terms & Conditions </label>--%>
<%--                </div>--%>
<%--              </div>--%>
              <div class="mt-3">
                  <button type="button" id="registerBtn"
                          class="btn btn-block btn-primary btn-lg font-weight-medium auth-form-btn">
                     注册
                  </button>
                  <span id="respMessage"></span>
              </div>
              <div class="text-center mt-4 font-weight-light">已经有账号了？ <a href="login.jsp" class="text-primary">登录</a>
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
    let registerBtn = document.getElementById("registerBtn");
    let respMessage = document.getElementById("respMessage");
    registerBtn.addEventListener("click", function () {
        const username = document.getElementById("exampleInputUsername1").value;
        const password = document.getElementById("exampleInputPassword1").value;
        const payload = {
            username: username,
            password: password
        };

        fetch("/api/admin/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
            .then(response => response.json())
            .then(data => {
                if (data.code === "0") {
                    // 例如跳转主页
                    window.location.href = "/login.jsp"
                }else{
                    respMessage.innerHTML = "<span class='text-danger'>注册失败：" + (data.message || "未知错误") + "</span>";
                }
            })
            .catch(error => {
                console.error("请求失败：", error);

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