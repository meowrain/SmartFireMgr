<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <% // Check if user is logged in if (session.getAttribute("username")==null) { response.sendRedirect("login.jsp"); }
        %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>智慧消防管理系统 - 主页</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    margin: 0;
                    padding: 0;
                    background-color: #f4f4f4;
                }

                .header {
                    background-color: #007bff;
                    color: white;
                    padding: 15px 20px;
                    text-align: center;
                }

                .header h1 {
                    margin: 0;
                }

                .content {
                    padding: 20px;
                }

                .logout {
                    float: right;
                    color: white;
                    text-decoration: none;
                }
            </style>
        </head>

        <body>
            <div class="header">
                <a href="LogoutServlet" class="logout">登出</a>
                <h1>智慧消防管理系统</h1>
            </div>

            <div class="content">
                <h2>欢迎, <%= session.getAttribute("username") %>!</h2>
                <p>这里是系统主面板。您可以在这里管理消防设备、查看警报和生成报告。</p>
                <!-- Dashboard content will go here -->
            </div>

        </body>

        </html>