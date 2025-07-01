package com.xszx.dto.req.equipment;
// 搜索请求数据传输对象
public class SearchEquipmentRequestDTO {
    private String equipment_name;
    private Integer page = 1;
    private Integer size = 10;

    public String getEquipment_name() {
        return equipment_name;
    }

    public void setEquipment_name(String equipment_name) {
        this.equipment_name = equipment_name;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
