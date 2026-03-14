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
import models.AttendanceRecord;

public interface IAttendanceDAO {
    List<AttendanceRecord> getRecordsByEmployee(String id);
    List<AttendanceRecord> getAllRecords();
    void saveRecord(AttendanceRecord record);
    void saveRecord(String id, String date, String timeIn, String timeOut); //Overload
    void updateTimeOut(String empId, String date, String timeOut);
}