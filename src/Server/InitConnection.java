package Server;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import Server.History;
import Server.Local_IP;

public class InitConnection extends Thread {
    ServerSocket server = null;
    DataInputStream in = null;
    DataOutputStream out = null;
    Robot robot = null;
    Rectangle rectangle = null;
    String password;

    public InitConnection(int port, String password) {
        System.out.println("Waiting for connection");
        this.password = password;
        try {
            server = new ServerSocket(port);
            start();
        } catch (IOException ex) {
            Logger.getLogger(InitConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        try {
            GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gDev = gEnv.getDefaultScreenDevice();
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            String width = "" + dim.getWidth();
            String height = "" + dim.getHeight();
            rectangle = new Rectangle(dim);

            try {
                robot = new Robot(gDev);
            } catch (AWTException e) {
                e.printStackTrace();
            }

            while (true) {
                Socket cSocket = server.accept();
                System.out.println("Accept connection from " + cSocket.getLocalAddress());
                
                // Kiểm tra xem client có đang kết nối với chính máy của mình không
                String clientIP = cSocket.getInetAddress().getHostAddress();
                String serverIP1 = InetAddress.getLocalHost().getHostAddress();
                String serverIP2 = new Local_IP().getIPAddress();
                
                if (clientIP.equals(serverIP1) || clientIP.equals(serverIP2)) {
                    // Nếu client và server có cùng IP, từ chối kết nối
                    System.out.println("Client cannot connect to its own machine");
                    DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
                    out.writeUTF("You cannot connect to your own machine.");
                    cSocket.close();
                    continue;  // Quay lại vòng lặp chờ kết nối tiếp theo
                }

                in = new DataInputStream(cSocket.getInputStream());
                out = new DataOutputStream(cSocket.getOutputStream());
                History.LogIP(clientIP);

                String pass = in.readUTF();
                if (password.equals(pass)) {
                    out.writeUTF("valid");
                    out.writeUTF(width);
                    out.writeUTF(height);
                    new FTPServer();
                    new SendScreen(cSocket, robot, rectangle);
                    new ReceiveEvents(cSocket, robot);
                    new ServerChat(54321);
                } else {
                    out.writeUTF("invalid");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
