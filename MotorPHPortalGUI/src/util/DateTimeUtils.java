/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Winter Melon
 */
public class DateTimeUtils {
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");

    public static String getNowTime() {
        return LocalDateTime.now().format(TIME_FMT);
    }

    public static String getNowDate() {
        return LocalDateTime.now().format(DATE_FMT);
    }
}