/*
 * Polymorphism — "many forms": one name, different behaviour
 * -------------------------------------------------------------
 * Two kinds in Java:
 *   1) Compile-time (static)  -> method OVERLOADING   (resolved by the compiler)
 *   2) Runtime (dynamic)      -> method OVERRIDING     (resolved by the JVM,
 *                                 based on the actual object, not the reference type)
 */
public class PolymorphismDemo {

    public static void main(String[] args) {
        compileTimePolymorphism();
        runtimePolymorphism();
        realWorldUseCase();
    }

    // -----------------------------------------------------------------
    // 1. Compile-time polymorphism: method overloading
    //    Same method name, DIFFERENT parameter list (type/count/order).
    //    The compiler picks the right one based on the arguments you pass.
    // -----------------------------------------------------------------
    static class Calculator {
        int add(int a, int b) {
            return a + b;
        }
        double add(double a, double b) {          // different parameter TYPE
            return a + b;
        }
        int add(int a, int b, int c) {            // different parameter COUNT
            return a + b + c;
        }
        // NOTE: overloading is NOT decided by return type alone - that
        // would not compile. Parameters must differ.
    }

    static void compileTimePolymorphism() {
        System.out.println("--- compile-time polymorphism (overloading) ---");
        Calculator calc = new Calculator();
        System.out.println("add(2, 3)         = " + calc.add(2, 3));
        System.out.println("add(2.5, 3.5)     = " + calc.add(2.5, 3.5));
        System.out.println("add(1, 2, 3)      = " + calc.add(1, 2, 3));
        // Which method runs is already decided when the code COMPILES.
    }

    // -----------------------------------------------------------------
    // 2. Runtime polymorphism: method overriding + dynamic dispatch
    //    The reference type can be the parent, but the METHOD THAT RUNS
    //    depends on the actual object created with `new`.
    // -----------------------------------------------------------------
    static abstract class Shape {
        abstract double area();                  // contract every shape must fulfil

        void printArea() {
            System.out.println(getClass().getSimpleName() + " area = " + area());
        }
    }
    static class Circle extends Shape {
        double radius;
        Circle(double radius) { this.radius = radius; }
        @Override double area() { return Math.PI * radius * radius; }
    }
    static class Rectangle extends Shape {
        double w, h;
        Rectangle(double w, double h) { this.w = w; this.h = h; }
        @Override double area() { return w * h; }
    }

    static void runtimePolymorphism() {
        System.out.println("\n--- runtime polymorphism (overriding) ---");
        Shape[] shapes = {
            new Circle(3),
            new Rectangle(4, 5)
        };

        for (Shape s : shapes) {
            // `s` is declared as Shape, but s.area() calls the ACTUAL object's
            // version - Circle's or Rectangle's - decided at RUNTIME.
            s.printArea();
        }
    }

    // -----------------------------------------------------------------
    // 3. Real-world use case: payment processing that doesn't care HOW
    //    each payment method actually works internally.
    // -----------------------------------------------------------------
    interface PaymentMethod {
        void pay(double amount);
    }
    static class CreditCard implements PaymentMethod {
        public void pay(double amount) {
            System.out.println("Charged $" + amount + " to credit card");
        }
    }
    static class UpiPayment implements PaymentMethod {
        public void pay(double amount) {
            System.out.println("Debited $" + amount + " via UPI");
        }
    }

    static void checkout(PaymentMethod method, double amount) {
        // This function works for ANY current or future PaymentMethod
        // implementation, without modification. That's the payoff of
        // polymorphism combined with abstraction.
        method.pay(amount);
    }

    static void realWorldUseCase() {
        System.out.println("\n--- real world: pluggable payment methods ---");
        checkout(new CreditCard(), 250.0);
        checkout(new UpiPayment(), 99.5);
    }
}
