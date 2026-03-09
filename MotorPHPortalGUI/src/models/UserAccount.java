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
    private String employeeId; //USERNAME
    private String password;
    private Departments role;
    
    public UserAccount(String employeeId, String password, Departments role) {
        this.employeeId = employeeId;
        this.password = password;
        this.role = role;
    }

    // GETTERS
    public String getEmployeeId() {
        return employeeId;
    }

    public String getPassword() {
        return password;
    }

    public Departments getRole() {
        return role;
    }
    
    
}
