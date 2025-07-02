<%-- Created by IntelliJ IDEA. User: 隆邱豪 Date: 2025-06-26 Time: 14:59 To change this template use File | Settings | File
  Templates. --%>
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
                <h4 class="card-title">设备添加</h4>
                <p class="card-description"> 消防设备信息添加 </p>
                <form class="forms-sample" method="post">
                  <div class="form-group">
                    <label for="exampleInputName1">设备名称</label>
                    <input type="text" name="equipment_name" class="form-control" id="exampleInputName1"
                      placeholder="设备名称" required>
                    <%-- <c:if test="${not empty errorMessage}">--%>
                      <%-- <div class="alert alert-danger" style="margin-top: 5px;">--%>
                        <%-- ${errorMessage}--%>
                          <%-- </div>--%>
                            <%-- </c:if>--%>
                  </div>
                  <div class="form-group">
                    <label for="equipment_type">设备类型</label>
                    <select class="form-control" id="equipment_type" name="equipment_type" required>
                      <option value="">请选择设备类型</option>
                      <option value="灭火器">灭火器</option>
                      <option value="消防栓">消防栓</option>
                      <option value="烟雾探测器">烟雾探测器</option>
                      <option value="灭火毯">灭火毯</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label for="exampleInputPassword4">规格型号</label>
                    <input type="text" name="specification" class="form-control" id="exampleInputPassword4"
                      placeholder="规格型号">
                  </div>
                  <div class="form-group">
                    <label for="exampleInputdate">生产日期</label>
                    <input type="date" name="production_date" class="form-control" id="exampleInputdate">
                  </div>
                  <div class="form-group">
                    <label for="expiry_date">有效期至</label>
                    <input type="date" name="expiry_date" class="form-control" id="expiry_date">
                  </div>

                  <div class="form-group">
                    <label for="location">所在位置</label>
                    <input type="text" name="location" class="form-control" id="location" placeholder="所在位置">
                  </div>

                  <div class="form-group">
                    <label for="status">设备状态</label>
                    <select class="form-control" id="status" name="status">
                      <option value="1">正常</option>
                      <option value="2">维修中</option>
                      <option value="3">已过期</option>
                      <option value="4">待检修</option>
                    </select>
                  </div>

                  <div class="form-group">
                    <label for="responsible_person">负责人</label>
                    <input type="text" name="responsible_person" class="form-control" id="responsible_person"
                      placeholder="负责人">
                  </div>



                  <button type="submit" class="btn btn-primary mr-2">添加</button>
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

      <!-- 引入认证脚本 -->
      <script src="js/auth.js"></script>
      <script src="./js/userinfo.js"></script>
      <!-- 设备添加页面的API对接脚本 -->
      <script>
        document.addEventListener('DOMContentLoaded', function () {
          // 检查用户是否已登录
          if (!checkAuthentication()) {
            return; // 如果认证失败，不执行后续操作
          }

          // 阻止表单默认提交行为
          const form = document.querySelector('.forms-sample');
          form.addEventListener('submit', function (e) {
            e.preventDefault();
            addEquipment();
          });
        });

        // 添加设备
        function addEquipment() {
          // 获取表单数据
          const formData = {
            equipmentName: document.querySelector('input[name="equipment_name"]').value.trim(),
            equipmentType: document.querySelector('select[name="equipment_type"]').value,
            specification: document.querySelector('input[name="specification"]').value.trim(),
            productionDate: document.querySelector('input[name="production_date"]').value,
            expiryDate: document.querySelector('input[name="expiry_date"]').value,
            location: document.querySelector('input[name="location"]').value.trim(),
            status: document.querySelector('select[name="status"]').value,
            responsiblePerson: document.querySelector('input[name="responsible_person"]').value.trim()
          };

          // 基本验证
          if (!formData.equipmentName) {
            showError('请输入设备名称');
            return;
          }
          if (!formData.equipmentType) {
            showError('请选择设备类型');
            return;
          }
          if (!formData.specification) {
            showError('请输入规格型号');
            return;
          }
          if (!formData.location) {
            showError('请输入所在位置');
            return;
          }
          if (!formData.responsiblePerson) {
            showError('请输入负责人');
            return;
          }

          // 显示加载状态
          const submitBtn = document.querySelector('button[type="submit"]');
          const originalText = submitBtn.textContent;
          submitBtn.textContent = '添加中...';
          submitBtn.disabled = true;

          // 发送API请求
          fetch('/api/equip/add', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
          })
            .then(response => {
              if (!response.ok) {
                throw new Error('添加设备失败');
              }
              return response.json();
            })
            .then(data => {
              if (data.code === '0') {
                showSuccess('设备添加成功！');
                // 清空表单
                document.querySelector('.forms-sample').reset();
                // 3秒后跳转到设备列表页
                setTimeout(() => {
                  window.location.href = 'equipmentlist.jsp';
                }, 2000);
              } else {
                console.error('添加设备失败:', data.message);
                showError('添加设备失败: ' + data.message);
              }
            })
            .catch(error => {
              console.error('添加请求失败:', error);
              showError('添加请求失败，请检查网络连接');
            })
            .finally(() => {
              // 恢复按钮状态
              submitBtn.textContent = originalText;
              submitBtn.disabled = false;
            });
        }

        // 显示错误消息
        function showError(message) {
          // 创建错误提示元素
          showMessage(message, 'danger');
        }

        // 显示成功消息
        function showSuccess(message) {
          showMessage(message, 'success');
        }

        // 显示消息的通用方法
        function showMessage(message, type) {
          // 移除已存在的消息
          const existingAlert = document.querySelector('.alert-message');
          if (existingAlert) {
            existingAlert.remove();
          }

          // 创建新的消息元素
          const alertDiv = document.createElement('div');
          alertDiv.className = 'alert alert-' + type + ' alert-message';
          alertDiv.style.marginTop = '10px';
          alertDiv.textContent = message;

          // 插入到表单前面
          const form = document.querySelector('.forms-sample');
          form.parentNode.insertBefore(alertDiv, form);

          // 3秒后自动隐藏
          setTimeout(() => {
            if (alertDiv.parentNode) {
              alertDiv.remove();
            }
          }, 3000);
        }
      </script>

      <!-- endinject -->
    </body>

    </html>