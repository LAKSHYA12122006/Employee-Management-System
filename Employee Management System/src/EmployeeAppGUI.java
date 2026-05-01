import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class EmployeeAppGUI extends JFrame {

    JTextField fname, lname, salary, email, phone, joiningDate, search;
    JComboBox<String> department;
    JTable table;
    DefaultTableModel model;
    Connection con;

    JLabel totalEmp, itCount, hrCount, financeCount;

    int currentPage = 1;
    int rowsPerPage = 10;
    int totalRows = 0;

    JButton prevBtn, nextBtn;
    JLabel pageLabel;

    public EmployeeAppGUI() {

        setTitle("Employee Management System");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Color bg = new Color(18,18,18);
        Color panelBg = new Color(28,28,28);
        getContentPane().setBackground(bg);

        con = DBConnection.getConnection();

        // ===== TITLE =====
        JLabel title = new JLabel("Employee Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        // ===== DASHBOARD =====
        JPanel dashboard = new JPanel(new GridLayout(1,4,10,10));
        dashboard.setBackground(bg);
        dashboard.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        totalEmp = createCard("Total");
        itCount = createCard("IT");
        hrCount = createCard("HR");
        financeCount = createCard("Finance");

        // colors
        totalEmp.setBackground(new Color(76,175,80));
        itCount.setBackground(new Color(33,150,243));
        hrCount.setBackground(new Color(255,152,0));
        financeCount.setBackground(new Color(156,39,176));

        dashboard.add(totalEmp);
        dashboard.add(itCount);
        dashboard.add(hrCount);
        dashboard.add(financeCount);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(bg);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(dashboard, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // ===== FORM =====
        JPanel form = new JPanel(new GridLayout(12,1,8,8));
        form.setBackground(panelBg);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Employee Details",
                0,0,new Font("Segoe UI",Font.BOLD,14),Color.WHITE
        ));
        form.setPreferredSize(new Dimension(260,500));

        fname = createField(form, "First Name");
        lname = createField(form, "Last Name");
        salary = createField(form, "Salary");
        email = createField(form, "Email");
        phone = createField(form, "Phone");

        JLabel dLabel = new JLabel("Department");
        dLabel.setForeground(Color.WHITE);
        department = new JComboBox<>(new String[]{"IT","HR","Finance","Marketing","Sales"});
        form.add(dLabel);
        form.add(department);

        joiningDate = createField(form, "Joining Date (YYYY-MM-DD)");

        JButton addBtn = createBtn("Add", new Color(0,200,83));
        JButton updateBtn = createBtn("Update", new Color(33,150,243));
        JButton deleteBtn = createBtn("Delete", new Color(244,67,54));
        JButton clearBtn = createBtn("Clear", new Color(120,120,120));
        JButton graphBtn = createBtn("Show Graphs", new Color(255,152,0));

        form.add(addBtn);
        form.add(updateBtn);
        form.add(deleteBtn);
        form.add(clearBtn);
        form.add(graphBtn);

        add(form, BorderLayout.WEST);

        // ===== TABLE =====
        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Salary");
        model.addColumn("Email");
        model.addColumn("Phone");
        model.addColumn("Department");
        model.addColumn("Joining Date");

        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(70,130,180));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(45,45,45));
        header.setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // ===== SEARCH =====
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(bg);

        search = new JTextField();
        search.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        search.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(search, BorderLayout.CENTER);

        add(searchPanel, BorderLayout.SOUTH);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) {}
            void filter() {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + search.getText()));
            }
        });

        // ===== PAGINATION =====
        JPanel paginationPanel = new JPanel();
        paginationPanel.setBackground(bg);

        prevBtn = new JButton("Previous");
        nextBtn = new JButton("Next");
        pageLabel = new JLabel("Page 1");
        pageLabel.setForeground(Color.WHITE);

        paginationPanel.add(prevBtn);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextBtn);

        add(paginationPanel, BorderLayout.PAGE_END);

        // ===== LOAD =====
        getTotalRows();
        loadData();
        loadDashboard();

        // ===== ACTIONS =====
        addBtn.addActionListener(e -> addEmployee());
        updateBtn.addActionListener(e -> updateEmployee());
        deleteBtn.addActionListener(e -> deleteEmployee());
        clearBtn.addActionListener(e -> clearFields());

        graphBtn.addActionListener(e -> {
            try {
                new ProcessBuilder("python", System.getProperty("user.dir")+"\\graphs.py")
                        .inheritIO().start();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        prevBtn.addActionListener(e -> {
            if(currentPage > 1){ currentPage--; loadData(); }
        });

        nextBtn.addActionListener(e -> {
            if(currentPage * rowsPerPage < totalRows){ currentPage++; loadData(); }
        });

        setVisible(true);
    }

    JTextField createField(JPanel p, String label){
        JLabel l = new JLabel(label);
        l.setForeground(Color.WHITE);
        JTextField t = new JTextField();
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

    JLabel createCard(String text){
        JLabel lbl = new JLabel(text + ": 0", JLabel.CENTER);
        lbl.setOpaque(true);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(BorderFactory.createEmptyBorder(15,10,15,10));
        return lbl;
    }

    void loadDashboard(){
        try{
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM employees");
            if(rs.next()) totalEmp.setText("Total: " + rs.getInt(1));

            rs = st.executeQuery("SELECT COUNT(*) FROM employees WHERE department='IT'");
            if(rs.next()) itCount.setText("IT: " + rs.getInt(1));

            rs = st.executeQuery("SELECT COUNT(*) FROM employees WHERE department='HR'");
            if(rs.next()) hrCount.setText("HR: " + rs.getInt(1));

            rs = st.executeQuery("SELECT COUNT(*) FROM employees WHERE department='Finance'");
            if(rs.next()) financeCount.setText("Finance: " + rs.getInt(1));

        }catch(Exception e){ e.printStackTrace(); }
    }

    void getTotalRows(){
        try{
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM employees");
            if(rs.next()) totalRows = rs.getInt(1);
        }catch(Exception e){ e.printStackTrace(); }
    }

    void loadData(){
        try{
            model.setRowCount(0);
            int offset = (currentPage - 1) * rowsPerPage;

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM employees LIMIT ? OFFSET ?"
            );
            ps.setInt(1, rowsPerPage);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getDate(8)
                });
            }

            pageLabel.setText("Page " + currentPage);

        }catch(Exception e){ e.printStackTrace(); }
    }

    void addEmployee(){
        try{
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO employees(first_name,last_name,salary,email,phone,department,joining_date) VALUES(?,?,?,?,?,?,?)"
            );

            ps.setString(1, fname.getText());
            ps.setString(2, lname.getText());
            ps.setDouble(3, Double.parseDouble(salary.getText()));
            ps.setString(4, email.getText());
            ps.setString(5, phone.getText());
            ps.setString(6, department.getSelectedItem().toString());
            ps.setDate(7, Date.valueOf(joiningDate.getText()));

            ps.executeUpdate();
            getTotalRows(); loadData(); loadDashboard();

        }catch(Exception e){ e.printStackTrace(); }
    }

    void updateEmployee(){
        int row = table.getSelectedRow();
        if(row == -1) return;

        try{
            int id = (int)model.getValueAt(row,0);

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE employees SET first_name=?,last_name=?,salary=?,email=?,phone=?,department=?,joining_date=? WHERE emp_id=?"
            );

            ps.setString(1, fname.getText());
            ps.setString(2, lname.getText());
            ps.setDouble(3, Double.parseDouble(salary.getText()));
            ps.setString(4, email.getText());
            ps.setString(5, phone.getText());
            ps.setString(6, department.getSelectedItem().toString());
            ps.setDate(7, Date.valueOf(joiningDate.getText()));
            ps.setInt(8, id);

            ps.executeUpdate();
            loadData(); loadDashboard();

        }catch(Exception e){ e.printStackTrace(); }
    }

    void deleteEmployee(){
        int row = table.getSelectedRow();
        if(row == -1) return;

        try{
            int id = (int)model.getValueAt(row,0);

            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM employees WHERE emp_id=?"
            );
            ps.setInt(1, id);
            ps.executeUpdate();

            getTotalRows(); loadData(); loadDashboard();

        }catch(Exception e){ e.printStackTrace(); }
    }

    void clearFields(){
        fname.setText("");
        lname.setText("");
        salary.setText("");
        email.setText("");
        phone.setText("");
        joiningDate.setText("");
        department.setSelectedIndex(0);
    }
}