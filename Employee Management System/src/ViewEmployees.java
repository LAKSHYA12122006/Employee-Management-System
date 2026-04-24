import java.sql.*;

public class ViewEmployees {
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/employee_management",
                "root",
                "roman123448"
            );

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM employees");

            while(rs.next()) {
                System.out.println(
                    rs.getInt("emp_id") + " " +
                    rs.getString("first_name") + " " +
                    rs.getDouble("salary")
                );
            }

            con.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
