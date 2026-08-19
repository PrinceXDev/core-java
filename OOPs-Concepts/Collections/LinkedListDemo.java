/*
 * ================================================================
 * THEORY — LinkedList (java.util.LinkedList)
 * ================================================================
 * - Doubly linked list: every node holds data + pointer to prev AND next node.
 * - Implements both List and Deque -> can be used as a List, a Stack, or a Queue.
 * - No shifting on insert/remove at the ends -> addFirst()/addLast()/removeFirst()/
 *   removeLast() are O(1).
 * - Random access (get(index)) is O(n) — it has to walk the chain from head/tail,
 *   whichever is closer. This is the #1 reason NOT to use it for lots of get(i) calls.
 * - Insert/remove in the MIDDLE is still O(n) to find the position, but O(1) once
 *   you're there (just relinking pointers, no shifting like ArrayList).
 *
 * Rule of thumb (asked in almost every interview):
 *   ArrayList  -> frequent get(index), rare insert/remove in middle
 *   LinkedList -> frequent add/remove at the ends (queue/stack/undo-history style),
 *                 rarely need random access by index
 *
 * Real use: browser history / undo-redo stack, a task queue, an LRU cache's
 * ordering structure.
 * ================================================================
 */

import java.util.LinkedList;

public class LinkedListDemo {

    public static void main(String[] args) {
        LinkedList<String> history = new LinkedList<>();

        // acts as a Deque
        history.addLast("Home");
        history.addLast("Products");
        history.addLast("Cart");
        System.out.println("history = " + history);

        history.removeLast();                 // user hits "Back"
        System.out.println("after back = " + history);

        history.addFirst("Login");             // pushed before everything
        System.out.println("after addFirst = " + history);

        System.out.println("peekFirst = " + history.peekFirst());
        System.out.println("peekLast  = " + history.peekLast());

        // get(index) works but is O(n) — avoid in a hot loop
        System.out.println("get(1) = " + history.get(1));

        System.out.println("\n--- Interview Question ---");
        Node head = buildList(new int[] {1, 2, 3, 4, 5, 6, 7});
        System.out.println("List: " + printList(head));
        System.out.println("Middle node (fast/slow pointer): " + findMiddle(head).val);
        System.out.println("Middle node (alternative, count+traverse): " + findMiddleAlt(head).val);
    }

    /*
     * INTERVIEW QUESTION (asked to Java dev, 3+ yrs exp):
     * "Find the middle node of a singly linked list in a single pass."
     *
     * Note: java.util.LinkedList doesn't expose its internal Node, so to actually
     * demonstrate pointer manipulation (a very common interview ask) we model a
     * simple custom singly-linked Node here — this is what the interviewer wants
     * to see: your understanding of the underlying data structure, not just API calls.
     *
     * APPROACH:
     *  - Naive: traverse once to get length n, traverse again to node n/2. Two passes.
     *  - Optimal: Floyd's slow/fast pointer. slow moves 1 step, fast moves 2 steps.
     *    When fast reaches the end, slow is at the middle. Single pass.
     *
     * HINT: When does the loop stop for fast? fast == null or fast.next == null
     *       (handles both even and odd length lists).
     *
     * SOLUTION: findMiddle() below.
     * ALTERNATIVE (without the slow/fast trick — two-pass counting): findMiddleAlt() below.
     */
    static Node findMiddle(Node head) {
        Node slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    static Node findMiddleAlt(Node head) {
        int length = 0;
        for (Node n = head; n != null; n = n.next) {
            length++;
        }
        Node cur = head;
        for (int i = 0; i < length / 2; i++) {
            cur = cur.next;
        }
        return cur;
    }

    static Node buildList(int[] values) {
        Node dummy = new Node(0);
        Node tail = dummy;
        for (int v : values) {
            tail.next = new Node(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    static String printList(Node head) {
        StringBuilder sb = new StringBuilder();
        for (Node n = head; n != null; n = n.next) {
            sb.append(n.val);
            if (n.next != null) sb.append(" -> ");
        }
        return sb.toString();
    }

    static class Node {
        int val;
        Node next;
        Node(int val) { this.val = val; }
    }
}
