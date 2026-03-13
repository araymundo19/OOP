/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author Winter Melon
 */

import java.util.List;
import models.Employee;

public interface IEmployeeDAO {
    Employee getEmployeeById(String id);
    List<Employee> getAllEmployees();
    void updateEmployee(Employee emp); 
}
