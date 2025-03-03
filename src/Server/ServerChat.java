package Server;

import Main.ChatBoxUI;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerChat {
    private static List<PrintWriter> clientWriters = new ArrayList<>();
    private static ChatBoxUI chatBoxUI;

    public ServerChat(int port) {
        new Thread(new ChatServer(port)).start();
    }

    private class ChatServer implements Runnable {
        private int port;

        public ChatServer(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    synchronized (clientWriters) {
                        clientWriters.add(out);
                    }

                    new Thread(new ClientHandler(clientSocket, out)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket, PrintWriter out) {
            this.socket = socket;
            this.out = out;
        }

        // Hàm để hiển thị tin nhắn trên chatbox của Server
        private void showMessageFromClient(String message) {
            // Kiểm tra xem chatBoxUI đã được khởi tạo chưa
            if (chatBoxUI == null || !chatBoxUI.isDisplayable()) {
                // Nếu chưa khởi tạo hoặc đã đóng, khởi tạo chatbox
                chatBoxUI = new ChatBoxUI(clientWriters, null, -1);
                chatBoxUI.setVisible(true);
            }
            chatBoxUI.showMessage(message);  // Hiển thị tin nhắn từ Client
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    showMessageFromClient(message);  // Hiển thị tin nhắn khi nhận được từ Client
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                synchronized (clientWriters) {
                    clientWriters.remove(out);  // Xóa client ra khỏi danh sách khi ngắt kết nối
                }
            }
        }
    }
}
