/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author Winter Melon
 */
import java.io.*;
import java.util.*;
import models.LeaveRecord;

public class LeaveDAO {
    private final String FILE_PATH = "src/resources/MotorPH-Employee-Data-LeaveRecord.csv";

    public void saveRecord(LeaveRecord request) {
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println(String.format("%s,%s,%s,%s,%s,%s,%s",
                request.getRequestId(),
                request.getEmployeeId(),
                request.getLeaveType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getReason(),
                request.getStatus()
            ));
        } catch (IOException e) {
            System.err.println("Error writing to Leave CSV: " + e.getMessage());
        }
    }

    public List<LeaveRecord> getAllRequests() {
        List<LeaveRecord> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean isHeader = true;
            
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                // Regex
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                if (data.length >= 7) {
                    list.add(new LeaveRecord(
                        data[0].trim(), // Request ID
                        data[1].trim(), // Employee #
                        data[2].trim(), // Type
                        data[3].trim(), // StartDate
                        data[4].trim(), // EndDate
                        data[5].trim(), // Reason
                        data[6].trim()  // Status
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Leave CSV: " + e.getMessage());
        }
        return list;
    }
}