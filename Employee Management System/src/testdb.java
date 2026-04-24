import java.sql.*;

public class testdb {
    public static void main(String[] args) {
        try {
            
        Connection con = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/employee_management",
    "root",
    "roman123448"
);
            System.out.println("Connected Successfully!");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}