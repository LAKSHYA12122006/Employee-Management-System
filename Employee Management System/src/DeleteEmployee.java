import java.sql.*;
import java.util.Scanner;

public class DeleteEmployee {
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/employee_management",
                "root",
                "roman123448"
            );

            Scanner sc = new Scanner(System.in);

            System.out.print("Enter Employee ID to delete: ");
            int id = sc.nextInt();

            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM employees WHERE emp_id=?"
            );

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if(rows > 0) {
                System.out.println("Employee Deleted Successfully!");
            } else {
                System.out.println("Employee not found!");
            }

            con.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}