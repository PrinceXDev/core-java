/*
 * static and final — two unrelated keywords that beginners often mix up
 * -------------------------------------------------------------------------
 * static  = belongs to the CLASS, not to any one object. Shared by everyone,
 *           exists even before an object is created.
 * final   = cannot be changed/re-assigned/overridden/extended once set.
 *           Meaning depends on WHAT it's applied to (variable / method / class).
 */
public class StaticAndFinalDemo {

    public static void main(String[] args) {
        staticFieldSharedAcrossObjects();
        staticMethodNoObjectNeeded();
        staticInitializerBlock();
        finalVariable();
        finalMethodCannotBeOverridden();
        finalClassCannotBeExtended();
    }

    // -----------------------------------------------------------------
    // 1. static field: ONE copy shared by every instance
    // -----------------------------------------------------------------
    static class Counter {
        static int totalCount = 0;   // lives on the class, shared
        int id;                      // lives on each object, separate per instance

        Counter() {
            totalCount++;            // every new object bumps the SAME counter
            id = totalCount;
        }
    }

    static void staticFieldSharedAcrossObjects() {
        System.out.println("--- static field shared across instances ---");
        new Counter();
        new Counter();
        Counter c3 = new Counter();
        System.out.println("c3.id = " + c3.id);                     // 3 (instance-specific)
        System.out.println("Counter.totalCount = " + Counter.totalCount); // 3 (shared, accessed via class name)
    }

    // -----------------------------------------------------------------
    // 2. static method: callable without creating an object (e.g. Math.max)
    // -----------------------------------------------------------------
    static class MathUtils {
        static int square(int n) { return n * n; }   // no instance state needed
    }

    static void staticMethodNoObjectNeeded() {
        System.out.println("\n--- static method ---");
        System.out.println("square(5) = " + MathUtils.square(5));   // called via class name directly
    }

    // -----------------------------------------------------------------
    // 3. static initializer block: runs ONCE, when the class is first loaded
    // -----------------------------------------------------------------
    static class Config {
        static String appName;
        static {
            // Good for one-time setup that's more than a single expression
            appName = "CoreJavaLearning";
            System.out.println("static block ran - Config class loaded");
        }
    }

    static void staticInitializerBlock() {
        System.out.println("\n--- static initializer block ---");
        System.out.println("Config.appName = " + Config.appName);
    }

    // -----------------------------------------------------------------
    // 4. final variable: assign once, then it's locked
    // -----------------------------------------------------------------
    static void finalVariable() {
        System.out.println("\n--- final variable ---");
        final double TAX_RATE = 0.18;
        System.out.println("TAX_RATE = " + TAX_RATE);
        // TAX_RATE = 0.20;   // COMPILE ERROR: cannot assign a value to final variable
    }

    // -----------------------------------------------------------------
    // 5. final method: subclasses CANNOT override it (locks the behaviour)
    // -----------------------------------------------------------------
    static class Vehicle {
        final void startEngine() {   // final -> guaranteed behaviour, no subclass can change it
            System.out.println("Engine starting the standard way");
        }
    }

    static class SportsCar extends Vehicle {
        // void startEngine() { ... }   // COMPILE ERROR: cannot override final method
    }

    static void finalMethodCannotBeOverridden() {
        System.out.println("\n--- final method ---");
        new SportsCar().startEngine();   // always the parent's version, guaranteed
    }

    // -----------------------------------------------------------------
    // 6. final class: NOBODY can extend it (e.g. java.lang.String is final)
    // -----------------------------------------------------------------
    static final class ImmutablePoint {
        final int x, y;
        ImmutablePoint(int x, int y) { this.x = x; this.y = y; }
    }
    // static class ExtendedPoint extends ImmutablePoint { }   // COMPILE ERROR

    static void finalClassCannotBeExtended() {
        System.out.println("\n--- final class ---");
        ImmutablePoint p = new ImmutablePoint(3, 4);
        System.out.println("point = (" + p.x + ", " + p.y + ") - class cannot be subclassed");
    }
}
