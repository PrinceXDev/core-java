package company.payroll;

import company.hr.Employee;

/*
 * PayrollProcessor — a DIFFERENT package (company.payroll), extends Employee.
 *
 * This is the file that proves the difference between "protected" and
 * "default": being a subclass gives you protected access from anywhere,
 * but it gives you NOTHING extra for default (package-private) members.
 */
public class PayrollProcessor extends Employee {

    public PayrollProcessor(String employeeId, String name, double baseSalary, String ssn) {
        super(employeeId, name, baseSalary, ssn);
    }

    public double calculateMonthlyPay() {
        // protected -> allowed here even though this class lives in a
        // different package, BECAUSE it's a subclass of Employee.
        return baseSalary / 12;

        // internalHrNotes;   // COMPILE ERROR: default access, different package - subclass doesn't help here
        // ssn;               // COMPILE ERROR: private to Employee, no exceptions for anyone
    }
}
