package com.leavemanagement.model;

public class Manager extends FullTimeEmployee {
    private static final int BONUS_LEAVE_DAYS = 8;
    private static final int MAX_DAYS_PER_REQUEST = 15;

    private final int teamSize;

    public Manager(String employeeId, String name, String department, int baseLeaveBalance, int teamSize) {
        super(employeeId, name, department, baseLeaveBalance + BONUS_LEAVE_DAYS);
        if (teamSize < 0) {
            throw new IllegalArgumentException("Team size cannot be negative.");
        }
        this.teamSize = teamSize;
    }

    @Override
    public String getEmployeeType() {
        return "Manager";
    }

    @Override
    public int getMaxDaysPerRequest() {
        return MAX_DAYS_PER_REQUEST;
    }

    @Override
    public String getLeavePolicy() {
        return "Managers can request up to " + MAX_DAYS_PER_REQUEST + " days at a time and get "
                + BONUS_LEAVE_DAYS + " bonus days.";
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("      team size: " + teamSize);
    }

    public int getTeamSize() { return teamSize; }
}
