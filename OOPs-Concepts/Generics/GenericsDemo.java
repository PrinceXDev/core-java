import java.util.ArrayList;
import java.util.List;

/*
 * Generics — type-safety at compile time, without repeating yourself
 * -----------------------------------------------------------------------
 * Before generics, collections held plain Object and you had to cast
 * everything back, risking a ClassCastException at RUNTIME.
 * Generics let you write ONE class/method that works with any type, while
 * the compiler catches type mistakes at COMPILE time instead.
 */
public class GenericsDemo {

    public static void main(String[] args) {
        lifeWithoutGenerics();
        lifeWithGenerics();
        genericClass();
        genericMethod();
        boundedTypeParameter();
    }

    // -----------------------------------------------------------------
    // 1. THE PROBLEM: raw List of Object -> unsafe casting, fails at runtime
    // -----------------------------------------------------------------
    @SuppressWarnings({ "unchecked", "rawtypes" })
    static void lifeWithoutGenerics() {
        System.out.println("--- without generics (old, unsafe way) ---");
        List rawList = new ArrayList(); // raw type - holds ANY Object
        rawList.add("hello");
        rawList.add(42); // compiler allows this - no type checking!

        try {
            for (Object o : rawList) {
                String s = (String) o; // BOOM on the Integer - ClassCastException
                System.out.println(s);
            }
        } catch (ClassCastException e) {
            System.out.println("Crashed at runtime: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // 2. THE FIX: List<String> -> compiler stops the wrong type at compile time
    // -----------------------------------------------------------------
    static void lifeWithGenerics() {
        System.out.println("\n--- with generics (type-safe) ---");
        List<String> names = new ArrayList<>();
        names.add("hello");
        // names.add(42); // COMPILE ERROR: int cannot be added to List<String>
        for (String s : names) {
            System.out.println(s.toUpperCase()); // no cast needed, compiler knows it's a String
        }
    }

    // -----------------------------------------------------------------
    // 3. Generic class: <T> is a placeholder decided when you USE the class
    // -----------------------------------------------------------------
    static class Box<T> {
        private T content;

        void put(T content) {
            this.content = content;
        }

        T get() {
            return content;
        }
    }

    static void genericClass() {
        System.out.println("\n--- generic class ---");
        Box<String> textBox = new Box<>();
        textBox.put("secret message");
        System.out.println("textBox holds: " + textBox.get());

        Box<Integer> numberBox = new Box<>(); // SAME class, different type this time
        numberBox.put(100);
        System.out.println("numberBox holds: " + numberBox.get());
    }

    // -----------------------------------------------------------------
    // 4. Generic method: <T> declared on the method itself, independent of the
    // class
    // -----------------------------------------------------------------
    static <T> void printArray(T[] items) {
        for (T item : items) {
            System.out.print(item + " ");
        }
        System.out.println();
    }

    static void genericMethod() {
        System.out.println("\n--- generic method ---");
        Integer[] numbers = { 1, 2, 3 };
        String[] words = { "a", "b", "c" };
        printArray(numbers); // same method, works for both types
        printArray(words);
    }

    // -----------------------------------------------------------------
    // 5. Bounded type parameter: restrict T to a family of types
    // (here: only things that are Number or a subclass of it)
    // -----------------------------------------------------------------
    static <T extends Number> double sumAll(List<T> numbers) {
        double total = 0;
        for (T n : numbers) {
            total += n.doubleValue(); // only possible because T is guaranteed to be a Number
        }
        return total;
    }

    static void boundedTypeParameter() {
        System.out.println("\n--- bounded type parameter (T extends Number) ---");
        List<Integer> ints = List.of(1, 2, 3);
        List<Double> doubles = List.of(1.5, 2.5);
        System.out.println("sum of ints = " + sumAll(ints));
        System.out.println("sum of doubles = " + sumAll(doubles));
        // sumAll(List.of("a", "b")); // COMPILE ERROR: String is not a Number
    }
}
