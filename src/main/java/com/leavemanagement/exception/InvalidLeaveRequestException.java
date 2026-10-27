package com.leavemanagement.exception;

public class InvalidLeaveRequestException extends LeaveException {
    public InvalidLeaveRequestException(String message) {
        super(message);
    }
}
