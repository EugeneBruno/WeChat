package client;

import javax.swing.*;

public class LoginWindow extends JFrame {
    private JTextField usernameField;

    public LoginWindow() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        usernameField = new JTextField(15);
        JButton loginButton = new JButton("Login");

        add(new JLabel("Enter username:"));
        add(usernameField);
        add(loginButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (!username.isEmpty()) {
                dispose(); // close login window
                new ChatWindow(username); // ✅ launch the chat window
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a username");
            }
        });

        setVisible(true);
    }
}
