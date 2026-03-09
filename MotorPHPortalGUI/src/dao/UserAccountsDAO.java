/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import models.UserAccount;
import models.Departments;

/**
 *
 * @author Winter Melon
 */

public class UserAccountDAO implements IUserAccountDAO {
    private final String FILE_PATH = "src/resources/MotorPH-Employee-Data-Accounts.csv";

    @Override
    public List<UserAccount> getAllAccounts() {
        List<UserAccount> accounts = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = br.readLine(); // Skip header
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    String id = data[0].trim();
                    String pass = data[1].trim();
                    String role = data[2].trim(); //Not used but stored
                    Departments department = Departments.valueOf(data[3].trim());
                    
                    accounts.add(new UserAccount(id, pass, role, department));
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading accounts: " + e.getMessage());
        }
        return accounts;
    }

    // Get Account by Employee ID
    public UserAccount getAccountById(String id) {
        for (UserAccount acc : getAllAccounts()) {
            if (acc.getEmployeeId().equals(id)) {
                return acc;
            }
        }
        return null;
    }

    @Override
    public UserAccount authenticate(String id, String password) {
        for (UserAccount acc : getAllAccounts()) {
            if (acc.getEmployeeId().equals(id) && acc.getPassword().equals(password)) {
                return acc;
            }
        }
        return null;
    }
}