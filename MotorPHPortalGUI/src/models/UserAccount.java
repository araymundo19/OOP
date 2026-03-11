/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author Winter Melon
 */
public class UserAccount {
    private String employeeId; // USERNAME
    private String password;
    private String role; // FOR FUTURE IMPLEMENTATIONS WE WILL USE DEPARTMENTS INSTEAD
    private Departments department; // FOR RBAC (DEPARTMENTALIZED)
    
    public UserAccount(String employeeId, String password, String role, Departments department) {
        this.employeeId = employeeId;
        this.password = password;
        this.role = role;
        this.department = department;
    }

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
    
}