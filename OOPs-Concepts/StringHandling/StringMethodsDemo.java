/*
 * ================================================================
 * STRING IN JAVA — COMPLETE METHOD REFERENCE + INTERVIEW DRILL
 * ================================================================
 * Companion to StringHandlingDemo.java. That file teaches the 4 core
 * ideas (immutability, pool, == vs equals, StringBuilder). THIS file
 * walks EVERY method on java.lang.String (Java 17), grouped by purpose,
 * each group followed by the interview question an enterprise-level
 * panel actually asks about it — with the answer.
 *
 * WHAT A STRING *IS* (the 30-second answer interviewers want):
 *
 *   public final class String
 *       implements Serializable, Comparable<String>, CharSequence,
 *                  Constable, ConstantDesc
 *
 *   - It is a CLASS, not a primitive. "abc" is an object.
 *   - It is FINAL     -> cannot be subclassed (nobody can hand you a
 *                        "String" whose equals() lies).
 *   - It is IMMUTABLE -> the backing array is private final and never
 *                        handed out; every "mutator" returns a NEW String.
 *   - Java 8 and below: private final char[] value   (2 bytes per char)
 *     Java 9 and above: private final byte[] value + byte coder
 *                       ("Compact Strings", JEP 254). Pure-Latin1 text
 *                       stores 1 byte per char, halving heap for most apps.
 *     This is invisible to your code — charAt() still returns char.
 *
 *   WHY IMMUTABLE? (the follow-up, always)
 *     1. Security  — a file path / SQL / class name passed to a library
 *                    cannot be mutated after a security check passes
 *                    (classic time-of-check-to-time-of-use attack).
 *     2. Pooling   — sharing one object between many references is only
 *                    safe if nobody can change it.
 *     3. hashCode caching — the hash is computed once and cached in a
 *                    field, which is why String is an ideal HashMap key.
 *     4. Thread safety — immutable objects need no synchronization.
 *
 * MEMORY MODEL — where strings live:
 *
 *      String a = "java";                 String c = new String("java");
 *            |                                     |
 *            v                                     v
 *   +------------------------+          +------------------------+
 *   |  String Constant Pool  |          |     Normal Heap        |
 *   |  (inside the heap      |          |  brand-new object, its |
 *   |   since Java 7;        |<---------|  value[] points at a   |
 *   |   was PermGen <= 6)    |  intern()|  COPY of the chars     |
 *   |   "java"  <-- one copy |          +------------------------+
 *   +------------------------+
 *            ^
 *            |
 *      String b = "java";   // reuses the SAME pooled object -> a == b is true
 *
 * ================================================================
 */

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class StringMethodsDemo {

    public static void main(String[] args) {
        creationAndPool();
        lengthAndEmptiness();
        characterAccess();
        comparisonMethods();
        searchingMethods();
        extractionMethods();
        replacementAndCase();
        whitespaceMethods();
        staticFactoryMethods();
        joinAndFormat();
        functionalMethods();
        internAndIdentity();
        builderVsBuffer();
        classicGotchas();
    }

    private static void title(String s) {
        System.out.println("\n=========== " + s + " ===========");
    }

    /*
     * NOTE ON RUNNING THIS: sections 7 and 8 print non-ASCII characters (the
     * Turkish dotless i, a Unicode EN QUAD space). A Windows console defaults to
     * a legacy code page and shows them as '?'. The String values are correct —
     * only the terminal is lossy. To see them properly:
     *     java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 StringMethodsDemo
     * (or compare .length() values, which are encoding-independent, as done below).
     */

    // =================================================================
    // 1. CREATION — literal vs new vs char[] vs byte[]
    // =================================================================
    /*
     * CONSTRUCTORS you will actually use:
     *   new String()                     -> "" (empty, pointless, avoid)
     *   new String(String original)      -> forced copy, bypasses the pool
     *   new String(char[] value)         -> builds from a char array (COPIES it)
     *   new String(char[], offset, count)-> partial copy
     *   new String(byte[] bytes)         -> decode with the DEFAULT charset (danger!)
     *   new String(byte[], Charset)      -> decode with an EXPLICIT charset (correct)
     *   new String(byte[], off, len, cs) -> partial decode
     *   new String(int[] codePoints, off, count) -> from Unicode code points
     *   new String(StringBuilder sb)     -> snapshot of a builder (sb.toString() is better)
     */
    static void creationAndPool() {
        title("1. CREATION & THE STRING POOL");

        String literal     = "java";                          // pooled
        String sameLiteral = "java";                          // SAME pooled object
        String viaNew      = new String("java");              // new heap object
        String fromChars   = new String(new char[]{'j','a','v','a'});
        String fromBytes   = new String(new byte[]{106,97,118,97}, StandardCharsets.UTF_8);

        System.out.println("literal == sameLiteral : " + (literal == sameLiteral));
        System.out.println("literal == viaNew      : " + (literal == viaNew));
        System.out.println("literal == fromChars   : " + (literal == fromChars));
        System.out.println("all .equals()          : " + (literal.equals(viaNew)
                && literal.equals(fromChars) && literal.equals(fromBytes)));

        // Compile-time constant folding: the compiler joins these AT COMPILE TIME,
        // so the result is itself a literal and lands in the pool.
        String folded = "ja" + "va";
        System.out.println("\"ja\"+\"va\" == \"java\"  : " + (folded == literal));

        // Runtime concatenation of NON-final variables cannot be folded -> new object.
        String part1 = "ja", part2 = "va";
        System.out.println("part1+part2 == \"java\" : " + ((part1 + part2) == literal));

        // ...but final locals initialised with constants CAN be folded.
        final String f1 = "ja", f2 = "va";
        System.out.println("final f1+f2 == \"java\" : " + ((f1 + f2) == literal));

        /* ------------------------------------------------------------------
         * INTERVIEW Q (enterprise level):
         *   "How many objects are created by:
         *        String s1 = "hello";
         *        String s2 = new String("hello");   ?"
         *
         * A: TWO in total — not two per line.
         *    Line 1 creates ONE object in the string constant pool (or reuses
         *    it if "hello" was already interned by any earlier-loaded class).
         *    Line 2 creates ONE MORE object on the normal heap; its "hello"
         *    argument is the pooled object from line 1, so no third object
         *    appears. Hence s1 != s2 but s1.equals(s2).
         *
         *    Follow-up they love: "Then why does new String() exist?"
         *    Almost never needed. Two defensible uses: (a) you want a
         *    deliberately distinct identity for a lock/sentinel object,
         *    (b) pre-Java-7 it detached a small substring from a huge parent
         *    char[] — obsolete since Java 7 makes substring() copy.
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 2. SIZE — length(), isEmpty(), isBlank()
    // =================================================================
    static void lengthAndEmptiness() {
        title("2. length() / isEmpty() / isBlank()");

        String text   = "Hello";
        String empty  = "";
        String spaces = "   \t\n ";

        System.out.println("\"Hello\".length()     : " + text.length());
        System.out.println("\"\".isEmpty()         : " + empty.isEmpty());
        System.out.println("\"   \".isEmpty()      : " + spaces.isEmpty());   // false - it HAS chars
        System.out.println("\"   \".isBlank()      : " + spaces.isBlank());   // true  (Java 11+)
        System.out.println("\"\".isBlank()         : " + empty.isBlank());

        // length() counts UTF-16 code UNITS, not visible characters.
        String emoji = "A\uD83D\uDE00";                  // 'A' + grinning-face emoji
        System.out.println("\"A\"+emoji length()   : " + emoji.length());
        System.out.println("codePointCount()      : " + emoji.codePointCount(0, emoji.length()));

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "isEmpty() vs isBlank() — and why does length() return 3 for a
         *    string you can only see 2 characters in?"
         *
         * A: isEmpty() is length() == 0. isBlank() (Java 11) is true when the
         *    string is empty OR holds only Character.isWhitespace() chars —
         *    the correct check for "user submitted a form field of spaces".
         *
         *    length() returns UTF-16 CODE UNITS. Characters outside the Basic
         *    Multilingual Plane (emoji, rare CJK, math symbols) are stored as a
         *    SURROGATE PAIR = 2 chars. Real bug this causes: truncating a
         *    display name with substring(0,20) can cut a surrogate pair in
         *    half, producing an invalid string that renders as a black diamond
         *    or breaks JSON encoding downstream.
         *    Correct: codePointCount()/offsetByCodePoints(), or BreakIterator
         *    for user-perceived characters (grapheme clusters — a flag emoji,
         *    or "e" + combining accent).
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 3. CHARACTER ACCESS — charAt, codePointAt, toCharArray, getChars, getBytes
    // =================================================================
    static void characterAccess() {
        title("3. CHARACTER & CODE POINT ACCESS");

        String s = "Interview";

        System.out.println("charAt(0)              : " + s.charAt(0));
        System.out.println("charAt(length()-1)     : " + s.charAt(s.length() - 1));
        System.out.println("codePointAt(0)         : " + s.codePointAt(0));
        System.out.println("codePointBefore(1)     : " + s.codePointBefore(1));
        System.out.println("codePointCount(0,4)    : " + s.codePointCount(0, 4));
        System.out.println("offsetByCodePoints(0,3): " + s.offsetByCodePoints(0, 3));
        System.out.println("toCharArray()          : " + Arrays.toString("abc".toCharArray()));
        System.out.println("vowels via charAt loop : " + countVowels(s));

        // getChars(srcBegin, srcEnd, dest[], destBegin) — bulk copy into your own buffer
        char[] dest = new char[5];
        s.getChars(0, 5, dest, 0);
        System.out.println("getChars(0,5,dest,0)   : " + new String(dest));

        // getBytes — ALWAYS pass a charset
        byte[] utf8  = "h\u00e9llo".getBytes(StandardCharsets.UTF_8);
        byte[] ascii = "h\u00e9llo".getBytes(StandardCharsets.US_ASCII);
        System.out.println("getBytes(UTF_8).length : " + utf8.length);   // 6 - the accent costs 2 bytes
        System.out.println("getBytes(ASCII) back   : " + new String(ascii, StandardCharsets.US_ASCII));

        // chars() / codePoints() — IntStream views (Java 8+)
        System.out.println("\"abc\".chars().sum()    : " + "abc".chars().sum());
        System.out.println("codePoints().count()   : " + "a\uD83D\uDE00".codePoints().count());

        // describeConstable() (Java 12) — String as a loadable constant (JVM condy API)
        Optional<String> constable = "abc".describeConstable();
        System.out.println("describeConstable()    : " + constable);

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "What is wrong with `"data".getBytes()` in a service that runs on a
         *    developer's Windows laptop and in a Linux container?"
         *
         * A: The no-arg getBytes() — and new String(byte[]) — uses the PLATFORM
         *    DEFAULT charset. Historically that was windows-1252 on Windows and
         *    UTF-8 on Linux, so identical code produced different bytes on each:
         *    the classic "mojibake only in production" bug, and a real cause of
         *    corrupted rows in message queues and CSV exports.
         *    Always pass a Charset: getBytes(StandardCharsets.UTF_8).
         *    Senior-level detail: JEP 400 (Java 18) finally made UTF-8 the
         *    default everywhere, but explicit charsets remain the rule because
         *    most enterprises still run Java 8/11/17.
         *
         *    Bounds detail they check: charAt() with index < 0 or >= length()
         *    throws StringIndexOutOfBoundsException — a subclass of
         *    IndexOutOfBoundsException, NOT ArrayIndexOutOfBoundsException.
         * ------------------------------------------------------------------ */
    }

    private static int countVowels(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if ("aeiouAEIOU".indexOf(s.charAt(i)) >= 0) count++;
        }
        return count;
    }

    // =================================================================
    // 4. COMPARISON — equals, equalsIgnoreCase, compareTo, contentEquals, hashCode
    // =================================================================
    static void comparisonMethods() {
        title("4. COMPARISON METHODS");

        String a = "apple", b = "Apple", c = new String("apple");

        System.out.println("a.equals(c)                 : " + a.equals(c));            // true  - content
        System.out.println("a == c                      : " + (a == c));               // false - identity
        System.out.println("a.equals(b)                 : " + a.equals(b));            // false - case
        System.out.println("a.equalsIgnoreCase(b)       : " + a.equalsIgnoreCase(b));  // true

        // compareTo: lexicographic. Returns the FIRST differing char's difference,
        // or the length difference when one string is a prefix of the other.
        System.out.println("\"apple\".compareTo(\"banana\") : " + a.compareTo("banana"));  // 'a'-'b' = -1
        System.out.println("\"Apple\".compareTo(\"apple\")  : " + b.compareTo(a));         // 'A'-'a' = -32
        System.out.println("\"abc\".compareTo(\"ab\")       : " + "abc".compareTo("ab"));  // 3-2   = 1
        System.out.println("compareToIgnoreCase         : " + b.compareToIgnoreCase(a));  // 0

        // contentEquals — compare a String against ANY CharSequence without toString()
        StringBuilder sb = new StringBuilder("apple");
        System.out.println("a.contentEquals(builder)    : " + a.contentEquals(sb));  // true
        System.out.println("a.equals(builder)           : " + a.equals(sb));         // false! different type

        // matches — full-string regex match (not a search; the WHOLE string must match)
        System.out.println("\"a1b2\".matches(\"[a-z0-9]+\") : " + "a1b2".matches("[a-z0-9]+"));
        System.out.println("\"a1b2\".matches(\"[a-z]+\")    : " + "a1b2".matches("[a-z]+"));

        // hashCode: s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
        System.out.println("\"Aa\".hashCode()             : " + "Aa".hashCode());
        System.out.println("\"BB\".hashCode()             : " + "BB".hashCode());
        System.out.println("collision (equal hashes)?   : " + ("Aa".hashCode() == "BB".hashCode()));

        // CASE_INSENSITIVE_ORDER — the ready-made Comparator constant.
        // Natural order is by char code, so ALL capitals sort before ANY lowercase
        // ('A'=65 .. 'Z'=90 come before 'a'=97), which is almost never what a user wants.
        List<String> names = Arrays.asList("banana", "Apple", "cherry", "Date");
        List<String> sorted = names.stream().sorted().collect(Collectors.toList());
        List<String> ci = names.stream().sorted(String.CASE_INSENSITIVE_ORDER)
                                        .collect(Collectors.toList());
        System.out.println("natural sort (ASCII order)  : " + sorted);
        System.out.println("CASE_INSENSITIVE_ORDER      : " + ci);

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "\"Aa\" and \"BB\" have the SAME hashCode. Does that break HashMap?
         *    And what does String.hashCode() actually compute?"
         *
         * A: It does not break HashMap. hashCode() only decides the BUCKET;
         *    within a bucket HashMap compares keys with equals(). "Aa" and "BB"
         *    collide into one bucket, then equals() distinguishes them, so both
         *    entries coexist correctly — just with a slightly slower lookup.
         *    The contract is one-directional: equal objects MUST have equal
         *    hash codes; equal hash codes do NOT imply equal objects.
         *
         *    Formula: s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1], computed
         *    lazily and cached in the private `hash` field (0 means "not yet
         *    computed", which is why the empty string recomputes every time —
         *    harmlessly, since it is 0).
         *    Why 31? It is an odd prime, gives a good spread for ASCII text,
         *    and 31*i compiles to (i << 5) - i, a shift and a subtract.
         *
         *    Bonus trap they add: "so is compareTo() == 0 the same as equals()?"
         *    For String, yes — String's ordering is consistent with equals.
         *    That is NOT true of every Comparable (BigDecimal is the famous
         *    counterexample: 1.0 and 1.00 compareTo 0 but are not equals).
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 5. SEARCHING — indexOf, lastIndexOf, contains, startsWith, endsWith, regionMatches
    // =================================================================
    static void searchingMethods() {
        title("5. SEARCHING METHODS");

        String s = "hello world";

        System.out.println("indexOf('o')              : " + s.indexOf('o'));        // 4
        System.out.println("indexOf('o', 5)           : " + s.indexOf('o', 5));     // 7
        System.out.println("indexOf(\"world\")          : " + s.indexOf("world"));  // 6
        System.out.println("indexOf(\"world\", 7)       : " + s.indexOf("world", 7));// -1
        System.out.println("indexOf(\"xyz\")            : " + s.indexOf("xyz"));    // -1 - NOT an exception
        System.out.println("lastIndexOf('o')          : " + s.lastIndexOf('o'));    // 7
        System.out.println("lastIndexOf('o', 6)       : " + s.lastIndexOf('o', 6)); // 4 - searches BACKWARD
        System.out.println("lastIndexOf(\"l\")          : " + s.lastIndexOf("l"));  // 9
        System.out.println("contains(\"lo w\")          : " + s.contains("lo w"));  // true
        System.out.println("startsWith(\"hell\")        : " + s.startsWith("hell"));
        System.out.println("startsWith(\"world\", 6)    : " + s.startsWith("world", 6)); // offset form
        System.out.println("endsWith(\"rld\")           : " + s.endsWith("rld"));

        // regionMatches — compare a slice of THIS string with a slice of another,
        // without allocating substrings. The boolean form ignores case.
        System.out.println("regionMatches(6,\"the world\",4,5) : "
                + s.regionMatches(6, "the world", 4, 5));
        System.out.println("regionMatches(true,0,\"HELLO\",0,5): "
                + s.regionMatches(true, 0, "HELLO", 0, 5));

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "contains() vs indexOf() vs matches() — and which do you use to
         *    check whether a log line contains an IP address?"
         *
         * A: contains(CharSequence) is a convenience wrapper: it literally
         *    returns indexOf(seq) > -1. Use indexOf() when you need the
         *    POSITION or want to continue scanning from an offset; contains()
         *    when you only need a yes/no. Neither takes a regex — both are
         *    plain literal searches, which is exactly why they are fast.
         *    matches() IS regex but anchors to the WHOLE string, so
         *    "log: 10.0.0.1".matches("\\d+\\.\\d+\\.\\d+\\.\\d+") is FALSE.
         *    For "contains a pattern" you need Pattern/Matcher.find(), and in
         *    a hot path you must precompile the Pattern as a static final
         *    field — String.matches()/replaceAll()/split() recompile the regex
         *    on every single call, a very common enterprise performance defect.
         *
         *    Also worth saying: regionMatches() is the allocation-free way to
         *    do a case-insensitive prefix check on a big string, instead of
         *    substring().equalsIgnoreCase(), which allocates.
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 6. EXTRACTION — substring, subSequence, split, lines, toCharArray
    // =================================================================
    static void extractionMethods() {
        title("6. EXTRACTION METHODS");

        String s = "hello world";

        System.out.println("substring(6)          : " + s.substring(6));       // world
        System.out.println("substring(0, 5)       : " + s.substring(0, 5));    // hello (end EXCLUSIVE)
        System.out.println("substring(11)         : [" + s.substring(11) + "]"); // "" - legal!
        System.out.println("subSequence(0, 5)     : " + s.subSequence(0, 5));  // CharSequence view

        try {
            s.substring(12);                                   // beyond length -> boom
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("substring(12) throws  : " + e.getClass().getSimpleName());
        }

        // split(regex) — the trailing-empty-string trap
        String csv = "a,b,,,";
        System.out.println("\"a,b,,,\".split(\",\")      -> " + Arrays.toString(csv.split(","))
                + " length=" + csv.split(",").length);
        System.out.println("\"a,b,,,\".split(\",\", -1)  -> " + Arrays.toString(csv.split(",", -1))
                + " length=" + csv.split(",", -1).length);
        System.out.println("split(\",\", 2) (limit)    -> " + Arrays.toString(csv.split(",", 2)));

        // split takes a REGEX, so metacharacters must be escaped
        System.out.println("\"1.2.3\".split(\".\")       -> length="
                + "1.2.3".split("\\.").length + " (escaped) vs "
                + "1.2.3".split(".").length + " (unescaped, all empty)");

        // lines() (Java 11) — splits on \n, \r\n or \r, and returns a Stream
        String multi = "line1\nline2\r\nline3";
        System.out.println("lines().count()       : " + multi.lines().count());
        multi.lines().forEach(l -> System.out.println("   line -> [" + l + "]"));

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "\"a,b,,,\".split(\",\").length returns 2, not 5. Why? And is
         *    substring() O(1) or O(n)?"
         *
         * A: split(regex) with the default limit of 0 applies the pattern as
         *    many times as possible AND DISCARDS TRAILING EMPTY STRINGS —
         *    so ["a","b","","",""] collapses to ["a","b"]. Leading and middle
         *    empties are kept; only trailing ones vanish. Pass a NEGATIVE
         *    limit (-1) to keep them all. This is a genuine production bug
         *    source when parsing fixed-column CSV: an empty last column
         *    silently shortens the array and you get an
         *    ArrayIndexOutOfBoundsException three columns later.
         *    (A positive limit N applies the pattern at most N-1 times and
         *    leaves the remainder intact in the last element.)
         *
         *    substring() is O(n) since Java 7 — it COPIES the range into a new
         *    array. In Java 6 and earlier it was O(1): the new String shared
         *    the parent's char[] with an offset+count, which meant holding a
         *    2-character substring of a 10 MB file kept all 10 MB alive — the
         *    famous substring memory leak. Java 7 traded O(1) slicing for
         *    predictable memory. Consequence for the trivia round: since
         *    Java 7, new String(bigString.substring(...)) is pointless.
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 7. TRANSFORMATION — concat, replace, replaceAll, replaceFirst, case
    // =================================================================
    static void replacementAndCase() {
        title("7. REPLACEMENT & CASE CONVERSION");

        System.out.println("\"foo\".concat(\"bar\")            : " + "foo".concat("bar"));
        System.out.println("replace('l','L') on hello      : " + "hello".replace('l', 'L'));
        System.out.println("replace(\"ll\",\"LL\") on hello    : " + "hello".replace("ll", "LL"));

        // THE classic distinction: replace() is LITERAL, replaceAll() is REGEX
        System.out.println("\"1.2.3\".replace(\".\",\"-\")       : " + "1.2.3".replace(".", "-"));
        System.out.println("\"1.2.3\".replaceAll(\".\",\"-\")    : " + "1.2.3".replaceAll(".", "-"));
        System.out.println("\"1.2.3\".replaceAll(\"\\\\.\",\"-\")   : " + "1.2.3".replaceAll("\\.", "-"));
        System.out.println("replaceFirst(\"\\\\.\",\"-\")         : " + "1.2.3".replaceFirst("\\.", "-"));

        // Case conversion — the Locale overloads exist for a reason
        System.out.println("toUpperCase()                  : " + "istanbul".toUpperCase(Locale.ROOT));
        System.out.println("toLowerCase()                  : " + "ISTANBUL".toLowerCase(Locale.ROOT));

        Locale turkish = Locale.forLanguageTag("tr");
        System.out.println("\"TITLE\".toLowerCase(ROOT)      : " + "TITLE".toLowerCase(Locale.ROOT));
        System.out.println("\"TITLE\".toLowerCase(tr)        : " + "TITLE".toLowerCase(turkish));
        System.out.println("\"i\".toUpperCase(tr)            : " + "i".toUpperCase(turkish));

        // Case conversion can even change LENGTH (not a 1:1 char mapping)
        System.out.println("\"\\u00df\".toUpperCase() length     : "
                + "\u00df".toUpperCase(Locale.ROOT).length() + " -> "
                + "\u00df".toUpperCase(Locale.ROOT));

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "replace() vs replaceAll() — and why did our German/Turkish
         *    customers fail login after we 'normalised' usernames with
         *    toLowerCase()?"
         *
         * A: replace(CharSequence, CharSequence) replaces EVERY occurrence of a
         *    LITERAL. replaceAll(String, String) treats the first argument as a
         *    REGEX and the second as a replacement string in which $ and \ are
         *    special. So "1.2.3".replace(".","-") gives "1-2-3", while
         *    replaceAll(".","-") gives "-----" because "." matches any char.
         *    "All" in replaceAll refers to all MATCHES (replaceFirst does one),
         *    not to "more thorough than replace" — both replace everything.
         *    Gotchas: replaceAll("$","X") and replacements containing "\" need
         *    Matcher.quoteReplacement(); and replaceAll recompiles the regex
         *    each call.
         *
         *    The login bug is the TURKISH-I problem. The no-arg
         *    toLowerCase()/toUpperCase() use the JVM's DEFAULT LOCALE. In
         *    Turkish (tr), 'I' lowercases to the dotless 'i' (U+0131), so
         *    "TITLE".toLowerCase() becomes "tItle" with a dotless i and no
         *    longer equals "title" — so lookups and cache keys miss. Same class
         *    of bug: German sharp-s uppercases to TWO characters ("SS"), so case
         *    conversion is not even length-preserving.
         *    Rule: for machine-facing comparisons ALWAYS pass Locale.ROOT
         *    (or use equalsIgnoreCase / CASE_INSENSITIVE_ORDER); use the
         *    user's locale ONLY for text you are displaying to them.
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 8. WHITESPACE & TEXT BLOCKS — trim, strip*, repeat, indent, translateEscapes
    // =================================================================
    static void whitespaceMethods() {
        title("8. WHITESPACE & TEXT-BLOCK METHODS");

        String padded = "  hello  ";
        System.out.println("trim()          : [" + padded.trim() + "]");
        System.out.println("strip()         : [" + padded.strip() + "]");
        System.out.println("stripLeading()  : [" + padded.stripLeading() + "]");
        System.out.println("stripTrailing() : [" + padded.stripTrailing() + "]");

        // The difference that matters: a Unicode space that trim() does NOT remove
        String unicodeSpace = "\u2000hello\u2000";        // EN QUAD, a real space char
        System.out.println("unicode trim()  : [" + unicodeSpace.trim() + "] len="
                + unicodeSpace.trim().length());
        System.out.println("unicode strip() : [" + unicodeSpace.strip() + "] len="
                + unicodeSpace.strip().length());

        // repeat(n) (Java 11) — replaces the old for-loop / Collections.nCopies trick
        System.out.println("\"ab\".repeat(3)  : " + "ab".repeat(3));
        System.out.println("\"ab\".repeat(0)  : [" + "ab".repeat(0) + "]");

        // indent(n) (Java 12) — adds n spaces to every line AND normalises line endings
        System.out.print("indent(4) of two lines:\n" + "one\ntwo".indent(4));

        // Text block (Java 15) + stripIndent() + translateEscapes()
        String block = """
                {
                  "id": 1
                }""";
        System.out.println("text block:\n" + block);
        System.out.println("stripIndent() of \"  a  \\n  b  \" :\n[" + "  a  \n  b  ".stripIndent() + "]");
        System.out.println("translateEscapes() of \"a\\\\tb\"     : [" + "a\\tb".translateEscapes() + "]");

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "trim() vs strip() — we already had trim() since Java 1.0, so why
         *    did Java 11 add strip()?"
         *
         * A: They use different definitions of whitespace.
         *    trim() removes any char with a code point <= U+0020 (space). That
         *    predates Unicode: it strips control characters like NUL and BEL,
         *    yet MISSES genuine Unicode spaces above U+0020 — NBSP (U+00A0),
         *    EN QUAD (U+2000), ideographic space (U+3000). Text pasted from
         *    Word, Excel or a web form routinely contains NBSP, so trim() left
         *    it in place and "  12.50 " failed to parse as a number.
         *    strip() uses Character.isWhitespace(), is code-point aware
         *    (surrogate-safe), and comes with stripLeading()/stripTrailing()
         *    for one-sided trimming.
         *    Rule: use strip() on anything that came from a human; trim() only
         *    survives for backwards compatibility. Note NBSP is a wrinkle even
         *    for strip(): Character.isWhitespace(U+00A0) is false because NBSP
         *    is non-breaking, so truly hostile input needs an explicit regex.
         *
         *    While here, the Java 11-15 additions exist mostly to serve text
         *    blocks: lines(), isBlank(), strip*(), repeat(), indent(),
         *    stripIndent() and translateEscapes() (the last two are what the
         *    compiler conceptually applies to a """ block).
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 9. STATIC FACTORIES — valueOf, copyValueOf
    // =================================================================
    static void staticFactoryMethods() {
        title("9. STATIC FACTORY METHODS");

        System.out.println("valueOf(42)          : " + String.valueOf(42));
        System.out.println("valueOf(3.14)        : " + String.valueOf(3.14));
        System.out.println("valueOf(true)        : " + String.valueOf(true));
        System.out.println("valueOf('x')         : " + String.valueOf('x'));
        System.out.println("valueOf(char[])      : " + String.valueOf(new char[]{'h','i'}));
        System.out.println("valueOf(char[],0,1)  : " + String.valueOf(new char[]{'h','i'}, 0, 1));
        System.out.println("copyValueOf(char[])  : " + String.copyValueOf(new char[]{'h','i'}));

        Object nullObject = null;
        System.out.println("valueOf((Object)null): " + String.valueOf(nullObject));  // "null"

        try {
            System.out.println(String.valueOf(null));      // picks the char[] overload -> NPE
        } catch (NullPointerException e) {
            System.out.println("valueOf(null) throws : NullPointerException (char[] overload chosen!)");
        }

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "String.valueOf(someObject) vs someObject.toString() vs \"\" + obj —
         *    which is safe when the object may be null?"
         *
         * A: obj.toString() throws NullPointerException on a null reference.
         *    String.valueOf(obj) returns the literal text "null" — safe.
         *    "" + obj also yields "null" (the compiler routes it through
         *    String.valueOf), but it allocates via the concatenation machinery
         *    and reads like an accident, so valueOf() or Objects.toString(obj,
         *    "N/A") is preferred.
         *
         *    The trap in the code above: String.valueOf(null) with a bare null
         *    literal does NOT hit the (Object) overload. Overload resolution
         *    picks the MOST SPECIFIC applicable signature, and char[] is more
         *    specific than Object, so valueOf(char[]) is chosen and dereferences
         *    null -> NPE at runtime, compiling without error. Cast explicitly:
         *    String.valueOf((Object) null).
         *
         *    copyValueOf(char[]) is a Java 1.0 leftover that is now identical to
         *    valueOf(char[]) — both copy the array. Mention it only to show you
         *    know the API's history.
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 10. join / format / formatted
    // =================================================================
    static void joinAndFormat() {
        title("10. join() / format() / formatted()");

        System.out.println("join with varargs   : " + String.join("-", "2026", "08", "19"));
        System.out.println("join with Iterable  : " + String.join(", ", List.of("a", "b", "c")));
        System.out.println("join with empty     : [" + String.join(",", List.of()) + "]");

        System.out.println("format %s %d %.2f   : "
                + String.format("%s scored %d (%.2f%%)", "Prince", 87, 87.4567));
        System.out.println("format padding      : ["
                + String.format("%-10s|%5d|", "left", 42) + "]");
        System.out.println("format with Locale  : "
                + String.format(Locale.GERMANY, "%,.2f", 1234567.891));
        System.out.println("format US Locale    : "
                + String.format(Locale.US, "%,.2f", 1234567.891));
        System.out.println("formatted() (Java15): "
                + "Hello %s, you are %d".formatted("Prince", 25));

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "Your report service formats a million currency values with
         *    String.format(\"%,.2f\", amount) and it is the top hotspot in the
         *    profiler. Explain why, and what you would change."
         *
         * A: String.format() parses the format string and builds a new
         *    Formatter, a StringBuilder and locale-specific
         *    DecimalFormatSymbols on EVERY call. It is roughly an order of
         *    magnitude slower than plain concatenation or an appended
         *    StringBuilder — fine for logging and error messages, wrong in a
         *    tight loop.
         *    Fixes, in order: (1) reuse one preconfigured NumberFormat /
         *    DecimalFormat instance per thread (they are NOT thread-safe, so
         *    ThreadLocal or one per worker); (2) append into a single
         *    StringBuilder instead of building throwaway strings;
         *    (3) for logging, use the framework's parameterised form
         *    (log.debug("x={}", x)) so the message is never built when the
         *    level is disabled.
         *
         *    Also flag the correctness half: format() without a Locale uses the
         *    default locale, so the same code emits "1.234.567,89" in Germany
         *    and "1,234,567.89" in the US. For machine-readable output (CSV,
         *    JSON, file names, SQL) always pass Locale.ROOT or Locale.US;
         *    otherwise a downstream parser breaks the moment the server locale
         *    changes.
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 11. FUNCTIONAL — chars, codePoints, lines, transform
    // =================================================================
    static void functionalMethods() {
        title("11. FUNCTIONAL / STREAM METHODS");

        // transform(Function) (Java 12) — apply a function to the string, fluently
        String result = "  hello  ".transform(String::strip)
                                   .transform(String::toUpperCase)
                                   .transform(s -> s + "!");
        System.out.println("transform() chain    : " + result);

        // Practical stream pipelines over characters
        String sentence = "The Quick Brown Fox";
        System.out.println("uppercase count      : "
                + sentence.chars().filter(Character::isUpperCase).count());
        System.out.println("distinct letters     : "
                + sentence.chars().filter(Character::isLetter)
                          .mapToObj(ch -> String.valueOf((char) ch))
                          .map(String::toLowerCase).distinct().sorted()
                          .collect(Collectors.joining()));
        System.out.println("reversed via chars   : "
                + new StringBuilder(sentence).reverse());

        // lines() + stream for parsing config-style text
        String config = "host=localhost\nport=8080\n\ndebug=true";
        String parsed = config.lines()
                              .filter(l -> !l.isBlank())
                              .map(l -> l.split("=", 2))
                              .map(kv -> kv[0].toUpperCase(Locale.ROOT) + " -> " + kv[1])
                              .collect(Collectors.joining(" | "));
        System.out.println("lines() pipeline     : " + parsed);

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "Why does "abc".chars() give you an IntStream instead of a
         *    Stream<Character>, and when would you use lines() over
         *    split(\"\\n\")?"
         *
         * A: chars() returns IntStream to avoid BOXING every character into a
         *    Character object — a primitive specialisation, the same reason
         *    IntStream/LongStream/DoubleStream exist at all. The cost is
         *    ergonomic: you must cast back with (char) ch to print a letter
         *    rather than its numeric code, which is the single most common
         *    surprise ("why did my letters print as 97 98 99?").
         *    codePoints() is the surrogate-aware sibling: use it whenever the
         *    text can contain emoji or non-BMP characters.
         *
         *    lines() (Java 11) beats split("\n") on three counts: it recognises
         *    all three line terminators (\n, \r\n, \r) so it works on files
         *    written on Windows; it is LAZY, so you can process a huge string
         *    without materialising an array of every line; and it has no
         *    trailing-empty-string surprise. split() also pays regex
         *    compilation on every call. Use split() only when the separator is
         *    genuinely not a line break.
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 12. intern() and identity
    // =================================================================
    static void internAndIdentity() {
        title("12. intern() & IDENTITY");

        String pooled = "hello";
        String heap   = new String("hello");
        String interned = heap.intern();

        System.out.println("heap == pooled           : " + (heap == pooled));      // false
        System.out.println("heap.intern() == pooled  : " + (interned == pooled));  // true
        System.out.println("heap == heap.intern()    : " + (heap == interned));    // false

        // Runtime-built strings are not pooled until you intern them
        String built = new StringBuilder("ja").append("va").toString();
        System.out.println("built == \"java\"          : " + (built == "java"));
        System.out.println("built.intern() == \"java\" : " + (built.intern() == "java"));

        // switch on String: compiled to a hashCode switch + equals() check
        System.out.println("switch on String result  : " + describeEnv("PROD"));

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "What does intern() do, and would you use it to deduplicate
         *    millions of repeated strings coming off a Kafka topic?"
         *
         * A: intern() checks the JVM's string table for a string equal to this
         *    one: if present it returns the POOLED instance, otherwise it adds
         *    this instance and returns it. The point is to make == work and to
         *    share one copy of a repeated value.
         *
         *    I would not reach for it first. Problems: (1) the string table is
         *    a fixed-size native hash table (default ~60013 buckets, tunable
         *    with -XX:StringTableSize) — overload it and every intern()
         *    degrades into a long bucket walk; (2) intern() is a native call
         *    that can show up as a scalability bottleneck under contention;
         *    (3) before Java 7 pooled strings lived in PermGen and could
         *    trigger OutOfMemoryError: PermGen space.
         *    Better options today: a plain HashMap<String,String> canonicalising
         *    cache you control and can bound, or simply enabling
         *    -XX:+UseStringDeduplication with G1, which lets GC share identical
         *    char/byte arrays with zero code changes.
         *
         *    Related trivia they chain onto this: switch on String is not magic
         *    — javac compiles it to a switch on hashCode() followed by an
         *    equals() confirmation, which is why it needs both methods to be
         *    well behaved, and why case labels must be compile-time constants.
         * ------------------------------------------------------------------ */
    }

    private static String describeEnv(String env) {
        switch (env) {
            case "DEV":  return "local machine";
            case "UAT":  return "client testing";
            case "PROD": return "live traffic - be careful";
            default:     return "unknown environment";
        }
    }

    // =================================================================
    // 13. String vs StringBuilder vs StringBuffer
    // =================================================================
    /*
     *   +----------------+-----------+------------------+------------------+
     *   |                | String    | StringBuilder    | StringBuffer     |
     *   +----------------+-----------+------------------+------------------+
     *   | mutable        | NO        | YES              | YES              |
     *   | thread-safe    | YES (imm) | NO               | YES (synchronized)|
     *   | speed          | slow (in  | fastest          | slower (lock     |
     *   |                | loops)    |                  | overhead)        |
     *   | since          | 1.0       | 1.5              | 1.0              |
     *   | pooled         | YES       | NO               | NO               |
     *   | use when       | keys,     | building text    | legacy shared    |
     *   |                | constants | in one thread    | buffer           |
     *   +----------------+-----------+------------------+------------------+
     *
     *   Both builders share the same API (AbstractStringBuilder):
     *     append(anything), insert(index, x), delete(start,end), deleteCharAt(i),
     *     replace(start,end,str), reverse(), setCharAt(i,c), setLength(n),
     *     capacity(), ensureCapacity(n), trimToSize(), charAt/indexOf/substring,
     *     chars(), compareTo(), toString()
     *   Default capacity is 16 chars; growth is (old * 2) + 2 with an array copy,
     *   so size the builder up front when you know the answer.
     */
    static void builderVsBuffer() {
        title("13. String vs StringBuilder vs StringBuffer");

        StringBuilder sb = new StringBuilder("Hello");
        System.out.println("initial capacity(16+5) : " + sb.capacity());
        sb.append(" World").append('!').append(2026);
        System.out.println("after appends          : " + sb);
        sb.insert(5, ",");
        System.out.println("insert(5, \",\")         : " + sb);
        sb.replace(0, 5, "Howdy");
        System.out.println("replace(0,5,\"Howdy\")   : " + sb);
        sb.deleteCharAt(sb.length() - 1);
        System.out.println("deleteCharAt(last)     : " + sb);
        sb.delete(0, 6);
        System.out.println("delete(0,6)            : " + sb);
        System.out.println("reverse()              : " + new StringBuilder(sb).reverse());
        sb.setLength(5);
        System.out.println("setLength(5) truncates : " + sb);

        // Measured cost of concatenating in a loop (small n so the demo stays quick)
        int n = 20_000;
        long t0 = System.nanoTime();
        String slow = "";
        for (int i = 0; i < n; i++) slow += "x";          // O(n^2): copies the whole string each time
        long concatMs = (System.nanoTime() - t0) / 1_000_000;

        long t1 = System.nanoTime();
        StringBuilder fast = new StringBuilder(n);
        for (int i = 0; i < n; i++) fast.append("x");     // O(n): one growing buffer
        long builderMs = (System.nanoTime() - t1) / 1_000_000;

        System.out.println("loop of " + n + " with += : " + concatMs + " ms (length "
                + slow.length() + ")");
        System.out.println("loop of " + n + " builder : " + builderMs + " ms (length "
                + fast.length() + ")");

        /* ------------------------------------------------------------------
         * INTERVIEW Q:
         *   "You see `result += row;` inside a loop over 100k database rows in
         *    a code review. What exactly is wrong, what do you change, and is
         *    plain `a + b + c` on one line also a problem?"
         *
         * A: Because String is immutable, `result += row` cannot append in
         *    place: each iteration allocates a fresh String and COPIES every
         *    character accumulated so far. That is O(n^2) time and O(n^2)
         *    garbage — the single most common cause of a method that works in
         *    a unit test and times out in production. Fix: one StringBuilder
         *    outside the loop, pre-sized when you can estimate the total, and
         *    append into it; or for a simple delimiter, String.join() /
         *    Collectors.joining(). Use StringBuffer only if the buffer is
         *    genuinely shared across threads — which almost always signals a
         *    design problem instead.
         *
         *    A single-line `a + b + c` is fine: the compiler fuses the whole
         *    expression into ONE concatenation. In Java 8 javac emitted a
         *    StringBuilder chain; since Java 9 it emits an invokedynamic call
         *    to StringConcatFactory (JEP 280), which the JVM links to an
         *    optimal, exactly-sized routine at runtime. What the compiler
         *    CANNOT do is hoist that optimisation across loop iterations —
         *    the builder is created and discarded inside the loop body — which
         *    is precisely why the loop case stays quadratic.
         * ------------------------------------------------------------------ */
    }

    // =================================================================
    // 14. THE GOTCHAS PANEL — the traps that actually get asked
    // =================================================================
    static void classicGotchas() {
        title("14. CLASSIC GOTCHAS");

        // (a) null concatenation does NOT throw
        String nothing = null;
        System.out.println("(a) \"value=\" + null    : " + ("value=" + nothing));

        // (b) NPE-safe comparison: put the literal FIRST, or use Objects.equals
        System.out.println("(b) \"x\".equals(null)   : " + "x".equals(nothing));
        try {
            nothing.equals("x");
        } catch (NullPointerException e) {
            System.out.println("    null.equals(\"x\")   : NullPointerException");
        }
        System.out.println("    Objects.equals()   : " + java.util.Objects.equals(nothing, "x"));

        // (c) methods return new strings - the original is untouched
        String original = "hello";
        mutateAttempt(original);
        System.out.println("(c) after mutateAttempt: " + original);

        // (d) char + char is int arithmetic, not concatenation
        System.out.println("(d) 'a' + 'b'          : " + ('a' + 'b'));
        System.out.println("    \"\" + 'a' + 'b'     : " + "" + 'a' + 'b');

        // (e) + is left-associative: order changes the result
        System.out.println("(e) 1 + 2 + \"3\"        : " + (1 + 2 + "3"));
        System.out.println("    \"1\" + 2 + 3        : " + ("1" + 2 + 3));

        // (f) equal content, different hash bucket neighbours - still safe as map keys
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("Aa", "first");
        map.put("BB", "second");         // same hashCode, different key -> both survive
        System.out.println("(f) map with hash clash: " + map);

        /* ------------------------------------------------------------------
         * INTERVIEW Q (the one that separates 2-year from 5-year candidates):
         *   "String is immutable and final. Now write a method that changes
         *    the value of a String someone else holds — or explain why you
         *    cannot. And then: is immutability enough to call String
         *    thread-safe?"
         *
         * A: You cannot do it through the API. Java is pass-by-value of
         *    references: mutateAttempt() receives a COPY of the reference, so
         *    reassigning the parameter rebinds only the local copy — the
         *    caller's variable still points at "hello". There is no setter and
         *    the value[] field is private final and never leaked (getBytes()
         *    and toCharArray() hand back copies precisely for this reason).
         *    The only way in is reflection with setAccessible(true) on the
         *    private value field — which is blocked on java.lang by the module
         *    system since Java 9 (strong encapsulation, --illegal-access=deny
         *    by default from Java 16), and if forced would corrupt the string
         *    table and the cached hash. So: "not through any legitimate route,
         *    and the JVM now actively prevents the illegitimate one."
         *
         *    On thread safety: yes, an immutable object is inherently
         *    thread-safe — no thread can observe a partially updated String,
         *    and even the lazily cached hash field is benign (every thread
         *    computes the same value, so a race just recomputes it). What is
         *    NOT thread-safe is a shared MUTABLE REFERENCE to a String: two
         *    threads reassigning the same non-volatile field is a visibility
         *    problem in your code, not in String. That distinction —
         *    immutable OBJECT vs mutable REFERENCE — is the answer they are
         *    listening for.
         * ------------------------------------------------------------------ */
    }

    /** Receives a COPY of the reference: reassigning it cannot affect the caller. */
    private static void mutateAttempt(String s) {
        s = s.toUpperCase(Locale.ROOT);
        s = s.concat(" MUTATED");
        // the caller's variable is completely unaffected
    }
}
