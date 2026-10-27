package com.leavemanagement.model;

public class PartTimeEmployee extends Employee {

    public PartTimeEmployee(String id, String name, String dept) {
        super(id, name, dept, 12);
    }

    @Override
    public boolean applyLeave(int days) {
        if (days > 5) {
            System.out.println("Part-time employees can take max 5 days at once");
            return false;
        }
        return super.applyLeave(days);
    }

    @Override
    public String getEmployeeType() {
        return "Part-Time";
    }
}
