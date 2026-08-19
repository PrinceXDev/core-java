/* 
Concurrency:- Multiple task make progress during the same time window
*/

/* There is absolute no guarantee which thread doing (run) first, there are all running at the same time completely independently.
so, there is going to be some slight variance in their timing  */

class Multithreading extends Thread {
    private int threadNumber;

    @Override
    public void run() {
        for (int i = 0; i <= 5; i++) {
            System.out.println(i + " from thread " + threadNumber);

            if (threadNumber == 1) {
                throw new RuntimeException();
            }

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public Multithreading() {
        System.out.println("Default constructor");
    }

    public Multithreading(int threadNumber) {
        this.threadNumber = threadNumber;
    }
}

public class Demo {
    public static void main(String[] args) {
        Multithreading thread = new Multithreading();
        Multithreading thread2 = new Multithreading();

        // thread.run();

        // use start method, if you want to multiple concurrent thread
        // thread2.start();

        // if you run bunch of thread to run

        /*
         * 
         * for (int i = 0; i < 5; i++) {
         * Multithreading mything = new MultiThreading();
         * mything.start();
         * }
         * 
         */

        for (int i = 0; i < 2; i++) {
            Multithreading mything = new Multithreading(i);
            mything.start();
        }
    }
}
