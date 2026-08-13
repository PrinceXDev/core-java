/*
 * byte — the smallest integer type in Java
 * ----------------------------------------
 * Size  : 8 bits (1 byte)
 * Range : -128 .. 127   (signed, two's complement)
 * Default value : 0
 */
public class ByteDemo {

    public static void main(String[] args) throws Exception {
        basics();
        overflow();
        memorySaving();
        realWorldUseCase();
    }

    // 1. Declaring and using byte
    static void basics() {
        byte age = 25;          // fits in -128..127, so it compiles
        // byte bad = 200;      // ERROR: 200 is out of range
        byte narrowed = (byte) 200;  // explicit cast -> wraps to -56


        /* Rule collision
            - There is no byte + byte in java. It is promoted to int.
            - Java has no arithmetic operators for bytes.
         */
        byte x = 10, y = 20;
        // Type of expression: int -> cast to byte
        byte sum = (byte) (x + y);
        System.out.println("10 + 20 as byte = " + sum + "  (int would be " + (x + y) + ")");

        System.out.println("--- basics ---");
        System.out.println("age            = " + age);
        System.out.println("(byte) 200     = " + narrowed);
        System.out.println("Byte.MIN_VALUE = " + Byte.MIN_VALUE);
        System.out.println("Byte.MAX_VALUE = " + Byte.MAX_VALUE);
    }

    // 2. Why range matters: silent wrap-around
    static void overflow() {
        byte b = 127;
        b++;                    // 128 doesn't exist -> wraps to -128
        System.out.println("\n--- overflow ---");
        System.out.println("127 + 1 as byte = " + b);

        byte x = 100, y = 100;
        // byte sum = x + y;    // ERROR: byte arithmetic is promoted to int
        byte sum = (byte) (x + y);
        System.out.println("100 + 100 as byte = " + sum + "  (int would be " + (x + y) + ")");
    }

    // 3. Memory: 1 million elements
    static void memorySaving() {
        int n = 1_000_000;
        byte[] asByte = new byte[n];   // ~1 MB
        int[]  asInt  = new int[n];    // ~4 MB
        System.out.println("\n--- memory ---");
        System.out.println("byte[1M] ~ " + (n * 1) / 1024 + " KB");
        System.out.println("int [1M] ~ " + (n * 4) / 1024 + " KB  (4x more for the same values)");
        System.out.println("lengths: " + asByte.length + ", " + asInt.length);
    }

    /*
     * 4. THE real use case: raw binary I/O.
     * Files, sockets, images, encryption, compression — all of it is a stream
     * of bytes. Java's I/O API is built on byte[], not int[].
     *
     * Here we write some bytes to a file, read them back, and inspect them.
     */
    static void realWorldUseCase() throws Exception {
        java.nio.file.Path path = java.nio.file.Paths.get("hello.bin");

        // Text -> bytes (this is what actually travels over disk/network)
        byte[] data = "Hello Java".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        java.nio.file.Files.write(path, data);

        // Read the raw bytes back
        byte[] readBack = java.nio.file.Files.readAllBytes(path);

        System.out.println("\n--- real world: binary I/O ---");
        System.out.print("bytes  : ");
        for (byte b : readBack) System.out.print(b + " ");
        System.out.println();

        System.out.print("as hex : ");
        for (byte b : readBack) System.out.printf("%02X ", b & 0xFF);   // mask because byte is signed
        System.out.println();

        System.out.println("back to text : " +
                new String(readBack, java.nio.charset.StandardCharsets.UTF_8));

        // Classic gotcha: byte is SIGNED, so values > 127 print as negative.
        // Mask with 0xFF to see the unsigned 0..255 value.
        byte signed = (byte) 0xF0;              // -16
        int unsigned = signed & 0xFF;           // 240
        System.out.println("signed byte 0xF0 = " + signed + " | unsigned = " + unsigned);

        java.nio.file.Files.deleteIfExists(path);
    }
}
