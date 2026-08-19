/*
 * ================================================================
 * QUICK RECAP — TreeMap
 * ================================================================
 * ONE LINE: A HashMap that keeps its keys sorted, all the time.
 *
 * THINK OF IT AS: a dictionary. Words (keys) are always in order, so you
 * can flip to "the word just before X" without reading the whole book.
 *
 * WHAT YOU GET
 * - Keys always sorted. You never call sort() — insert in any order,
 *   printing always comes out sorted.
 * - Navigation for free: "the key just before / just after X".
 *     firstKey / lastKey          -> the two ends
 *     floorKey(x)                 -> x, or the closest key below it
 *     ceilingKey(x)               -> x, or the closest key above it
 *     headMap / tailMap / subMap  -> a slice of the map
 *
 * WHAT YOU PAY
 * - get/put/remove are O(log n), not HashMap's O(1). Sorting isn't free.
 * - No null keys. It must compare keys to place them, and null can't be
 *   compared -> NullPointerException. (null VALUES are fine.)
 * - Keys must be comparable: either the key class implements Comparable,
 *   or you pass a Comparator to the constructor.
 *
 * USE IT WHEN
 * - You need results in key order (leaderboards, dated records).
 * - You need range questions: "all orders between these two dates".
 * - You need "closest match" instead of "exact match".
 * Otherwise use HashMap — it's faster and it's the default.
 *
 * REMEMBER: HashMap = fast lookup, no order.
 *           TreeMap  = sorted keys + range/closest queries, a bit slower.
 *
 * (Same family: TreeSet is just a TreeMap with the values ignored, exactly
 *  like HashSet is a HashMap with the values ignored.)
 * ================================================================
 */

import java.util.TreeMap;

public class TreeMapDemo {

    public static void main(String[] args) {
        TreeMap<Integer, String> events = new TreeMap<>();
        events.put(900, "Market opens");
        events.put(1200, "Lunch break");
        events.put(1600, "Market closes");
        events.put(1000, "First trade alert");
        System.out.println("events (always sorted by key) = " + events);

        System.out.println("earliest event = " + events.firstEntry());
        System.out.println("latest event = " + events.lastEntry());
        System.out.println("event at/before 1300 (floorKey) = " + events.floorKey(1300));
        System.out.println("event at/after 1300 (ceilingKey) = " + events.ceilingKey(1300));
        System.out.println("events before 1200 (headMap) = " + events.headMap(1200));
        System.out.println("events from 1200 on (tailMap) = " + events.tailMap(1200));

        System.out.println("\n--- Interview Question ---");
        int[] timestamps = { 100, 300, 700, 900, 1200 };
        int target = 650;
        System.out.println("Timestamps: " + java.util.Arrays.toString(timestamps) + ", target = " + target);
        System.out.println("Closest timestamp (TreeMap approach): " + closestTimestamp(timestamps, target));
        System.out
                .println("Closest timestamp (alternative, binary search): " + closestTimestampAlt(timestamps, target));
    }

    /*
     * INTERVIEW QUESTION (asked to Java dev, 3+ yrs exp):
     * "Given a sorted list of event timestamps and a target time, find the
     * timestamp closest to the target."
     *
     * APPROACH:
     * - TreeMap gives floorKey()/ceilingKey() for free — the closest key
     * at-or-below and at-or-above the target. Compare those two candidates.
     * - Load the array into a TreeMap<Integer, Integer> (timestamp -> itself,
     * or -> some payload). Get floorKey(target) and ceilingKey(target),
     * pick whichever is numerically closer to target.
     *
     * HINT: Handle the edge cases — target below the smallest key (floorKey
     * returns null) or above the largest key (ceilingKey returns null).
     *
     * SOLUTION: closestTimestamp() below. Time O(n log n) to build + O(log n)
     * per query.
     * ALTERNATIVE (without TreeMap — binary search directly on the sorted
     * array, since it's already sorted): closestTimestampAlt() below.
     * Time O(log n), Space O(1) — better here since the array is already sorted,
     * but TreeMap wins when the data arrives unsorted/dynamically and you need
     * repeated insert + closest-lookup operations.
     */
    static int closestTimestamp(int[] timestamps, int target) {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        for (int t : timestamps) {
            map.put(t, t);
        }
        Integer floor = map.floorKey(target);
        Integer ceiling = map.ceilingKey(target);
        if (floor == null)
            return ceiling;
        if (ceiling == null)
            return floor;
        return (target - floor) <= (ceiling - target) ? floor : ceiling;
    }

    static int closestTimestampAlt(int[] timestamps, int target) {
        int lo = 0, hi = timestamps.length - 1;
        if (target <= timestamps[lo])
            return timestamps[lo];
        if (target >= timestamps[hi])
            return timestamps[hi];
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            if (timestamps[mid] == target)
                return timestamps[mid];
            if (timestamps[mid] < target)
                lo = mid + 1;
            else
                hi = mid;
        }
        int after = timestamps[lo];
        int before = timestamps[lo - 1];
        return (target - before) <= (after - target) ? before : after;
    }
}
