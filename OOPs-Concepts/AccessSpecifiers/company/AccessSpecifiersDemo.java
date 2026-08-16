package company;

import company.hr.Employee;
import company.hr.Manager;
import company.payroll.PayrollProcessor;

/*
 * Access Specifiers — who is allowed to touch what
 * -------------------------------------------------
 * The scenario: an enterprise HR system split across two packages,
 * company.hr (owns Employee data) and company.payroll (calculates pay).
 *
 * The four levels, enterprise reason each exists:
 *   public    - employeeId, name        -> every part of the app needs these
 *   protected - baseSalary              -> payroll (a different package) needs it via
 *                                          inheritance, but random code shouldn't
 *   default   - internalHrNotes         -> only other classes IN company.hr are
 *                                          trusted with internal notes
 *   private   - ssn                     -> nobody but Employee itself, ever
 *
 * Run `javac company/AccessSpecifiersDemo.java company/hr/*.java company/payroll/*.java`
 * then `java company.AccessSpecifiersDemo` from inside the AccessSpecifiers folder.
 */
public class AccessSpecifiersDemo {

    public static void main(String[] args) {
        publicAccessEverywhere();
        protectedAcrossPackagesViaInheritance();
        defaultOnlyInsideSamePackage();
        privateNeverLeavesItsClass();
    }

    // -----------------------------------------------------------------
    // 1. PUBLIC: reachable from this class, which is in yet another
    // package (company), with no inheritance relationship at all.
    // -----------------------------------------------------------------
    static void publicAccessEverywhere() {
        System.out.println("--- public: reachable from any package ---");
        Employee e = new Employee("E100", "Asha", 60000, "123-45-6789");
        System.out.println(e.employeeId + " - " + e.name);
    }

    // -----------------------------------------------------------------
    // 2. PROTECTED: PayrollProcessor lives in company.payroll, a
    // different package - but because it EXTENDS Employee, it can
    // still reach baseSalary.
    // -----------------------------------------------------------------
    static void protectedAcrossPackagesViaInheritance() {
        System.out.println("\n--- protected: different package, but a subclass ---");
        PayrollProcessor p = new PayrollProcessor("E101", "Ben", 72000, "222-33-4444");
        System.out.println(p.name + "'s monthly pay = " + p.calculateMonthlyPay());

        // p.baseSalary; // COMPILE ERROR here in AccessSpecifiersDemo:
        // // this class is NEITHER in company.hr NOR a subclass of Employee.
    }

    // -----------------------------------------------------------------
    // 3. DEFAULT (package-private): Manager is in the SAME package as
    // Employee (company.hr), so it can read/write internalHrNotes.
    // Note PayrollProcessor above could NOT, despite also being a
    // subclass - default ignores inheritance, it only cares about package.
    // -----------------------------------------------------------------
    static void defaultOnlyInsideSamePackage() {
        System.out.println("\n--- default: same package only, inheritance doesn't matter ---");
        Manager m = new Manager("E102", "Chen", 90000, "333-22-1111");
        m.giveRaise(5000);
        m.printNotes();

        // From here (package company), this also fails to compile:
        // m.internalHrNotes; // COMPILE ERROR: default access, different package
    }

    // -----------------------------------------------------------------
    // 4. PRIVATE: not even Manager or PayrollProcessor (both subclasses)
    // can see Employee's ssn field directly - only a public method
    // that Employee itself controls can expose a safe, masked version.
    // -----------------------------------------------------------------
    static void privateNeverLeavesItsClass() {
        System.out.println("\n--- private: not even a subclass can see it ---");
        Employee e = new Employee("E103", "Deepa", 55000, "999-88-7777");
        System.out.println("SSN on file: " + e.getMaskedSsn());
        // e.ssn; // COMPILE ERROR: private to Employee, no exceptions
    }
}
