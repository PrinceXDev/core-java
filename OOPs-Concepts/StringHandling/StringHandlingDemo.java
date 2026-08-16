/*
 * String Handling — Strings are immutable, and that changes everything
 * -------------------------------------------------------------------------
 * A String's contents can NEVER change after creation. Every "modifying"
 * method (concat, toUpperCase, replace...) returns a brand-new String.
 * This is why beginners get bitten by "I called .toUpperCase() but nothing
 * changed" - you must capture the returned value.
 */
public class StringHandlingDemo {

    public static void main(String[] args) {
        immutabilityGotcha();
        stringPoolVsNewString();
        comparingStrings();
        stringBuilderForLoops();
    }

    // -----------------------------------------------------------------
    // 1. Immutability: methods return NEW strings, the original never changes
    // -----------------------------------------------------------------
    static void immutabilityGotcha() {
        System.out.println("--- immutability gotcha ---");
        String s = "hello";
        s.toUpperCase();                     // return value thrown away - does nothing useful!
        System.out.println("after toUpperCase() ignored: " + s);   // still "hello"

        s = s.toUpperCase();                 // correct: capture the new String
        System.out.println("after capturing result: " + s);       // "HELLO"
    }

    // -----------------------------------------------------------------
    // 2. String pool: literals are cached and reused; `new String()` forces a new object
    // -----------------------------------------------------------------
    static void stringPoolVsNewString() {
        System.out.println("\n--- string pool vs new String() ---");
        String a = "java";              // goes into the string pool (or reuses existing entry)
        String b = "java";              // reuses the SAME pooled object as `a`
        String c = new String("java");  // explicitly creates a NEW object, bypassing the pool

        System.out.println("a == b (same pooled object)? " + (a == b));   // true
        System.out.println("a == c (new object on heap)?  " + (a == c)); // false
        System.out.println("a.equals(c) (same content)?   " + a.equals(c)); // true
    }

    // -----------------------------------------------------------------
    // 3. == compares references/identity, .equals() compares content
    //    RULE: always use .equals() for String comparison, never ==
    // -----------------------------------------------------------------
    static void comparingStrings() {
        System.out.println("\n--- == vs .equals() ---");
        String name1 = new String("Prince");
        String name2 = new String("Prince");
        System.out.println("name1 == name2 (identity)   -> " + (name1 == name2));       // false
        System.out.println("name1.equals(name2) (value) -> " + name1.equals(name2));    // true
    }

    // -----------------------------------------------------------------
    // 4. StringBuilder: mutable, use it when building strings in a loop
    //    (concatenating with + in a loop creates a NEW String object every
    //     iteration -> wasteful; StringBuilder mutates one internal buffer)
    // -----------------------------------------------------------------
    static void stringBuilderForLoops() {
        System.out.println("\n--- StringBuilder for loops ---");

        // Bad for large loops: each += silently creates a new String object
        String result = "";
        for (int i = 1; i <= 5; i++) {
            result += i + ",";   // 5 throwaway String objects created behind the scenes
        }
        System.out.println("String concatenation result: " + result);

        // Good: one mutable buffer, no throwaway objects
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i).append(",");   // mutates the SAME object each time
        }
        System.out.println("StringBuilder result: " + sb.toString());
    }
}
