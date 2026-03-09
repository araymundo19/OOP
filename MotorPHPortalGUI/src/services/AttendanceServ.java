/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author Winter Melon
 */

import dao.IAttendanceDAO;
import dao.AttendanceDAO;
import models.Departments;
import models.Attendance;
import java.util.List;

public class AttendanceService {
    private IAttendanceDAO attendanceDao = new AttendanceDAO();
    private AuthService auth = new AuthService(); // To know who is logged in

    public List<Attendance> getVisibleLogs(String targetEmployeeId) {
        Departments userDept = auth.getLoggedInUser().getDepartment();

        // RBAC
        if (userDept == Departments.HR || userDept == Departments.ADMIN) {
            // HR can search for ANY employee's logs
            return attendanceDao.getRecordsByEmployee(targetEmployeeId);
        } else {
            // Regular employees can ONLY see their own logs
            return attendanceDao.getRecordsByEmployee(auth.getLoggedInUser().getEmployeeId());
        }
    }
}