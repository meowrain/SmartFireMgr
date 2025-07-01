<%--
  Created by IntelliJ IDEA.
  User: 隆邱豪
  Date: 2025-06-26
  Time: 14:59
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
  <link rel="stylesheet" href="vendors/simple-line-icons/css/simple-line-icons.css">
  <link rel="stylesheet" href="vendors/flag-icon-css/css/flag-icon.min.css">
  <link rel="stylesheet" href="vendors/css/vendor.bundle.base.css">
  <!-- endinject -->
  <!-- Plugin css for this page -->
  <link rel="stylesheet" href="./vendors/daterangepicker/daterangepicker.css">
  <link rel="stylesheet" href="./vendors/chartist/chartist.min.css">
  <!-- End plugin css for this page -->
  <!-- inject:css -->
  <!-- endinject -->
  <!-- Layout styles -->
  <link rel="stylesheet" href="./css/style.css">
  <!-- End layout styles -->
  <link rel="shortcut icon" href="./images/favicon.png" />
</head>
<body>

<div class="container-scroller">
  <!-- partial:partials/_navbar.html -->

  <jsp:include page="header.jsp"></jsp:include>

  <!-- partial -->
  <div class="container-fluid page-body-wrapper">
    <!-- partial:partials/_sidebar.html -->

    <jsp:include page="left.jsp"></jsp:include>

    <!-- partial -->

    <div class="col-lg-10 grid-margin stretch-card">
      <div class="card">
        <div class="card-body">
          <h4 class="card-title">设备管理 > 设备列表</h4>

          <form action="SearchLikeEquipmentServlet" method="post" >
            <div class="form-group" style="width: 500px">
              <div class="input-group">
                <input value="${equipment_name}" type="text" name="equipment_name" class="form-control" placeholder="请输入要搜索的设备名称..." aria-label="Recipient's username" aria-describedby="basic-addon2">
                <div class="input-group-append">
                  <button class="btn btn-sm btn-primary" type="submit">搜索</button>
                </div>
              </div>
            </div>
          </form>


          <table class="table table-hover">
            <thead>
            <tr>
              <th>设备编号</th>
              <th>设备名称</th>
              <th>设备类型</th>
              <th>规格型号</th>
              <th>生产日期</th>
              <th>有效期至</th>
              <th>所在位置</th>
              <th>当前状态</th>
              <th>负责人</th>
              <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${equipments}" var="equipment" varStatus="e">
              <tr>
                <td>${e.index + 1}</td>
                <td>${equipment.equipment_name}</td>
                <td>${equipment.equipment_type}</td>
                <td>${equipment.specification}</td>
                <td>${equipment.production_date}</td>
                <td>${equipment.expiry_date}</td>
                <td>${equipment.location}</td>
                <td>
                  <c:if test="${equipment.status == 1}">
                    <label class="badge badge-success">正常</label>
                  </c:if>
                  <c:if test="${equipment.status == 2}">
                    <label class="badge badge-warning">维修中</label>
                  </c:if>
                  <c:if test="${equipment.status == 3}">
                    <label class="badge badge-danger">已过期</label>
                  </c:if>
                  <c:if test="${equipment.status == 4}">
                    <label class="badge badge-dark">待检修</label>
                  </c:if>
                </td>
                <td>${equipment.responsible_person}</td>
                <td>
                  <a href="SearchEquipmentServlet?equipment_id=${equipment.equipment_id}" class="btn btn-outline-secondary btn-sm badge-success">修改</a>
                  <a href="DeleteEquipmentServlet?equipment_id=${equipment.equipment_id}" onclick="return confirm('您确认删除吗？')" class="btn btn-outline-secondary btn-sm badge-danger">删除</a>
                </td>
              </tr>
            </c:forEach>>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- main-panel ends -->
  </div>
  <!-- page-body-wrapper ends -->
</div>
<!-- container-scroller -->
<!-- plugins:js -->
<script src="vendors/js/vendor.bundle.base.js"></script>
<!-- endinject -->
<!-- Plugin js for this page -->
<script src="./vendors/chart.js/Chart.min.js"></script>
<script src="./vendors/moment/moment.min.js"></script>
<script src="./vendors/daterangepicker/daterangepicker.js"></script>
<script src="./vendors/chartist/chartist.min.js"></script>
<!-- End plugin js for this page -->
<!-- inject:js -->
<script src="js/off-canvas.js"></script>
<script src="js/misc.js"></script>
<!-- endinject -->
<!-- Custom js for this page -->
<script src="./js/dashboard.js"></script>
<!-- End custom js for this page -->
</body>
</html>

