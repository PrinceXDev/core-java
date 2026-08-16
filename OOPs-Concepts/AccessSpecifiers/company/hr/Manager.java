package company.hr;

/*
 * Manager — same package as Employee (company.hr).
 *
 * Being in the SAME PACKAGE means it can reach the default (package-private)
 * field directly, on top of the protected/public ones it gets via extends.
 */
public class Manager extends Employee {

    public Manager(String employeeId, String name, double baseSalary, String ssn) {
        super(employeeId, name, baseSalary, ssn);
    }

    public void giveRaise(double amount) {
        baseSalary += amount;        // protected - fine, same package
        internalHrNotes = "raise of " + amount + " approved"; // default - fine, same package
        System.out.println(name + "'s new base salary = " + baseSalary);
    }

    public void printNotes() {
        System.out.println(name + "'s HR notes: " + internalHrNotes);
        // System.out.println(ssn);  // COMPILE ERROR: ssn is private to Employee, not even a subclass can see it
    }
}
