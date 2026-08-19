interface Payable {
    double calculatePayment();
}

class Employee implements Payable {
    private double salary;

    Employee(double salary) {
        this.salary = salary;
    }

    @Override
    public double calculatePayment() {
        return salary;
    }
}

class Contractor implements Payable {
    private double hourlyRate;
    private int hoursWorked;

    Contractor(double hourlyRate, int hoursWorked) {
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    @Override
    public double calculatePayment() {
        return hourlyRate * hoursWorked;
    }
}

public class Demo {
    public static void main(String[] args) {
        Payable[] people = {
                new Employee(5000),
                new Contractor(50, 120)
        };

        for (Payable p : people) {
            System.out.println("Payment: " + p.calculatePayment());
        }
    }
}