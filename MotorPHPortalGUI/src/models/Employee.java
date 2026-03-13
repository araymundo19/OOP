/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author Winter Melon
 */
public abstract class Employee extends UserAccount{
    protected String lastName;
    protected String firstName;
    
    // Personal Details
    protected String birthday;
    protected String address;
    protected String phoneNumber;
    
    // Statutory IDs
    protected String sssNumber;
    protected String philHealthNumber;
    protected String tinNumber;
    protected String pagIbigNumber;    

    // Employement Details
    protected String position;
    protected EmpStatus empStatus; //Enum
    protected String immediateSupervisor;
    
    
    // CONTRSTRUCTORS    
    public Employee() {
        super();
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public Employee(String lastName, String firstName, String birthday, String address, String phoneNumber, String sssNumber, String philHealthNumber, String tinNumber, String pagIbigNumber, String position, EmpStatus empStatus, String immediateSupervisor, double basicSalary, double riceSubsidy, double phoneAllowance, double clothingAllowance, double grossSemiMonthlyRate, double hourlyRate) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.sssNumber = sssNumber;
        this.philHealthNumber = philHealthNumber;
        this.tinNumber = tinNumber;
        this.pagIbigNumber = pagIbigNumber;
        this.position = position;
        this.empStatus = empStatus;
        this.immediateSupervisor = immediateSupervisor;
    }

    // GETTERS
    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSssNumber() {
        return sssNumber;
    }

    public String getPhilHealthNumber() {
        return philHealthNumber;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public String getPagIbigNumber() {
        return pagIbigNumber;
    }

    public String getPosition() {
        return position;
    }

    public EmpStatus getEmpStatus() {
        return empStatus;
    }

    public String getImmediateSupervisor() {
        return immediateSupervisor;
    }

   
    // SETTERS
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSssNumber(String sssNumber) {
        this.sssNumber = sssNumber;
    }

    public void setPhilHealthNumber(String philHealthNumber) {
        this.philHealthNumber = philHealthNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber;
    }

    public void setPagIbigNumber(String pagIbigNumber) {
        this.pagIbigNumber = pagIbigNumber;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setEmpStatus(EmpStatus empStatus) {
        this.empStatus = empStatus;
    }

    public void setImmediateSupervisor(String immediateSupervisor) {
        this.immediateSupervisor = immediateSupervisor;
    }

}