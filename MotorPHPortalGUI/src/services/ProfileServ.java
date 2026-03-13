/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author Winter Melon
 */
import dao.*;
import models.*;

public class ProfileServ {
    private IEmployeeDAO empDao = new EmployeeDAO();
    private IUserAccountsDAO accDao = new UserAccountsDAO();

    public Employee getUserProfile(String id) {
        Employee emp = empDao.getEmployeeById(id);
        UserAccount acc = accDao.getUserAccountById(id);
        
        if (emp != null && acc != null) {
            emp.setDepartment(acc.getDepartment());
            emp.setRole(acc.getRole());
        }        
        return emp;
    }
}