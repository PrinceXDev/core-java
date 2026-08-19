/*
 * static METHOD — the one idea to hold on to:
 * -------------------------------------------------------------------------
 * A static method belongs to the CLASS. An instance method belongs to an OBJECT.
 *
 * Instance method -> needs a "this" (a specific object) to work on.
 * Static method   -> has NO "this" at all. That single fact explains every
 *                    rule below. Every "why can't I..." about static traces
 *                    back to "because there is no this".
 */
public class StaticMethodDemo {

    public static void main(String[] args) {
        whyNoThisMeansNoInstanceAccess();
        staticCanCallStaticOnly();
        callingConventions();
        staticIsNotOverridden();
        utilityVsBehaviour();
    }

    // -----------------------------------------------------------------
    // 1. No "this" => a static method cannot touch instance fields
    // -----------------------------------------------------------------
    static class Student {
        String name;            // one per object
        static String school;   // one for the whole class

        Student(String name) { this.name = name; }

        // instance method: has an implicit "this", so "name" means "this.name"
        void printName() {
            System.out.println("  instance method sees name = " + name);
        }

        // static method: no object exists here, so "name" is meaningless
        static void printSchool() {
            System.out.println("  static method sees school = " + school);
            // System.out.println(name);  // COMPILE ERROR: non-static variable
                                          // name cannot be referenced from a
                                          // static context
        }

        // if a static method NEEDS object data, you must hand it the object
        static String describe(Student s) {
            return s.name + " @ " + school;   // "s." is the missing "this"
        }
    }

    static void whyNoThisMeansNoInstanceAccess() {
        System.out.println("--- 1. static has no 'this' ---");
        Student.school = "ZURU Java Training";
        Student p = new Student("Prince");
        p.printName();
        Student.printSchool();                        // no object needed
        System.out.println("  describe() = " + Student.describe(p)); // object passed in
    }

    // -----------------------------------------------------------------
    // 2. Which direction can call which
    // -----------------------------------------------------------------
    static class Calls {
        int value = 10;

        static int staticHelper() { return 5; }
        int instanceHelper()      { return value; }

        void fromInstance() {
            System.out.println("  instance -> static   : OK  (" + staticHelper() + ")");
            System.out.println("  instance -> instance : OK  (" + instanceHelper() + ")");
        }

        static void fromStatic() {
            System.out.println("  static -> static     : OK  (" + staticHelper() + ")");
            // instanceHelper();                      // COMPILE ERROR - needs an object
            System.out.println("  static -> instance   : only via an object");
            System.out.println("                         (" + new Calls().instanceHelper() + ")");
        }
    }

    static void staticCanCallStaticOnly() {
        System.out.println("\n--- 2. who can call whom ---");
        new Calls().fromInstance();
        Calls.fromStatic();
    }

    // -----------------------------------------------------------------
    // 3. How you're supposed to call it
    // -----------------------------------------------------------------
    static class Temp {
        static double toCelsius(double f) { return (f - 32) * 5 / 9; }
    }

    static void callingConventions() {
        System.out.println("\n--- 3. calling a static method ---");
        System.out.println("  ClassName.method()  = " + Temp.toCelsius(98.6));  // correct style

        Temp t = new Temp();
        System.out.println("  object.method()     = " + t.toCelsius(98.6));
        System.out.println("  ^ legal, but misleading: the object is ignored,");
        System.out.println("    the compiler rewrites it to Temp.toCelsius(). Don't do this.");

        Temp nothing = null;
        System.out.println("  even on null ref    = " + nothing.toCelsius(212.0));
        System.out.println("  ^ no NullPointerException - proof the object is never used.");
    }

    // -----------------------------------------------------------------
    // 4. THE classic trap: static methods are HIDDEN, not OVERRIDDEN
    //    (no "this" => no runtime dispatch => the reference TYPE decides)
    // -----------------------------------------------------------------
    static class Parent {
        static void staticGreet()   { System.out.println("  Parent.staticGreet()"); }
        void        instanceGreet() { System.out.println("  Parent.instanceGreet()"); }
    }

    static class Child extends Parent {
        static void staticGreet()   { System.out.println("  Child.staticGreet()"); }
        @Override
        void        instanceGreet() { System.out.println("  Child.instanceGreet()"); }
    }

    static void staticIsNotOverridden() {
        System.out.println("\n--- 4. hiding vs overriding ---");
        Parent ref = new Child();     // declared Parent, actually a Child

        System.out.print("  ref.instanceGreet() -> "); ref.instanceGreet();
        System.out.println("  ^ OVERRIDDEN: the real OBJECT (Child) decides. Runtime.");

        System.out.print("  ref.staticGreet()   -> "); ref.staticGreet();
        System.out.println("  ^ HIDDEN: the declared TYPE (Parent) decides. Compile time.");
        System.out.println("  So polymorphism does NOT apply to static methods.");
    }

    // -----------------------------------------------------------------
    // 5. When SHOULD a method be static?
    // -----------------------------------------------------------------
    static class Account {
        private double balance;               // per-object state

        Account(double balance) { this.balance = balance; }

        // instance: the answer depends on WHICH account -> needs state
        double balanceAfterInterest() { return balance * 1.05; }

        // static: pure input -> output, same answer forever, no state involved
        static boolean isValidAmount(double amount) { return amount > 0; }
    }

    static void utilityVsBehaviour() {
        System.out.println("\n--- 5. when to make it static ---");
        System.out.println("  Account.isValidAmount(-50) = " + Account.isValidAmount(-50));
        System.out.println("  new Account(1000).balanceAfterInterest() = "
                           + new Account(1000).balanceAfterInterest());
        System.out.println("  Rule of thumb: if the method never reads or writes");
        System.out.println("  instance state, it can be static. If it does, it can't.");
        System.out.println("  Real examples: Math.max(), Integer.parseInt(), and main().");
    }
}
