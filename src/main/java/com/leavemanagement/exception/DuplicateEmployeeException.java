package com.leavemanagement.exception;

public class DuplicateEmployeeException extends LeaveException {
    public DuplicateEmployeeException(String employeeId) {
        super("An employee with ID " + employeeId + " is already registered.");
    }
}
