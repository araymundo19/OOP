/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author Winter Melon
 */
public abstract class UserAccount {
    protected String employeeId; // USERNAME
    protected String password;
    protected String role; // FOR FUTURE IMPLEMENTATIONS! WE WILL USE DEPARTMENTS FOR NOW
    protected Departments department; // FOR RBAC (DEPARTMENTALIZED)
    
    public UserAccount(String employeeId, String password, String role, Departments department) {
        setEmployeeId(employeeId);
        this.password = password;
        this.role = role;
        this.department = department;
    }

    public void setEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be empty.");
        }
        this.employeeId = employeeId;
    }
    
    public UserAccount() {}
    
    public abstract String getAccessLevel();
    
    // GETTERS
    public String getEmployeeId() {
        return employeeId;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
    
    public Departments getDepartment() {
        return department;
    }
    
    // SETTERS

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setDepartment(Departments department) {
        this.department = department;
    }    
}