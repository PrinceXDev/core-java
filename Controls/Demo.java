public class Demo {
    public static void main(String[] args) {
        int age = 20;

        if (age > 18) {
            System.out.println("Adult");
        } else if (age >= 13) {
            System.out.println("Teen");
        } else {
            System.out.println("Child");
        }

        int day = 3;
        switch (day) {
            case 1:
                System.out.println("Monday");
            case 2:
                System.out.println("Tuesday");
            case 3:
                System.out.println("Wednesday");
            default:
                System.out.println("Invalid");
        }

        System.out.println("=================");

        int[] nums = { 10, 20, 30 };
        for (int n : nums) {
            System.out.println(n);
        }

        System.out.println("========================");

        int i = 0;
        do {
            System.out.println(i);
            i++;
        } while (i < 5);

        System.out.println("======================");

        for (int j = 0; j < 5; j++) {
            if (j == 2)
                continue; // skips printing 2
            System.out.println(j);
        }
    }
}
