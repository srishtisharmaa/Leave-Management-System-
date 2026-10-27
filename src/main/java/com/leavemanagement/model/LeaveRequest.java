package com.leavemanagement.model;

public class LeaveRequest {
    private final int requestId;
    private final int days;
    private final String reason;
    private LeaveStatus status = LeaveStatus.APPLIED;

    // package-private: only Employee creates and cancels requests
    LeaveRequest(int requestId, int days, String reason) {
        this.requestId = requestId;
        this.days = days;
        this.reason = (reason == null || reason.isBlank()) ? "not given" : reason;
    }

    void cancel() {
        status = LeaveStatus.CANCELLED;
    }

    public boolean isActive() {
        return status == LeaveStatus.APPLIED;
    }

    public int getRequestId() { return requestId; }
    public int getDays() { return days; }
    public String getReason() { return reason; }
    public LeaveStatus getStatus() { return status; }

    @Override
    public String toString() {
        return "#" + requestId + " | " + days + " day(s) | " + status + " | reason: " + reason;
    }
}
