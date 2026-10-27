package com.leavemanagement.exception;

public class InsufficientLeaveBalanceException extends LeaveException {
    public InsufficientLeaveBalanceException(int requested, int available) {
        super("Requested " + requested + " day(s) but only " + available + " day(s) are left.");
    }
}
