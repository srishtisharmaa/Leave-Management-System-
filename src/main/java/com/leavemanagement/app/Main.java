package com.leavemanagement.app;

import com.leavemanagement.model.*;

public class Main {
    public static void main(String[] args) {
        Employee e1 = new Employee("E101", "Aarav Sharma", "Engineering", 20);
        Employee e2 = new Employee("E102", "Meera Nair", "HR", 20);
        Employee e3 = new Employee("E103", "Rohan Das", "Finance", 20);
        e1.displayDetails();
        e2.displayDetails();
        e3.displayDetails();
    }
}
