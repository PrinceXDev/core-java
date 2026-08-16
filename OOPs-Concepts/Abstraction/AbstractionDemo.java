/*
 * Abstraction — hide the HOW, expose only the WHAT
 * -----------------------------------------------------
 * Two tools in Java:
 *   1) abstract class -> partial blueprint: some methods have bodies
 *      (shared code), some don't (must be filled in by subclasses).
 *   2) interface       -> a pure contract: "any class that implements me
 *      MUST provide these methods." (Java 8+ also allows default/static
 *      methods with bodies.)
 */

/* 
Abstraction is the process of hiding implementation details and showing only the required functionality to the user.
In other words, it displays only the essential features while hiding internal details.
*/

public class AbstractionDemo {

    public static void main(String[] args) {
        abstractClassExample();
        interfaceExample();
        interfaceDefaultMethod();
    }

    // -----------------------------------------------------------------
    // 1. Abstract class: shared code (template) + subclass-specific parts
    // -----------------------------------------------------------------
    static abstract class Employee {
        protected String name;

        Employee(String name) {
            this.name = name;
        }

        // Concrete method - shared by every subclass, no need to repeat it
        void clockIn() {
            System.out.println(name + " clocked in at 9:00 AM");
        }

        // Abstract method - no body here; EVERY subclass must supply one
        abstract double calculateSalary();

        void printPayslip() {
            System.out.println(name + "'s salary = $" + calculateSalary());
        }
    }

    static class FullTimeEmployee extends Employee {
        double monthlySalary;

        FullTimeEmployee(String name, double monthlySalary) {
            super(name);
            this.monthlySalary = monthlySalary;
        }

        @Override
        double calculateSalary() {
            return monthlySalary;
        }
    }

    static class ContractEmployee extends Employee {
        double hourlyRate;
        int hoursWorked;

        ContractEmployee(String name, double hourlyRate, int hoursWorked) {
            super(name);
            this.hourlyRate = hourlyRate;
            this.hoursWorked = hoursWorked;
        }

        @Override
        double calculateSalary() {
            return hourlyRate * hoursWorked;
        }
    }

    static void abstractClassExample() {
        System.out.println("--- abstract class ---");
        // Employee e = new Employee("X"); // COMPILE ERROR: can't instantiate an
        // abstract class

        Employee[] staff = {
                new FullTimeEmployee("Meera", 5000),
                new ContractEmployee("Ravi", 25, 120)
        };
        for (Employee e : staff) {
            e.clockIn(); // shared, inherited behaviour
            e.printPayslip(); // uses each subclass's own calculateSalary()
        }
    }

    // -----------------------------------------------------------------
    // 2. Interface: pure contract, used by UNRELATED classes
    // (a Printer and a PDF exporter have nothing else in common,
    // but both can promise to "print")
    // -----------------------------------------------------------------
    interface Printable {
        void print(); // implicitly public + abstract
    }

    static class Invoice implements Printable {
        public void print() {
            System.out.println("Printing invoice...");
        }
    }

    static class Report implements Printable {
        public void print() {
            System.out.println("Printing report...");
        }
    }

    static void interfaceExample() {
        System.out.println("\n--- interface (pure contract) ---");
        Printable[] documents = { new Invoice(), new Report() };
        for (Printable doc : documents) {
            doc.print(); // caller doesn't care HOW each one prints internally
        }
    }

    // -----------------------------------------------------------------
    // 3. Interface with a default method (Java 8+): shared code even
    // inside an interface, without forcing every implementer to
    // rewrite it. Implementers can still override it if they need to.
    // -----------------------------------------------------------------
    interface Greeter {
        String getName();

        default void greet() { // has a body - optional to override
            System.out.println("Hello, " + getName() + "!");
        }
    }

    static class EnglishGreeter implements Greeter {
        public String getName() {
            return "World";
        }
        // uses Greeter's default greet() as-is
    }

    static class CustomGreeter implements Greeter {
        public String getName() {
            return "ZURU";
        }

        @Override
        public void greet() { // overrides the default
            System.out.println("Namaste, " + getName() + "!");
        }
    }

    static void interfaceDefaultMethod() {
        System.out.println("\n--- interface default method ---");
        new EnglishGreeter().greet(); // "Hello, World!"
        new CustomGreeter().greet(); // "Namaste, ZURU!"
    }
}
