/*
 * Interfaces — a pure "contract" of behaviour, no state
 * -------------------------------------------------------
 * An interface says WHAT a class can do, never HOW (mostly).
 * A class `implements` an interface and must provide the body for every
 * abstract method, or itself be declared abstract.
 *
 * Interface vs abstract class (common interview question):
 *   - Interface: 100% contract, a class can implement MANY interfaces
 *     (Java's answer to "multiple inheritance"). No instance fields.
 *   - Abstract class: can hold state + partial implementation, but a
 *     class can extend only ONE abstract class.
 *   Rule of thumb: interface for "CAN-DO" capability, abstract class for
 *   "IS-A" shared identity.
 */
public class InterfaceDemo {

    public static void main(String[] args) {
        basicContract();
        multipleInterfaces();
        defaultAndStaticMethods();
        functionalInterfaceExample();
    }

    // -----------------------------------------------------------------
    // 1. Basic contract: interface declares, class implements
    // -----------------------------------------------------------------
    interface Payable {
        double calculateSalary();   // implicitly public + abstract
    }

    static class Employee implements Payable {
        double baseSalary;
        Employee(double baseSalary) { this.baseSalary = baseSalary; }

        @Override
        public double calculateSalary() {
            return baseSalary;
        }
    }

    static void basicContract() {
        System.out.println("--- basic interface contract ---");
        Payable p = new Employee(50000);   // reference type is the interface
        System.out.println("salary = " + p.calculateSalary());
    }

    // -----------------------------------------------------------------
    // 2. A class can implement MULTIPLE interfaces (no single-parent limit)
    // -----------------------------------------------------------------
    interface Swimmer { void swim(); }
    interface Flyer { void fly(); }

    static class Duck implements Swimmer, Flyer {
        @Override public void swim() { System.out.println("Duck swims"); }
        @Override public void fly()  { System.out.println("Duck flies"); }
    }

    static void multipleInterfaces() {
        System.out.println("\n--- multiple interfaces ---");
        Duck duck = new Duck();
        duck.swim();
        duck.fly();
    }

    // -----------------------------------------------------------------
    // 3. default and static methods (Java 8+): interfaces can now carry code
    // -----------------------------------------------------------------
    interface Greeter {
        String name();

        // default method: implementing classes get this for free, can override it
        default void greet() {
            System.out.println("Hello, " + name() + "!");
        }

        // static method: called on the interface itself, like Greeter.info()
        static void info() {
            System.out.println("Greeter interface v1.0");
        }
    }

    static class Person implements Greeter {
        String personName;
        Person(String personName) { this.personName = personName; }

        @Override
        public String name() { return personName; }
        // greet() not overridden -> uses the interface's default implementation
    }

    static void defaultAndStaticMethods() {
        System.out.println("\n--- default & static interface methods ---");
        Greeter.info();                 // static method on the interface
        new Person("Prince").greet();   // default method, inherited
    }

    // -----------------------------------------------------------------
    // 4. Functional interface: exactly ONE abstract method -> usable with lambdas
    //    (java.util.function.* ships dozens of these; you can define your own)
    // -----------------------------------------------------------------
    @FunctionalInterface
    interface Calculator {
        int operate(int a, int b);
    }

    static void functionalInterfaceExample() {
        System.out.println("\n--- functional interface + lambda ---");
        Calculator add = (a, b) -> a + b;        // lambda IS the method body
        Calculator multiply = (a, b) -> a * b;
        System.out.println("3 + 4 = " + add.operate(3, 4));
        System.out.println("3 * 4 = " + multiply.operate(3, 4));
        // Deep dive on lambdas/streams lives in Java8Features/
    }
}
