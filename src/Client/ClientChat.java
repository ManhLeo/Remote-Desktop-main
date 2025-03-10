package Client;

import Main.ChatBoxUI;
import java.io.*;
import java.net.*;

public class ClientChat {
    private Socket socket;
    private PrintWriter outPrintWriter;
    private BufferedReader in;
    private ChatBoxUI chatBoxUI;

    public ClientChat(String hostString, int port) throws IOException {
        // Khởi tạo UI chat
        chatBoxUI = new ChatBoxUI(null, hostString, port); // Không cần clientWriters cho client
        socket = new Socket(hostString, port);
        outPrintWriter = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(new IncomingReader()).start();
    }

    private class IncomingReader implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    chatBoxUI.showMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
