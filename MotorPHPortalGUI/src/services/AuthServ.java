/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author Winter Melon
 */

import dao.IUserAccountsDAO;
import dao.UserAccountsDAO;
import models.UserAccount;
import models.Departments;

public class AuthServ {
    private static UserAccount loggedInUser;
    private static final IUserAccountsDAO accountDao = new UserAccountsDAO();
    
    public static boolean authenticate(String id, String password) {
        UserAccount account = accountDao.authenticate(id, password);
        if (account != null) {
            loggedInUser = account;
            return true;
        }
        return false;
    }

    public static UserAccount getLoggedInUser() {
        return loggedInUser;
    }
    
    public static void syncUserSession(dao.EmployeeDAO empDao) {
        if (loggedInUser instanceof models.EmployeeSalary emp) {
            models.Employee fullData = empDao.getEmployeeById(emp.getEmployeeId());
            if (fullData != null) {
                emp.setFirstName(fullData.getFirstName());
                emp.setLastName(fullData.getLastName());
        }
    }
}

    // --- RBAC HELPER METHODS ---
    // GUI WILL USE TO HIDE/SHOW PARTS
    
    public static boolean isAdmin() {
        return loggedInUser != null && loggedInUser.getDepartment() == Departments.ADMIN;
    }
    
    public static boolean isIT() {
        return loggedInUser != null && loggedInUser.getDepartment() == Departments.IT;
    }
    
    public static boolean isHR() {
        return loggedInUser != null && loggedInUser.getDepartment() == Departments.HR;
    }

    public static boolean isFinance() {
        return loggedInUser != null && loggedInUser.getDepartment() == Departments.FINANCE;
    }
    
    
    public static boolean hasPrivilegedAccess() {
        return isHR() || isIT() || isAdmin() || isFinance();
    }
    
    public static void logout() {
        loggedInUser = null;
    }
}