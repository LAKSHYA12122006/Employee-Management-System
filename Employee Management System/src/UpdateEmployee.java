import java.sql.*;
import java.util.Scanner;

public class UpdateEmployee {
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/employee_management",
                "root",
                "roman123448"
            );

            Scanner sc = new Scanner(System.in);

            System.out.print("Enter Employee ID to update: ");
            int id = sc.nextInt();

            System.out.print("Enter new Salary: ");
            double salary = sc.nextDouble();

            PreparedStatement ps = con.prepareStatement(
                "UPDATE employees SET salary=? WHERE emp_id=?"
            );

            ps.setDouble(1, salary);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if(rows > 0) {
                System.out.println("Employee Updated Successfully!");
            } else {
                System.out.println("Employee not found!");
            }

            con.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
