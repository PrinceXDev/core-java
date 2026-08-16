import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/*
 * The Object contract — equals(), hashCode(), toString()
 * -------------------------------------------------------------------------
 * Every class in Java implicitly extends Object, which gives 3 methods that
 * you almost always want to override for your own classes:
 *
 *   toString()  - human-readable text instead of "ClassName@1a2b3c"
 *   equals()    - "are these two objects logically the same?" (content, not identity)
 *   hashCode()  - a numeric fingerprint; REQUIRED to keep equals() consistent
 *                 when the object is used in a HashMap/HashSet
 *
 * GOLDEN RULE: if you override equals(), you MUST override hashCode() too.
 * Breaking this rule causes objects to silently "disappear" in Sets/Maps.
 */
public class ObjectMethodsDemo {

    public static void main(String[] args) {
        defaultObjectMethodsAreUseless();
        properToStringEqualsHashCode();
        theBrokenHalfImplementation();
    }

    // -----------------------------------------------------------------
    // 1. Default Object behaviour: identity comparison, ugly toString()
    // -----------------------------------------------------------------
    static class RawPoint {
        int x, y;
        RawPoint(int x, int y) { this.x = x; this.y = y; }
        // no overrides at all - uses Object's defaults
    }

    static void defaultObjectMethodsAreUseless() {
        System.out.println("--- default Object methods (before overriding) ---");
        RawPoint p1 = new RawPoint(1, 2);
        RawPoint p2 = new RawPoint(1, 2);   // same coordinates, different object
        System.out.println("p1.toString() = " + p1);                 // RawPoint@<hashcode garbage>
        System.out.println("p1.equals(p2)? " + p1.equals(p2));       // false! only checks identity
    }

    // -----------------------------------------------------------------
    // 2. Proper overrides: equals() compares content, hashCode() matches it
    // -----------------------------------------------------------------
    static class Point {
        final int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }

        @Override
        public String toString() {
            return "Point(" + x + ", " + y + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;                 // same object -> trivially equal
            if (!(obj instanceof Point other)) return false;   // different type -> not equal
            return this.x == other.x && this.y == other.y;     // compare actual content
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);   // must be equal for two Points that are .equals()
        }
    }

    static void properToStringEqualsHashCode() {
        System.out.println("\n--- proper equals/hashCode/toString ---");
        Point a = new Point(3, 4);
        Point b = new Point(3, 4);
        System.out.println("a = " + a);                       // readable output
        System.out.println("a.equals(b)? " + a.equals(b));    // true - same content now
        System.out.println("a.hashCode() == b.hashCode()? " + (a.hashCode() == b.hashCode()));

        // Because equals+hashCode are consistent, HashSet correctly treats them as duplicates
        Set<Point> points = new HashSet<>();
        points.add(a);
        points.add(b);
        System.out.println("HashSet size (should be 1, not 2) = " + points.size());
    }

    // -----------------------------------------------------------------
    // 3. THE BUG: overriding equals() but forgetting hashCode()
    //    -> logically-equal objects land in different HashSet "buckets"
    // -----------------------------------------------------------------
    static class BrokenPoint {
        final int x, y;
        BrokenPoint(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BrokenPoint other)) return false;
            return this.x == other.x && this.y == other.y;
        }
        // hashCode() NOT overridden -> still uses Object's identity-based hash
    }

    static void theBrokenHalfImplementation() {
        System.out.println("\n--- broken: equals() overridden, hashCode() forgotten ---");
        Set<BrokenPoint> set = new HashSet<>();
        set.add(new BrokenPoint(1, 1));
        set.add(new BrokenPoint(1, 1));   // logically a duplicate, per equals()
        // But HashSet uses hashCode() FIRST to pick a bucket; different hashCodes
        // mean it never even calls equals() to notice they match.
        System.out.println("HashSet size (bug: shows 2, not 1) = " + set.size());
    }
}
