/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author Winter Melon
 */
import dao.LeaveDAO;
import java.util.List;
import java.util.stream.Collectors;
import models.LeaveRecord;

public class LeaveServ {
    private LeaveDAO leaveDAO = new LeaveDAO();

public boolean submitLeaveRequest(String empId, String type, java.util.Date start, java.util.Date end, String reason) {
    if (type.equals("[Leave Type]") || start == null || end == null || reason.isEmpty()) {
        return false;
    }

    if (end.before(start)) return false;

    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
    String startStr = sdf.format(start);
    String endStr = sdf.format(end);

    String requestId = "LR" + System.currentTimeMillis();
    models.LeaveRecord newRecord = new models.LeaveRecord(
        requestId, empId, type, startStr, endStr, reason, "Pending"
    );

    leaveDAO.saveRecord(newRecord);
    return true;
}

    // Retrieves all leave records for a specific employee
    public List<LeaveRecord> getEmployeeLeaveHistory(String empId) {
        return leaveDAO.getAllRequests().stream()
                .filter(r -> r.getEmployeeId().equals(empId))
                .collect(Collectors.toList());
    }
    
    // Retrieves all leave records for Admin review
    public List<LeaveRecord> getAllLeaveRecords() {
        return leaveDAO.getAllRequests();
    }
}