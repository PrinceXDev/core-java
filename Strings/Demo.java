package Strings;

public class Demo {
    public static void main(String[] args) {
        String s = "Hello";
        s.concat(" World"); // creates a NEW string, thrown away
        System.out.println(s); // Hello ← unchanged

        StringBuilder sb = new StringBuilder("Hello");
        sb.append(" World"); // mutates the SAME object
        System.out.println(sb); // Hello World ← changed

        System.out.println("================================================");

        // ❌ O(n²) — each += copies every character accumulated so far
        String slow = "";
        for (int i = 0; i < 20_000; i++)
            slow += "x";

        // ✅ O(n) — one growing buffer
        StringBuilder fast = new StringBuilder(20_000);
        for (int i = 0; i < 20_000; i++)
            fast.append("x");

        /*
         * Measured on your JDK 17 (from section 13 of the demo file):
         * 
         * loop of 20000 with += : 40 ms
         * loop of 20000 builder : 0 ms
         */
    }
}
