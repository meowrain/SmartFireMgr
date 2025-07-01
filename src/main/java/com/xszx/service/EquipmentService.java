package com.xszx.service;

import com.xszx.dao.entity.EquipmentDAO;
import com.xszx.dto.req.equipment.SearchEquipmentRequestDTO;
import com.xszx.dto.resp.equipment.SearchEquipmentResponseDTO;

import java.util.List;

public interface EquipmentService {

    /**
     * 获取所有设备列表
     */
    List<EquipmentDAO> getAllEquipments();

    /**
     * 根据名称模糊搜索设备
     */
    List<EquipmentDAO> searchByName(String equipmentName);

    void add(EquipmentDAO equipmentDAO);

    void update(EquipmentDAO equipmentDAO);

    void delete(Integer id);

    EquipmentDAO getById(Integer id);
}
