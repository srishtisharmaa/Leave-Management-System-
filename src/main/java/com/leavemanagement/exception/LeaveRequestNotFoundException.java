package com.leavemanagement.exception;

public class LeaveRequestNotFoundException extends LeaveException {
    public LeaveRequestNotFoundException(int requestId) {
        super("Leave request #" + requestId + " does not exist.");
    }
}
