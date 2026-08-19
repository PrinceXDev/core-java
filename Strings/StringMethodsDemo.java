/*
 * Every method of java.lang.String (through Java 17), grouped by job.
 * -------------------------------------------------------------------------
 * The one rule behind all of them: String is IMMUTABLE. No method here ever
 * changes the receiver — each one returns a new String (or a primitive).
 * If you ignore the return value, nothing happened.
 *
 * Since Java 9 a String stores a byte[] + a coder flag (LATIN1 or UTF16),
 * not a char[] — "compact strings". Indexes are still char (UTF-16 code
 * unit) based, which is why emoji need the codePoint* methods.
 *
 * Real-time example running through the file: cleaning up a CSV row that
 * arrived from a partner feed with messy spacing and casing.
 */

/* Visit 10th block - most asked area */

import java.util.Arrays;
import java.util.StringJoiner;

public class StringMethodsDemo {

    public static void main(String[] args) {
        lengthAndEmptiness();
        characterAccess();
        searching();
        extracting();
        joiningAndBuilding();
        replacing();
        caseConversion();
        trimmingAndStripping();
        splitting();
        comparing();
        conversionAndFactories();
        formatting();
        java11Plus();
        identityAndPool();
        realWorldCsvCleanup();
    }

    // =================================================================
    // 1. Length / emptiness
    // =================================================================
    static void lengthAndEmptiness() {
        System.out.println("--- 1. length & emptiness ---");
        String s = "Hello Java";

        // length() -> number of UTF-16 code units (a METHOD, unlike array.length)
        System.out.println("length()   = " + s.length());

        // isEmpty() (Java 6) -> length == 0 only
        System.out.println("\"\".isEmpty()      = " + "".isEmpty());
        System.out.println("\"  \".isEmpty()    = " + "  ".isEmpty());

        // isBlank() (Java 11) -> empty OR only whitespace
        System.out.println("\"  \".isBlank()    = " + "  ".isBlank());

        // Trap: length() counts code units, not characters the user sees.
        // Written with escapes so the file's encoding can't change the result.
        String emoji = "A😀"; // 'A' + grinning face (a surrogate PAIR)
        System.out.println("emoji length()          = " + emoji.length()); // 3
        System.out.println("emoji codePointCount()  = " + emoji.codePointCount(0, emoji.length())); // 2
    }

    // =================================================================
    // 2. Character / code point access
    // =================================================================
    static void characterAccess() {
        System.out.println("\n--- 2. character access ---");
        String s = "Java";

        System.out.println("charAt(0)      = " + s.charAt(0));
        System.out.println("codePointAt(0) = " + s.codePointAt(0)); // 74
        System.out.println("codePointBefore(1) = " + s.codePointBefore(1));
        System.out.println("codePointCount(0,4) = " + s.codePointCount(0, 4));
        System.out.println("offsetByCodePoints(0,2) = " + s.offsetByCodePoints(0, 2));
        System.out.println("chars().count() = " + s.chars().count()); // IntStream, Java 8
        System.out.println("codePoints().max = " + s.codePoints().max().getAsInt());

        // getChars(srcBegin, srcEnd, dest, destBegin) — copy into your own char[]
        char[] dest = new char[4];
        s.getChars(0, 4, dest, 0);
        System.out.println("getChars()     = " + Arrays.toString(dest));

        try {
            s.charAt(4);
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("charAt(4) -> StringIndexOutOfBoundsException");
        }
    }

    // =================================================================
    // 3. Searching
    // =================================================================
    static void searching() {
        System.out.println("\n--- 3. searching ---");
        String s = "order-1024-order-2048";

        System.out.println("indexOf(\"order\")        = " + s.indexOf("order")); // 0
        System.out.println("indexOf(\"order\", 1)     = " + s.indexOf("order", 1)); // 10
        System.out.println("indexOf('-')            = " + s.indexOf('-'));
        System.out.println("lastIndexOf(\"order\")    = " + s.lastIndexOf("order"));
        System.out.println("lastIndexOf('-', 10)    = " + s.lastIndexOf('-', 10));
        System.out.println("indexOf(\"missing\")      = " + s.indexOf("missing") + " (not found = -1)");

        System.out.println("contains(\"1024\")        = " + s.contains("1024"));
        System.out.println("startsWith(\"order\")     = " + s.startsWith("order"));

        String port = "port: 1024 open";
        // 0123456
        // ^ index 6 is '1'

        port.startsWith("1024", 6); // true — the chars at 6,7,8,9 are '1','0','2','4'
        port.startsWith("1024", 5); // false — index 5 is a space, so the window is " 102"
        port.startsWith("1024"); // false — index 0 is 'p'

        System.out.println("startsWith(\"1024\", 6)   = " + s.startsWith("1024", 6)); // offset overload
        System.out.println("endsWith(\"2048\")        = " + s.endsWith("2048"));

        // matches(regex) — whole string must match, not a substring
        System.out.println("matches(\"order-\\\\d+.*\") = " + s.matches("order-\\d+.*"));
        System.out.println("matches(\"\\\\d+\")         = " + s.matches("\\d+"));

        // regionMatches — compare a slice without allocating substrings
        System.out.println("regionMatches(11,\"ORDER\",0,5)          = "
                + s.regionMatches(11, "ORDER", 0, 5));
        System.out.println("regionMatches(true,11,\"ORDER\",0,5)     = "
                + s.regionMatches(true, 11, "ORDER", 0, 5)); // ignoreCase flag
    }

    // =================================================================
    // 4. Extracting
    // =================================================================
    static void extracting() {
        System.out.println("\n--- 4. extracting ---");
        String s = "INV-2024-00871";

        System.out.println("substring(4)       = " + s.substring(4)); // to the end
        System.out.println("substring(4, 8)    = " + s.substring(4, 8)); // [4,8) — end exclusive
        System.out.println("substring(4, 4)    = [" + s.substring(4, 4) + "] (empty, legal)");
        System.out.println("subSequence(0, 3)  = " + s.subSequence(0, 3)); // CharSequence view

        // Trap: substring allocates a fresh copy since Java 7 (no more shared
        // char[] / no more memory leak from holding a tiny slice of a huge string).
        try {
            s.substring(20);
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("substring(20) -> StringIndexOutOfBoundsException");
        }
    }

    // =================================================================
    // 5. Joining / building
    // =================================================================
    static void joiningAndBuilding() {
        System.out.println("\n--- 5. joining & building ---");

        System.out.println("concat()        = " + "Hello".concat(" World"));
        System.out.println("+ operator      = " + ("Hello" + " " + "World")); // compiler uses StringConcatFactory
        System.out.println("join(delim,...) = " + String.join(", ", "a", "b", "c"));
        System.out.println("join(iterable)  = " + String.join("/", Arrays.asList("usr", "local", "bin")));
        System.out.println("repeat(3)       = " + "ab".repeat(3)); // Java 11
        System.out.println("repeat(0)       = [" + "ab".repeat(0) + "]");

        // Why StringBuilder matters: 1000 concats in a loop = 1000 throwaway Strings.
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++)
            sb.append("row").append(i).append(';');
        System.out.println("StringBuilder   = " + sb);

        StringJoiner sj = new StringJoiner(", ", "[", "]");
        sj.add("x").add("y");
        System.out.println("StringJoiner    = " + sj);
    }

    // =================================================================
    // 6. Replacing
    // =================================================================
    static void replacing() {
        System.out.println("\n--- 6. replacing ---");
        String s = "a.b.c.d";

        // replace() takes LITERAL text — no regex meaning at all
        System.out.println("replace('.','-')            = " + s.replace('.', '-'));
        System.out.println("replace(\".\", \"-\")           = " + s.replace(".", "-"));

        // replaceAll()/replaceFirst() take a REGEX — '.' means "any char"
        System.out.println("replaceAll(\".\", \"-\")         = " + s.replaceAll(".", "-"));
        System.out.println("replaceAll(\"\\\\.\", \"-\")       = " + s.replaceAll("\\.", "-"));
        System.out.println("replaceFirst(\"\\\\.\", \"-\")     = " + s.replaceFirst("\\.", "-"));
        System.out.println("replaceAll(\"\\\\s+\", \" \")      = "
                + "too   many    spaces".replaceAll("\\s+", " "));
    }

    // =================================================================
    // 7. Case conversion
    // =================================================================
    static void caseConversion() {
        System.out.println("\n--- 7. case conversion ---");
        System.out.println("toUpperCase() = " + "istanbul".toUpperCase(java.util.Locale.ROOT));
        System.out.println("toLowerCase() = " + "JAVA".toLowerCase(java.util.Locale.ROOT));

        // Trap: the no-arg overloads use the DEFAULT locale. In Turkish locale
        // "i".toUpperCase() is a dotted capital I, which breaks equals() checks
        // on protocol keywords. Always pass Locale.ROOT for machine-facing text.
        System.out.println("Turkish locale: " + "i".toUpperCase(new java.util.Locale("tr")));
    }

    // =================================================================
    // 8. Trimming / stripping
    // =================================================================
    static void trimmingAndStripping() {
        System.out.println("\n--- 8. trimming ---");
        String padded = "   hello   "; // EN QUAD + plain spaces on both ends

        // trim() (Java 1.0) only removes chars <= U+0020
        System.out.println("trim()          = [" + padded.trim() + "]");
        // strip() (Java 11) is Unicode-aware — Character.isWhitespace
        System.out.println("strip()         = [" + padded.strip() + "]");
        System.out.println("stripLeading()  = [" + padded.stripLeading() + "]");
        System.out.println("stripTrailing() = [" + padded.stripTrailing() + "]");

        // The real difference: U+2000 (EN QUAD) is Unicode whitespace but is
        // NOT <= U+0020, so trim() leaves it behind and strip() removes it.
        String unicodePadded = " hello ";
        System.out.println("unicode trim()  length = " + unicodePadded.trim().length()
                + "  (trim left it in place)");
        System.out.println("unicode strip() length = " + unicodePadded.strip().length()
                + "  (strip removed it)");
    }

    // =================================================================
    // 9. Splitting
    // =================================================================
    static void splitting() {
        System.out.println("\n--- 9. splitting ---");
        String csv = "id,name,,city,,";

        // split(regex) — trailing empty strings are DROPPED
        System.out.println("split(\",\")        = " + Arrays.toString(csv.split(",")));
        // limit = -1 keeps them; limit > 0 caps the number of pieces
        System.out.println("split(\",\", -1)    = " + Arrays.toString(csv.split(",", -1)));
        System.out.println("split(\",\", 2)     = " + Arrays.toString(csv.split(",", 2)));
        // split takes a REGEX, so "." or "|" must be escaped
        System.out.println("split(\"\\\\|\")       = " + Arrays.toString("a|b|c".split("\\|")));
        System.out.println("lines()           = " + "l1\nl2\nl3".lines().count() + " lines (Java 11)");
    }

    // =================================================================
    // 10. Comparing — the single most asked area
    // =================================================================
    static void comparing() {
        System.out.println("\n--- 10. comparing ---");
        String a = "Java";
        String b = "java";

        System.out.println("equals()             = " + a.equals(b)); // false, case sensitive

        /*
         * "are these two strings the same text, ignoring capitalisation?"
         */
        System.out.println("equalsIgnoreCase()   = " + a.equalsIgnoreCase(b)); // true
        System.out.println("compareTo()          = " + a.compareTo(b)); // negative: 'J'(74) - 'j'(106)
        System.out.println("compareToIgnoreCase()= " + a.compareToIgnoreCase(b));
        System.out.println("contentEquals(sb)    = " + a.contentEquals(new StringBuilder("Java")));
        System.out.println("CASE_INSENSITIVE_ORDER = "
                + String.CASE_INSENSITIVE_ORDER.compare(a, b));
        System.out.println("hashCode()           = " + a.hashCode() + " (s[0]*31^(n-1) + ...)");

        // Null-safe idiom: constant first, so a null variable can't NPE.
        String maybeNull = null;
        System.out.println("\"Java\".equals(null) = " + "Java".equals(maybeNull));
    }

    // =================================================================
    // 11. Conversion & static factories
    // =================================================================
    static void conversionAndFactories() {
        System.out.println("\n--- 11. conversion ---");

        System.out.println("toCharArray()      = " + Arrays.toString("Java".toCharArray()));
        System.out.println("getBytes().length  = " + "Java".getBytes().length);
        System.out.println("toString()         = " + "Java".toString()); // returns itself

        System.out.println("valueOf(int)       = " + String.valueOf(42));
        System.out.println("valueOf(double)    = " + String.valueOf(3.14));
        System.out.println("valueOf(boolean)   = " + String.valueOf(true));
        System.out.println("valueOf(char[])    = " + String.valueOf(new char[] { 'h', 'i' }));
        System.out.println("valueOf(null obj)  = " + String.valueOf((Object) null)); // "null", no NPE
        System.out.println("copyValueOf(char[])= " + String.copyValueOf(new char[] { 'h', 'i' }));

        // Constructors worth knowing
        System.out.println("new String(char[]) = " + new String(new char[] { 'J', 'D', 'K' }));
        System.out.println("new String(bytes, UTF_8) = "
                + new String("Java".getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        java.nio.charset.StandardCharsets.UTF_8));

        // Trap: String.valueOf(char[]) prints the chars, but "" + char[] or
        // System.out.println(charArray) behave differently from an Object.
        System.out.println("valueOf(65) vs (char)65 = " + String.valueOf(65) + " / " + (char) 65);
    }

    // =================================================================
    // 12. Formatting
    // =================================================================
    static void formatting() {
        System.out.println("\n--- 12. formatting ---");
        System.out.println(String.format("format: %s scored %d (%.2f%%)", "Asha", 87, 87.456));
        System.out.println("formatted (Java 15): "
                + "%s-%03d".formatted("INV", 7));
    }

    // =================================================================
    // 13. Java 11+ additions rounding out the API
    // =================================================================
    static void java11Plus() {
        System.out.println("\n--- 13. Java 11+ ---");
        System.out.print("indent(4):\n" + "a\nb".indent(4)); // Java 12
        System.out.println("transform(): " + "  42 ".transform(String::strip) // Java 12
                .transform(Integer::parseInt) + " (now an int)");
        System.out.println("describeConstable(): " + "x".describeConstable().isPresent());
        // Java 15 text block (multi-line literal, no escaping):
        String json = """
                {"id": 1, "name": "Asha"}""";
        System.out.println("text block: " + json);
    }

    // =================================================================
    // 14. Identity, pool, intern
    // =================================================================
    static void identityAndPool() {
        System.out.println("\n--- 14. pool & intern ---");
        String lit1 = "java";
        String lit2 = "java";
        String obj = new String("java");
        String concatAtCompileTime = "ja" + "va"; // folded by the compiler
        String part = "ja";
        String concatAtRuntime = part + "va"; // built at runtime, not pooled

        System.out.println("lit1 == lit2                = " + (lit1 == lit2)); // true
        System.out.println("lit1 == new String()        = " + (lit1 == obj)); // false
        System.out.println("lit1 == \"ja\"+\"va\" (const)  = " + (lit1 == concatAtCompileTime)); // true
        System.out.println("lit1 == part+\"va\" (runtime) = " + (lit1 == concatAtRuntime)); // false
        System.out.println("lit1 == runtime.intern()    = " + (lit1 == concatAtRuntime.intern())); // true
        System.out.println("lit1.equals(all of them)    = " + lit1.equals(concatAtRuntime));
        // Rule: compare content with equals(), never ==. Since Java 7 the pool
        // lives in the heap, not PermGen, so intern() no longer risks PermGen OOM.
    }

    // =================================================================
    // 15. Putting it together — clean one messy partner CSV row
    // =================================================================
    static void realWorldCsvCleanup() {
        System.out.println("\n--- 15. real-world CSV cleanup ---");
        String raw = "  1024 , asha   SHARMA ,  MUMBAI , ,  ";

        String[] fields = raw.split(",", -1); // keep the empty trailing cells
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].strip().replaceAll("\\s+", " ");
        }

        String id = fields[0];
        String name = titleCase(fields[1]);
        String city = fields[2].toUpperCase(java.util.Locale.ROOT);
        String phone = fields[3].isBlank() ? "N/A" : fields[3];

        System.out.println(String.format("id=%s | name=%s | city=%s | phone=%s", id, name, city, phone));
    }

    static String titleCase(String input) {
        StringBuilder out = new StringBuilder(input.length());
        for (String word : input.split(" ")) {
            if (word.isEmpty())
                continue;
            out.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase(java.util.Locale.ROOT))
                    .append(' ');
        }
        return out.toString().strip();
    }
}
