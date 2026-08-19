/*
 * ================================================================
 * THEORY — HashMap (java.util.HashMap) + INTERNAL WORKING
 * ================================================================
 * Stores key -> value pairs. Keys unique, values can repeat, null key allowed
 * (only one), multiple null values allowed. No ordering guarantee.
 *
 * INTERNAL WORKING (the part interviewers actually probe for 3+ yrs devs):
 *
 *   put(key, value)
 *        |
 *        v
 *   hash()          -> HashMap computes hash = key.hashCode() ^ (hashCode >>> 16)
 *        |              (the >>> 16 "spreads" high bits into low bits so the bucket
 *        |               index doesn't ignore them — reduces collisions)
 *        v
 *   bucket          -> index = hash & (capacity - 1)   (capacity is always a power
 *        |              of 2, so this is a fast substitute for hash % capacity)
 *        v
 *   collision?      -> two different keys can land in the SAME bucket.
 *        |              Java 8+: bucket holds a LinkedList of entries; if a single
 *        |              bucket's list grows past 8 entries AND table capacity >= 64,
 *        |              it's converted to a Red-Black Tree (O(log n) instead of O(n)
 *        |              lookup in that bucket) — this was a Java 8 optimization
 *        |              against hash-flooding DoS attacks.
 *        v
 *   equals()        -> within a bucket, HashMap uses equals() to check if a key
 *        |              already exists (hashCode alone isn't enough — two different
 *        |              keys CAN share a hash, this is exactly what a collision is).
 *        |              If equals() matches -> overwrite value. Else -> new entry.
 *        v
 *   load factor      -> default 0.75. When size > capacity * loadFactor, the map
 *        |              needs to grow (a full bucket array made lookups slower).
 *        v
 *   resize()         -> capacity DOUBLES (e.g. 16 -> 32), and EVERY existing entry
 *                        is rehashed and redistributed into the new, bigger bucket
 *                        array. This is why resize is expensive — if you know the
 *                        expected size upfront, pass it to the constructor:
 *                        new HashMap<>(expectedSize / 0.75f) to avoid resizing.
 *
 * CONTRACT TO REMEMBER: if you override equals(), you MUST override hashCode()
 * too (equal objects must have equal hash codes), or a custom key class will
 * silently break HashMap lookups.
 * ================================================================
 */

import java.util.HashMap;
import java.util.Map;

public class HashMapDemo {

    public static void main(String[] args) {
        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("Mouse", 50);
        inventory.put("Keyboard", 30);
        inventory.put("Mouse", 45);           // overwrites - equals() matched existing key
        System.out.println("inventory = " + inventory);

        System.out.println("get Keyboard = " + inventory.get("Keyboard"));
        System.out.println("getOrDefault Monitor = " + inventory.getOrDefault("Monitor", 0));

        inventory.putIfAbsent("Monitor", 10);  // only inserts if key absent
        inventory.putIfAbsent("Mouse", 999);   // no-op, "Mouse" already present
        System.out.println("after putIfAbsent = " + inventory);

        inventory.merge("Mouse", 5, Integer::sum);  // atomic "read, compute, write"
        System.out.println("after merge (+5 to Mouse) = " + inventory);

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println("\n--- Interview Question ---");
        String s = "swiss";
        System.out.println("String: \"" + s + "\"");
        System.out.println("First non-repeating char (HashMap approach): " + firstNonRepeating(s));
        System.out.println("First non-repeating char (alternative, no HashMap): " + firstNonRepeatingAlt(s));
    }

    /*
     * INTERVIEW QUESTION (asked to Java dev, 3+ yrs exp):
     * "Find the first non-repeating character in a string."
     *
     * APPROACH:
     *  - We need frequency counts first, then a second pass to find the first
     *    character whose count is 1 — a HashMap gives O(1) count lookups.
     *  - Pass 1: build a Map<Character, Integer> of frequency counts.
     *  - Pass 2: iterate the string IN ORDER (not the map — maps don't guarantee
     *    order) and return the first char whose map count == 1.
     *
     * HINT: Why two passes and not one? Because you can't know a character is
     *       "non-repeating" until you've seen the WHOLE string.
     *
     * SOLUTION: firstNonRepeating() below. Time O(n), Space O(1) (bounded by
     * alphabet size, technically O(k)).
     * ALTERNATIVE (without HashMap — nested loop, O(n^2) but O(1) extra space):
     * firstNonRepeatingAlt() below.
     */
    static Character firstNonRepeating(String s) {
        Map<Character, Integer> counts = new HashMap<>();
        for (char c : s.toCharArray()) {
            counts.merge(c, 1, Integer::sum);
        }
        for (char c : s.toCharArray()) {
            if (counts.get(c) == 1) {
                return c;
            }
        }
        return null;
    }

    static Character firstNonRepeatingAlt(String s) {
        for (int i = 0; i < s.length(); i++) {
            boolean repeated = false;
            for (int j = 0; j < s.length(); j++) {
                if (i != j && s.charAt(i) == s.charAt(j)) {
                    repeated = true;
                    break;
                }
            }
            if (!repeated) {
                return s.charAt(i);
            }
        }
        return null;
    }
}
