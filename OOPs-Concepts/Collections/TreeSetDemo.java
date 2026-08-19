/*
 * ================================================================
 * THEORY — TreeSet (java.util.TreeSet)
 * ================================================================
 * - Backed by a Red-Black Tree (self-balancing binary search tree).
 * - Elements are always kept in SORTED order (natural order via Comparable,
 *   or a custom Comparator passed to the constructor).
 * - No duplicates, like any Set.
 * - add() / remove() / contains() are O(log n) — slower than HashSet's O(1),
 *   that's the price you pay for maintaining sorted order.
 * - Extra navigation methods HashSet doesn't have (this is the interview
 *   differentiator): first(), last(), higher(x), lower(x), ceiling(x), floor(x),
 *   headSet(x), tailSet(x), pollFirst(), pollLast().
 * - Custom objects MUST implement Comparable (or you pass a Comparator) —
 *   TreeSet uses compareTo()/compare() to order AND to detect duplicates,
 *   NOT equals()/hashCode() like HashSet does.
 *
 * Real use: leaderboard (always sorted by score), range queries
 * ("give me all orders between these two dates"), nearest-neighbor lookups.
 * ================================================================
 */

import java.util.TreeSet;

public class TreeSetDemo {

    public static void main(String[] args) {
        TreeSet<Integer> scores = new TreeSet<>();
        scores.add(88);
        scores.add(42);
        scores.add(95);
        scores.add(67);
        System.out.println("scores (always sorted) = " + scores);

        System.out.println("lowest score = " + scores.first());
        System.out.println("highest score = " + scores.last());
        System.out.println("smallest score >= 70 (ceiling) = " + scores.ceiling(70));
        System.out.println("largest score <= 70 (floor) = " + scores.floor(70));
        System.out.println("scores below 88 (headSet) = " + scores.headSet(88));
        System.out.println("scores from 67 up (tailSet) = " + scores.tailSet(67));

        System.out.println("\n--- Interview Question ---");
        int[] nums = {7, 10, 4, 3, 20, 15};
        int k = 3;
        System.out.println("Array: " + java.util.Arrays.toString(nums) + ", k = " + k);
        System.out.println("Kth largest (TreeSet approach): " + kthLargest(nums, k));
        System.out.println("Kth largest (alternative, sorting): " + kthLargestAlt(nums, k));
    }

    /*
     * INTERVIEW QUESTION (asked to Java dev, 3+ yrs exp):
     * "Find the Kth largest element in an array (assume distinct elements)."
     *
     * APPROACH:
     *  - TreeSet keeps things sorted for free, and pollLast() gives O(log n)
     *    removal of the current max.
     *  - Put every element into a TreeSet (sorted ascending). Then call
     *    pollLast() k times — the k-th call gives the k-th largest element.
     *
     * HINT: This assumes no duplicates (a Set silently drops them) — mention
     *       that limitation out loud in the interview; for arrays with
     *       duplicates you'd need a different structure (e.g. a min-heap of
     *       size k, see PriorityQueueDemo.java).
     *
     * SOLUTION: kthLargest() below. Time O(n log n), Space O(n).
     * ALTERNATIVE (without TreeSet — sort descending, index k-1):
     * kthLargestAlt() below. Time O(n log n), Space O(1) extra, and it DOES
     * handle duplicates correctly (something the TreeSet approach can't).
     */
    static int kthLargest(int[] nums, int k) {
        TreeSet<Integer> set = new TreeSet<>();
        for (int n : nums) {
            set.add(n);
        }
        int result = 0;
        for (int i = 0; i < k; i++) {
            result = set.pollLast();
        }
        return result;
    }

    static int kthLargestAlt(int[] nums, int k) {
        int[] copy = nums.clone();
        java.util.Arrays.sort(copy);
        return copy[copy.length - k];
    }
}
