package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ChatWindow extends JFrame {
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    private JTextField searchField;
    private JComboBox<String> userSelector;
    private JTextField inputField;
    private JPanel chatPanel;
    private Map<String, JTextArea> chatAreas = new HashMap<>();

    public ChatWindow(String username) {
        this.username = username;
        setTitle("Chat - " + username);
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔼 Top Panel
        JPanel topPanel = new JPanel();
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Open Chat");
        userSelector = new JComboBox<>();
        topPanel.add(new JLabel("To:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(userSelector);
        add(topPanel, BorderLayout.NORTH);

        // 🧩 Chat Area Panel (CardLayout)
        chatPanel = new JPanel(new CardLayout());
        add(chatPanel, BorderLayout.CENTER);

        // 🔽 Input Panel
        JPanel inputPanel = new JPanel();
        inputField = new JTextField(30);
        JButton sendBtn = new JButton("Send");
        inputPanel.add(inputField);
        inputPanel.add(sendBtn);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);

        // 🌐 Connect to server
        try {
            Socket socket = new Socket("localhost", 12345); // Change to IP for LAN
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Send username
            in.readLine(); // "Enter username:"
            out.println(username);
            in.readLine(); // "Welcome ..."

            // Start receiving messages
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        if (msg.contains(":")) {
                            final String msgCopy = msg; // ✅ Make a final copy
                            String from = msg.split(":")[0].trim();
                            SwingUtilities.invokeLater(() -> {
                                JTextArea area = getOrCreateChatArea(from);
                                area.append(msgCopy + "\n");
                    
                                if (((DefaultComboBoxModel<String>) userSelector.getModel()).getIndexOf(from) == -1) {
                                    userSelector.addItem(from);
                                }
                            });
                        }
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not connect to server.");
            System.exit(1);
        }

        // 🔍 Open Chat Button
        searchBtn.addActionListener(e -> {
            String target = searchField.getText().trim();
            if (!target.isEmpty()) {
                getOrCreateChatArea(target);
                if (((DefaultComboBoxModel<String>) userSelector.getModel()).getIndexOf(target) == -1) {
                    userSelector.addItem(target);
                }
                userSelector.setSelectedItem(target);
                ((CardLayout) chatPanel.getLayout()).show(chatPanel, target);
            }
        });

        // 🔁 Switch between chats
        userSelector.addActionListener(e -> {
            String selectedUser = (String) userSelector.getSelectedItem();
            if (selectedUser != null) {
                ((CardLayout) chatPanel.getLayout()).show(chatPanel, selectedUser);
            }
        });

        // 📨 Send Button
        sendBtn.addActionListener(e -> {
            String target = (String) userSelector.getSelectedItem();
            String msg = inputField.getText().trim();
            if (target != null && !msg.isEmpty()) {
                out.println("SEND " + target + " " + msg);
                JTextArea area = getOrCreateChatArea(target);
                area.append("Me: " + msg + "\n");
                inputField.setText("");
            }
        });
    }

    // 📦 Create chat area if not exists
    private JTextArea getOrCreateChatArea(String user) {
        if (!chatAreas.containsKey(user)) {
            JTextArea area = new JTextArea(15, 40);
            area.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(area);
            chatPanel.add(scrollPane, user);
            chatAreas.put(user, area);
        }
        return chatAreas.get(user);
    }
}
