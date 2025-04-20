package com.dct.parkingticket.dto.auth;

import com.dct.parkingticket.dto.mapping.IRoleDTO;
import com.dct.parkingticket.dto.response.AuditingEntityDTO;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class AccountDTO extends AuditingEntityDTO {

    private Integer id;
    private String fullname;
    private String username;
    private String email;
    private String address;
    private String phone;
    private String status;
    private String deviceId;
    private List<IRoleDTO> authorities = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<IRoleDTO> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<IRoleDTO> authorities) {
        this.authorities = authorities;
    }
}
