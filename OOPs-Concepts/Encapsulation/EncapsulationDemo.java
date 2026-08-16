/*
 * Encapsulation — bundling data with the methods that guard it
 * -------------------------------------------------------------
 * Rule: fields are `private`. The outside world can only read/change them
 * through public methods (getters/setters) that the class controls.
 *
 * Why: it stops invalid states (like a negative balance) from ever existing,
 * because ALL access is funneled through one place that can validate.
 */
public class EncapsulationDemo {

    public static void main(String[] args) {
        withoutEncapsulation();
        withEncapsulation();
        readOnlyField();
    }

    // -----------------------------------------------------------------
    // 1. THE PROBLEM: no encapsulation -> anyone can corrupt the object
    // -----------------------------------------------------------------
    static class BrokenAccount {
        public double balance;   // public - no protection at all
    }

    static void withoutEncapsulation() {
        System.out.println("--- without encapsulation ---");
        BrokenAccount acc = new BrokenAccount();
        acc.balance = 100;
        acc.balance = -9999;     // nothing stops this - invalid state!
        System.out.println("balance = " + acc.balance + "  <-- should never be legal");
    }

    // -----------------------------------------------------------------
    // 2. THE FIX: private field + public methods that enforce rules
    // -----------------------------------------------------------------
    static class BankAccount {
        private double balance;           // hidden from outside
        private final String owner;

        BankAccount(String owner, double openingBalance) {
            this.owner = owner;           // `this` disambiguates field vs param
            this.balance = Math.max(openingBalance, 0);
        }

        // Getter - controlled READ access
        public double getBalance() {
            return balance;
        }

        // Setter equivalent - controlled WRITE access, with validation
        public void deposit(double amount) {
            if (amount <= 0) {
                System.out.println("Rejected: deposit must be positive");
                return;
            }
            balance += amount;
        }

        public void withdraw(double amount) {
            if (amount <= 0) {
                System.out.println("Rejected: withdrawal must be positive");
                return;
            }
            if (amount > balance) {
                System.out.println("Rejected: insufficient funds");
                return;
            }
            balance -= amount;
        }

        @Override
        public String toString() {
            return owner + "'s balance = " + balance;
        }
    }

    static void withEncapsulation() {
        System.out.println("\n--- with encapsulation ---");
        BankAccount acc = new BankAccount("Prince", 100);

        // acc.balance = -9999;   // COMPILE ERROR: balance is private, can't touch it

        acc.deposit(50);
        acc.withdraw(30);
        acc.withdraw(99999);      // rejected - keeps the object always valid
        acc.deposit(-10);         // rejected

        System.out.println(acc);
    }

    // -----------------------------------------------------------------
    // 3. Read-only field: expose a getter, but no setter at all
    // -----------------------------------------------------------------
    static class Employee {
        private final String employeeId;   // set once, never changes

        Employee(String employeeId) {
            this.employeeId = employeeId;
        }

        public String getEmployeeId() {
            return employeeId;
        }
        // no setEmployeeId() on purpose -> caller can read but never modify
    }

    static void readOnlyField() {
        System.out.println("\n--- read-only field via getter-only ---");
        Employee e = new Employee("ZURU-1024");
        System.out.println("id = " + e.getEmployeeId());
        // e.employeeId = "HACK";      // COMPILE ERROR: private
        // e.setEmployeeId("HACK");    // COMPILE ERROR: method doesn't exist
    }
}
