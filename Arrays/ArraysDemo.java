/*
 * Arrays in Java — a fixed-size, indexed container of same-type values
 * -------------------------------------------------------------------------
 * Key facts that decide almost every interview answer:
 *
 * 1. An array is an OBJECT. Even `int[]` lives on the heap; the variable on
 *    the stack only holds a reference. That's why `arr.length` works and why
 *    passing an array to a method lets the method mutate the caller's data.
 * 2. Size is fixed at creation and stored in the object header. `length` is a
 *    FIELD, not a method (contrast: String.length(), List.size()).
 * 3. Elements are contiguous in memory, so index access is O(1).
 * 4. Elements are auto-initialised: 0 / 0.0 / false / null / the null char.
 * 5. Arrays are COVARIANT (String[] is an Object[]) but NOT generic-safe —
 *    this is the ArrayStoreException trap below.
 * 6. Index is validated at runtime -> ArrayIndexOutOfBoundsException.
 *    Negative size -> NegativeArraySizeException (compiles fine, fails later).
 *
 * Real-time example: a monthly sales report. Exactly 12 months, never 13,
 * never 11 — a fixed-size array is the honest data structure for that.
 */

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ArraysDemo {

    public static void main(String[] args) {
        declarationAndDefaults();
        oneDimensionalTraversal();
        twoDimensionalAndJagged();
        arraysUtilityMethods();
        copyingArrays();
        covarianceTrap();
        varargsIsAnArray();
        arrayVsArrayList();
    }

    // -----------------------------------------------------------------
    // 1. Declaring, sizing, default values
    // -----------------------------------------------------------------
    static void declarationAndDefaults() {
        System.out.println("--- 1. declaration & defaults ---");

        int[] sales = new int[12]; // preferred style: type[] name
        int legacy[] = new int[3]; // legal C-style, discouraged
        int[] literal = { 150, 220, 310 }; // size inferred = 3
        int[] explicit = new int[] { 1, 2, 3 }; // needed when passing inline

        System.out.println("sales.length = " + sales.length); // length is a FIELD
        System.out.println("int default   = " + sales[0]); // 0
        System.out.println("literal       = " + Arrays.toString(literal));
        System.out.println("legacy/explicit sizes: " + legacy.length + ", " + explicit.length);

        String[] names = new String[2];
        boolean[] flags = new boolean[2];
        double[] rates = new double[2];
        char[] grades = new char[2];
        System.out.println("String default  = " + names[0]); // null
        System.out.println("boolean default = " + flags[0]); // false
        System.out.println("double default  = " + rates[0]); // 0.0
        System.out.println("char default    = " + (int) grades[0] + " (the null char)");

        // Runtime failure, not compile-time:
        try {
            System.out.println(sales[12]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("caught: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // 2. Traversal: index loop vs enhanced for
    // -----------------------------------------------------------------
    static void oneDimensionalTraversal() {
        System.out.println("\n--- 2. traversal ---");
        int[] monthlySales = { 150, 220, 310, 90, 400, 275, 180, 330, 260, 410, 500, 620 };

        int total = 0;
        for (int i = 0; i < monthlySales.length; i++) { // index loop: you know WHERE you are
            total += monthlySales[i];
        }

        int max = monthlySales[0];
        int bestMonth = 0;
        for (int i = 1; i < monthlySales.length; i++) {
            if (monthlySales[i] > max) {
                max = monthlySales[i];
                bestMonth = i;
            }
        }

        int average = total / monthlySales.length;
        int aboveAverage = 0;
        for (int value : monthlySales) { // enhanced for: read-only, no index
            if (value > average)
                aboveAverage++;
        }

        System.out.println("total = " + total + ", average = " + average);
        System.out.println("best month index = " + bestMonth + " (value " + max + ")");
        System.out.println("months above average = " + aboveAverage);

        // Enhanced for CANNOT write back — `value` is a copy of the element.
        for (int value : monthlySales)
            value = 0;
        System.out.println("after enhanced-for assignment, first element still = " + monthlySales[0]);
    }

    // -----------------------------------------------------------------
    // 3. 2D arrays are arrays OF arrays — so rows can differ in length
    // -----------------------------------------------------------------
    static void twoDimensionalAndJagged() {
        System.out.println("\n--- 3. 2D & jagged ---");

        int[][] quarterly = new int[4][3]; // 4 rows, 3 columns
        quarterly[0][0] = 150;
        quarterly[3][2] = 620;
        System.out.println("rectangular: " + Arrays.deepToString(quarterly));

        // Jagged: each store reports a different number of days
        int[][] jagged = new int[3][];
        jagged[0] = new int[] { 10, 20 };
        jagged[1] = new int[] { 5 };
        jagged[2] = new int[] { 7, 8, 9, 10 };
        for (int r = 0; r < jagged.length; r++) {
            System.out.println("row " + r + " length " + jagged[r].length
                    + " -> " + Arrays.toString(jagged[r]));
        }

        // toString on a 2D array prints references — use deepToString.
        System.out.println("Arrays.toString(2D)     = " + Arrays.toString(jagged));
        System.out.println("Arrays.deepToString(2D) = " + Arrays.deepToString(jagged));
    }

    // -----------------------------------------------------------------
    // 4. java.util.Arrays — the utility class you are expected to know
    // -----------------------------------------------------------------
    static void arraysUtilityMethods() {
        System.out.println("\n--- 4. java.util.Arrays ---");
        int[] a = { 5, 3, 9, 1, 7 };

        int[] sorted = Arrays.copyOf(a, a.length);
        Arrays.sort(sorted); // dual-pivot quicksort for primitives
        System.out.println("sort            = " + Arrays.toString(sorted));

        // binarySearch REQUIRES a sorted array; result on unsorted input is undefined.
        System.out.println("binarySearch(7) = " + Arrays.binarySearch(sorted, 7));
        System.out.println("binarySearch(8) = " + Arrays.binarySearch(sorted, 8)
                + "  (negative = -(insertionPoint) - 1)");

        int[] part = { 9, 8, 7, 1, 2 };
        Arrays.sort(part, 0, 3); // sort only [0,3)
        System.out.println("sort(range)     = " + Arrays.toString(part));

        Integer[] boxed = { 5, 3, 9, 1, 7 }; // Comparator needs objects
        Arrays.sort(boxed, Comparator.reverseOrder());
        System.out.println("sort(desc)      = " + Arrays.toString(boxed));

        int[] filled = new int[5];
        Arrays.fill(filled, 42);
        System.out.println("fill            = " + Arrays.toString(filled));

        int[] squares = new int[5];
        Arrays.setAll(squares, i -> i * i); // generator by index
        System.out.println("setAll          = " + Arrays.toString(squares));

        System.out.println("equals          = " + Arrays.equals(new int[] { 1, 2 }, new int[] { 1, 2 }));
        System.out.println("deepEquals      = " + Arrays.deepEquals(
                new int[][] { { 1, 2 } }, new int[][] { { 1, 2 } }));
        System.out.println("compare         = " + Arrays.compare(new int[] { 1, 2 }, new int[] { 1, 3 }));
        System.out.println("mismatch        = " + Arrays.mismatch(new int[] { 1, 2, 3 }, new int[] { 1, 9, 3 }));
        System.out.println("hashCode        = " + Arrays.hashCode(new int[] { 1, 2 }));
        System.out.println("stream sum      = " + Arrays.stream(a).sum());

        // asList: fixed-size VIEW backed by the array — add/remove throw.
        List<Integer> view = Arrays.asList(boxed);
        try {
            view.add(11);
        } catch (UnsupportedOperationException e) {
            System.out.println("asList().add -> UnsupportedOperationException");
        }
        view.set(0, 99); // set IS allowed
        System.out.println("asList set writes through: boxed[0] = " + boxed[0]);
    }

    // -----------------------------------------------------------------
    // 5. Copying: clone() is a SHALLOW copy
    // -----------------------------------------------------------------
    static void copyingArrays() {
        System.out.println("\n--- 5. copying ---");
        int[] src = { 1, 2, 3, 4, 5 };

        int[] byClone = src.clone();
        int[] byCopyOf = Arrays.copyOf(src, 7); // pads with 0
        int[] byRange = Arrays.copyOfRange(src, 1, 4); // [1,4) -> 2,3,4
        int[] dest = new int[5];
        System.arraycopy(src, 0, dest, 0, src.length); // fastest, native

        System.out.println("clone       = " + Arrays.toString(byClone));
        System.out.println("copyOf(7)   = " + Arrays.toString(byCopyOf));
        System.out.println("copyOfRange = " + Arrays.toString(byRange));
        System.out.println("arraycopy   = " + Arrays.toString(dest));

        // Shallow copy proof: the outer array is new, the inner rows are SHARED.
        int[][] matrix = { { 1, 2 }, { 3, 4 } };
        int[][] shallow = matrix.clone();
        shallow[0][0] = 999;
        System.out.println("after shallow[0][0]=999, matrix[0][0] = " + matrix[0][0]);

        int[][] deep = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++)
            deep[i] = matrix[i].clone();
        deep[0][0] = -1;
        System.out.println("after deep[0][0]=-1,   matrix[0][0] = " + matrix[0][0]);
    }

    // -----------------------------------------------------------------
    // 6. Covariance trap — arrays are not type-safe at compile time
    // -----------------------------------------------------------------
    static void covarianceTrap() {
        System.out.println("\n--- 6. covariance / ArrayStoreException ---");
        Object[] objects = new String[2]; // legal: String[] IS-A Object[]
        try {
            objects[0] = 42; // compiles fine, blows up at runtime
        } catch (ArrayStoreException e) {
            System.out.println("caught ArrayStoreException: " + e.getMessage());
        }
        // Generics fixed this: List<Object> x = new ArrayList<String>(); won't compile.
    }

    // -----------------------------------------------------------------
    // 7. varargs IS an array
    // -----------------------------------------------------------------
    static void varargsIsAnArray() {
        System.out.println("\n--- 7. varargs ---");
        System.out.println("sum()        = " + sum());
        System.out.println("sum(1,2,3)   = " + sum(1, 2, 3));
        System.out.println("sum(array)   = " + sum(new int[] { 4, 5, 6 }));
        // main(String[] args) is itself the classic array parameter.
    }

    static int sum(int... numbers) { // `numbers` is an int[] inside the method
        int total = 0;
        for (int n : numbers)
            total += n;
        return total;
    }

    // -----------------------------------------------------------------
    // 8. Array vs ArrayList — the one-line comparison they want
    // -----------------------------------------------------------------
    static void arrayVsArrayList() {
        System.out.println("\n--- 8. array vs ArrayList ---");
        System.out.println("size       : array fixed at creation | ArrayList grows (1.5x resize)");
        System.out.println("contents   : array holds primitives or objects | ArrayList objects only");
        System.out.println("length     : arr.length (field) | list.size() (method)");
        System.out.println("type safety: array covariant (ArrayStoreException) | generics checked at compile time");
        System.out.println("speed      : array no boxing overhead | ArrayList slower but flexible");
        System.out.println("use array when the size is genuinely fixed and known (12 months, RGB triple).");
    }
}
