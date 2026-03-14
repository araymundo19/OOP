package util;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Winter Melon
 */

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import models.EmployeeSalary;

public class SalaryComputationHelper {

    // Method to convert HH:mm time to decimal for math
    private static double convertToDecimal(String time) {
        if (time == null || time.isEmpty()) return 0.0;
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return hour + (minute / 60.0);
    }

    private static boolean isFirstHalf(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date date = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            return day <= 15;
        } catch (Exception e) {
            return false;
        }
    }

public static Map<String, String> computeMonthlySalary(
        EmployeeSalary employee,
        String monthNum,
        String year,
        String period,
        List<String[]> attendanceRecords
    ) {
        Map<String, String> salarySummary = new LinkedHashMap<>();
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);

        if (employee == null) {
            salarySummary.put("Error", "Employee not found.");
            return salarySummary;
        }

        double riceSubsidy = employee.getRiceSubsidy();
        double phoneAllowance = employee.getPhoneAllowance();
        double clothingAllowance = employee.getClothingAllowance();
        double hourlyRate = employee.getHourlyRate();
        double monthlyBasic = employee.getBasicSalary(); // For tax/statutory basis

        //Track Hours worked per half ---
        double hours1 = 0, totalLates1 = 0, totalOT1 = 0;
        double hours2 = 0, totalLates2 = 0, totalOT2 = 0;

        if (attendanceRecords != null) {
            for (String[] row : attendanceRecords) {
                boolean isFirst = isFirstHalf(row[0]); // Check date column
                double h = convertToDecimal(row[3]);   // Work Hours
                double l = convertToDecimal(row[4]);   // Lates
                double o = convertToDecimal(row[5]);   // OT
                
                if (isFirst) {
                    hours1 += h;
                    totalLates1 += l;
                    totalOT1 += o;
                } else {
                    hours2 += h;
                    totalLates2 += l;
                    totalOT2 += o;
                }
            }
        }

        // Allowances and subsidies split evenly
        double earnedBasic1 = hours1 * hourlyRate;
        double earnedBasic2 = hours2 * hourlyRate;

        double halfAllow1 = (hours1 > 0) ? (riceSubsidy + phoneAllowance + clothingAllowance) / 2.0 : 0.0;
        double halfAllow2 = (hours2 > 0) ? (riceSubsidy + phoneAllowance + clothingAllowance) / 2.0 : 0.0;
        
        double halfTax1 = (hours1 > 0) ? StatutoryDeductions.computeWithholdingTax(monthlyBasic, year) / 2.0 : 0.0;
        double halfTax2 = (hours2 > 0) ? StatutoryDeductions.computeWithholdingTax(monthlyBasic, year) / 2.0 : 0.0;

        // First half computation
        double sss = (hours1 > 0) ? StatutoryDeductions.computeSSS(monthlyBasic, year) : 0.0;
        double lateDeduc1 = totalLates1 * hourlyRate;
        double otPay1 = totalOT1 * hourlyRate * 1.25;
        double gross1 = earnedBasic1 + halfAllow1 + otPay1; 
        double deduction1 = sss + halfTax1 + lateDeduc1;
        double net1 = gross1 - deduction1;

        // Second half computation
        double philhealth = (hours2 > 0) ? StatutoryDeductions.computePhilhealth(monthlyBasic, year) : 0.0;
        double pagibig = (hours2 > 0) ? StatutoryDeductions.computePagibig(monthlyBasic, year) : 0.0;
        double lateDeduc2 = totalLates2 * hourlyRate;
        double otPay2 = totalOT2 * hourlyRate * 1.25;
        double gross2 = earnedBasic2 + halfAllow2 + otPay2; 
        double deduction2 = philhealth + pagibig + halfTax2 + lateDeduc2;
        double net2 = gross2 - deduction2;

        String dispBasic, dispAllow, dispOT, dispLate, dispTax, dispSSS, dispPagibig, dispPhil, dispGross, dispDeduc, dispNet, dispDate;

        if (period.equals("1st - 15th")) {
            dispBasic = fmt.format(earnedBasic1);
            dispAllow = fmt.format(halfAllow1);
            dispOT = fmt.format(otPay1);
            dispLate = fmt.format(lateDeduc1);
            dispTax = fmt.format(halfTax1);
            dispSSS = fmt.format(sss);
            dispPagibig = "0.00"; dispPhil = "0.00";
            dispGross = fmt.format(gross1);
            dispDeduc = fmt.format(deduction1);
            dispNet = fmt.format(net1);
            dispDate = monthNum + "/01/" + year + " to " + monthNum + "/15/" + year;
        } 
        else if (period.equals("16th - End")) {
            dispBasic = fmt.format(earnedBasic2);
            dispAllow = fmt.format(halfAllow2);
            dispOT = fmt.format(otPay2);
            dispLate = fmt.format(lateDeduc2);
            dispTax = fmt.format(halfTax2);
            dispSSS = "0.00";
            dispPagibig = fmt.format(pagibig);
            dispPhil = fmt.format(philhealth);
            dispGross = fmt.format(gross2);
            dispDeduc = fmt.format(deduction2);
            dispNet = fmt.format(net2);
            dispDate = monthNum + "/16/" + year + " to " + monthNum + "/End/" + year;
        } 
        else { // Whole Month
            dispBasic = fmt.format(earnedBasic1 + earnedBasic2);
            dispAllow = fmt.format(halfAllow1 + halfAllow2);
            dispOT = fmt.format(otPay1 + otPay2);
            dispLate = fmt.format(lateDeduc1 + lateDeduc2);
            dispTax = fmt.format(halfTax1 + halfTax2);
            dispSSS = fmt.format(sss);
            dispPagibig = fmt.format(pagibig);
            dispPhil = fmt.format(philhealth);
            dispGross = fmt.format(gross1 + gross2);
            dispDeduc = fmt.format(deduction1 + deduction2);
            dispNet = fmt.format(net1 + net2);
            dispDate = monthNum + "/01/" + year + " to " + monthNum + "/End/" + year;
        }

        salarySummary.put("EmployeeID", employee.getEmployeeId());
        salarySummary.put("Name", employee.getFirstName() + " " + employee.getLastName());
        salarySummary.put("DateCovered", dispDate);
        
        salarySummary.put("BasicPay", dispBasic);
        salarySummary.put("Allowances", dispAllow);
        salarySummary.put("Overtime", dispOT);
        salarySummary.put("Late", dispLate);
        salarySummary.put("Tax", dispTax);
        salarySummary.put("SSS", dispSSS);
        salarySummary.put("Pagibig", dispPagibig);
        salarySummary.put("Philhealth", dispPhil);
        
        salarySummary.put("Gross", dispGross);
        salarySummary.put("TotalDeductions", dispDeduc);
        salarySummary.put("NetPay", dispNet);

        return salarySummary;
    }
}