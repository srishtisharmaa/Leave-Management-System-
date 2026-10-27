package com.leavemanagement.model;

import com.leavemanagement.exception.InsufficientLeaveBalanceException;
import com.leavemanagement.exception.InvalidLeaveRequestException;
import com.leavemanagement.exception.LeaveException;
import com.leavemanagement.exception.LeaveRequestNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Employee {
    private final String employeeId;
    private final String name;
    private final String department;
    private int leaveBalance;
    private final List<LeaveRequest> leaveRequests = new ArrayList<>();
    private int nextRequestId = 1;

    protected Employee(String employeeId, String name, String department, int leaveBalance) {
        this.employeeId = requireText(employeeId, "Employee ID");
        this.name = requireText(name, "Employee name");
        this.department = requireText(department, "Department");
        if (leaveBalance < 0) {
            throw new IllegalArgumentException("Leave balance cannot be negative.");
        }
        this.leaveBalance = leaveBalance;
    }

    public abstract String getEmployeeType();

    public abstract int getMaxDaysPerRequest();

    public String getLeavePolicy() {
        return getEmployeeType() + " employees can request up to " + getMaxDaysPerRequest() + " days at a time.";
    }

    protected void checkTypeSpecificRules(int days, String reason) throws InvalidLeaveRequestException {
    }

    public LeaveRequest applyLeave(int days) throws LeaveException {
        return applyLeave(days, null);
    }

    public LeaveRequest applyLeave(int days, String reason) throws LeaveException {
        validate(days, reason);
        LeaveRequest request = new LeaveRequest(nextRequestId++, days, reason);
        leaveBalance -= days;
        leaveRequests.add(request);
        return request;
    }

    public LeaveRequest cancelLeave(int requestId) throws LeaveException {
        LeaveRequest request = findRequest(requestId);
        if (!request.isActive()) {
            throw new InvalidLeaveRequestException("Request #" + requestId + " is already cancelled.");
        }
        request.cancel();
        leaveBalance += request.getDays();
        return request;
    }

    public int checkLeaveBalance() {
        return leaveBalance;
    }

    public void displayDetails() {
        System.out.printf("%-5s | %-12s | %-12s | %-9s | balance: %2d | max per request: %2d%n",
                employeeId, name, department, getEmployeeType(), leaveBalance, getMaxDaysPerRequest());
    }

    private void validate(int days, String reason) throws LeaveException {
        if (days <= 0) {
            throw new InvalidLeaveRequestException("Leave days must be greater than zero.");
        }
        if (days > getMaxDaysPerRequest()) {
            throw new InvalidLeaveRequestException("Request of " + days + " day(s) is above the "
                    + getEmployeeType() + " limit of " + getMaxDaysPerRequest() + " per request.");
        }
        checkTypeSpecificRules(days, reason);
        if (days > leaveBalance) {
            throw new InsufficientLeaveBalanceException(days, leaveBalance);
        }
    }

    private LeaveRequest findRequest(int requestId) throws LeaveRequestNotFoundException {
        for (LeaveRequest request : leaveRequests) {
            if (request.getRequestId() == requestId) {
                return request;
            }
        }
        throw new LeaveRequestNotFoundException(requestId);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
        return value.trim();
    }

    public String getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }

    public List<LeaveRequest> getLeaveRequests() {
        return Collections.unmodifiableList(leaveRequests);
    }
}
