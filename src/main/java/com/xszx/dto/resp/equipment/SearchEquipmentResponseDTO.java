package com.xszx.dto.resp.equipment;

import com.xszx.dao.entity.EquipmentDAO;

import java.util.List;

//搜索响应数据传输对象
public class SearchEquipmentResponseDTO {
    private List<EquipmentDAO> equipments;
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalElements;

    public List<EquipmentDAO> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<EquipmentDAO> equipments) {
        this.equipments = equipments;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }
}
