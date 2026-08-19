/*
 * ================================================================
 * THEORY — PriorityQueue (java.util.PriorityQueue)
 * ================================================================
 * - A queue where elements come out in PRIORITY order, not insertion order.
 * - Backed internally by a binary heap (an array-based complete binary tree).
 *   Default = MIN-heap -> poll() always returns the SMALLEST element.
 *   Pass a Comparator for a max-heap or custom ordering:
 *     new PriorityQueue<>(Collections.reverseOrder())  -> max-heap
 * - offer()/add() -> O(log n) (sift up), poll()/remove() -> O(log n) (sift down),
 *   peek() -> O(1) (root of the heap, no removal).
 * - NOT sorted internally as a whole — only the root (peek/poll target) is
 *   guaranteed correct. Iterating a PriorityQueue directly does NOT give you
 *   sorted order (common interview trap question!). To get fully sorted
 *   output you must poll() repeatedly.
 * - Custom objects need Comparable implemented, or a Comparator supplied.
 *
 * Real use: task schedulers (highest priority job runs first), Dijkstra's/
 * A* shortest path algorithms, merge-k-sorted-lists, and the extremely common
 * "top K elements" family of interview questions.
 * ================================================================
 */

import java.util.Collections;
import java.util.PriorityQueue;

public class PriorityQueueDemo {

    public static void main(String[] args) {
        // Min-heap: default - smallest comes out first
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.offer(50);
        minHeap.offer(10);
        minHeap.offer(30);
        System.out.println("peek (smallest) = " + minHeap.peek());
        System.out.println("poll order = " + minHeap.poll() + ", " + minHeap.poll() + ", " + minHeap.poll());

        // Max-heap: reverse comparator - largest comes out first
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        maxHeap.offer(50);
        maxHeap.offer(10);
        maxHeap.offer(30);
        System.out.println("\nmax-heap poll order = " + maxHeap.poll() + ", " + maxHeap.poll() + ", " + maxHeap.poll());

        // trap: iterating does NOT give sorted order
        PriorityQueue<Integer> pq = new PriorityQueue<>(java.util.List.of(9, 4, 7, 1, 3));
        System.out.println("\ndirect toString (NOT sorted!) = " + pq);

        System.out.println("\n--- Interview Question ---");
        int[] nums = {3, 2, 1, 5, 6, 4};
        int k = 2;
        System.out.println("Array: " + java.util.Arrays.toString(nums) + ", k = " + k);
        System.out.println("Kth largest (PriorityQueue approach): " + findKthLargest(nums, k));
        System.out.println("Kth largest (alternative, sorting): " + findKthLargestAlt(nums, k));
    }

    /*
     * INTERVIEW QUESTION (asked to Java dev, 3+ yrs exp):
     * "Find the Kth largest element in an unsorted array (duplicates allowed)."
     *
     * APPROACH:
     *  - Naive: sort descending, return index k-1. O(n log n).
     *  - Optimal for large streams / when k << n: keep a MIN-heap of size k.
     *    Walk the array; push each element. Whenever heap size exceeds k,
     *    poll() (removes the smallest) so only the k LARGEST-so-far survive.
     *    After processing everything, the root of the min-heap (peek()) is
     *    exactly the Kth largest element.
     *
     * HINT: Counter-intuitive but important — to find the Kth LARGEST you use
     *       a MIN-heap (of bounded size k), not a max-heap. A max-heap of all
     *       n elements would work too but wastes O(n) space; the size-k
     *       min-heap trick is the answer interviewers are fishing for.
     *
     * SOLUTION: findKthLargest() below. Time O(n log k) — better than sorting
     * when k is small relative to n. Space O(k).
     * ALTERNATIVE (without PriorityQueue — sort the array descending):
     * findKthLargestAlt() below. Time O(n log n), Space O(1) extra (ignoring
     * sort's own space), simpler code but does more work when k is small.
     */
    static int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int n : nums) {
            minHeap.offer(n);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        return minHeap.peek();
    }

    static int findKthLargestAlt(int[] nums, int k) {
        int[] copy = nums.clone();
        java.util.Arrays.sort(copy);
        return copy[copy.length - k];
    }
}
