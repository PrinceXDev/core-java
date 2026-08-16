/*
 * Inheritance — reusing and specializing behaviour
 * ---------------------------------------------------
 * A subclass (child) `extends` a superclass (parent) and automatically
 * gets its non-private fields/methods, then adds its own or overrides
 * the parent's.
 *
 * Relationship to remember: subclass IS-A superclass.
 *   Car IS-A Vehicle  -> inheritance makes sense
 *   Car HAS-A Engine  -> that's composition, NOT inheritance (different tool)
 */
public class InheritanceDemo {

    public static void main(String[] args) {
        basicInheritance();
        constructorChaining();
        methodOverridingVsHiding();
        multiLevelInheritance();
    }

    // -----------------------------------------------------------------
    // 1. Basic inheritance: shared code lives once, in the parent
    // -----------------------------------------------------------------
    static class Vehicle {
        protected String brand;     // protected: visible to subclasses too
        private int wheels;

        Vehicle(String brand, int wheels) {
            this.brand = brand;
            this.wheels = wheels;
        }

        void start() {
            System.out.println(brand + " vehicle starting (" + wheels + " wheels)...");
        }
    }

    static class Car extends Vehicle {     // Car IS-A Vehicle
        Car(String brand) {
            super(brand, 4);                // must call parent constructor first
        }

        void honk() {                       // Car-specific behaviour
            System.out.println(brand + " says: Beep beep!");
        }
    }

    static void basicInheritance() {
        System.out.println("--- basic inheritance ---");
        Car car = new Car("Toyota");
        car.start();   // inherited from Vehicle
        car.honk();    // defined in Car
    }

    // -----------------------------------------------------------------
    // 2. Constructor chaining: super(...) must be the FIRST statement
    // -----------------------------------------------------------------
    static class Person {
        String name;
        Person(String name) {
            this.name = name;
            System.out.println("Person constructor: " + name);
        }
    }

    static class Student extends Person {
        String course;
        Student(String name, String course) {
            super(name);                    // calls Person(name) first
            this.course = course;
            System.out.println("Student constructor: " + course);
        }
    }

    static void constructorChaining() {
        System.out.println("\n--- constructor chaining ---");
        new Student("Asha", "Computer Science");
        // Output order proves Parent constructor runs BEFORE child constructor body
    }

    // -----------------------------------------------------------------
    // 3. Overriding (runtime, uses @Override) vs hiding static methods
    // -----------------------------------------------------------------
    static class Animal {
        void sound() { System.out.println("Animal makes a sound"); }
        static void staticInfo() { System.out.println("Animal.staticInfo()"); }
    }

    static class Dog extends Animal {
        @Override                            // instance method -> true overriding
        void sound() { System.out.println("Dog barks"); }

        static void staticInfo() { System.out.println("Dog.staticInfo()"); } // hides, doesn't override
    }

    static void methodOverridingVsHiding() {
        System.out.println("\n--- overriding vs hiding ---");
        Animal a = new Dog();          // reference type Animal, actual object Dog
        a.sound();                     // "Dog barks" -> decided at RUNTIME (polymorphism)
        a.staticInfo();                // "Animal.staticInfo()" -> decided at COMPILE TIME (reference type)
        // Gotcha: static methods are NOT polymorphic - they belong to the class, not the object.
    }

    // -----------------------------------------------------------------
    // 4. Multi-level inheritance: A -> B -> C (Java allows chains,
    //    just not multiple direct parents for classes)
    // -----------------------------------------------------------------
    static class LivingBeing {
        void breathe() { System.out.println("Breathing..."); }
    }
    static class AnimalBase extends LivingBeing {
        void eat() { System.out.println("Eating..."); }
    }
    static class Cat extends AnimalBase {   // Cat -> AnimalBase -> LivingBeing
        void meow() { System.out.println("Meow!"); }
    }

    static void multiLevelInheritance() {
        System.out.println("\n--- multi-level inheritance ---");
        Cat cat = new Cat();
        cat.breathe();   // from LivingBeing (grandparent)
        cat.eat();       // from AnimalBase (parent)
        cat.meow();      // from Cat itself
    }
}
