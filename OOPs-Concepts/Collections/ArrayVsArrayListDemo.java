/*
 * Array vs ArrayList — the practical differences.
 *
 * Array     : fixed size, part of the language itself, holds primitives OR objects.
 * ArrayList : resizable, a class in java.util, holds objects only, rich API.
 *
 * Rule of thumb: known fixed size + primitives -> array.
 *                unknown/changing size + need methods -> ArrayList.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayVsArrayListDemo {

    public static void main(String[] args) {

        // 1. SIZE — array is fixed, ArrayList grows
        String[] arr = new String[3];
        arr[0] = "Mon";
        arr[1] = "Tue";
        arr[2] = "Wed";
        // arr[3] = "Thu";  // ArrayIndexOutOfBoundsException — size is locked

        List<String> list = new ArrayList<>();
        list.add("Mon");
        list.add("Tue");
        list.add("Wed");
        list.add("Thu"); // no problem, it resizes itself
        System.out.println("Array length : " + arr.length);   // field, no ()
        System.out.println("List size    : " + list.size());  // method, with ()

        // 2. PRIMITIVES — array can hold them directly, ArrayList cannot
        int[] marks = {90, 85, 77};                 // real ints, no boxing
        List<Integer> marksList = new ArrayList<>();
        marksList.add(90);                          // autoboxed to Integer object
        System.out.println("\nint[] sum    : " + (marks[0] + marks[1] + marks[2]));
        System.out.println("Integer list : " + marksList);

        // 3. ADDING / REMOVING IN THE MIDDLE
        list.add(1, "Sun");   // shifts everything right
        list.remove("Wed");   // shifts everything left
        System.out.println("\nAfter insert/remove: " + list);
        // With an array you would have to write the shifting loop yourself,
        // or create a whole new bigger array and copy into it:
        String[] bigger = Arrays.copyOf(arr, arr.length + 1);
        bigger[3] = "Thu";
        System.out.println("Array grown manually: " + Arrays.toString(bigger));

        // 4. BUILT-IN METHODS — ArrayList has them, arrays don't
        System.out.println("\nlist.contains(\"Tue\") : " + list.contains("Tue"));
        System.out.println("list.indexOf(\"Tue\")  : " + list.indexOf("Tue"));
        // arrays need a helper: Arrays.asList(arr).contains(...) or a manual loop

        // 5. PRINTING — array prints its hashcode, ArrayList prints its content
        System.out.println("\nPrinting array direct : " + arr);              // [Ljava.lang.String;@...
        System.out.println("Arrays.toString(arr)  : " + Arrays.toString(arr));
        System.out.println("Printing list direct  : " + list);               // readable

        // 6. TYPE SAFETY — arrays are covariant, and that bites at runtime
        Object[] objects = new String[2];
        try {
            objects[0] = 42; // compiles fine, blows up at runtime
        } catch (ArrayStoreException e) {
            System.out.println("\nArrayStoreException: array covariance is unsafe");
        }
        // List<String> l = new ArrayList<Integer>(); // won't even compile — generics catch it

        // 7. CONVERTING BETWEEN THE TWO
        String[] backToArray = list.toArray(new String[0]);
        List<String> fromArray = new ArrayList<>(Arrays.asList(arr));
        System.out.println("\nlist -> array : " + Arrays.toString(backToArray));
        System.out.println("array -> list : " + fromArray);
        // Note: Arrays.asList(arr) alone gives a FIXED-SIZE view — add() throws.
        try {
            Arrays.asList(arr).add("Fri");
        } catch (UnsupportedOperationException e) {
            System.out.println("Arrays.asList() is fixed-size — wrap it in new ArrayList<>()");
        }
    }
}
