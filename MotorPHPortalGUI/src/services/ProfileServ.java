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
    
    private static EmployeeSalary currentUserProfile;
    
    public EmployeeSalary getCurrentUserProfile() {
        UserAccount session = AuthServ.getLoggedInUser();
        
        if (session == null) {
            return null;
        }
        
        return getUserProfile(session.getEmployeeId());
    }

    public EmployeeSalary getUserProfile(String id) {
        if (currentUserProfile != null && currentUserProfile.getEmployeeId().equals(id)) {
            return currentUserProfile;
        }
        
        EmployeeSalary emp = (EmployeeSalary) empDao.getEmployeeById(id);
        UserAccount acc = accDao.getUserAccountById(id);
        
        if (emp != null && acc != null) {
            emp.setDepartment(acc.getDepartment());
            emp.setRole(acc.getRole());
            currentUserProfile = emp;
        }        
        return emp;
    }
    
    public static void clearCache() {
        currentUserProfile = null;
    }
}