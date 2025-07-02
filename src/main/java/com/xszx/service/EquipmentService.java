package com.xszx.service;

import com.xszx.dao.entity.EquipmentDAO;
import com.xszx.dto.req.equipment.SearchEquipmentRequestDTO;
import com.xszx.dto.resp.equipment.SearchEquipmentResponseDTO;

import java.util.List;

public interface EquipmentService {

    /**
     * 获取当前用户的所有设备列表
     */
    List<EquipmentDAO> getAllEquipments(Integer userId);

    /**
     * 根据名称模糊搜索当前用户的设备
     */
    List<EquipmentDAO> searchByName(String equipmentName, Integer userId);

    void add(EquipmentDAO equipmentDAO);

    void update(EquipmentDAO equipmentDAO);

    void delete(Integer id, Integer userId);

    EquipmentDAO getById(Integer id, Integer userId);
}
