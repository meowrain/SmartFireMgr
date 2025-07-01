package com.xszx.dao.entity;

import java.util.Date;

/**
 * 设备数据库实体对象
 */
public class EquipmentDAO {
    private Integer equipment_id;
    private String equipment_name;
    private String equipment_type;
    private String specification;
    private Date production_date;
    private Date expiry_date;
    private String location;
    private String status;
    private String responsible_person;

    public EquipmentDAO() {
    }

    public EquipmentDAO(Integer equipment_id, String equipment_name, String equipment_type, String specification, Date production_date, Date expiry_date, String location, String status, String responsible_person) {
        this.equipment_id = equipment_id;
        this.equipment_name = equipment_name;
        this.equipment_type = equipment_type;
        this.specification = specification;
        this.production_date = production_date;
        this.expiry_date = expiry_date;
        this.location = location;
        this.status = status;
        this.responsible_person = responsible_person;
    }

    public Integer getEquipmentId() {
        return equipment_id;
    }

    public void setEquipmentId(Integer equipment_id) {
        this.equipment_id = equipment_id;
    }

    public String getEquipmentName() {
        return equipment_name;
    }

    public void setEquipmentName(String equipment_name) {
        this.equipment_name = equipment_name;
    }

    public String getEquipmentType() {
        return equipment_type;
    }

    public void setEquipmentType(String equipment_type) {
        this.equipment_type = equipment_type;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public Date getProductionDate() {
        return production_date;
    }

    public void setProductionDate(Date production_date) {
        this.production_date = production_date;
    }

    public Date getExpiryDate() {
        return expiry_date;
    }

    public void setExpiryDate(Date expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponsiblePerson() {
        return responsible_person;
    }

    public void setResponsiblePerson(String responsible_person) {
        this.responsible_person = responsible_person;
    }
}
