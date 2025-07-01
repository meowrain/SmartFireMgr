package com.xszx.controller;

import com.xszx.common.result.Result;
import com.xszx.common.result.ResultBuilder;
import com.xszx.common.router.enums.HttpMethod;
import com.xszx.common.router.interfaces.*;
import com.xszx.dao.entity.EquipmentDAO;
import com.xszx.service.EquipmentService;
import com.xszx.service.impl.EquipmentServiceImpl;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class EquipmentController {

    private final EquipmentService equipmentService = new EquipmentServiceImpl();

    /**
     * 获取所有设备列表
     */
    @RequestMapping(path = "/api/equip/all", method = HttpMethod.GET)
    @ResponseBody
    public Result<List<EquipmentDAO>> getAllEquipments() {
        List<EquipmentDAO> equipments = equipmentService.getAllEquipments();
        return ResultBuilder.success(equipments);
    }

    /**
     * 根据名称模糊搜索设备
     */
    @RequestMapping(path = "/api/equip/search", method = HttpMethod.GET)
    @ResponseBody
    public Result<List<EquipmentDAO>> searchByName(@RequestParam("name") String equipmentName) throws UnsupportedEncodingException {
        equipmentName = new String(equipmentName.getBytes("ISO-8859-1"), "UTF-8");
        List<EquipmentDAO> equipments = equipmentService.searchByName(equipmentName);
        return ResultBuilder.success(equipments);
    }

    @RequestMapping(path = "/api/equip/add", method = HttpMethod.POST)
    @ResponseBody
    public Result<Void> addEquipment(@RequestBody EquipmentDAO equipmentDAO) {
        equipmentService.add(equipmentDAO);
        return ResultBuilder.success();
    }

    @RequestMapping(path = "/api/equip/update", method = HttpMethod.PUT)
    @ResponseBody
    public Result<Void> updateEquipment(@RequestBody EquipmentDAO equipmentDAO) {
        equipmentService.update(equipmentDAO);
        return ResultBuilder.success();
    }

    @RequestMapping(path = "/api/equip/delete/{id}", method = HttpMethod.DELETE)
    @ResponseBody
    public Result<Void> deleteEquipment(@PathVariable("id") Integer id) {
        equipmentService.delete(id);
        return ResultBuilder.success();
    }

    @RequestMapping(path = "/api/equip/{id}", method = HttpMethod.GET)
    @ResponseBody
    public Result<EquipmentDAO> getEquipmentById(@PathVariable("id") Integer id) {
        EquipmentDAO equipmentDAO = equipmentService.getById(id);
        return ResultBuilder.success(equipmentDAO);
    }
}
