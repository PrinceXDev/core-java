import java.util.ArrayList;
import java.util.List;

class Employee {
    private String name;

    public Employee(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Department {
    private String deptName;
    private List<Employee> employees; // aggregation: Department "has" Employees

    public Department(String deptName, List<Employee> employees) {
        this.deptName = deptName;
        this.employees = employees;
    }

    public void showEmployees() {
        System.out.println("Department: " + deptName);
        for (Employee e : employees) {
            System.out.println(" - " + e.getName());
        }
    }
}

public class AggregationDemo {
    public static void main(String[] args) {
        Employee e1 = new Employee("Alice");
        Employee e2 = new Employee("Bob");

        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(e1);
        employeeList.add(e2);

        Department dept = new Department("Engineering", employeeList);
        dept.showEmployees();

        // Employees still exist independently even if 'dept' is discarded
        Department otherDept = new Department("Marketing", employeeList);
    }
}