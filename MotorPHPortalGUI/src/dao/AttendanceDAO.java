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
import org.apache.commons.csv.*;
import models.AttendanceRecord;

public class AttendanceDAO implements IAttendanceDAO {
    // Make sure this matches your project's filename exactly
    private final String FILE_PATH = "src/resources/MotorPH-Employee-Data-AttendanceRecord.csv";
    private final String[] HEADERS = {"Employee #", "Last Name", "First Name", "Date", "Log In", "Log Out"};

    
    // READ
    
    @Override
    public List<AttendanceRecord> getRecordsByEmployee(String id) {
        List<AttendanceRecord> logs = new ArrayList<>();
        
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        try (FileReader reader = new FileReader(FILE_PATH);
             CSVParser csvParser = new CSVParser(reader, format)) {

            for (CSVRecord record : csvParser) {
                if (record.get("Employee #").trim().equals(id)) {
                    logs.add(mapToRecord(record));
                }
            }
        } catch (IOException e) {
            System.err.println("Error parsing CSV: " + e.getMessage());
        }
        return logs;
    }
    
    @Override
    public List<AttendanceRecord> getAllRecords() {
        List<AttendanceRecord> allLogs = new ArrayList<>();
        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();

        try (FileReader reader = new FileReader(FILE_PATH);
             CSVParser csvParser = new CSVParser(reader, format)) {

            for (CSVRecord record : csvParser) {
                allLogs.add(mapToRecord(record));
            }
        } catch (IOException e) {
            System.err.println("Error parsing All Records CSV: " + e.getMessage());
        }
        return allLogs;
    }
    
        // WRITE

    @Override
    public void saveRecord(AttendanceRecord record) {
        saveRecord(record.getEmployeeId(), record.getDate(), record.getTimeIn(), record.getTimeOut());
    }
    
    public void saveRecord(String id, String date, String timeIn, String timeOut) {
        try (FileWriter out = new FileWriter(FILE_PATH, true);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
            printer.printRecord(id, "", "", date, timeIn, timeOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateTimeOut(String empId, String date, String timeOut) {
        List<List<String>> allData = new ArrayList<>();
        String[] headers = {"Employee #", "Last Name", "First Name", "Date", "Log In", "Log Out"};
        
        try (FileReader reader = new FileReader(FILE_PATH);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
            
            for (CSVRecord record : csvParser) {
                List<String> row = new ArrayList<>();
                for (String value : record) { row.add(value); }
                
                if (record.get("Employee #").equals(empId) && record.get("Date").equals(date)) {
                    row.set(5, timeOut);
                }
                allData.add(row);
            }
        } catch (IOException e) { e.printStackTrace(); }
                
        try (FileWriter out = new FileWriter(FILE_PATH);
                CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.builder().setHeader(headers).build())) {
            printer.printRecords(allData);
        } catch (IOException e) { e.printStackTrace(); }
    } 
    
    private AttendanceRecord mapToRecord(CSVRecord record) {
        return new AttendanceRecord(
            record.get("Employee #"),
            record.get("Date"),
            record.get("Log In"),
            record.get("Log Out")
        );
    }
    }