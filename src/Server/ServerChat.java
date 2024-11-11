package Server;
import Main.ChatBoxServerUI;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerChat {
    private static List<PrintWriter> clientWriters = new ArrayList<>();
    private static ChatBoxServerUI chatBoxUI;

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
        
        private void showMessageFromClient(String message) {
            // Kiểm tra nếu chatBoxUI đã bị đóng, khởi tạo lại nó
            if (chatBoxUI == null || !chatBoxUI.isDisplayable()) {
                chatBoxUI = new ChatBoxServerUI(clientWriters);
                chatBoxUI.setVisible(true);
            }
            chatBoxUI.showMessage(message);
        }

        
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    showMessageFromClient(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }



        // private void sendToAllClients(String message) {
        //     synchronized (clientWriters) {
        //         for (PrintWriter writer : clientWriters) {
        //             writer.println(message);
        //         }
        //     }
        // }
    }

    public static void main(String[] args) {
        int port = 54321;
        new ServerChat(port);
    }
}
