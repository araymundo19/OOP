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
    private final IUserAccountsDAO accountDao = new UserAccountsDAO();
    
    public boolean authenticate(String id, String password) {
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

    // --- RBAC HELPER METHODS ---
    // GUI WILL USE TO HIDE/SHOW PARTS
    
    public boolean isAdmin() {
        return loggedInUser != null && loggedInUser.getDepartment() == Departments.ADMIN;
    }
    
    public boolean isIT() {
        return loggedInUser != null && loggedInUser.getDepartment() == Departments.IT;
    }
    
    public boolean isHR() {
        return loggedInUser != null && loggedInUser.getDepartment() == Departments.HR;
    }

    public boolean isFinance() {
        return loggedInUser != null && loggedInUser.getDepartment() == Departments.FINANCE;
    }
    
    
    public boolean hasPrivilegedAccess() {
        return isHR() || isIT() || isAdmin() || isFinance();
    }
    
    public static void logout() {
        loggedInUser = null;
    }
}