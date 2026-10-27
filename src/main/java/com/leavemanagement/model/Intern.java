package com.leavemanagement.model;

import com.leavemanagement.exception.InvalidLeaveRequestException;

public class Intern extends Employee {
    private static final int MAX_DAYS_PER_REQUEST = 2;

    public Intern(String employeeId, String name, String department, int leaveBalance) {
        super(employeeId, name, department, leaveBalance);
    }

    @Override
    public String getEmployeeType() {
        return "Intern";
    }

    @Override
    public int getMaxDaysPerRequest() {
        return MAX_DAYS_PER_REQUEST;
    }

    @Override
    protected void checkTypeSpecificRules(int days, String reason) throws InvalidLeaveRequestException {
        if (reason == null || reason.isBlank()) {
            throw new InvalidLeaveRequestException("Interns must give a reason for leave.");
        }
    }

    @Override
    public String getLeavePolicy() {
        return "Interns can request up to " + MAX_DAYS_PER_REQUEST + " days at a time and must give a reason.";
    }
}
