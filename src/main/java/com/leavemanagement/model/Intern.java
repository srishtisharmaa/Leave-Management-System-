package com.leavemanagement.model;

public class Intern extends Employee {

    public Intern(String id, String name, String dept) {
        super(id, name, dept, 6);
    }

    @Override
    public boolean applyLeave(int days) {
        if (days > 2) {
            System.out.println("Interns can take max 2 days at once");
            return false;
        }
        return super.applyLeave(days);
    }

    @Override
    public String getEmployeeType() {
        return "Intern";
    }
}
