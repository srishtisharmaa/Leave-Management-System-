package com.leavemanagement.exception;

public class EmployeeNotFoundException extends LeaveException {
    public EmployeeNotFoundException(String employeeId) {
        super("No employee found with ID " + employeeId + ".");
    }
}
