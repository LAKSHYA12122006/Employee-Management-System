import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginGUI extends JFrame {

    JTextField username;
    JPasswordField password;
    JCheckBox showPassword;
    Connection con;

    public LoginGUI() {

        setTitle("Login");
        setSize(400,300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Color bg = new Color(18,18,18);
        Color panelBg = new Color(28,28,28);

        getContentPane().setBackground(bg);

        con = DBConnection.getConnection();

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(panelBg);
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== TITLE =====
        JLabel title = new JLabel("Login", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        // ===== USERNAME =====
        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(userLabel, gbc);

        username = new JTextField(15);
        styleField(username);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(username, gbc);

        // ===== PASSWORD =====
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(passLabel, gbc);

        password = new JPasswordField(15);
        styleField(password);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(password, gbc);

        // ===== SHOW PASSWORD CHECKBOX 👁 =====
        showPassword = new JCheckBox("Show Password");
        showPassword.setForeground(Color.WHITE);
        showPassword.setBackground(panelBg);

        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(showPassword, gbc);

        // Toggle logic
        showPassword.addActionListener(e -> {
            if(showPassword.isSelected()){
                password.setEchoChar((char)0); // show
            } else {
                password.setEchoChar('•'); // hide
            }
        });

        // ===== LOGIN BUTTON =====
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(33,150,243));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        add(panel);

        // ===== ACTION =====
        loginBtn.addActionListener(e -> login());

        setVisible(true);
    }

    // ===== STYLE =====
    void styleField(JTextField field){
        field.setBackground(new Color(50,50,50));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    }

    // ===== LOGIN FUNCTION =====
    void login() {
        try {
            String user = username.getText().trim();
            String pass = new String(password.getPassword()).trim();

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE username=? AND password=?"
            );

            ps.setString(1, user);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                new EmployeeAppGUI();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials!");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}