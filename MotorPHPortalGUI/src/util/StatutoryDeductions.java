package util;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Winter Melon
 */

public class StatutoryDeductions {

    // ---------------- SSS CONTRIBUTION ---------------- //
    public static double computeSSS(double monthlySalary, String yearStr) {
        int year = Integer.parseInt(yearStr);
        if (year >= 2024) {
            return computeSSS2024(monthlySalary);
        } else {
            return computeSSS2023(monthlySalary);
        }
    }

    private static double computeSSS2024(double monthlySalary) {
        if (monthlySalary <= 3250) return 135.00;
        else if (monthlySalary <= 3750) return 157.50;
        else if (monthlySalary <= 4250) return 180.00;
        else if (monthlySalary <= 4750) return 202.50;
        else if (monthlySalary <= 5250) return 225.00;
        else if (monthlySalary <= 5750) return 247.50;
        else if (monthlySalary <= 6250) return 270.00;
        else if (monthlySalary <= 6750) return 292.50;
        else if (monthlySalary <= 7250) return 315.00;
        else if (monthlySalary <= 7750) return 337.50;
        else if (monthlySalary <= 8250) return 360.00;
        else if (monthlySalary <= 8750) return 382.50;
        else if (monthlySalary <= 9250) return 405.00;
        else if (monthlySalary <= 9750) return 427.50;
        else if (monthlySalary <= 10250) return 450.00;
        else if (monthlySalary <= 10750) return 472.50;
        else if (monthlySalary <= 11250) return 495.00;
        else if (monthlySalary <= 11750) return 517.50;
        else if (monthlySalary <= 12250) return 540.00;
        else if (monthlySalary <= 12750) return 562.50;
        else return 585.00; // max
    }

    private static double computeSSS2023(double monthlySalary) {
        return computeSSS2024(monthlySalary); // Same range in 2023
    }

    // ---------------- PAG-IBIG CONTRIBUTION ---------------- //
    public static double computePagibig(double monthlySalary, String yearStr) {
        int year = Integer.parseInt(yearStr);
        if (year >= 2024) {
            return computePagibig2024(monthlySalary);
        } else {
            return computePagibig2023(monthlySalary);
        }
    }

    private static double computePagibig2024(double monthlySalary) {
        double contribution = monthlySalary * 0.02;
        return Math.min(contribution, 200); // Capped at P200 for employee
    }

    private static double computePagibig2023(double monthlySalary) {
        double contribution = monthlySalary * 0.02;
        return Math.min(contribution, 100); // Capped at ₱100
    }
    
    // Placeholder
    private static double computePagibig2022(double monthlySalary) {
        return computePagibig2023(monthlySalary); // Same logic for now
    }
    
    // ---------------- PHILHEALTH CONTRIBUTION ---------------- //
    public static double computePhilhealth(double monthlySalary, String yearStr) {
        int year = Integer.parseInt(yearStr);
        if (year >= 2024) {
            return computePhilhealth2024(monthlySalary);
        } else {
            return computePhilhealth2023(monthlySalary);
        }
    }

    private static double computePhilhealth2024(double monthlySalary) {
        double rate = 0.035; // 3.5%
        if (monthlySalary <= 10000) return 300 / 2;
        else if (monthlySalary <= 59999.99) return (monthlySalary * rate / 2);
        else return 1800 / 2; // Shared by employee and employer
    }

    private static double computePhilhealth2023(double monthlySalary) {
        double rate = 0.03; // 3.0%
        return monthlySalary * rate / 2;
    }

    // ---------------- WITHHOLDING TAX ---------------- //
    public static double computeWithholdingTax(double monthlySalary, String yearStr) {
        // BIR Monthly Tax Table - simplified
        if (monthlySalary <= 20833) return 0;
        else if (monthlySalary <= 33333) return (monthlySalary - 20833) * 0.20;
        else if (monthlySalary <= 66667) return 2500 + (monthlySalary - 33333) * 0.25;
        else if (monthlySalary <= 166667) return 10833.33 + (monthlySalary - 66667) * 0.30;
        else if (monthlySalary <= 666667) return 40833.33 + (monthlySalary - 166667) * 0.32;
        else return 200833.33 + (monthlySalary - 666667) * 0.35;
    }
}