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
import models.Employee;
import models.EmployeeSalary;
import models.EmpStatus;

/**
 *
 * @author Winter Melon
 */

public class EmployeeDAO implements IEmployeeDAO {
    private final String FILE_PATH = "src/resources/MotorPH-Employee-Data-Details.csv";

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                // Regex to handle commas inside addresses
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length >= 19) {
                    list.add(mapToEmployee(data));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Employee getEmployeeById(String id) {
        // Advanced: We search line-by-line to save memory (Early Exit)
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data[0].trim().equals(id)) {
                    return mapToEmployee(data);
                }
            }
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
        return null;
    }
    
    

    
    private Employee mapToEmployee(String[] data) {
        EmployeeSalary emp = new EmployeeSalary();
        emp.setEmployeeId(data[0].trim());
        emp.setLastName(data[1].trim());
        emp.setFirstName(data[2].trim());
        emp.setBirthday(data[3].trim());
        emp.setAddress(data[4].trim());
        emp.setPhoneNumber(data[5].trim());
        emp.setSssNumber(data[6].trim());
        emp.setPhilHealthNumber(data[7].trim());
        emp.setTinNumber(data[8].trim());
        emp.setPagIbigNumber(data[9].trim());
        emp.setEmpStatus(EmpStatus.valueOf(data[10].trim().toUpperCase()));
        emp.setPosition(data[11].trim());
        emp.setImmediateSupervisor(data[12].trim());
        emp.setBasicSalary(parse(data[13]));
        emp.setRiceSubsidy(parse(data[14]));
        emp.setPhoneAllowance(parse(data[15]));
        emp.setClothingAllowance(parse(data[16]));
        emp.setGrossSemiMonthlyRate(parse(data[17]));
        emp.setHourlyRate(parse(data[18]));
        
        return emp;
    }
    
    private double parse(String val) {
        if (val == null || val.trim().isEmpty()) return 0.0;
    
        String cleaned = val.trim().replace(",", "").replace("\"", "");
    
    try {
        return Double.parseDouble(cleaned);
    } catch (NumberFormatException e) {
        System.err.println("DEBUG: Could not parse numeric value: " + val);
        return 0.0;
    }
    }
    
    

    @Override
    public void updateEmployee(Employee emp) { /* Future Implementation */ }
}