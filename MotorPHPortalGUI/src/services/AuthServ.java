/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author Winter Melon
 */

import dao.IUserAccountDAO;
import dao.UserAccountDAO;
import models.UserAccount;
import models.Departments;

public class AuthService {
    // We use the Interface type here (Abstraction!)
    private final IUserAccountDAO accountDao = new UserAccountDAO();
    
    // This "static" variable remembers the user across different screens
    private static UserAccount loggedInUser;

    public boolean login(String id, String password) {
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

    public static void logout() {
        loggedInUser = null;
    }

    // --- RBAC HELPER METHODS ---
    // Your GUI will call these to hide/show buttons!
    
    public boolean isHR() {
        return loggedInUser != null && loggedInUser.getRole() == Departments.HR;
    }

    public boolean isAdmin() {
        return loggedInUser != null && loggedInUser.getRole() == Departments.ADMIN;
    }
    
    public boolean canAccessPayroll() {
        return loggedInUser != null && 
              (loggedInUser.getRole() == Departments.FINANCE || 
               loggedInUser.getRole() == Departments.ADMIN);
    }
}
