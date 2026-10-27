package com.leavemanagement.model;

public class PartTimeEmployee extends Employee {
    private static final int MAX_DAYS_PER_REQUEST = 3;

    public PartTimeEmployee(String employeeId, String name, String department, int leaveBalance) {
        super(employeeId, name, department, leaveBalance);
    }

    @Override
    public String getEmployeeType() {
        return "Part-Time";
    }

    @Override
    public int getMaxDaysPerRequest() {
        return MAX_DAYS_PER_REQUEST;
    }
}
