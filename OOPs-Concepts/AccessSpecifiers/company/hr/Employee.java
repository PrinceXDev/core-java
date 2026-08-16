package company.hr;

/*
 * Employee — lives in the company.hr package.
 *
 * This class deliberately uses all four access levels on its fields so you
 * can see, in one place, who is allowed to touch what:
 *
 *   public     -> everyone, in every package
 *   protected  -> same package, PLUS subclasses in any package
 *   (default)  -> same package ONLY (no keyword at all = "package-private")
 *   private    -> this class ONLY, nobody else, not even a subclass
 */
public class Employee {

    // PUBLIC: safe to expose everywhere - a directory/UI in any package
    // needs these to show "who is this employee".
    public String employeeId;
    public String name;

    // PROTECTED: other HR-related subclasses (even in different packages,
    // like payroll) need this to calculate pay, so it can't be private.
    // But it's not public either - random unrelated code shouldn't read raw salary.
    protected double baseSalary;

    // DEFAULT (package-private): only classes inside company.hr should see
    // internal HR notes. A Manager in the same package is trusted with this;
    // PayrollProcessor in a different package is not, even though it's a subclass.
    String internalHrNotes;

    // PRIVATE: the most sensitive field. Nobody outside this exact class -
    // not a subclass, not the same package - should ever read/write it directly.
    private String ssn;

    public Employee(String employeeId, String name, double baseSalary, String ssn) {
        this.employeeId = employeeId;
        this.name = name;
        this.baseSalary = baseSalary;
        this.ssn = ssn;
        this.internalHrNotes = "no notes yet";
    }

    // Controlled access to the private field - the ONLY way anyone outside
    // this class can ever see the SSN, and only in masked form.
    public String getMaskedSsn() {
        return "***-**-" + ssn.substring(ssn.length() - 4);
    }

    public void setInternalHrNotes(String notes) {
        this.internalHrNotes = notes;
    }
}
