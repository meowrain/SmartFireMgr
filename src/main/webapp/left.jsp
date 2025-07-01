<%--
  Created by IntelliJ IDEA.
  User: 隆邱豪
  Date: 2025-06-26
  Time: 15:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="sidebar sidebar-offcanvas" id="sidebar">
    <ul class="nav">
        <li class="nav-item nav-profile">
            <a href="#" class="nav-link">
                <div class="profile-image">
                    <img class="img-xs rounded-circle" src="images/faces/face8.jpg" alt="profile image">
                    <div class="dot-indicator bg-success"></div>
                </div>
                <div class="text-wrapper">
                    <p class="profile-name">${name}</p>
                    <p class="designation">在线</p>
                </div>
                <div class="icon-container">
                    <i class="icon-bubbles"></i>
                    <div class="dot-indicator bg-danger"></div>
                </div>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" data-toggle="collapse" href="#ui-basic" aria-expanded="false" aria-controls="ui-basic">
                <span class="menu-title">设备管理</span>
                <i class="icon-layers menu-icon"></i>
            </a>
            <div class="collapse" id="ui-basic">
                <ul class="nav flex-column sub-menu">
                    <li class="nav-item"> <a class="nav-link" href="equipmentadd.jsp">设备添加</a></li>
                    <li class="nav-item"> <a class="nav-link" href="GetEquipmentServlet">设备列表</a></li>
                    <li class="nav-item"> <a class="nav-link" href="#">设备报修</a></li>

                </ul>
            </div>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="#">
                <span class="menu-title">数据采集和监测</span>
                <i class="icon-globe menu-icon"></i>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="#">
                <span class="menu-title">日常巡查管理</span>
                <i class="icon-book-open menu-icon"></i>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="#">
                <span class="menu-title">应急处理</span>
                <i class="icon-chart menu-icon"></i>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="#">
                <span class="menu-title">宣传培训</span>
                <i class="icon-grid menu-icon"></i>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-toggle="collapse" href="#auth" aria-expanded="false" aria-controls="auth">
                <span class="menu-title">可燃气体在线监测</span>
                <i class="icon-doc menu-icon"></i>
            </a>
            <div class="collapse" id="auth">
                <ul class="nav flex-column sub-menu">
                    <li class="nav-item"> <a class="nav-link" href="#"> Login </a></li>
                    <li class="nav-item"> <a class="nav-link" href="#"> 室外消火栓在线监测 </a></li>
                    <li class="nav-item"> <a class="nav-link" href="#"> 用户消防安全管理 </a></li>
                    <li class="nav-item"> <a class="nav-link" href="#"> 在线列表 </a></li>
                    <li class="nav-item"> <a class="nav-link" href="#"> 大屏监测 </a></li>
                </ul>
            </div>
        </li>
    </ul>
</nav>

