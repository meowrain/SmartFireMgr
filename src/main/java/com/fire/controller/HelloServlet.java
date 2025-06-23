package com.fire.controller;

import com.fire.common.base.BaseJsonServlet;
import com.fire.common.result.Result;
import com.fire.common.result.ResultBuilder;
import com.fire.pojo.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "hello servlet",urlPatterns = "/hello")
public class HelloServlet extends BaseJsonServlet {

    
}
