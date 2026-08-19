/*
 * ================================================================
 * THEORY — HashSet (java.util.HashSet)
 * ================================================================
 * - Backed internally by a HashMap (each element is stored as a key in a
 *   HashMap<E, Object>, mapped to a dummy constant value "PRESENT").
 * - No duplicates allowed — add() returns false if the element already exists.
 * - No guaranteed order — do NOT rely on iteration order (it depends on hashing,
 *   not insertion order). Use LinkedHashSet if you need insertion order,
 *   TreeSet if you need sorted order.
 * - add() / remove() / contains() are all O(1) average case (thanks to hashing).
 * - Uses hashCode() to find the bucket and equals() to check for duplicates
 *   within that bucket — so for custom objects you MUST override both, or
 *   duplicates won't be detected correctly.
 *
 * Real use: deduplicating a list, membership checks ("have I seen this before?"),
 * tracking visited nodes in graph/BFS/DFS traversal.
 * ================================================================
 */

import java.util.HashSet;
import java.util.Set;

public class HashSetDemo {

    public static void main(String[] args) {
        Set<String> visitedUrls = new HashSet<>();

        System.out.println("add Home: " + visitedUrls.add("/home"));       // true
        System.out.println("add Cart: " + visitedUrls.add("/cart"));       // true
        System.out.println("add Home again: " + visitedUrls.add("/home")); // false - duplicate

        System.out.println("visited = " + visitedUrls);
        System.out.println("contains /cart? " + visitedUrls.contains("/cart"));

        visitedUrls.remove("/cart");
        System.out.println("after remove = " + visitedUrls);

        // custom objects need hashCode()/equals() overridden, else HashSet
        // treats every instance as unique even if fields match
        Set<Point> points = new HashSet<>();
        points.add(new Point(1, 2));
        points.add(new Point(1, 2)); // logically same point
        System.out.println("points (with equals/hashCode overridden) = " + points.size()); // 1

        System.out.println("\n--- Interview Question ---");
        int[] nums = {4, 3, 2, 7, 8, 2, 3, 1};
        System.out.println("Array: " + java.util.Arrays.toString(nums));
        System.out.println("Duplicates (HashSet approach): " + findDuplicates(nums));
        System.out.println("Duplicates (alternative, sorting): " + findDuplicatesAlt(nums));
    }

    /*
     * INTERVIEW QUESTION (asked to Java dev, 3+ yrs exp):
     * "Given an array of integers, find all elements that appear more than once."
     *
     * APPROACH:
     *  - We need O(1) membership checks -> HashSet is the natural fit.
     *  - Walk the array once. Try adding each number to a "seen" set.
     *    If add() returns false, it means the number was already in the set
     *    -> it's a duplicate, add it to a second "result" set (to avoid
     *    reporting the same duplicate twice if it repeats 3+ times).
     *
     * HINT: HashSet.add() returning false IS your duplicate-detection signal —
     *       you don't need contains() + add() as two separate calls.
     *
     * SOLUTION: findDuplicates() below. Time O(n), Space O(n).
     * ALTERNATIVE (without HashSet — sort first, then scan for adjacent equal
     * elements): findDuplicatesAlt() below. Time O(n log n), Space O(1) extra
     * (ignoring sort's own space), but it MUTATES/needs a copy of the input.
     */
    static Set<Integer> findDuplicates(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        Set<Integer> duplicates = new HashSet<>();
        for (int n : nums) {
            if (!seen.add(n)) {
                duplicates.add(n);
            }
        }
        return duplicates;
    }

    static Set<Integer> findDuplicatesAlt(int[] nums) {
        int[] copy = nums.clone();
        java.util.Arrays.sort(copy);
        Set<Integer> duplicates = new HashSet<>();
        for (int i = 1; i < copy.length; i++) {
            if (copy[i] == copy[i - 1]) {
                duplicates.add(copy[i]);
            }
        }
        return duplicates;
    }

    static class Point {
        int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) return false;
            Point p = (Point) o;
            return x == p.x && y == p.y;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(x, y);
        }
    }
}
