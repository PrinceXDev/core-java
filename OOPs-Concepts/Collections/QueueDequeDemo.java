/*
 * ================================================================
 * THEORY — Queue / Deque (java.util.Queue, java.util.Deque)
 * ================================================================
 * Queue: FIFO (First-In-First-Out). Common implementations: LinkedList,
 * ArrayDeque, PriorityQueue.
 *   offer(e)  -> add to the tail (returns false instead of throwing if full)
 *   poll()    -> remove & return head (returns null instead of throwing if empty)
 *   peek()    -> look at head without removing (returns null if empty)
 *   (add/remove/element are the "throwing" twins of offer/poll/peek)
 *
 * Deque (Double-Ended Queue): insert/remove from BOTH ends. Can act as:
 *   - a Queue (offerLast + pollFirst -> FIFO)
 *   - a Stack (offerFirst/push + pollFirst/pop -> LIFO)
 * ArrayDeque is the recommended implementation for BOTH queue and stack use
 * cases now — it's faster than LinkedList (no node overhead, better cache
 * locality) and faster than the legacy Stack class (which is synchronized
 * and outdated). Rule of thumb asked in interviews: "never use java.util.Stack,
 * use ArrayDeque instead."
 *
 * Real use: task scheduling (FIFO queue), undo/redo & function call stacks
 * (LIFO via Deque), and the sliding-window family of algorithm problems
 * (Deque used to track a monotonic window of candidates).
 * ================================================================
 */

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class QueueDequeDemo {

    public static void main(String[] args) {
        // Queue: FIFO — customer support ticket queue
        Queue<String> tickets = new LinkedList<>();
        tickets.offer("Ticket#1");
        tickets.offer("Ticket#2");
        tickets.offer("Ticket#3");
        System.out.println("queue = " + tickets);
        System.out.println("next to handle (poll) = " + tickets.poll());
        System.out.println("queue after poll = " + tickets);

        // Deque as a Stack: LIFO — browser back-navigation / undo stack
        Deque<String> undoStack = new ArrayDeque<>();
        undoStack.push("Typed 'Hello'");
        undoStack.push("Typed 'Hello World'");
        undoStack.push("Deleted 'World'");
        System.out.println("\nundo stack (top first) = " + undoStack);
        System.out.println("undo (pop) = " + undoStack.pop());
        System.out.println("stack after undo = " + undoStack);

        // Deque as a Queue: both ends usable
        Deque<Integer> dq = new ArrayDeque<>();
        dq.addFirst(2);
        dq.addLast(3);
        dq.addFirst(1);
        dq.addLast(4);
        System.out.println("\ndeque both ends = " + dq);

        System.out.println("\n--- Interview Question ---");
        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        System.out.println("Array: " + java.util.Arrays.toString(nums) + ", window k = " + k);
        System.out.println("Sliding window max (Deque approach): " + java.util.Arrays.toString(maxSlidingWindow(nums, k)));
        System.out.println("Sliding window max (alternative, brute force): " + java.util.Arrays.toString(maxSlidingWindowAlt(nums, k)));
    }

    /*
     * INTERVIEW QUESTION (asked to Java dev, 3+ yrs exp — classic, comes up a lot):
     * "Given an array and a window size k, return the maximum of every
     * contiguous window of size k, as the window slides from left to right."
     *
     * APPROACH:
     *  - Naive: for each window, scan all k elements to find the max ->
     *    O(n*k). Too slow for large inputs, but it's the obvious first answer.
     *  - Optimal: keep a Deque of INDICES, maintained so that the values at
     *    those indices are always in decreasing order (a "monotonic deque").
     *    For each new element:
     *      1. Remove indices from the back whose values are <= current value
     *         (they can never be the max again, the new value beats them).
     *      2. Add the current index to the back.
     *      3. Remove the front index if it's now outside the window
     *         (index <= i - k).
     *      4. Once the window is full (i >= k-1), the front of the deque is
     *         the max for this window.
     *
     * HINT: The deque stores INDEXES, not values — you need the index to know
     *       when an element has "expired" out of the window.
     *
     * SOLUTION: maxSlidingWindow() below. Time O(n) — each index is
     * added/removed from the deque at most once. Space O(k).
     * ALTERNATIVE (without Deque — brute force scan per window):
     * maxSlidingWindowAlt() below. Time O(n*k), Space O(1) extra.
     */
    static int[] maxSlidingWindow(int[] nums, int k) {
        Deque<Integer> deque = new ArrayDeque<>(); // stores indices
        int[] result = new int[nums.length - k + 1];
        for (int i = 0; i < nums.length; i++) {
            while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
                deque.pollLast();
            }
            deque.offerLast(i);
            if (deque.peekFirst() <= i - k) {
                deque.pollFirst();
            }
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        return result;
    }

    static int[] maxSlidingWindowAlt(int[] nums, int k) {
        int[] result = new int[nums.length - k + 1];
        for (int i = 0; i <= nums.length - k; i++) {
            int max = Integer.MIN_VALUE;
            for (int j = i; j < i + k; j++) {
                max = Math.max(max, nums[j]);
            }
            result[i] = max;
        }
        return result;
    }
}
