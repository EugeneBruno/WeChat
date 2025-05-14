package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ChatWindow extends JFrame {
    private String username;
    private BufferedReader in;
    private PrintWriter out;
    private JTextField inputField;
    private JButton sendButton;
    private JComboBox<String> userSelector;
    private JPanel chatPanel;
    private HashMap<String, JTextArea> chatAreas = new HashMap<>();

    public ChatWindow(String username) throws IOException {
        this.username = username;
        Socket socket = new Socket("localhost", 12345);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Send the username to the server
        out.println(username);

        setTitle("Chat - " + username);
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top: Search bar and combo box
        JPanel topPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(10);
        JButton openChatButton = new JButton("Open Chat");
        userSelector = new JComboBox<>();
        topPanel.add(new JLabel("Search User:"));
        topPanel.add(searchField);
        topPanel.add(openChatButton);
        topPanel.add(userSelector);

        // Center: Card layout to switch between chat areas
        chatPanel = new JPanel(new CardLayout());

        // Bottom: Input field and send button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(chatPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action for opening a chat
        openChatButton.addActionListener(e -> {
            String user = searchField.getText().trim();
            if (!user.isEmpty() && !user.equals(username)) {
                getOrCreateChatArea(user);
                if (((DefaultComboBoxModel<String>) userSelector.getModel()).getIndexOf(user) == -1) {
                    userSelector.addItem(user);
                }
                userSelector.setSelectedItem(user);
            }
        });

        // Action for sending a message
        sendButton.addActionListener(e -> {
            String message = inputField.getText().trim();
            String recipient = (String) userSelector.getSelectedItem();
            if (!message.isEmpty() && recipient != null && !recipient.equals(username)) {
                out.println("SEND " + recipient + " " + message); // ✅ Correct format
                JTextArea area = getOrCreateChatArea(recipient);
                area.append("Me: " + message + "\n");
                inputField.setText("");
            }
        });

        // Thread to read incoming messages
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    final String finalMsg = msg;
                    SwingUtilities.invokeLater(() -> {
                        if (finalMsg.contains(":")) {
                            String from = finalMsg.split(":")[0].trim();
                            JTextArea area = getOrCreateChatArea(from);
                            area.append(finalMsg + "\n");
                            if (((DefaultComboBoxModel<String>) userSelector.getModel()).getIndexOf(from) == -1) {
                                userSelector.addItem(from);
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        setVisible(true);
    }

    private JTextArea getOrCreateChatArea(String user) {
        if (!chatAreas.containsKey(user)) {
            JTextArea area = new JTextArea();
            area.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(area);
            chatPanel.add(scrollPane, user);
            chatAreas.put(user, area);
        }
        CardLayout cl = (CardLayout) chatPanel.getLayout();
        cl.show(chatPanel, user);
        return chatAreas.get(user);
    }
}
