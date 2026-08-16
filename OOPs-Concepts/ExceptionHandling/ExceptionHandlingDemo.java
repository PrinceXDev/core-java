import java.io.FileReader;
import java.io.IOException;

/*
 * Exception Handling — dealing with things going wrong, on purpose
 * -------------------------------------------------------------------
 * An exception is an object describing an error. Instead of crashing, Java
 * lets you `catch` it and decide what to do.
 *
 * Hierarchy you must know:
 *   Throwable
 *   ├── Error            (JVM-level disasters, e.g. OutOfMemoryError - don't catch these)
 *   └── Exception
 *        ├── checked      (compiler FORCES you to handle or declare, e.g. IOException)
 *        └── RuntimeException (unchecked - compiler doesn't force you,
 *                               e.g. NullPointerException, ArithmeticException)
 *
 * Rule of thumb: checked = "this WILL happen sometimes in normal operation
 * (file missing, network down)", unchecked = "this is a bug in your code".
 */
public class ExceptionHandlingDemo {

    public static void main(String[] args) {
        basicTryCatch();
        multiCatchAndFinally();
        checkedExceptionMustBeHandled();
        tryWithResources();
        customException();
    }

    // -----------------------------------------------------------------
    // 1. Basic try-catch: without it, this throws and kills the program
    // -----------------------------------------------------------------
    static void basicTryCatch() {
        System.out.println("--- basic try-catch ---");
        int[] numbers = {1, 2, 3};
        try {
            System.out.println(numbers[5]);   // ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught it: " + e.getMessage());
        }
        System.out.println("Program keeps running after the catch block");
    }

    // -----------------------------------------------------------------
    // 2. Multiple catch blocks + finally (finally ALWAYS runs, error or not)
    // -----------------------------------------------------------------
    static void multiCatchAndFinally() {
        System.out.println("\n--- multi-catch + finally ---");
        String input = "abc";
        try {
            int value = Integer.parseInt(input);   // NumberFormatException
            int result = 10 / value;
        } catch (NumberFormatException e) {
            System.out.println("Bad number format: " + input);
        } catch (ArithmeticException e) {
            System.out.println("Math error: " + e.getMessage());
        } finally {
            // Runs whether an exception happened or not - great for cleanup
            System.out.println("finally: cleanup always happens here");
        }
    }

    // -----------------------------------------------------------------
    // 3. Checked exception: compiler forces try-catch OR `throws` in signature
    // -----------------------------------------------------------------
    static void checkedExceptionMustBeHandled() {
        System.out.println("\n--- checked exception (IOException) ---");
        try {
            readMissingFile();
        } catch (IOException e) {
            System.out.println("Caught checked exception: " + e.getMessage());
        }
    }

    // `throws IOException` = "I'm not handling it here, caller must deal with it"
    static void readMissingFile() throws IOException {
        FileReader reader = new FileReader("does-not-exist.txt");   // throws IOException
        reader.close();
    }

    // -----------------------------------------------------------------
    // 4. try-with-resources: auto-closes anything implementing AutoCloseable
    //    (no manual finally { resource.close(); } needed)
    // -----------------------------------------------------------------
    static class Connection implements AutoCloseable {
        Connection() { System.out.println("Connection opened"); }
        void query() { System.out.println("Running query..."); }

        @Override
        public void close() { System.out.println("Connection closed automatically"); }
    }

    static void tryWithResources() {
        System.out.println("\n--- try-with-resources ---");
        try (Connection conn = new Connection()) {
            conn.query();
        }
        // conn.close() is called here automatically, even if query() had thrown
    }

    // -----------------------------------------------------------------
    // 5. Custom exception: extend Exception (checked) or RuntimeException (unchecked)
    // -----------------------------------------------------------------
    static class InsufficientFundsException extends RuntimeException {
        InsufficientFundsException(String message) {
            super(message);
        }
    }

    static void withdraw(double balance, double amount) {
        if (amount > balance) {
            throw new InsufficientFundsException(
                "Cannot withdraw " + amount + ", balance is only " + balance);
        }
        System.out.println("Withdrawal of " + amount + " approved");
    }

    static void customException() {
        System.out.println("\n--- custom exception ---");
        try {
            withdraw(100, 500);
        } catch (InsufficientFundsException e) {
            System.out.println("Caught custom exception: " + e.getMessage());
        }
    }
}
