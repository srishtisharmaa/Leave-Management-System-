package com.leavemanagement.model;

public class FullTimeEmployee extends Employee {

    public FullTimeEmployee(String id, String name, String dept) {
        super(id, name, dept, 24);
    }

    @Override
    public boolean applyLeave(int days) {
        if (days > 10) {
            System.out.println("Full-time employees can take max 10 days at once");
            return false;
        }
        return super.applyLeave(days);
    }
}
