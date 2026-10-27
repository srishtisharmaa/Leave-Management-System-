package com.leavemanagement.model;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    private String employeeId;
    private String employeeName;
    private String department;
    private int leaveBalance;
    private List<Integer> leaveHistory = new ArrayList<>();

    public Employee(String employeeId, String employeeName, String department, int leaveBalance) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.leaveBalance = leaveBalance;
    }

    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getDepartment() { return department; }

    public boolean applyLeave(int days) {
        if (days <= 0 || days > leaveBalance) {
            System.out.println("Leave rejected for " + employeeName);
            return false;
        }
        leaveBalance -= days;
        leaveHistory.add(days);
        System.out.println(employeeName + " applied " + days + " day(s)");
        return true;
    }

    public boolean cancelLeave(int days) {
        if (!leaveHistory.remove(Integer.valueOf(days))) {
            System.out.println("No such leave to cancel for " + employeeName);
            return false;
        }
        leaveBalance += days;
        System.out.println(employeeName + " cancelled " + days + " day(s)");
        return true;
    }

    public int checkLeaveBalance() {
        return leaveBalance;
    }

    public void displayDetails() {
        System.out.println(employeeId + " | " + employeeName + " | " + department + " | balance = " + leaveBalance);
    }
}
