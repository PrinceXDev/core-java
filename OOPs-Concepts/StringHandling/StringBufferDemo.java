/*
 * ================================================================
 * StringBuffer — THE SYNCHRONIZED MUTABLE STRING
 * ================================================================
 * Third file in the string trilogy:
 *   StringHandlingDemo.java  -> immutability, pool, == vs equals
 *   StringMethodsDemo.java   -> every String method + interview drill
 *   StringBufferDemo.java    -> THIS FILE: the thread-safe builder
 *
 * WHAT IT IS (the definition to say out loud):
 *   A MUTABLE sequence of characters whose every public method is
 *   SYNCHRONIZED. Same job as StringBuilder — build text without
 *   allocating a new String per step — but safe to hand to multiple
 *   threads.
 *
 *      public final class StringBuffer
 *          extends AbstractStringBuilder
 *          implements Serializable, Comparable<StringBuffer>, CharSequence
 *
 * THE FAMILY TREE — this is why the two builders have an identical API:
 *
 *                    CharSequence  (interface: length, charAt, subSequence)
 *                          ^
 *                          |
 *              AbstractStringBuilder   (package-private; holds byte[] value,
 *                    ^        ^         int count, and ALL the real logic)
 *                    |        |
 *          StringBuffer     StringBuilder
 *          (since 1.0)      (since 1.5)
 *          synchronized     no locking
 *
 *   StringBuilder was added in Java 5 as an UNSYNCHRONIZED copy of
 *   StringBuffer, because 99% of buffers are used by one thread and were
 *   paying for a lock they did not need. Same story as
 *   Vector -> ArrayList and Hashtable -> HashMap.
 *
 * INTERNAL LAYOUT (Java 9+, after Compact Strings):
 *
 *   new StringBuffer()            capacity 16, count 0
 *        value -> [ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ ]
 *                   ^count=0
 *   append("Hello")               count 5, no reallocation needed
 *        value -> [ H e l l o _ _ _ _ _ _ _ _ _ _ _ ]
 *                             ^count=5
 *   append 12 more chars          17 > 16, so GROW:
 *        newCapacity = (oldCapacity << 1) + 2   ->  16*2+2 = 34
 *        then Arrays.copyOf(...)  <- the cost you avoid by pre-sizing
 *
 *   Constructors:
 *     new StringBuffer()              -> capacity 16
 *     new StringBuffer(int capacity)  -> exact capacity (pre-size me!)
 *     new StringBuffer(String s)      -> capacity = s.length() + 16
 *     new StringBuffer(CharSequence)  -> same, from any CharSequence
 * ================================================================
 */

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringBufferDemo {

    public static void main(String[] args) throws Exception {
        capacityAndGrowth();
        theFullApi();
        insertDeleteReplaceReverse();
        surrogateSafeReverse();
        toStringCache();
        theThreadSafetyProof();
        synchronizedIsNotAtomic();
        performanceCost();
        equalsAndMutableKeyTrap();
    }

    private static void title(String s) {
        System.out.println("\n=========== " + s + " ===========");
    }

    // =================================================================
    // 1. CAPACITY vs LENGTH — the distinction interviewers probe
    // =================================================================
    static void capacityAndGrowth() {
        title("1. CAPACITY vs LENGTH & GROWTH");

        StringBuffer empty = new StringBuffer();
        System.out.println("new StringBuffer()          -> length=" + empty.length()
                + " capacity=" + empty.capacity());

        StringBuffer fromString = new StringBuffer("Hello");
        System.out.println("new StringBuffer(\"Hello\")   -> length=" + fromString.length()
                + " capacity=" + fromString.capacity() + "   (5 + 16)");

        StringBuffer sized = new StringBuffer(100);
        System.out.println("new StringBuffer(100)       -> length=" + sized.length()
                + " capacity=" + sized.capacity());

        // Watch the (old * 2) + 2 growth rule fire
        StringBuffer grow = new StringBuffer();          // capacity 16
        System.out.println("\ngrowth trace:");
        int lastCapacity = grow.capacity();
        for (int i = 1; i <= 80; i++) {
            grow.append('x');
            if (grow.capacity() != lastCapacity) {
                System.out.println("   length " + grow.length() + " forced capacity "
                        + lastCapacity + " -> " + grow.capacity()
                        + "   (" + lastCapacity + "*2+2)");
                lastCapacity = grow.capacity();
            }
        }

        // ensureCapacity / trimToSize — manual control over the array
        StringBuffer manual = new StringBuffer();
        manual.ensureCapacity(500);
        System.out.println("\nafter ensureCapacity(500)   -> capacity=" + manual.capacity());
        manual.append("tiny");
        manual.trimToSize();
        System.out.println("after append+trimToSize()   -> length=" + manual.length()
                + " capacity=" + manual.capacity());

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "What is the difference between length() and capacity(), and what
         *    does new StringBuffer(\"Hello\").capacity() return?"
         *
         * A: length() is how many characters the buffer CURRENTLY holds;
         *    capacity() is how many it can hold before the backing array must
         *    be reallocated. capacity() >= length() always.
         *    new StringBuffer("Hello") returns 21 — the string's length plus
         *    the default 16 slots of headroom, so a few appends after
         *    construction are free.
         *    Growth is (oldCapacity << 1) + 2, and each growth is an
         *    Arrays.copyOf of everything so far. That is why building a large
         *    document in a default-capacity buffer performs a long chain of
         *    array copies: pass an estimated capacity to the constructor and
         *    you perform zero. This is the cheapest performance win in the
         *    whole String API, and the one candidates most often miss.
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 2. THE FULL API — every method group, on one buffer
    // =================================================================
    static void theFullApi() {
        title("2. THE COMPLETE StringBuffer API");

        StringBuffer sb = new StringBuffer();

        // --- append(...) : overloaded for EVERY type, all return `this` for chaining
        sb.append("text")          // String
          .append(' ')             // char
          .append(42)              // int
          .append(' ')
          .append(3.14)            // double
          .append(' ')
          .append(true)            // boolean
          .append(' ')
          .append(99L)             // long
          .append(' ')
          .append(2.5f)            // float
          .append(' ')
          .append(new char[]{'a','b'})       // char[]
          .append(' ')
          .append((Object) null)   // Object -> the literal text "null"
          .append(' ')
          .append("SUBSEQ", 0, 3); // CharSequence slice
        sb.appendCodePoint(33);    // int code point -> '!'
        System.out.println("append() chain      : " + sb);
        System.out.println("null appended as    : \"null\" text, NOT an NPE");

        // --- read-only accessors, inherited from CharSequence/AbstractStringBuilder
        StringBuffer s = new StringBuffer("hello world hello");
        System.out.println("\nlength()            : " + s.length());
        System.out.println("isEmpty() (Java 15) : " + s.isEmpty());
        System.out.println("charAt(1)           : " + s.charAt(1));
        System.out.println("codePointAt(0)      : " + s.codePointAt(0));
        System.out.println("indexOf(\"hello\")    : " + s.indexOf("hello"));
        System.out.println("indexOf(\"hello\", 1) : " + s.indexOf("hello", 1));
        System.out.println("lastIndexOf(\"hello\"): " + s.lastIndexOf("hello"));
        System.out.println("substring(6)        : " + s.substring(6));
        System.out.println("substring(0, 5)     : " + s.substring(0, 5));
        System.out.println("subSequence(0, 5)   : " + s.subSequence(0, 5));
        System.out.println("chars().count()     : " + s.chars().count());
        System.out.println("compareTo (Java 11) : "
                + new StringBuffer("abc").compareTo(new StringBuffer("abd")));
        System.out.println("toString()          : " + s.toString());

        // NOTE: substring() returns a String, NOT a StringBuffer — it is a
        // read-only extraction and does not modify the buffer.

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "Why does append() return StringBuffer instead of void?"
         *
         * A: To support METHOD CHAINING (a fluent interface): each call
         *    returns `this`, so sb.append(a).append(b).append(c) works on one
         *    object with no intermediate variables. It is also what made the
         *    compiler's own concatenation strategy possible — in Java 8, javac
         *    compiled a + b + c into exactly such a chained StringBuilder
         *    sequence. Do NOT mistake the return value for a new object: it is
         *    the SAME buffer, which is why sb.append("x") mutates the caller's
         *    buffer while s.concat("x") on a String does not.
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 3. MUTATORS — insert, delete, replace, reverse, setCharAt, setLength
    // =================================================================
    static void insertDeleteReplaceReverse() {
        title("3. MUTATOR METHODS (these change the buffer in place)");

        StringBuffer sb = new StringBuffer("Hello World");
        System.out.println("start                  : " + sb);

        sb.insert(5, ",");                       // insert at index, shifting the rest right
        System.out.println("insert(5, \",\")         : " + sb);

        sb.insert(0, 2026);                      // insert() is overloaded for every type too
        System.out.println("insert(0, 2026)        : " + sb);

        sb.replace(0, 4, "YEAR");                // replace a RANGE with a string of any length
        System.out.println("replace(0,4,\"YEAR\")    : " + sb);

        sb.deleteCharAt(4);                      // remove one char
        System.out.println("deleteCharAt(4)        : " + sb);

        sb.delete(0, 4);                         // remove a range [start, end)
        System.out.println("delete(0,4)            : " + sb);

        sb.setCharAt(0, 'J');                    // overwrite in place, returns void
        System.out.println("setCharAt(0,'J')       : " + sb);

        sb.reverse();                            // in-place reversal
        System.out.println("reverse()              : " + sb);

        sb.setLength(5);                         // truncate (or pad with NUL if longer)
        System.out.println("setLength(5) truncates : " + sb);

        sb.setLength(0);                         // THE idiom for "clear and reuse the buffer"
        System.out.println("setLength(0) clears    : [" + sb + "] length=" + sb.length()
                + " capacity kept=" + sb.capacity());

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "How do you clear a StringBuffer for reuse, and which way is best?"
         *
         * A: Three options, and the difference is allocation:
         *      sb.setLength(0)      -> best. Sets count to 0 and KEEPS the
         *                              existing array, so a buffer reused in a
         *                              loop never reallocates.
         *      sb.delete(0, len())  -> equivalent result, slightly more work.
         *      sb = new StringBuffer() -> throws the array away and restarts at
         *                              capacity 16, so the next big build has
         *                              to grow all over again. Worst of the
         *                              three.
         *    Note setLength(n) with n GREATER than the current length is legal
         *    and pads with NUL (' ') characters — a subtle source of
         *    trailing garbage when people use it to "resize" a buffer.
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 4. reverse() IS SURROGATE-AWARE — a detail that shows depth
    // =================================================================
    static void surrogateSafeReverse() {
        title("4. reverse() AND SURROGATE PAIRS");

        // Written as backslash-u escapes DELIBERATELY. (And note this comment says
        // "backslash-u" in words, not the escape itself: javac processes unicode
        // escapes in the SOURCE, before parsing, so a stray one in a COMMENT is a
        // compile error. I hit that too while writing this file.)
        // A raw emoji in the source depends on
        // javac's -encoding matching the file's bytes; on a default Windows setup a
        // UTF-8 emoji is decoded as mojibake and this whole demo silently lies.
        // (I hit exactly that while writing this file.) Escapes are encoding-proof.
        String withEmoji = "ab\uD83D\uDE00";        // 'a','b', then a 2-char emoji
        System.out.println("length in code units         : " + withEmoji.length() + " (a,b + 2)");
        System.out.println("code point count             : "
                + withEmoji.codePointCount(0, withEmoji.length()));

        // StringBuffer.reverse() keeps the surrogate PAIR intact
        StringBuffer reversed = new StringBuffer(withEmoji).reverse();
        System.out.println("reverse() kept pair intact   : "
                + (reversed.codePointAt(0) == 0x1F600));
        System.out.println("first code point after rev   : U+"
                + Integer.toHexString(reversed.codePointAt(0)).toUpperCase());

        // A naive manual reversal SPLITS the pair and produces broken text
        char[] chars = withEmoji.toCharArray();
        for (int i = 0, j = chars.length - 1; i < j; i++, j--) {
            char tmp = chars[i]; chars[i] = chars[j]; chars[j] = tmp;
        }
        System.out.println("naive char-swap first char is a HIGH surrogate? "
                + Character.isHighSurrogate(chars[0]));
        System.out.println("   (false = the LOW surrogate came first = invalid character)");

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "Reverse a string. Now: does your solution handle emoji?"
         *
         * A: new StringBuffer(s).reverse() (or StringBuilder) is the correct
         *    one-liner, and it is deliberately SURROGATE-AWARE: the javadoc
         *    guarantees valid surrogate pairs are treated as single characters
         *    and are NOT reversed internally, so the emoji survives.
         *    A hand-rolled char[] two-pointer swap reverses the code UNITS,
         *    putting the low surrogate before the high one and yielding an
         *    invalid character that renders as a black diamond.
         *    Full-honesty point to add: even reverse() does not preserve
         *    GRAPHEME clusters — an "e" plus a combining accent, a flag emoji,
         *    or a skin-tone modifier still comes out wrong, because those are
         *    multiple code POINTS. For genuinely correct reversal you need
         *    java.text.BreakIterator.getCharacterInstance().
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 5. toString() AND THE toStringCache FIELD
    // =================================================================
    static void toStringCache() {
        title("5. toString() & THE toStringCache FIELD (StringBuffer only)");

        StringBuffer sb = new StringBuffer("data");
        String first  = sb.toString();
        String second = sb.toString();
        System.out.println("two toString() calls -> same object? " + (first == second));
        System.out.println("   ...they are EQUAL though          : " + first.equals(second));

        // The cache is real — it just is not what people assume. Prove it exists:
        System.out.print("StringBuffer declared fields  : ");
        for (Field f : StringBuffer.class.getDeclaredFields()) System.out.print(f.getName() + " ");
        System.out.print("\nStringBuilder declared fields : ");
        for (Field f : StringBuilder.class.getDeclaredFields()) System.out.print(f.getName() + " ");
        System.out.println();

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "Name a functional difference between StringBuffer and
         *    StringBuilder other than synchronization."
         *
         * A: StringBuffer has a private `toStringCache` field that StringBuilder
         *    does not (visible in the reflection dump above). But be precise
         *    about what it caches, because the obvious guess is wrong:
         *
         *      public synchronized String toString() {
         *          if (toStringCache == null) {
         *              return toStringCache = <encode the buffer>;
         *          }
         *          return new String(toStringCache);     // <-- a NEW String!
         *      }
         *
         *    On a cache hit it still returns a NEW String object — so
         *    sb.toString() != sb.toString(), as printed above. What it avoids
         *    is re-ENCODING the character data: new String(String) copies only
         *    the reference to the already-built byte[], not the bytes. Any
         *    mutation nulls the cache.
         *    I checked this on the JDK in use rather than trusting the folklore
         *    answer ("StringBuffer returns the same String instance") — that
         *    claim is repeated widely and is false on modern JDKs.
         *    (Other real differences: StringBuffer dates from 1.0 so it appears
         *    in old signatures like Matcher.appendReplacement, and it is
         *    Comparable<StringBuffer> since Java 11.)
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 6. THE PROOF: thread safety, measured
    // =================================================================
    static void theThreadSafetyProof() throws Exception {
        title("6. THREAD SAFETY — StringBuffer vs StringBuilder, MEASURED");

        int threads = 8, appendsPerThread = 5_000;
        int expected = threads * appendsPerThread;

        // --- StringBuffer: every append is synchronized -> count is always exact
        StringBuffer buffer = new StringBuffer();
        runConcurrently(threads, appendsPerThread, buffer::append);
        System.out.println("StringBuffer  expected " + expected + " chars, got "
                + buffer.length() + "  -> "
                + (buffer.length() == expected ? "CORRECT" : "LOST DATA"));

        // --- StringBuilder: unsynchronized -> lost updates, or an outright crash
        StringBuilder builder = new StringBuilder();
        runConcurrently(threads, appendsPerThread, builder::append);
        System.out.println("StringBuilder expected " + expected + " chars, got "
                + builder.length() + "  -> "
                + (builder.length() == expected
                   ? "happened to match THIS run (still not safe!)"
                   : "LOST DATA (" + (expected - builder.length()) + " chars vanished)"));
        System.out.println("   (re-run this: the StringBuilder number changes every time, and");
        System.out.println("    threads may crash inside append() — that non-determinism IS");
        System.out.println("    the definition of a data race)");

        /* ------------------------------------------------------------------
         * WHY StringBuilder LOSES CHARACTERS:
         *   append() is roughly:  value[count] = ch;  count = count + 1;
         *   Two threads can read the same `count`, both write the same slot,
         *   and both store count+1 — one character is silently overwritten.
         *   Worse, a thread can read a stale `count` mid-resize and index past
         *   the array, throwing ArrayIndexOutOfBoundsException from deep inside
         *   the JDK — a stack trace that looks like a JDK defect but is always
         *   unsynchronized sharing. Both outcomes appear when this runs.
         * ------------------------------------------------------------------ */
    }

    /** Runs `threads` threads, each appending "x" `perThread` times, and waits for all. */
    private static void runConcurrently(int threads, int perThread,
                                        java.util.function.Consumer<String> appender)
            throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        for (int t = 0; t < threads; t++) {
            Thread thread = new Thread(() -> {
                try {
                    start.await();               // release all threads at once for max contention
                    for (int i = 0; i < perThread; i++) appender.accept("x");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
            // an unsynchronized builder can throw from inside append(); do not let that
            // kill the demo, just report it
            thread.setUncaughtExceptionHandler((th, ex) ->
                    System.out.println("   thread crashed inside append(): "
                            + ex.getClass().getSimpleName()));
            thread.start();
        }
        start.countDown();
        done.await();
    }

    // =================================================================
    // 7. THE SENIOR-LEVEL CATCH: synchronized != atomic
    // =================================================================
    static void synchronizedIsNotAtomic() throws Exception {
        title("7. \"THREAD-SAFE\" DOES NOT MEAN YOUR CODE IS CORRECT");

        int threads = 6, recordsPerThread = 300;
        int expectedRecords = threads * recordsPerThread;

        // Each append is atomic. A SEQUENCE of appends is NOT. Every thread writes
        // the 3-part record "<id>" using three separate locked calls.
        StringBuffer unsafe = buildRecords(threads, recordsPerThread, false);
        StringBuffer safe   = buildRecords(threads, recordsPerThread, true);

        report("3 separate append() calls", unsafe, expectedRecords);
        report("one synchronized block   ", safe, expectedRecords);

        System.out.println("\nEach append held the lock, but another thread grabbed it BETWEEN");
        System.out.println("the appends, splicing one record into the middle of another.");

        /* ------------------------------------------------------------------
         * INTERVIEW Q (this is the one that separates levels):
         *   "StringBuffer is thread-safe, so is a multi-threaded program using
         *    it automatically correct?"
         *
         * A: No. StringBuffer guarantees each METHOD CALL is atomic — no
         *    internal corruption, no lost characters. It guarantees NOTHING
         *    about a SEQUENCE of calls. Two patterns still break:
         *      1. Compound writes:  sb.append("<").append(id).append(">")
         *         is three separate lock acquisitions, so another thread can
         *         interleave and garble the record — counted above.
         *      2. Check-then-act:   if (sb.length() == 0) sb.append(header);
         *         the length can change between the check and the append, so
         *         two threads both write the header.
         *    The fix is to hold the lock across the whole logical operation
         *    (synchronized (sb) { ... }). And once you are doing that, the
         *    internal locking is redundant — which is exactly why the modern
         *    answer is "use StringBuilder inside your own synchronized block,
         *    or better, give each thread a local StringBuilder and merge at the
         *    end." Same reasoning that made Vector and Hashtable obsolete:
         *    per-method locking is almost never the right granularity.
         * ------------------------------------------------------------------ */
    }

    /** Every thread writes "<id>" as three appends; `guarded` wraps them in one lock. */
    private static StringBuffer buildRecords(int threads, int perThread, boolean guarded)
            throws Exception {
        StringBuffer log = new StringBuffer();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        for (int t = 0; t < threads; t++) {
            final int id = t;
            new Thread(() -> {
                try {
                    start.await();
                    for (int i = 0; i < perThread; i++) {
                        if (guarded) {
                            synchronized (log) {              // one lock, whole record
                                log.append('<').append(id).append('>');
                            }
                        } else {
                            // the yields simply widen a window that exists regardless
                            log.append('<');
                            Thread.yield();
                            log.append(id);
                            Thread.yield();
                            log.append('>');
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }
        start.countDown();
        done.await();
        return log;
    }

    private static final Pattern RECORD = Pattern.compile("<\\d>");

    /** Counts how many intact "<id>" records survived out of the number written. */
    private static void report(String label, StringBuffer log, int expectedRecords) {
        Matcher m = RECORD.matcher(log);
        int intact = 0;
        while (m.find()) intact++;
        System.out.println(label + " : " + intact + "/" + expectedRecords
                + " records intact" + (intact == expectedRecords ? "" : "  <-- GARBLED"));
        System.out.println("   first 48 chars: " + log.substring(0, Math.min(48, log.length())));
    }

    // =================================================================
    // 8. WHAT THE LOCK COSTS
    // =================================================================
    static void performanceCost() {
        title("8. SINGLE-THREADED COST OF THE LOCK");

        int n = 3_000_000;

        long t0 = System.nanoTime();
        StringBuffer buffer = new StringBuffer(n);
        for (int i = 0; i < n; i++) buffer.append('x');
        long bufferMs = (System.nanoTime() - t0) / 1_000_000;

        long t1 = System.nanoTime();
        StringBuilder builder = new StringBuilder(n);
        for (int i = 0; i < n; i++) builder.append('x');
        long builderMs = (System.nanoTime() - t1) / 1_000_000;

        long t2 = System.nanoTime();
        String slow = "";
        for (int i = 0; i < 30_000; i++) slow += "x";        // only 30k — it is quadratic
        long concatMs = (System.nanoTime() - t2) / 1_000_000;

        System.out.println("StringBuffer  " + n + " appends : " + bufferMs + " ms");
        System.out.println("StringBuilder " + n + " appends : " + builderMs + " ms");
        System.out.println("String +=     30000 appends  : " + concatMs
                + " ms  <- quadratic, on 100x FEWER operations");
        System.out.println("\nCAVEAT: crude wall-clock timing, not JMH — no warmup, so the JIT is");
        System.out.println("still compiling during the first loop. Treat the buffer/builder gap as");
        System.out.println("'the lock is not free', not as a precise ratio. The += result is the");
        System.out.println("only one whose SHAPE (quadratic) matters more than its timing.");

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "If StringBuffer is only somewhat slower single-threaded, why not
         *    just always use it and be safe?"
         *
         * A: Two reasons, and the second matters more.
         *    (1) Cost: the JIT can often optimise away an uncontended lock, so
         *        the penalty varies — but it is never negative, and the lock
         *        blocks other optimisations such as escape analysis / scalar
         *        replacement, which can eliminate a thread-local StringBuilder
         *        entirely.
         *    (2) Communication: choosing StringBuffer TELLS the next reader
         *        "this buffer is shared across threads." When it is not, you
         *        have written a lie into the type, and the next developer either
         *        trusts it (and reasons wrongly about the design) or ignores it
         *        (and stops trusting your other choices). As section 7 shows,
         *        per-method locking is not even sufficient for real sharing, so
         *        it buys false confidence.
         *    Rule: StringBuilder by default. StringBuffer only for a buffer
         *    genuinely shared between threads — and then ask why it is shared.
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 9. THE TRAPS: no equals(), and what "mutable key" really breaks
    // =================================================================
    static void equalsAndMutableKeyTrap() {
        title("9. equals()/hashCode() ARE NOT OVERRIDDEN");

        StringBuffer a = new StringBuffer("same");
        StringBuffer b = new StringBuffer("same");

        System.out.println("a.equals(b) (identity!) : " + a.equals(b));      // false!
        System.out.println("a.toString().equals(..) : " + a.toString().equals(b.toString()));
        System.out.println("a.compareTo(b) == 0     : " + (a.compareTo(b) == 0));
        System.out.println("\"same\".contentEquals(a) : " + "same".contentEquals(a));

        // Because hashCode() is NOT overridden, it is Object's IDENTITY hash — which
        // does NOT change when the buffer is mutated. So the entry is still findable
        // by the same reference. A buffer is a stable key, just a useless one:
        // nothing can ever look it up BY CONTENT.
        Map<StringBuffer, String> byBuffer = new HashMap<>();
        byBuffer.put(a, "stored under a");
        System.out.println("\nidentity hash of a      : " + a.hashCode());
        System.out.println("get(b) — equal text, different object : " + byBuffer.get(b));
        a.append("!");
        System.out.println("identity hash after mutation         : " + a.hashCode() + " (unchanged)");
        System.out.println("get(a) after mutating the key        : " + byBuffer.get(a));

        // The REAL mutable-key trap needs a key whose hashCode is CONTENT-based:
        List<String> listKey = new ArrayList<>(List.of("k"));
        Map<List<String>, String> byList = new HashMap<>();
        byList.put(listKey, "value");
        System.out.println("\nArrayList key hash before : " + listKey.hashCode()
                + " -> get = " + byList.get(listKey));
        listKey.add("!");
        System.out.println("ArrayList key hash after  : " + listKey.hashCode()
                + " -> get = " + byList.get(listKey) + "   <-- ENTRY UNREACHABLE");
        System.out.println("the entry is still in the map, orphaned : " + byList);

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "Why does StringBuffer not override equals(), and can you use one
         *    as a HashMap key?"
         *
         * A: Neither builder overrides equals() or hashCode(), so both inherit
         *    Object's IDENTITY semantics: two buffers holding identical text are
         *    NOT equal. Compare content with a.toString().equals(b.toString()),
         *    with compareTo() == 0 (Java 11+), or from the String side with
         *    "text".contentEquals(buffer).
         *
         *    Can you use one as a key? Technically yes, and this is where the
         *    usual answer overshoots — I checked rather than assumed. Because
         *    the hash is identity-based, it does NOT change when you mutate the
         *    buffer, so the entry stays reachable via the same reference
         *    (printed above). It is a legal key that is merely USELESS: no other
         *    object can ever match it, so the map degenerates into an
         *    identity map.
         *    The catastrophic version of the trap needs a key whose hashCode is
         *    CONTENT-based — an ArrayList, a HashSet, or your own class with a
         *    generated equals/hashCode. Mutate one of those after insertion and
         *    the entry becomes permanently unreachable while still occupying the
         *    map, as the second half of this demo shows. THAT is the real rule:
         *    map keys must be immutable, or at least never mutated in a way that
         *    changes their hash while stored. For a buffer, call toString() and
         *    key the map on the String.
         * ------------------------------------------------------------------ */
    }
}
