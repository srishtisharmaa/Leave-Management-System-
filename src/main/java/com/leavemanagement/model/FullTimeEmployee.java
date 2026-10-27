package com.leavemanagement.model;

public class FullTimeEmployee extends Employee {
    private static final int MAX_DAYS_PER_REQUEST = 10;

    public FullTimeEmployee(String employeeId, String name, String department, int leaveBalance) {
        super(employeeId, name, department, leaveBalance);
    }

    @Override
    public String getEmployeeType() {
        return "Full-Time";
    }

    @Override
    public int getMaxDaysPerRequest() {
        return MAX_DAYS_PER_REQUEST;
    }
}
