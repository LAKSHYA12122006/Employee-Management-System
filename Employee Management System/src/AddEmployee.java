import java.sql.*;
import java.util.Scanner;

public class AddEmployee {
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/employee_management",
                "root",
                "roman123448"
            );

            Scanner sc = new Scanner(System.in);

            System.out.print("Enter First Name: ");
            String fname = sc.nextLine();

            System.out.print("Enter Last Name: ");
            String lname = sc.nextLine();

            System.out.print("Enter Salary: ");
            double salary = sc.nextDouble();

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO employees (first_name, last_name, salary) VALUES (?, ?, ?)"
            );

            ps.setString(1, fname);
            ps.setString(2, lname);
            ps.setDouble(3, salary);

            ps.executeUpdate();

            System.out.println("Employee Added Successfully!");

            con.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
