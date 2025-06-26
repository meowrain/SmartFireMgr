package com.xszx.common.router.core;

import java.util.HashMap;
import java.util.Map;

/**
 * ModelAndView - 封装视图名称和模型数据
 * 类似于Spring MVC的ModelAndView
 */
public class ModelAndView {
    private String viewName;
    private Map<String, Object> model;

    public ModelAndView() {
        this.model = new HashMap<>();
    }

    public ModelAndView(String viewName) {
        this.viewName = viewName;
        this.model = new HashMap<>();
    }

    public ModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model != null ? model : new HashMap<>();
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model != null ? model : new HashMap<>();
    }

    /**
     * 添加单个属性到模型
     */
    public ModelAndView addObject(String attributeName, Object attributeValue) {
        this.model.put(attributeName, attributeValue);
        return this;
    }

    /**
     * 添加多个属性到模型
     */
    public ModelAndView addAllObjects(Map<String, Object> modelMap) {
        if (modelMap != null) {
            this.model.putAll(modelMap);
        }
        return this;
    }

    /**
     * 清空模型数据
     */
    public ModelAndView clear() {
        this.model.clear();
        return this;
    }

    /**
     * 检查是否有视图名称
     */
    public boolean hasView() {
        return this.viewName != null;
    }

    /**
     * 检查模型是否为空
     */
    public boolean isEmpty() {
        return this.model.isEmpty();
    }

    @Override
    public String toString() {
        return "ModelAndView{" +
                "viewName='" + viewName + '\'' +
                ", model=" + model +
                '}';
    }
}
