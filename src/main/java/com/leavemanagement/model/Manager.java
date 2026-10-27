package com.leavemanagement.model;

public class Manager extends Employee {

    private int teamSize;

    public Manager(String id, String name, String dept, int teamSize) {
        super(id, name, dept, 32);   // 24 normal + 8 managerial bonus
        this.teamSize = teamSize;
    }

    @Override
    public boolean applyLeave(int days) {
        if (days > 15) {
            System.out.println("Managers can take max 15 days at once");
            return false;
        }
        return super.applyLeave(days);
    }

    @Override
    public String getEmployeeType() {
        return "Manager";
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("   Team size = " + teamSize);
    }
}
