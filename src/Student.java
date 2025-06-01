
public class Student {

    // Class attributes
    private final String name;
    private final int age;
    private final String major;

    // Constructor 1:
    public Student() {
        this.name = "Unknown";
        this.age = 0;
        this.major = "Undeclared";
    }

    // Constructor 2:
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
        this.major = "Undeclared";
    }

    // Constructor 3:
    public Student(String name, int age, String major) {
        this.name = name;
        this.age = age;
        this.major = major;
    }

    // Method to display student details
    public void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Major: " + major);
    }

    // Main method to test the class
    public static void main(String[] args) {
        // Using default constructor
        Student student1 = new Student();
        System.out.println("Student 1 Details:");
        student1.displayInfo();

        System.out.println();

        // constructor name and age
        Student student2 = new Student("Nick", 20);
        System.out.println("Student 2 Details:");
        student2.displayInfo();

        System.out.println();

        // Using constructor all attributes
        Student student3 = new Student("Rick", 22, "Computer Science");
        System.out.println("Student 3 Details:");
        student3.displayInfo();
    }
}
