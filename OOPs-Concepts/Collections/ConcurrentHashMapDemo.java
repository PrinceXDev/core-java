/*
 * ================================================================
 * THEORY — ConcurrentHashMap (java.util.concurrent.ConcurrentHashMap)
 * ================================================================
 * - A thread-safe HashMap for multithreaded access, WITHOUT locking the
 *   entire map like Collections.synchronizedMap(new HashMap<>()) does.
 * - Java 8+: locking happens per-bucket (technically per-bin, using CAS
 *   operations and synchronized blocks only on the specific bin being
 *   modified) -> multiple threads can write to DIFFERENT buckets at the
 *   SAME time. Much better throughput than a fully synchronized map.
 * - Reads (get()) generally don't block at all, even during a write.
 * - Iteration is "weakly consistent" — it will NOT throw
 *   ConcurrentModificationException even if the map is modified during
 *   iteration (unlike a plain HashMap), but it also isn't guaranteed to
 *   reflect every change made after the iterator was created.
 * - Does NOT allow null keys or null values (HashMap allows one null key) —
 *   this is deliberate: in a concurrent map, map.get(key) == null is
 *   ambiguous (key absent, vs key present with null value) and unsafe to
 *   resolve without extra locking.
 * - Useful atomic methods: putIfAbsent(), compute(), computeIfAbsent(),
 *   merge() — these are thread-safe "read + modify + write" in one call,
 *   which a plain get()-then-put() on a HashMap is NOT (race condition).
 *
 * Real use: shared in-memory caches, request counters/rate limiters,
 * concurrent word-frequency counters — anywhere multiple threads read/write
 * the same map.
 * ================================================================
 */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapDemo {

    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> hits = new ConcurrentHashMap<>();

        // simulate multiple threads hitting the same endpoint counter
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                hits.merge("/api/orders", 1, Integer::sum); // atomic increment
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Correct total every time: 2000. A plain HashMap here would produce
        // a wrong/inconsistent count due to lost updates (race condition on
        // read-modify-write), and could even throw exceptions under concurrent
        // structural modification.
        System.out.println("Total hits on /api/orders = " + hits.get("/api/orders"));

        System.out.println("\n--- Interview Question ---");
        System.out.println(concurrencyExplanation());
    }

    /*
     * INTERVIEW QUESTION (asked to Java dev, 3+ yrs exp):
     * "Why is HashMap not thread-safe, and how does ConcurrentHashMap solve
     * it without just synchronizing every method?"
     *
     * APPROACH (how to answer, not code):
     *  - HashMap has NO internal locking. Two threads calling put() at the
     *    same time can corrupt the internal bucket linked list (in old Java
     *    versions this could even cause an infinite loop during resize!),
     *    or cause lost updates (both threads read the same old value, both
     *    write back +1, you lose one increment).
     *  - Collections.synchronizedMap(hashMap) fixes correctness but every
     *    single operation locks the WHOLE map — one thread writing blocks
     *    every other thread even reading a totally different key. Bad
     *    throughput under contention.
     *  - ConcurrentHashMap locks at the bucket/bin level (Java 8+, using
     *    CAS + fine-grained synchronized blocks), so unrelated keys in
     *    different buckets don't block each other. It also exposes atomic
     *    compound operations (merge/compute/putIfAbsent) so callers don't
     *    have to hand-roll their own external locking for read-modify-write.
     *
     * HINT: the answer they actually want to hear is "granular/bucket-level
     *       locking instead of a single lock for the whole map" plus
     *       "atomic compute methods instead of separate get()+put()".
     *
     * SOLUTION: see the demo above — ConcurrentHashMap.merge() used as the
     * atomic increment, proven correct with two threads doing 1000 increments
     * each (always totals 2000).
     *
     * ALTERNATIVE (without ConcurrentHashMap — synchronize manually around a
     * plain HashMap, or wrap it with Collections.synchronizedMap): works
     * correctly but serializes ALL access to the map, one thread at a time,
     * even for unrelated keys — noticeably worse under real concurrency.
     */
    static String concurrencyExplanation() {
        return "HashMap: no locking -> race conditions / lost updates under concurrent writes.\n"
             + "synchronizedMap: whole-map lock -> correct but serializes everything.\n"
             + "ConcurrentHashMap: bucket-level locking + atomic compute/merge -> correct AND concurrent.";
    }
}
