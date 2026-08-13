import java.util.*;

/*
 * NON-PRIMITIVE (REFERENCE) DATA TYPES IN JAVA
 * ============================================
 * Primitive : the variable HOLDS the value.          int a = 10;   -> [10]
 * Reference : the variable HOLDS AN ADDRESS to an    String s = "hi";
 *             object living on the heap.                            -> [0x7f3a] --> "hi"
 *
 * The 5 kinds of non-primitive type:
 *   1. String          (special: immutable + literal pool)
 *   2. Array           (int[], String[], ... - an object, even for primitives)
 *   3. Class / Object   (your own types: Employee, Order, ...)
 *   4. Interface        (a reference type you cannot instantiate directly)
 *   5. Enum             (a fixed set of named object constants)
 *  (+ Wrapper classes  Integer, Byte, Double ... - objects that box a primitive)
 */
public class NonPrimitiveDemo {

    public static void main(String[] args) {
        one_primitiveVsReference();
        two_stringAndEqualsTrap();
        three_arrays();
        four_classesAndObjects();
        five_interfaces();
        six_enums();
        seven_wrappersAndCollections();
        eight_passByValueOfReference();
    }

    // ---------- 1. The core difference ----------
    static void one_primitiveVsReference() {
        System.out.println("=== 1. primitive vs reference ===");

        int a = 10;
        int b = a;          // COPY OF THE VALUE
        b = 99;
        System.out.println("primitive: a=" + a + " b=" + b + "   -> a unaffected");

        int[] p = {10};
        int[] q = p;        // COPY OF THE ADDRESS - both point to the SAME array
        q[0] = 99;
        System.out.println("reference: p[0]=" + p[0] + " q[0]=" + q[0] + " -> both changed!");

        // Only reference types can be null. Primitives cannot.
        String s = null;            // legal
        // int i = null;            // ERROR
        System.out.println("reference default/assignable value: " + s);
    }

    // ---------- 2. String: the type everyone gets wrong ----------
    static void two_stringAndEqualsTrap() {
        System.out.println("\n=== 2. String ===");

        String s1 = "ZURU";               // goes in the String constant pool
        String s2 = "ZURU";               // SAME pooled object
        String s3 = new String("ZURU");   // brand-new object on the heap

        System.out.println("s1 == s2      : " + (s1 == s2));        // true  (same address)
        System.out.println("s1 == s3      : " + (s1 == s3));        // false (different address!)
        System.out.println("s1.equals(s3) : " + s1.equals(s3));     // true  (same content)
        // RULE: == compares ADDRESSES. equals() compares CONTENT. Always use equals() for objects.

        // Strings are IMMUTABLE - methods return a new String, they never modify in place
        String name = "prince";
        name.toUpperCase();                       // result thrown away
        System.out.println("after ignoring result: " + name);
        name = name.toUpperCase();                // must reassign
        System.out.println("after reassigning    : " + name);

        // Building strings in a loop: use StringBuilder (mutable), not +=
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) sb.append(i).append(",");
        System.out.println("StringBuilder result : " + sb);
    }

    // ---------- 3. Arrays ----------
    static void three_arrays() {
        System.out.println("\n=== 3. arrays ===");

        int[] marks = new int[3];                 // elements default to 0
        String[] names = new String[3];           // elements default to null  <-- reference type!
        System.out.println("int[]    defaults: " + Arrays.toString(marks));
        System.out.println("String[] defaults: " + Arrays.toString(names) + "  <- NPE risk");

        int[][] matrix = { {1, 2, 3}, {4, 5, 6} };
        System.out.println("2D array         : " + Arrays.deepToString(matrix));
        System.out.println("length is FIXED  : " + matrix[0].length + " (arrays cannot grow)");
    }

    // ---------- 4. Class + object: the one you write yourself ----------
    static class Employee {
        private final int id;
        private final String name;
        private final Department dept;             // enum field
        private double salary;

        Employee(int id, String name, Department dept, double salary) {
            this.id = id; this.name = name; this.dept = dept; this.salary = salary;
        }

        void raise(double pct) { this.salary += this.salary * pct / 100; }

        int getId() { return id; }
        String getName() { return name; }
        Department getDept() { return dept; }
        double getSalary() { return salary; }

        // Whenever you use == vs equals, THIS is what equals() calls
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Employee)) return false;
            return id == ((Employee) o).id;        // identity = id
        }
        @Override public int hashCode() { return Integer.hashCode(id); }
        @Override public String toString() {
            return String.format("Employee{id=%d, name=%s, dept=%s, salary=%.0f}", id, name, dept, salary);
        }
    }

    static void four_classesAndObjects() {
        System.out.println("\n=== 4. class / object ===");

        Employee e1 = new Employee(101, "Prince", Department.ENGINEERING, 50000);
        Employee e2 = new Employee(101, "Prince", Department.ENGINEERING, 50000);

        System.out.println(e1);
        System.out.println("e1 == e2      : " + (e1 == e2));      // false - two separate objects
        System.out.println("e1.equals(e2) : " + e1.equals(e2));   // true  - our rule: same id

        e1.raise(10);
        System.out.println("after 10% raise: " + e1);
    }

    // ---------- 5. Interface as a reference type ----------
    interface Payable {                            // cannot do: new Payable()
        double monthlyPay();
        default String label() { return "Payable"; }   // default method
    }

    static class Contractor implements Payable {
        private final double dayRate; private final int days;
        Contractor(double dayRate, int days) { this.dayRate = dayRate; this.days = days; }
        public double monthlyPay() { return dayRate * days; }
        public String label() { return "Contractor"; }
    }

    static class Salaried implements Payable {
        private final double annual;
        Salaried(double annual) { this.annual = annual; }
        public double monthlyPay() { return annual / 12; }
        public String label() { return "Salaried"; }
    }

    static void five_interfaces() {
        System.out.println("\n=== 5. interface ===");

        // ONE reference type, MANY runtime objects -> polymorphism
        List<Payable> payroll = List.of(new Contractor(500, 20), new Salaried(96000));
        double total = 0;
        for (Payable p : payroll) {
            System.out.printf("%-11s pays %.2f/month%n", p.label(), p.monthlyPay());
            total += p.monthlyPay();
        }
        System.out.printf("total monthly outlay: %.2f%n", total);
    }

    // ---------- 6. Enum ----------
    enum Department {
        ENGINEERING("Auckland"), DESIGN("Modena"), SALES("Shenzhen");
        private final String hub;
        Department(String hub) { this.hub = hub; }
        String hub() { return hub; }
    }

    static void six_enums() {
        System.out.println("\n=== 6. enum ===");
        for (Department d : Department.values())
            System.out.println(d.ordinal() + " " + d + " -> hub " + d.hub());

        Department d = Department.DESIGN;
        // enum constants are singletons, so == IS safe here (and preferred)
        System.out.println("DESIGN == valueOf(\"DESIGN\") : " + (d == Department.valueOf("DESIGN")));

        switch (d) {
            case ENGINEERING -> System.out.println("builds it");
            case DESIGN      -> System.out.println("draws it");
            case SALES       -> System.out.println("sells it");
        }
    }

    // ---------- 7. Wrappers + collections ----------
    static void seven_wrappersAndCollections() {
        System.out.println("\n=== 7. wrappers & collections ===");

        // Collections can only hold OBJECTS, never primitives -> wrappers exist for this
        // List<int> is illegal;  List<Integer> is fine
        List<Integer> nums = new ArrayList<>();
        nums.add(10);                     // autoboxing: int 10 -> Integer.valueOf(10)
        nums.add(20);
        int first = nums.get(0);          // unboxing: Integer -> int
        System.out.println("List<Integer> : " + nums + "  first as int = " + first);

        Map<String, Employee> byName = new LinkedHashMap<>();
        Employee e = new Employee(102, "Asha", Department.SALES, 60000);
        byName.put(e.getName(), e);
        System.out.println("Map lookup    : " + byName.get("Asha"));

        // Wrapper gotcha: cached -128..127, so == "works" then suddenly doesn't
        Integer a = 127, b = 127, c = 128, d = 128;
        System.out.println("127 == 127 (Integer) : " + (a == b) + "   <- cached");
        System.out.println("128 == 128 (Integer) : " + (c == d) + "  <- NOT cached, use equals()");

        // Wrapper gotcha: null unboxing = NullPointerException
        Integer maybe = null;
        try { int boom = maybe; System.out.println(boom); }
        catch (NullPointerException ex) { System.out.println("unboxing null -> NullPointerException"); }
    }

    // ---------- 8. Java is pass-by-value... of the reference ----------
    static void mutate(Employee emp)   { emp.raise(100); }              // affects caller's object
    static void reassign(Employee emp) { emp = new Employee(999, "Ghost", Department.SALES, 1); }

    static void eight_passByValueOfReference() {
        System.out.println("\n=== 8. passing objects to methods ===");
        Employee e = new Employee(103, "Ravi", Department.ENGINEERING, 40000);

        mutate(e);
        System.out.println("after mutate()   : " + e + "  <- object CHANGED");

        reassign(e);
        System.out.println("after reassign() : " + e + "  <- caller UNCHANGED");
        // Why: the method got a COPY of the address. Following it changes the shared object;
        // overwriting the copy just repoints the local variable.
    }
}
