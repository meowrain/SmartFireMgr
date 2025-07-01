<%--
  Created by IntelliJ IDEA.
  User: 隆邱豪
  Date: 2025-06-27
  Time: 19:09
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

    <div class="col-10">
      <div class="card">
        <div class="card-body">
          <h4 class="card-title">设备修改</h4>
          <p class="card-description"> 消防设备信息修改 </p>
          <form class="forms-sample" method="post" action="UpdateEquipmentServlet">

            <input type="hidden" name="equipment_id" value="${equipment.equipment_id}">

            <div class="form-group">
              <label for="exampleInputName1">设备名称</label>
              <input value="${equipment.equipment_name}" type="text" name="equipment_name" class="form-control" id="exampleInputName1" placeholder="设备名称">
            </div>
            <div class="form-group">
              <label for="equipment_type">设备类型</label>
              <select class="form-control" id="equipment_type" name="equipment_type">
                <c:if test="${equipment.equipment_type == '灭火器'}">
                  <option selected>灭火器</option>
                  <option>消防栓</option>
                  <option>烟雾探测器</option>
                  <option>灭火毯</option>
                </c:if>
                <c:if test="${equipment.equipment_type == '消防栓'}">
                  <option>灭火器</option>
                  <option selected>消防栓</option>
                  <option>烟雾探测器</option>
                  <option>灭火毯</option>
                </c:if>
                <c:if test="${equipment.equipment_type == '烟雾探测器'}">
                  <option>灭火器</option>
                  <option>消防栓</option>
                  <option selected>烟雾探测器</option>
                  <option>灭火毯</option>
                </c:if>
                <c:if test="${equipment.equipment_type == '灭火毯'}">
                  <option>灭火器</option>
                  <option>消防栓</option>
                  <option>烟雾探测器</option>
                  <option selected>灭火毯</option>
                </c:if>
              </select>
            </div>
            <div class="form-group">
              <label for="exampleInputPassword4">规格型号</label>
              <input value="${equipment.specification}" type="text" name="specification" class="form-control" id="exampleInputPassword4" placeholder="规格型号">
            </div>
            <div class="form-group">
              <label for="exampleInputdate">生产日期</label>
              <input value="${equipment.production_date}" type="date" name="production_date" class="form-control" id="exampleInputdate">
            </div>
            <div class="form-group">
              <label for="expiry_date">有效期至</label>
              <input value="${equipment.expiry_date}" type="date" name="expiry_date" class="form-control" id="expiry_date">
            </div>

            <div class="form-group">
              <label for="location">所在位置</label>
              <input value="${equipment.location}" type="text" name="location" class="form-control" id="location" placeholder="所在位置">
            </div>

            <div class="form-group">
              <label for="status">设备状态</label>
              <select class="form-control" id="status" name="status">
                <c:if test="${equipment.status == '1'}">
                  <option value="1" selected>正常</option>
                  <option value="2">维修中</option>
                  <option value="3">已过期</option>
                  <option value="4">待检修</option>
                </c:if>
                <c:if test="${equipment.status == '2'}">
                  <option value="1">正常</option>
                  <option value="2" selected>维修中</option>
                  <option value="3">已过期</option>
                  <option value="4">待检修</option>
                </c:if>
                <c:if test="${equipment.status == '3'}">
                  <option value="1">正常</option>
                  <option value="2">维修中</option>
                  <option value="3" selected>已过期</option>
                  <option value="4">待检修</option>
                </c:if>
                <c:if test="${equipment.status == '4'}">
                  <option value="1">正常</option>
                  <option value="2">维修中</option>
                  <option value="3">已过期</option>
                  <option value="4" selected>待检修</option>
                </c:if>
              </select>
            </div>

            <div class="form-group">
              <label for="responsible_person">负责人</label>
              <input value="${equipment.responsible_person}" type="text" name="responsible_person" class="form-control" id="responsible_person" placeholder="负责人">
            </div>


            <div class="form-group">
              <label>设备图片</label>
              <input type="file" name="img[]" class="file-upload-default">
              <div class="input-group col-xs-12">
                <input type="file" class="form-control file-upload-info" disabled placeholder="Upload Image">
                <span class="input-group-append">
                            <button class="file-upload-browse btn btn-primary" type="button">Upload</button>
                          </span>
              </div>
            </div>
            <button type="submit" class="btn btn-primary mr-2">修改</button>
            <button type="reset" class="btn btn-light">重置</button>
          </form>
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


