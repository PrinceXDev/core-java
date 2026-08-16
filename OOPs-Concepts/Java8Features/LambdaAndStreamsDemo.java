import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/*
 * Java 8 Features — Lambdas, Streams, Optional
 * -------------------------------------------------------------------------
 * Java 8 added functional-style programming on top of OOP. Modern backend
 * code (Spring, etc.) leans on these heavily, so they're must-know even
 * though they're "just" library + syntax additions, not new OOP concepts.
 *
 *   Lambda   : a function written inline, e.g. (a, b) -> a + b
 *   Stream   : a pipeline of operations (filter/map/reduce) over a collection,
 *              WITHOUT writing manual for-loops
 *   Optional : a box that may or may not contain a value - forces you to
 *              think about the "missing value" case instead of getting a
 *              surprise NullPointerException
 */
public class LambdaAndStreamsDemo {

    public static void main(String[] args) {
        lambdaBasics();
        streamFilterMapReduce();
        collectingResults();
        optionalAvoidsNullChecks();
    }

    // -----------------------------------------------------------------
    // 1. Lambda expressions implement a functional interface's single method
    // -----------------------------------------------------------------
    static void lambdaBasics() {
        System.out.println("--- lambda basics ---");
        Predicate<Integer> isEven = n -> n % 2 == 0;     // boolean test(T t)
        Function<Integer, Integer> square = n -> n * n;  // R apply(T t)

        System.out.println("isEven.test(4) = " + isEven.test(4));
        System.out.println("square.apply(5) = " + square.apply(5));
    }

    // -----------------------------------------------------------------
    // 2. Stream pipeline: filter -> map -> reduce, no manual loop needed
    // -----------------------------------------------------------------
    static void streamFilterMapReduce() {
        System.out.println("\n--- stream: filter, map, reduce ---");
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        int sumOfSquaresOfEvens = numbers.stream()
            .filter(n -> n % 2 == 0)      // keep only even numbers: 2,4,6,8,10
            .map(n -> n * n)              // square each one: 4,16,36,64,100
            .reduce(0, Integer::sum);     // add them all up: 220

        System.out.println("sum of squares of evens = " + sumOfSquaresOfEvens);

        // Old-style equivalent, for comparison:
        int manualSum = 0;
        for (int n : numbers) {
            if (n % 2 == 0) manualSum += n * n;
        }
        System.out.println("same result the manual-loop way = " + manualSum);
    }

    // -----------------------------------------------------------------
    // 3. collect(): turn a stream back into a List/Set/Map
    // -----------------------------------------------------------------
    static void collectingResults() {
        System.out.println("\n--- collecting stream results ---");
        List<String> names = List.of("Prince", "Asha", "Bob", "Amit");

        List<String> namesStartingWithA = names.stream()
            .filter(name -> name.startsWith("A"))
            .map(String::toUpperCase)
            .collect(Collectors.toList());

        System.out.println("names starting with A = " + namesStartingWithA);
    }

    // -----------------------------------------------------------------
    // 4. Optional: explicit handling of "might be missing", instead of null
    // -----------------------------------------------------------------
    static Optional<String> findUserById(int id) {
        if (id == 1) return Optional.of("Prince");
        return Optional.empty();   // no user found - explicit, not a silent null
    }

    static void optionalAvoidsNullChecks() {
        System.out.println("\n--- Optional instead of null ---");

        Optional<String> found = findUserById(1);
        System.out.println("found.isPresent()? " + found.isPresent());
        System.out.println("value or default = " + found.orElse("Unknown"));

        Optional<String> notFound = findUserById(99);
        // .get() without checking would throw NoSuchElementException - orElse is safer
        System.out.println("missing user, fallback = " + notFound.orElse("Unknown"));

        // ifPresent: run code only when a value actually exists
        found.ifPresent(name -> System.out.println("Welcome back, " + name + "!"));
    }
}
