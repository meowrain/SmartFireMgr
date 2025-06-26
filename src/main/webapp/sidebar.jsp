<%--
  Created by IntelliJ IDEA.
  User: meowr
  Date: 2025/6/26
  Time: 15:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- partial:partials/_sidebar.html -->
<nav class="sidebar sidebar-offcanvas" id="sidebar">
  <ul class="nav">
    <li class="nav-item nav-profile">
      <a href="#" class="nav-link">
        <div class="profile-image">
          <img class="img-xs rounded-circle" src="images/faces/face8.jpg" alt="profile image">
          <div class="dot-indicator bg-success"></div>
        </div>
        <div class="text-wrapper">
          <p class="profile-name">${username}</p>
          <p class="designation">在线</p>
        </div>
        <div class="icon-container">
          <i class="icon-bubbles"></i>
          <div class="dot-indicator bg-danger"></div>
        </div>
      </a>
    </li>

    <li class="nav-item nav-category"><span class="nav-link">UI Elements</span></li>
    <li class="nav-item">
      <a class="nav-link" data-toggle="collapse" href="#ui-basic" aria-expanded="false"
         aria-controls="ui-basic">
        <span class="menu-title">设备管理</span>
        <i class="icon-layers menu-icon"></i>
      </a>
      <div class="collapse" id="ui-basic">
        <ul class="nav flex-column sub-menu">
          <li class="nav-item"><a class="nav-link" href="/equipmentadd.jsp">设备添加</a>
          </li>
          <li class="nav-item"><a class="nav-link"
                                  href="pages/ui-features/typography.html">设备列表</a></li>
          <li class="nav-item"><a class="nav-link"
                                  href="pages/ui-features/typography.html">设备报修</a></li>
        </ul>
      </div>
    </li>
    <li class="nav-item">
      <a class="nav-link" href="pages/icons/simple-line-icons.html">
        <span class="menu-title">数据采集与监测</span>
        <i class="icon-globe menu-icon"></i>
      </a>
    </li>
    <li class="nav-item">
      <a class="nav-link" href="pages/forms/basic_elements.html">
        <span class="menu-title">日常巡检</span>
        <i class="icon-book-open menu-icon"></i>
      </a>
    </li>
    <li class="nav-item">
      <a class="nav-link" href="pages/charts/chartist.html">
        <span class="menu-title">应急处理</span>
        <i class="icon-chart menu-icon"></i>
      </a>
    </li>
    <li class="nav-item nav-category"><span class="nav-link">宣传培训</span></li>
    <li class="nav-item">
      <a class="nav-link" data-toggle="collapse" href="#auth" aria-expanded="false" aria-controls="auth">
        <span class="menu-title">大屏监测</span>
        <i class="icon-doc menu-icon"></i>
      </a>
<%--      <div class="collapse" id="auth">--%>
<%--        <ul class="nav flex-column sub-menu">--%>
<%--          <li class="nav-item"><a class="nav-link" href="pages/samples/login.html"> 登录 </a></li>--%>
<%--          <li class="nav-item"><a class="nav-link" href="pages/samples/register.html"> 注册 </a>--%>
<%--          </li>--%>
<%--          <li class="nav-item"><a class="nav-link" href="pages/samples/error-404.html"> 404 </a></li>--%>
<%--          <li class="nav-item"><a class="nav-link" href="pages/samples/error-500.html"> 500 </a></li>--%>
<%--          <li class="nav-item"><a class="nav-link" href="pages/samples/blank-page.html"> Blank--%>
<%--            Page </a></li>--%>
<%--        </ul>--%>
<%--      </div>--%>
    </li>
    <li class="nav-item pro-upgrade">
		  <span class="nav-link">
			<a class="btn btn-block px-0 btn-rounded btn-upgrade" href="#" target="_blank"> <i
                    class="icon-badge mx-2"></i> Upgrade to Pro</a>
		  </span>
    </li>
  </ul>
</nav>