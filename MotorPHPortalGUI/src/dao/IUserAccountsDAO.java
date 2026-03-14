/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author Winter Melon
 */

import java.util.List;
import models.UserAccount;

public interface IUserAccountsDAO {
    UserAccount authenticate(String id, String password);
    UserAccount getUserAccountById(String id);
    List<UserAccount> getAllAccounts();
}