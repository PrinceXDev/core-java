/* 

Rules of Abstraction
Achieved via abstract class or interface in Java — you cannot instantiate an abstract class or interface directly.
Abstract methods have no body — subclasses/implementers must provide the implementation (unless the subclass is also abstract).
A class with even one abstract method must itself be declared abstract.
Interfaces are 100% abstraction (traditionally) — all methods are implicitly public abstract (until Java 8 added default/static methods, which are exceptions).
Focus on "what" not "how" — abstraction is about design/interface; encapsulation (which it's often confused with) is about hiding data via access modifiers (private, getters/setters).
Abstract classes can have constructors, fields, and concrete methods too — they're not purely abstract; interfaces (pre-Java 8) cannot.
A class extending an abstract class must implement all abstract methods, or be declared abstract itself.

Abstraction vs Encapsulation (commonly mixed up):

Abstraction = hiding complexity (design level — "what operations exist")
Encapsulation = hiding data (implementation level — "how data is protected/accessed")

*/

abstract class ATM {
    // exposed behavior (what)
    public final void withdraw(double amount) {
        if (validateAccount() && checkFraud()) {
            dispenseCash(amount);
        }
    }

    // hidden steps (how) — each bank/vendor implements differently
    protected abstract boolean validateAccount();

    protected abstract boolean checkFraud();

    protected abstract void dispenseCash(double amount);
}

class HDFCAtm extends ATM {
    protected boolean validateAccount() {
        System.out.println("Validating with HDFC core banking system...");
        return true;
    }

    protected boolean checkFraud() {
        System.out.println("Running HDFC fraud engine...");
        return true;
    }

    protected void dispenseCash(double amount) {
        System.out.println("Dispensing ₹" + amount);
    }
}

public class Main {
    public static void main(String[] args) {
        ATM atm = new HDFCAtm();
        atm.withdraw(5000); // user only calls this — internals stay hidden

        Rule1Demo.run();
        Rule7Demo.run();
    }
}

/*
 * Rule 1: Achieved via abstract class or interface — cannot instantiate
 * directly
 *
 * "Instantiate" means creating an object with new. Java won't let you do new
 * on an abstract class or interface directly — because they're incomplete
 * blueprints, not finished classes. You must new a concrete subclass instead.
 */

abstract class Shape {
    abstract double area();
}

class Circle extends Shape {
    double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    double area() {
        return 3.14 * radius * radius;
    }
}

class Rule1Demo {
    static void run() {
        System.out.println("--- Rule 1: cannot instantiate abstract class/interface ---");

        // Shape s = new Shape(); // ❌ COMPILE ERROR: Shape is abstract; cannot be instantiated
        // ATM a = new ATM();     // ❌ same problem — ATM is also abstract

        Shape s = new Circle(5); // ✅ reference type can be abstract, object must be concrete
        System.out.println("Circle area = " + s.area());
    }
}

/*
 * Rule 7: A class extending an abstract class must implement ALL abstract
 * methods, or be declared abstract itself.
 *
 * Two legal options for a subclass:
 *   Option A - implement every abstract method  -> becomes concrete, can use `new`
 *   Option B - implement only some (or none)     -> must stay `abstract` itself,
 *              passing the remaining obligation down to the next subclass.
 *
 * Illegal option (won't compile): extend an abstract class, skip a method,
 * and forget to mark yourself abstract too.
 */

abstract class Shape2 {
    abstract double area();

    abstract double perimeter();
}

// Option B: only implements area(), so Polygon MUST remain abstract
abstract class Polygon extends Shape2 {
    @Override
    double area() {
        return 0; // placeholder shared by all polygons here
    }
    // perimeter() still missing -> compiler forces `abstract` on this class
}

// Option A: fills in the last remaining method -> now fully concrete
class Square extends Polygon {
    double side;

    Square(double side) {
        this.side = side;
    }

    @Override
    double perimeter() {
        return 4 * side;
    }
}

/*
 * class Triangle extends Shape2 {
 *     // forgot area() and perimeter()
 * }
 * // ❌ COMPILE ERROR: "Triangle is not abstract and does not override
 * // abstract method area() in Shape2"
 */

class Rule7Demo {
    static void run() {
        System.out.println("\n--- Rule 7: must implement all abstract methods, or stay abstract ---");

        // Polygon p = new Polygon(); // ❌ COMPILE ERROR: Polygon is still abstract

        Square sq = new Square(4); // ✅ Square implemented the last missing piece
        System.out.println("Square area = " + sq.area() + ", perimeter = " + sq.perimeter());
    }
}