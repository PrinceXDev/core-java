/*
 * Constructors in Java — reference notes + runnable examples.
 *
 * - A constructor shares the class name and has no return type.
 * - It runs automatically when an object is created with `new`.
 * - If you write ANY constructor, Java stops giving you the free no-arg one.
 * - this(...) calls another constructor in the same class (constructor chaining).
 * - super(...) calls the parent class's constructor.
 * - Both this(...) and super(...), if used, must be the first statement.
 * - Constructors cannot be static, final, or abstract.
 */
public class ConstructorDemo {

    public static void main(String[] args) {
        // 1. Default constructor (no-arg)
        Employee e1 = new Employee();
        System.out.println(e1);

        // 2. Parameterized constructor
        Employee e2 = new Employee("Alex", 50000);
        System.out.println(e2);

        // 3. Constructor overloading (this(...) chaining)
        Employee e3 = new Employee("Sam");
        System.out.println(e3);

        // 4. Copy constructor (manual, Java has no built-in one)
        Employee e4 = new Employee(e2);
        System.out.println(e4);

        // 5. super(...) calling the parent class constructor
        Manager m1 = new Manager("Priya", 90000, "Engineering");
        System.out.println(m1);
    }
}

class Employee {
    String name;
    double salary;

    // Default constructor
    public Employee() {
        this("Unknown", 0.0); // chains to the parameterized constructor
    }

    // Overloaded constructor: name only, salary defaults to 0
    public Employee(String name) {
        this(name, 0.0);
    }

    // Parameterized constructor
    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    // Copy constructor
    public Employee(Employee other) {
        this(other.name, other.salary);
    }

    @Override
    public String toString() {
        return "Employee{name=" + name + ", salary=" + salary + "}";
    }
}

class Manager extends Employee {
    String department;

    public Manager(String name, double salary, String department) {
        super(name, salary); // must be the first statement
        this.department = department;
    }

    @Override
    public String toString() {
        return "Manager{name=" + name + ", salary=" + salary + ", department=" + department + "}";
    }
}
