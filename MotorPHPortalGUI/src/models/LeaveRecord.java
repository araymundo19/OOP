package models;

public class LeaveRecord {
    private String requestId;
    private String employeeId;
    private String leaveType;
    private String startDate;
    private String endDate;
    private String reason;
    private String status; // Pending, Approved, Rejected

    public LeaveRecord(String requestId, String employeeId, String leaveType, 
                        String startDate, String endDate, String reason, String status) {
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
    }

    // Getters
    public String getRequestId() { return requestId; }
    public String getEmployeeId() { return employeeId; }
    public String getLeaveType() { return leaveType; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
}