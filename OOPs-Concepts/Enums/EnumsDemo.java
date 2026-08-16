/*
 * Enums — a fixed set of constants, type-safe
 * -------------------------------------------------------------------------
 * Before enums, people used int/String constants (DAY_MONDAY = 0, ...), but
 * nothing stopped you from passing an invalid number. An enum restricts a
 * variable to ONLY the values you define - the compiler enforces it.
 *
 * Under the hood: an enum is a special kind of class. Each constant is a
 * public static final instance of it, so enums can have fields, constructors,
 * and even methods (including a different method body PER constant).
 */
public class EnumsDemo {

    public static void main(String[] args) {
        basicEnum();
        enumInSwitch();
        enumWithFieldsAndConstructor();
        enumWithAbstractMethodPerConstant();
    }

    // -----------------------------------------------------------------
    // 1. Basic enum: a fixed, type-safe list of values
    // -----------------------------------------------------------------
    enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

    static void basicEnum() {
        System.out.println("--- basic enum ---");
        Day today = Day.WEDNESDAY;
        System.out.println("today = " + today);
        System.out.println("ordinal (position, 0-based) = " + today.ordinal());
        System.out.println("all values = " + java.util.Arrays.toString(Day.values()));
        // Day today = "WEDNESDAY";   // COMPILE ERROR: can't sneak in a raw String
    }

    // -----------------------------------------------------------------
    // 2. Enums work great with switch - compiler warns if you miss a case
    // -----------------------------------------------------------------
    static boolean isWeekend(Day day) {
        return switch (day) {
            case SATURDAY, SUNDAY -> true;
            default -> false;
        };
    }

    static void enumInSwitch() {
        System.out.println("\n--- enum in switch ---");
        System.out.println("Is SATURDAY a weekend? " + isWeekend(Day.SATURDAY));
        System.out.println("Is MONDAY a weekend?   " + isWeekend(Day.MONDAY));
    }

    // -----------------------------------------------------------------
    // 3. Enum with fields + constructor: each constant carries its own data
    // -----------------------------------------------------------------
    enum Planet {
        MERCURY(3.30e23, 2.44e6),
        EARTH(5.97e24, 6.37e6),
        JUPITER(1.90e27, 6.99e7);

        final double mass;    // kg
        final double radius;  // meters

        // Enum constructors are ALWAYS private/package-private - called only
        // internally, once per constant, when the enum class is first loaded.
        Planet(double mass, double radius) {
            this.mass = mass;
            this.radius = radius;
        }

        double surfaceGravity() {
            final double G = 6.67300E-11;
            return G * mass / (radius * radius);
        }
    }

    static void enumWithFieldsAndConstructor() {
        System.out.println("\n--- enum with fields & constructor ---");
        for (Planet p : Planet.values()) {
            System.out.printf("%s surface gravity = %.2f m/s^2%n", p, p.surfaceGravity());
        }
    }

    // -----------------------------------------------------------------
    // 4. Enum with a DIFFERENT method body per constant (anonymous class per value)
    // -----------------------------------------------------------------
    enum Operation {
        ADD {
            @Override int apply(int a, int b) { return a + b; }
        },
        SUBTRACT {
            @Override int apply(int a, int b) { return a - b; }
        },
        MULTIPLY {
            @Override int apply(int a, int b) { return a * b; }
        };

        abstract int apply(int a, int b);   // each constant above supplies its own body
    }

    static void enumWithAbstractMethodPerConstant() {
        System.out.println("\n--- enum with per-constant method body ---");
        for (Operation op : Operation.values()) {
            System.out.println(op + "(4, 2) = " + op.apply(4, 2));
        }
    }
}
