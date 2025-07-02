package com.xszx.service.impl;

import com.xszx.dao.entity.EquipmentDAO;
import com.xszx.dto.req.equipment.SearchEquipmentRequestDTO;
import com.xszx.dto.resp.equipment.SearchEquipmentResponseDTO;
import com.xszx.service.EquipmentService;
import com.xszx.util.db.JDBCTemplate;

import java.util.ArrayList;
import java.util.List;

public class EquipmentServiceImpl implements EquipmentService {
    private final JDBCTemplate jdbcTemplate = new JDBCTemplate();

    @Override
    public void add(EquipmentDAO equipmentDAO) {
        // 确保设备添加时包含用户ID
        jdbcTemplate.insert("t_equipment", equipmentDAO);
    }

    @Override
    public void update(EquipmentDAO equipmentDAO) {
        String sql = "UPDATE t_equipment SET equipment_name=?, equipment_type=?, specification=?, production_date=?, expiry_date=?, location=?, status=?, responsible_person=? WHERE equipment_id=? AND user_id=?";
        jdbcTemplate.update(sql,
                equipmentDAO.getEquipmentName(),
                equipmentDAO.getEquipmentType(),
                equipmentDAO.getSpecification(),
                equipmentDAO.getProductionDate(),
                equipmentDAO.getExpiryDate(),
                equipmentDAO.getLocation(),
                equipmentDAO.getStatus(),
                equipmentDAO.getResponsiblePerson(),
                equipmentDAO.getEquipmentId(),
                equipmentDAO.getUserId());
    }

    @Override
    public void delete(Integer id, Integer userId) {
        String sql = "DELETE FROM t_equipment WHERE equipment_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public EquipmentDAO getById(Integer id, Integer userId) {
        String sql = "SELECT * FROM t_equipment WHERE equipment_id = ? AND user_id = ?";
        return jdbcTemplate.queryForObject(sql, EquipmentDAO.class, id, userId);
    }

    @Override
    public List<EquipmentDAO> getAllEquipments(Integer userId) {
        String sql = "SELECT * FROM t_equipment WHERE user_id = ? ORDER BY equipment_id DESC";
        return jdbcTemplate.queryForList(sql, EquipmentDAO.class, userId);
    }

    @Override
    public List<EquipmentDAO> searchByName(String equipmentName, Integer userId) {
        if (equipmentName == null || equipmentName.trim().isEmpty()) {
            return getAllEquipments(userId);
        }

        String sql = "SELECT * FROM t_equipment WHERE equipment_name LIKE ? AND user_id = ? ORDER BY equipment_id DESC";
        String keyword = "%" + equipmentName.trim() + "%";

        // 添加调试日志
        System.out.println("搜索关键字: " + equipmentName);
        System.out.println("用户ID: " + userId);
        System.out.println("SQL: " + sql);
        System.out.println("模糊查询参数: " + keyword);

        List<EquipmentDAO> result = jdbcTemplate.queryForList(sql, EquipmentDAO.class, keyword, userId);
        System.out.println("查询结果数量: " + result.size());

        return result;
    }
}
