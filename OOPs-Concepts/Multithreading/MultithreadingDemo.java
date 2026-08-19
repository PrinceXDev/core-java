/*
 * Multithreading — running more than one piece of code at the same time
 * -------------------------------------------------------------------------
 * A backend server handles MANY requests at once. Under the hood that means
 * many threads running concurrently. You need to know:
 *   1. how to start a thread
 *   2. why shared mutable state is dangerous (race conditions)
 *   3. how `synchronized` fixes it
 *
 * NOTE: thread timing is non-deterministic - re-running this file may print
 * lines in a different order. That unpredictability IS the whole point.
 */

/* 

Process:- Process is a instance of program execution. When you enter an application, It's a process. The OS
assign it's own Stack & Heap Memory area.

Thread:- Thread is a lightweight process. It is a unit of execution within a given program. A single process
may contain multiple threads. Each thread in the process shares the memory and resources.

*/

public class MultithreadingDemo {

    public static void main(String[] args) throws InterruptedException {
        creatingThreads();
        raceConditionProblem();
        synchronizedFix();
        threadControlsDemo();
    }

    // -----------------------------------------------------------------
    // 1. Two ways to create a thread: extend Thread, or implement Runnable
    // (prefer Runnable - Java has single inheritance, so extending Thread
    // burns your one `extends` slot for no good reason)
    // -----------------------------------------------------------------
    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("MyThread running on: " + Thread.currentThread().getName());
        }
    }

    static void creatingThreads() throws InterruptedException {
        System.out.println("--- creating threads ---");

        MyThread t1 = new MyThread();
        t1.start(); // start() -> runs run() on a NEW thread
        t1.join(); // wait for t1 to finish before moving on

        Runnable task = () -> System.out.println("Runnable running on: " + Thread.currentThread().getName());
        Thread t2 = new Thread(task); // preferred style: pass a Runnable to Thread
        t2.start();
        t2.join();

        // t1.run(); // WRONG on purpose (not shown running): calling run() directly
        // // just executes it like a normal method on the CURRENT thread -
        // // only start() actually spins up a new thread.
    }

    // -----------------------------------------------------------------
    // 2. THE PROBLEM: race condition - two threads modifying shared state
    // at the same time can lose updates
    // -----------------------------------------------------------------
    static class UnsafeCounter {
        int count = 0;

        void increment() {
            count++;
        } // NOT atomic: read, add 1, write-back = 3 steps
    }

    static void raceConditionProblem() throws InterruptedException {
        System.out.println("\n--- race condition (unsynchronized) ---");
        UnsafeCounter counter = new UnsafeCounter();
        Runnable incrementTask = () -> {
            for (int i = 0; i < 10000; i++)
                counter.increment();
        };

        Thread t1 = new Thread(incrementTask);
        Thread t2 = new Thread(incrementTask);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Expected 20000, but two threads can interleave their read-modify-write
        // steps and stomp on each other's update -> final count is often LESS
        System.out.println("Expected 20000, got: " + counter.count + "  <-- likely wrong");
    }

    // -----------------------------------------------------------------
    // 3. THE FIX: synchronized ensures only ONE thread runs increment() at a time
    // -----------------------------------------------------------------
    static class SafeCounter {
        int count = 0;

        synchronized void increment() { // acquires a lock on `this` before running
            count++;
        }
    }

    static void synchronizedFix() throws InterruptedException {
        System.out.println("\n--- fixed with synchronized ---");
        SafeCounter counter = new SafeCounter();
        Runnable incrementTask = () -> {
            for (int i = 0; i < 10000; i++)
                counter.increment();
        };

        Thread t1 = new Thread(incrementTask);
        Thread t2 = new Thread(incrementTask);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Expected 20000, got: " + counter.count + "  <-- always correct now");
    }

    // -----------------------------------------------------------------
    // 4. THREAD CONTROLS: getState(), setDaemon(), interrupt()
    // -----------------------------------------------------------------
    static void threadControlsDemo() throws InterruptedException {
        System.out.println("\n--- thread controls (getState / setDaemon / interrupt) ---");

        Runnable sleepyTask = () -> {
            try {
                System.out.println("worker: going to sleep for 5s...");
                Thread.sleep(5000);
                System.out.println("worker: woke up normally (should NOT print)");
            } catch (InterruptedException e) {
                System.out.println("worker: interrupted while sleeping - stopping early");
            }
        };

        Thread worker = new Thread(sleepyTask, "worker-thread");
        worker.setDaemon(true); // must be set BEFORE start() - marks it as background work
        System.out.println("state before start(): " + worker.getState()); // NEW

        worker.start();
        Thread.sleep(200); // give it a moment to actually reach sleep()
        System.out.println("state while sleeping: " + worker.getState()); // TIMED_WAITING

        worker.interrupt(); // wake it up early instead of waiting the full 5s
        worker.join();
        System.out.println("state after finishing: " + worker.getState()); // TERMINATED
        System.out.println("was it a daemon thread? " + worker.isDaemon());
    }
}
