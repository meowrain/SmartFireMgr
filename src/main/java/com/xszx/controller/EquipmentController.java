package com.xszx.controller;

import com.xszx.common.result.Result;
import com.xszx.common.result.ResultBuilder;
import com.xszx.common.router.enums.HttpMethod;
import com.xszx.common.router.interfaces.*;
import com.xszx.dao.entity.EquipmentDAO;
import com.xszx.service.EquipmentService;
import com.xszx.service.impl.EquipmentServiceImpl;
import com.xszx.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class EquipmentController {

    private final EquipmentService equipmentService = new EquipmentServiceImpl();

    /**
     * 获取当前用户的所有设备列表
     */
    @RequestMapping(path = "/api/equip/all", method = HttpMethod.GET)
    @ResponseBody
    public Result<List<EquipmentDAO>> getAllEquipments(HttpServletRequest request) {
        Integer userId = JwtUtil.getCurrentUserId(request);
        List<EquipmentDAO> equipments = equipmentService.getAllEquipments(userId);
        return ResultBuilder.success(equipments);
    }

    /**
     * 根据名称模糊搜索当前用户的设备
     */
    @RequestMapping(path = "/api/equip/search", method = HttpMethod.GET)
    @ResponseBody
    public Result<List<EquipmentDAO>> searchByName(@RequestParam("name") String equipmentName,
            HttpServletRequest request) throws UnsupportedEncodingException {
        equipmentName = new String(equipmentName.getBytes("ISO-8859-1"), "UTF-8");
        Integer userId = JwtUtil.getCurrentUserId(request);
        List<EquipmentDAO> equipments = equipmentService.searchByName(equipmentName, userId);
        return ResultBuilder.success(equipments);
    }

    @RequestMapping(path = "/api/equip/add", method = HttpMethod.POST)
    @ResponseBody
    public Result<Void> addEquipment(@RequestBody EquipmentDAO equipmentDAO, HttpServletRequest request) {
        Integer userId = JwtUtil.getCurrentUserId(request);
        System.out.println(userId);
        equipmentDAO.setUserId(userId); // 设置当前用户ID
        equipmentService.add(equipmentDAO);
        return ResultBuilder.success();
    }

    @RequestMapping(path = "/api/equip/update", method = HttpMethod.PUT)
    @ResponseBody
    public Result<Void> updateEquipment(@RequestBody EquipmentDAO equipmentDAO, HttpServletRequest request) {
        Integer userId = JwtUtil.getCurrentUserId(request);
        equipmentDAO.setUserId(userId); // 确保只能更新自己的设备
        equipmentService.update(equipmentDAO);
        return ResultBuilder.success();
    }

    @RequestMapping(path = "/api/equip/delete/{id}", method = HttpMethod.DELETE)
    @ResponseBody
    public Result<Void> deleteEquipment(@PathVariable("id") Integer id, HttpServletRequest request) {
        Integer userId = JwtUtil.getCurrentUserId(request);
        equipmentService.delete(id, userId);
        return ResultBuilder.success();
    }

    @RequestMapping(path = "/api/equip/{id}", method = HttpMethod.GET)
    @ResponseBody
    public Result<EquipmentDAO> getEquipmentById(@PathVariable("id") Integer id, HttpServletRequest request) {
        Integer userId = JwtUtil.getCurrentUserId(request);
        EquipmentDAO equipmentDAO = equipmentService.getById(id, userId);
        return ResultBuilder.success(equipmentDAO);
    }
}
