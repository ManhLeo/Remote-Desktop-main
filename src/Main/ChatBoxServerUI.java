package Main;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.List;

public class ChatBoxServerUI extends JFrame {
    private JTextArea textArea;
    private JTextField textField;
    private List<PrintWriter> clientWriters;

    public ChatBoxServerUI(List<PrintWriter> clientWriters) {
        this.clientWriters = clientWriters;

        setTitle("Server ChatBox");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Thiết lập textArea cho tin nhắn hiển thị
        textArea = new JTextArea();
        textArea.setEditable(false); // Không cho phép người dùng nhập vào textArea
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBackground(new Color(245, 245, 245));
        textArea.setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Thiết lập panel cho khu vực nhập tin nhắn và nút gửi
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(30, 144, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Sự kiện gửi tin nhắn khi nhấn nút gửi hoặc Enter
        sendButton.addActionListener(e -> sendMessageToClients());
        textField.addActionListener(e -> sendMessageToClients());
        setVisible(true);
    }

    private void sendMessageToClients() {
        String message = textField.getText();
        if (message.isEmpty()) return;
        textArea.append("Me: " + message + "\n");

        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message); // Gửi tin nhắn tới tất cả client
            }
        }
        textField.setText("");
    }

    public void showMessage(String message) {
        // Chỉ thêm "Client: " nếu tin nhắn không bắt đầu với "Me:" hoặc "Server:"
        if (!message.startsWith("Me: ") && !message.startsWith("Server: ") && !message.startsWith("Client: ")) {
            message = "Client: " + message;
        }
        textArea.append(message + "\n");
    }

}
