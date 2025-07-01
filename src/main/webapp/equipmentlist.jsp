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

          <div class="col-lg-10 grid-margin stretch-card">
            <div class="card">
              <div class="card-body">
                <h4 class="card-title">设备管理 > 设备列表</h4>

                <!-- 搜索和添加按钮区域 -->
                <div class="d-flex justify-content-between align-items-center mb-3">
                  <div class="form-group mb-0" style="width: 500px">
                    <div class="input-group">
                      <input type="text" name="equipment_name" class="form-control" placeholder="请输入要搜索的设备名称..."
                        aria-label="Recipient's username" aria-describedby="basic-addon2">
                      <div class="input-group-append">
                        <button class="btn btn-sm btn-primary" type="button" onclick="searchEquipment()">搜索</button>
                      </div>
                    </div>
                  </div>
                  <div>
                    <a href="equipmentadd.jsp" class="btn btn-success">
                      <i class="icon-plus"></i> 添加设备
                    </a>
                  </div>
                </div>


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
                  <tbody id="equipmentTableBody">
                    <!-- 设备数据将通过JavaScript动态加载 -->
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

      <!-- 引入认证脚本 -->
      <script src="js/auth.js"></script>

      <!-- 设备列表页面的API对接脚本 -->
      <script>
        // 页面加载完成后获取设备列表
        document.addEventListener('DOMContentLoaded', function () {
          // 检查用户是否已登录
          if (checkAuthentication()) {
            // 认证通过后再加载设备列表
            loadEquipmentList();
          }
        });

        // 加载设备列表
        function loadEquipmentList() {
          fetch('/api/equip/all')
            .then(response => {
              if (!response.ok) {
                throw new Error('获取设备列表失败');
              }
              return response.json();
            })
            .then(data => {
              if (data.code === '0') {
                renderEquipmentTable(data.data);
              } else {
                console.error('获取设备列表失败:', data.message);
                showError('获取设备列表失败: ' + data.message);
              }
            })
            .catch(error => {
              console.error('请求失败:', error);
              showError('网络请求失败，请检查连接');
            });
        }

        // 搜索设备
        function searchEquipment() {
          const searchInput = document.querySelector('input[name="equipment_name"]');
          const equipmentName = searchInput.value.trim();

          if (!equipmentName) {
            loadEquipmentList(); // 如果搜索框为空，加载所有设备
            return;
          }

          fetch('/api/equip/search?name=' + encodeURIComponent(equipmentName))
            .then(response => {
              if (!response.ok) {
                throw new Error('搜索设备失败');
              }
              return response.json();
            })
            .then(data => {
              if (data.code === '0') {
                renderEquipmentTable(data.data);
              } else {
                console.error('搜索设备失败:', data.message);
                showError('搜索设备失败: ' + data.message);
              }
            })
            .catch(error => {
              console.error('搜索请求失败:', error);
              showError('搜索请求失败，请检查连接');
            });
        }

        // 渲染设备表格
        function renderEquipmentTable(equipments) {
          const tbody = document.querySelector('table tbody');
          tbody.innerHTML = '';

          if (!equipments || equipments.length === 0) {
            tbody.innerHTML = '<tr><td colspan="10" class="text-center">暂无设备数据</td></tr>';
            return;
          }

          equipments.forEach((equipment, index) => {
            const row = document.createElement('tr');

            // 格式化日期函数
            const formatDate = (dateStr) => {
              if (!dateStr) return '';
              try {
                const date = new Date(dateStr);
                return date.toLocaleDateString('zh-CN');
              } catch (e) {
                return dateStr;
              }
            };

            row.innerHTML =
              '<td>' + (equipment.equipmentId || (index + 1)) + '</td>' +
              '<td>' + (equipment.equipmentName || '') + '</td>' +
              '<td>' + (equipment.equipmentType || '') + '</td>' +
              '<td>' + (equipment.specification || '') + '</td>' +
              '<td>' + formatDate(equipment.productionDate) + '</td>' +
              '<td>' + formatDate(equipment.expiryDate) + '</td>' +
              '<td>' + (equipment.location || '') + '</td>' +
              '<td>' + getStatusBadge(equipment.status) + '</td>' +
              '<td>' + (equipment.responsiblePerson || '') + '</td>' +
              '<td>' +
              '<button onclick="editEquipment(' + equipment.equipmentId + ')" class="btn btn-outline-primary btn-sm">修改</button> ' +
              '<button onclick="deleteEquipment(' + equipment.equipmentId + ')" class="btn btn-outline-danger btn-sm">删除</button>' +
              '</td>';
            tbody.appendChild(row);
          });
        }

        // 获取状态徽章
        function getStatusBadge(status) {
          // 处理字符串和数字类型的状态
          const statusStr = String(status);
          switch (statusStr) {
            case '1':
              return '<label class="badge badge-success">正常</label>';
            case '2':
              return '<label class="badge badge-warning">维修中</label>';
            case '3':
              return '<label class="badge badge-danger">已过期</label>';
            case '4':
              return '<label class="badge badge-dark">待检修</label>';
            default:
              return '<label class="badge badge-secondary">未知(' + status + ')</label>';
          }
        }

        // 删除设备
        function deleteEquipment(equipmentId) {
          if (!confirm('您确认删除这个设备吗？')) {
            return;
          }

          fetch('/api/equip/delete/' + equipmentId, {
            method: 'DELETE'
          })
            .then(response => {
              if (!response.ok) {
                throw new Error('删除设备失败');
              }
              return response.json();
            })
            .then(data => {
              if (data.code === '0') {
                showSuccess('设备删除成功');
                loadEquipmentList(); // 重新加载列表
              } else {
                console.error('删除设备失败:', data.message);
                showError('删除设备失败: ' + data.message);
              }
            })
            .catch(error => {
              console.error('删除请求失败:', error);
              showError('删除请求失败，请检查连接');
            });
        }

        // 编辑设备
        function editEquipment(equipmentId) {
          window.location.href = 'equipmentupdate.jsp?id=' + equipmentId;
        }

        // 显示错误消息
        function showError(message) {
          // 可以使用更好的提示组件，这里简单使用alert
          alert('错误: ' + message);
        }

        // 显示成功消息
        function showSuccess(message) {
          alert('成功: ' + message);
        }
      </script>

      <!-- endinject -->
    </body>

    </html>