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
import models.Attendance;

public interface IAttendanceDAO {
    // Contract: We need to see history and add new logs
    List<Attendance> getRecordsByEmployee(String employeeId);
    void saveRecord(Attendance record); 
}
