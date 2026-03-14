package models;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Winter Melon
 */

public class AttendanceRecord {
    private String employeeId;
    private String date;
    private String timeIn;
    private String timeOut;  

public AttendanceRecord() {}

public AttendanceRecord(String employeeId, String date, String timeIn, String timeOut) {
    this. employeeId = employeeId;
    this.date = date;
    this.timeIn = timeIn;
    this.timeOut = timeOut;
}

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDate() {
        return date;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }
}