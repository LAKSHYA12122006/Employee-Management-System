import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class EmployeeAppGUI extends JFrame {

    JTextField fname, lname, salary;
    JTable table;
    DefaultTableModel model;
    Connection con;

    public EmployeeAppGUI() {

        setTitle("Employee Management System");
        setSize(900,550);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Color bg = new Color(18,18,18);
        Color panelBg = new Color(28,28,28);

        getContentPane().setBackground(bg);

        // DB Connection
        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/employee_management",
                "root",
                "roman123448"
            );
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Title
        JLabel title = new JLabel("Employee Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel form = new JPanel(new GridLayout(7,1,10,10));
        form.setBackground(panelBg);
        form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        fname = createField(form, "First Name");
        lname = createField(form, "Last Name (optional)");
        salary = createField(form, "Salary");

        JButton addBtn = createBtn("Add", new Color(0,200,83));
        JButton updateBtn = createBtn("Update", new Color(33,150,243));
        JButton deleteBtn = createBtn("Delete", new Color(244,67,54));

        form.add(addBtn);
        form.add(updateBtn);
        form.add(deleteBtn);

        add(form, BorderLayout.WEST);

        // Table
        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Salary");

        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setBackground(new Color(35,35,35));
        table.setForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(45,45,45));
        header.setForeground(Color.WHITE);

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();

        // Auto fill fields
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            int row = table.getSelectedRow();
            if(row != -1){
                fname.setText(model.getValueAt(row,1).toString());

                Object ln = model.getValueAt(row,2);
                lname.setText(ln == null ? "" : ln.toString());

                salary.setText(model.getValueAt(row,3).toString());
            }
        });

        // Buttons
        addBtn.addActionListener(e -> addEmployee());
        updateBtn.addActionListener(e -> updateEmployee());
        deleteBtn.addActionListener(e -> deleteEmployee());

        setVisible(true);
    }

    JTextField createField(JPanel p, String label){
        JLabel l = new JLabel(label);
        l.setForeground(Color.WHITE);
        JTextField t = new JTextField();
        t.setBackground(new Color(50,50,50));
        t.setForeground(Color.WHITE);
        p.add(l);
        p.add(t);
        return t;
    }

    JButton createBtn(String text, Color color){
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    void loadData(){
        try{
            model.setRowCount(0);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM employees");

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("emp_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDouble("salary")
                });
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void addEmployee(){
        try{
            if(fname.getText().trim().isEmpty() || salary.getText().trim().isEmpty()){
                JOptionPane.showMessageDialog(this,"First Name & Salary required!");
                return;
            }

            double sal = Double.parseDouble(salary.getText());

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO employees(first_name,last_name,salary) VALUES(?,?,?)"
            );

            ps.setString(1,fname.getText());

            if(lname.getText().trim().isEmpty())
                ps.setNull(2,Types.VARCHAR);
            else
                ps.setString(2,lname.getText());

            ps.setDouble(3,sal);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Employee Added!");

            clearFields();
            loadData();

        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(this,"Salary must be number!");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void updateEmployee(){
        int row = table.getSelectedRow();

        if(row==-1){
            JOptionPane.showMessageDialog(this,"Select row first!");
            return;
        }

        try{
            double sal = Double.parseDouble(salary.getText());

            int id = (int)model.getValueAt(row,0);

            PreparedStatement ps = con.prepareStatement(
                "UPDATE employees SET first_name=?,last_name=?,salary=? WHERE emp_id=?"
            );

            ps.setString(1,fname.getText());

            if(lname.getText().trim().isEmpty())
                ps.setNull(2,Types.VARCHAR);
            else
                ps.setString(2,lname.getText());

            ps.setDouble(3,sal);
            ps.setInt(4,id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Employee Updated!");

            clearFields();
            loadData();

        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(this,"Enter valid salary!");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void deleteEmployee(){
        int row = table.getSelectedRow();

        if(row==-1){
            JOptionPane.showMessageDialog(this,"Select row first!");
            return;
        }

        try{
            int confirm = JOptionPane.showConfirmDialog(this,"Delete this employee?");
            if(confirm != 0) return;

            int id = (int)model.getValueAt(row,0);

            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM employees WHERE emp_id=?"
            );

            ps.setInt(1,id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Employee Deleted!");

            clearFields();
            loadData();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void clearFields(){
        fname.setText("");
        lname.setText("");
        salary.setText("");
    }

    public static void main(String[] args){
        new EmployeeAppGUI();
    }
}