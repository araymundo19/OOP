/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import models.*;

/**
 *
 * @author Winter Melon
 */

public class UserAccountsDAO implements IUserAccountsDAO {
    private final String FILE_PATH = "src/resources/MotorPH-Employee-Data-Accounts.csv";

// ADMIN METHOD - GET ALL ACCOUNTS
    @Override
    public List<UserAccount> getAllAccounts() {
        List<UserAccount> accounts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                UserAccount acc = parseUserAccount(line);
                if (acc != null) accounts.add(acc);
            }
        } catch (Exception e) {
            System.err.println("Error reading all accounts: " + e.getMessage());
        }
        return accounts;
    }

    // FOR LOGIN AND PROFILE
    public UserAccount getUserAccountById(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].trim().equals(id)) {
                    return parseUserAccount(line);
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding specific account: " + e.getMessage());
        }
        return null;
    }

    // ATHENTICATE
    @Override
    public UserAccount authenticate(String id, String password) {
        UserAccount acc = getUserAccountById(id);
        if (acc != null && acc.getPassword().equals(password)) {
            return acc;
        }
        return null;
    }

    private UserAccount parseUserAccount(String csvLine) {
        try {
            String[] data = csvLine.split(",");
            return new EmployeeSalary(
                data[0].trim(), 
                data[1].trim(), 
                data[2].trim(), 
                Departments.valueOf(data[3].trim().toUpperCase())
            );
        } catch (Exception e) {
            return null; // Skip malformed rows
        }
    }
}