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
import models.Attendance;

public class AttendanceDAO implements IAttendanceDAO {
    private final String FILE_PATH = "src/Attendance_Logs.csv";

    @Override
    public List<Attendance> getRecordsByEmployee(String id) {
        List<Attendance> logs = new ArrayList<>();
        // 1. Open your old BufferedReader logic here
        // 2. Filter lines where data[0] matches the 'id'
        // 3. Return the list
        return logs;
    }

    @Override
    public void saveRecord(Attendance record) {
        // Use FileWriter(FILE_PATH, true) to APPEND a new line
        // This is where "Time In" and "Time Out" get saved
    }
}