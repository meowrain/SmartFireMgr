package com.xszx.dao.entity;

/**
 * 管理员实体类
 */
public class AdminDAO {
    /**
     * 无参构造器
     */
    public AdminDAO() {
    }

    public AdminDAO(Long id, String name, String password, String state) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.state = state;
    }

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 电话号码
     */
    private String phone;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 用户状态
     */
    private String state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
