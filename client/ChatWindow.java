package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ChatWindow extends JFrame {
    private final String username;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final JComboBox<String> userSelector;
    private final JTextField messageInput;
    private final JPanel chatPanelContainer;
    private final CardLayout cardLayout;
    private final Map<String, JTextArea> chatAreas = new HashMap<>();
    private final Map<String, JLabel> typingLabels = new HashMap<>();

    public ChatWindow(String username) throws IOException {
        this.username = username;
        this.socket = new Socket("localhost", 12345);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);

        setTitle("Chat - " + username);
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        out.println(username);

        userSelector = new JComboBox<>();
        JTextField searchField = new JTextField(10);
        JButton openChatBtn = new JButton("Open Chat");

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Search User:"));
        topPanel.add(searchField);
        topPanel.add(openChatBtn);
        topPanel.add(userSelector);
        add(topPanel, BorderLayout.NORTH);

        chatPanelContainer = new JPanel();
        cardLayout = new CardLayout();
        chatPanelContainer.setLayout(cardLayout);
        add(chatPanelContainer, BorderLayout.CENTER);

        messageInput = new JTextField();
        add(messageInput, BorderLayout.SOUTH);

        openChatBtn.addActionListener(e -> {
            String user = searchField.getText().trim();
            if (!user.isEmpty()) {
                if (((DefaultComboBoxModel<String>) userSelector.getModel()).getIndexOf(user) == -1) {
                    userSelector.addItem(user);
                }
                cardLayout.show(chatPanelContainer, user);
                userSelector.setSelectedItem(user);
                getOrCreateChatArea(user);
            }
        });

        userSelector.addActionListener(e -> {
            String selectedUser = (String) userSelector.getSelectedItem();
            if (selectedUser != null) {
                cardLayout.show(chatPanelContainer, selectedUser);
            }
        });

        messageInput.addActionListener(e -> {
            String selectedUser = (String) userSelector.getSelectedItem();
            String msg = messageInput.getText().trim();
            if (!msg.isEmpty() && selectedUser != null) {
                out.println(selectedUser + ":" + msg);
                getOrCreateChatArea(selectedUser).append("Me: " + msg + "\n");
                messageInput.setText("");
            }
        });

        messageInput.addKeyListener(new KeyAdapter() {
            private long lastSent = 0;

            @Override
            public void keyPressed(KeyEvent e) {
                long now = System.currentTimeMillis();
                if (now - lastSent > 1000) {
                    String selectedUser = (String) userSelector.getSelectedItem();
                    if (selectedUser != null) {
                        out.println("TYPING:" + username + ":" + selectedUser);
                        lastSent = now;
                    }
                }
            }
        });

        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.startsWith("TYPING:")) {
                        String[] parts = msg.split(":", 3);
                        String from = parts[1];
                        String to = parts[2];
                        if (to.equals(username)) {
                            SwingUtilities.invokeLater(() -> {
                                JLabel label = typingLabels.get(from);
                                if (label != null) {
                                    label.setText(from + " is typing...");
                                    Timer timer = new Timer(2500, ev -> label.setText(" "));
                                    timer.setRepeats(false);
                                    timer.start();
                                }
                            });
                        }
                    } else if (msg.contains(":")) {
                        String from = msg.split(":")[0].trim();
                        final String finalMsg = msg; // Declare finalMsg here
                        SwingUtilities.invokeLater(() -> {
                            JTextArea area = getOrCreateChatArea(from);
                            area.append(finalMsg + "\n"); // Use finalMsg instead of msg
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

        setVisible(true);
    }

    private JTextArea getOrCreateChatArea(String user) {
        if (!chatAreas.containsKey(user)) {
            JPanel panel = new JPanel(new BorderLayout());
            JTextArea area = new JTextArea();
            area.setEditable(false);
            panel.add(new JScrollPane(area), BorderLayout.CENTER);

            JLabel typingLabel = new JLabel(" ");
            typingLabel.setFont(new Font("Arial", Font.ITALIC, 10));
            panel.add(typingLabel, BorderLayout.SOUTH);

            chatPanelContainer.add(panel, user);
            chatAreas.put(user, area);
            typingLabels.put(user, typingLabel);
        }
        return chatAreas.get(user);
    }
}
