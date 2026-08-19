package Strings;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Finds the frequency of each character in a String.
 *
 * Approach: walk the string once, and for every character bump its counter in a
 * LinkedHashMap. LinkedHashMap is used (instead of HashMap) so the output keeps
 * the order in which characters first appear in the string.
 */
public class CharFrequencyDemo {

    public static Map<Character, Integer> frequency(String input) {
        Map<Character, Integer> counts = new LinkedHashMap<>();
        for (char ch : input.toCharArray()) {
            // getOrDefault avoids the "is the key already there?" if-else
            counts.put(ch, counts.getOrDefault(ch, 0) + 1);
        }
        return counts;
    }

    public static void main(String[] args) {
        String input = "programming";
        System.out.println("Input: " + input);

        Map<Character, Integer> counts = frequency(input);

        System.out.println("\nCharacter frequencies:");
        for (Map.Entry<Character, Integer> entry : counts.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        // Characters that occur more than once
        System.out.println("\nRepeated characters:");
        counts.forEach((ch, count) -> {
            if (count > 1) {
                System.out.println(ch + " (" + count + " times)");
            }
        });
    }
}
