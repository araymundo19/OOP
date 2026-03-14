/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author Winter Melon
 */
import models.Employee;

public class SessionServ {
    // This stores the FULL profile globally once the login is successful
    private static Employee currentUser;

    public static void startSession(Employee user) {
        currentUser = user;
    }

    public static Employee getCurrentUser() {
        return currentUser;
    }

    public static void cleanSession() {
        currentUser = null;
    }
}