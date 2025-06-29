package com.xszx.controller;

import com.xszx.common.result.Result;
import com.xszx.common.router.enums.HttpMethod;
import com.xszx.common.router.interfaces.Controller;
import com.xszx.common.router.interfaces.RequestMapping;
import com.xszx.common.router.interfaces.ResponseBody;
import com.xszx.dto.req.equipment.SearchEquipmentRequestDTO;
import com.xszx.dto.resp.equipment.SearchEquipmentResponseDTO;

@Controller
public class EquipmentController {

    @RequestMapping(path = "/api/equip/search", method = HttpMethod.POST)
    @ResponseBody
    public Result<SearchEquipmentResponseDTO> searchEquipment(SearchEquipmentRequestDTO searchEquipmentRequestDTO) {

        //  TODO:待实现
        return null;
    }
}
