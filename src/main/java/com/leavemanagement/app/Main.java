package com.leavemanagement.app;

import com.leavemanagement.model.*;

public class Main {
    public static void main(String[] args) {
        FullTimeEmployee e1 = new FullTimeEmployee("E101", "Aarav Sharma", "Engineering");
        PartTimeEmployee e2 = new PartTimeEmployee("E102", "Meera Nair", "HR");
        Intern e3 = new Intern("E103", "Rohan Das", "Finance");

        e1.applyLeave(8);
        e2.applyLeave(8);   // rejected: max 5
        e3.applyLeave(2);

        e1.displayDetails();
        e2.displayDetails();
        e3.displayDetails();
    }
}
