/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author Winter Melon
 */

import dao.EmployeeDAO;
import dao.UserAccountDAO;
import models.Employee;
import models.UserAccount;


public class EmployeeServ {
    private EmployeeDAO employeeDao = new EmployeeDAO();
    private UserAccountDAO accountDao = new UserAccountDAO();

// Set department by Employee Id
public Employee getEmployeeProfile(String id) {
    Employee emp = employeeDao.getEmployeeById(id);
    UserAccount acc = accountDao.getAccountById(id);
    if (emp != null && acc != null) {
            emp.setDepartment(acc.getDepartment());
        }
        
        return emp;
    }
}