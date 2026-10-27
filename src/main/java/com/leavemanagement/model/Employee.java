package com.leavemanagement.model;

public class Employee {

    private String employeeId;
    private String employeeName;
    private String department;
    private int leaveBalance;

    public Employee(String employeeId, String employeeName, String department, int leaveBalance) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.leaveBalance = leaveBalance;
    }

    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getDepartment() { return department; }

    public int checkLeaveBalance() {
        return leaveBalance;
    }

    public void displayDetails() {
        System.out.println(employeeId + " | " + employeeName + " | " + department + " | balance = " + leaveBalance);
    }
}
