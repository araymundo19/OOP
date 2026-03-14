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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import models.AttendanceRecord;

public class AttendanceServ {
    private final IAttendanceDAO attendanceDao = new AttendanceDAO();
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public void clockIn(String empId) {
        String today = LocalDate.now().format(DATE_FORMAT);
        String now = LocalTime.now().format(TIME_FORMAT);
        attendanceDao.saveRecord(empId, today, now, "00:00");
    }

    public void clockOut(String empId) {
        String today = LocalDate.now().format(DATE_FORMAT);
        String now = LocalTime.now().format(TIME_FORMAT);
        attendanceDao.updateTimeOut(empId, today, now);
    }
    
    
    // For Current logged user
    public List<String[]> getAttendanceSummary(String month, String year) {
        String currentId = AuthServ.getLoggedInUser().getEmployeeId();
        List<AttendanceRecord> myLogs = attendanceDao.getRecordsByEmployee(currentId); 
        return processLogs(myLogs, month, year, false);
    }
    
    // FOr Specific Employee (HR/Admin)
    public List<String[]> getAttendanceSummary(String empId, String month, String year) {
        List<AttendanceRecord> rawLogs = attendanceDao.getRecordsByEmployee(empId);
        return processLogs(rawLogs, month, year, false);
    }
    
    public List<String[]> getAllAttendanceSummary(String month, String year) {
        List<AttendanceRecord> allLogs = attendanceDao.getAllRecords(); 
        return processLogs(allLogs, month, year, true); // true = HR View (includes ID column)
    }
        
    private List<String[]> processLogs(List<AttendanceRecord> logs, String month, String year, boolean isHRView) {
        List<String[]> summary = new ArrayList<>();
        DateTimeFormatter flexibleTime = DateTimeFormatter.ofPattern("H:mm");
    DateTimeFormatter flexibleDate = DateTimeFormatter.ofPattern("M/d/yyyy");
        
        for (AttendanceRecord log : logs) {
            try {
                String dateStr = log.getDate().trim();
                String timeInStr = log.getTimeIn().trim();
                String timeOutStr = log.getTimeOut().trim();
                
                LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
                
                // Filter by month/year
                if (!String.valueOf(date.getYear()).equals(year) || 
                    !String.format("%02d", date.getMonthValue()).equals(month)) continue;
                
                if (timeOutStr.equals("00:00") || timeOutStr.isEmpty()) continue;

                LocalTime inTime = LocalTime.parse(timeInStr, flexibleTime);
            LocalTime outTime = LocalTime.parse(timeOutStr, flexibleTime);

                // Math: Work hours (minus 1 hr break), Late (after 8:10), OT (after 17:00)
                Duration work = Duration.between(inTime, outTime).minusHours(1);
                Duration lateDur = inTime.isAfter(LocalTime.of(8, 10)) ? Duration.between(LocalTime.of(8, 0), inTime) : Duration.ZERO;
                Duration otDur = outTime.isAfter(LocalTime.of(17, 0)) ? Duration.between(LocalTime.of(17, 0), outTime) : Duration.ZERO;

                summary.add(createRow(log,
                        isHRView,
                        timeInStr,
                        timeOutStr, 
                        formatDuration(work),
                        formatDuration(lateDur),
                        formatDuration(otDur)));

            } catch (Exception e) {
                System.err.println("Skipped Row: " + log.getDate() + " Error: " + e.getMessage());
            }
        }
        return summary;
    }

    private String[] createRow(AttendanceRecord log, boolean isHR, String in, String out, String work, String late, String ot) {
        if (isHR) {
            return new String[] { log.getEmployeeId(), log.getDate(), in, out, work, late, ot };
        } else {
            return new String[] { log.getDate(), in, out, work, late, ot };
        }
    }

    private String formatDuration(Duration d) {
        long totalMinutes = d.toMinutes();
        long hours = Math.max(0, totalMinutes / 60);
        long minutes = Math.max(0, totalMinutes % 60);
        return String.format("%02d:%02d", hours, minutes);
    }
}