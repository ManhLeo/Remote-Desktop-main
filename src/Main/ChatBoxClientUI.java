package Main;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatBoxClientUI extends JFrame {
    private JTextArea textArea;
    private JTextField textField;
    private PrintWriter outPrintWriter;

    public ChatBoxClientUI(String serverAddress, int port) {
        setTitle("Client ChatBox");
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
        sendButton.addActionListener(e -> sendMessage());
        textField.addActionListener(e -> sendMessage());

        try {
            Socket socket = new Socket(serverAddress, port);
            outPrintWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        setVisible(true);
    }

    private void sendMessage() {
        String message = textField.getText();
        if (message.isEmpty()) return;
        textArea.append("Me: " + message + "\n");
        if (outPrintWriter != null) {
            outPrintWriter.println(message); // Gửi tin nhắn tới server
        }
        textField.setText("");
    }

    public void showMessage(String message) {
        // Thêm "Server: " nếu chưa có tiền tố "Server:"
        if (!message.startsWith("Server: ")) {
            message = "Server: " + message;
        }
        textArea.append(message + "\n");
    }
}
