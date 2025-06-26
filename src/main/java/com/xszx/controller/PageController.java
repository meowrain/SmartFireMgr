package com.xszx.controller;

import com.xszx.common.router.interfaces.Controller;
import com.xszx.common.router.interfaces.RequestMapping;
import com.xszx.common.router.interfaces.PathVariable;
import com.xszx.common.router.interfaces.RequestParam;
import com.xszx.common.router.enums.HttpMethod;
import com.xszx.common.router.core.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JSP页面控制器 - 演示ModelAndView的使用
 */
@Controller
public class PageController {

    /**
     * 首页 - 传递用户信息到index.jsp
     */
    @RequestMapping(path = "/home", method = HttpMethod.GET)
    public ModelAndView home(@RequestParam(value = "username", defaultValue = "游客") String username) {
        ModelAndView mv = new ModelAndView("index");

        // 设置用户信息
        mv.addObject("username", username);
        mv.addObject("currentTime", new Date());
        mv.addObject("systemName", "智慧消防管理系统");

        // 模拟一些统计数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDevices", 156);
        stats.put("onlineDevices", 142);
        stats.put("alertCount", 3);
        stats.put("offlineDevices", 14);

        mv.addObject("stats", stats);

        return mv;
    }

    /**
     * 用户详情页面
     */
    @RequestMapping(path = "/user/{id}", method = HttpMethod.GET)
    public ModelAndView userDetail(@PathVariable("id") Long userId) {
        ModelAndView mv = new ModelAndView("user-detail");

        // 模拟用户数据
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("name", "用户" + userId);
        user.put("email", "user" + userId + "@example.com");
        user.put("role", "管理员");
        user.put("lastLogin", new Date());

        mv.addObject("user", user);
        mv.addObject("pageTitle", "用户详情 - " + user.get("name"));

        return mv;
    }

    /**
     * 设备列表页面
     */
    @RequestMapping(path = "/devices", method = HttpMethod.GET)
    public ModelAndView deviceList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        ModelAndView mv = new ModelAndView("device-list");

        // 模拟设备数据
        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("currentPage", page);
        pageInfo.put("pageSize", size);
        pageInfo.put("totalPages", 10);
        pageInfo.put("totalElements", 95);

        mv.addObject("pageInfo", pageInfo);
        mv.addObject("devices", generateMockDevices(page, size));
        mv.addObject("pageTitle", "设备管理");

        return mv;
    }

    /**
     * 报告页面
     */
    @RequestMapping(path = "/reports", method = HttpMethod.GET)
    public ModelAndView reports() {
        ModelAndView mv = new ModelAndView("reports");

        // 模拟报告数据
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("totalAlerts", 45);
        reportData.put("resolvedAlerts", 42);
        reportData.put("pendingAlerts", 3);
        reportData.put("avgResponseTime", "15分钟");

        mv.addObject("reportData", reportData);
        mv.addObject("pageTitle", "系统报告");
        mv.addObject("generateTime", new Date());

        return mv;
    }

    /**
     * 生成模拟设备数据
     */
    private Object[] generateMockDevices(int page, int size) {
        Object[] devices = new Object[size];
        for (int i = 0; i < size; i++) {
            Map<String, Object> device = new HashMap<>();
            int deviceId = (page - 1) * size + i + 1;
            device.put("id", deviceId);
            device.put("name", "消防设备-" + deviceId);
            device.put("type", i % 3 == 0 ? "烟感器" : (i % 3 == 1 ? "温感器" : "水压监测器"));
            device.put("status", i % 5 == 0 ? "离线" : "正常");
            device.put("location", "楼层" + ((i % 10) + 1));
            devices[i] = device;
        }
        return devices;
    }
}
