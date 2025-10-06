package com.company.inventory3.model;

import java.time.LocalDateTime;

public class Employee {
    private int id;
    private String name;
    private String email;
    private String address;
    private String password;
    private Role role;
    private LocalDateTime createdAt;

    public enum Role {
        ADMIN, EMPLOYEE
    }


    public Employee() {}

    public Employee(String name, String email, String address, String password, Role role) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.password = password;
        this.role = role;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}