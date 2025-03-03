package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Authentication {
    private DataInputStream dataIn = null;
    private DataOutputStream password = null;
    String verify = "";
    String width = "", height = "";

    public void Verify(Socket cSocket, String serverIP, String serverPassword) {
        try {  
            // Tạo các luồng nhập/xuất
            dataIn = new DataInputStream(cSocket.getInputStream());
            password = new DataOutputStream(cSocket.getOutputStream());
            
            // Gửi mật khẩu tới server
            password.writeUTF(serverPassword);
            
            // Nhận thông báo từ server
            verify = dataIn.readUTF();  // Nhận phản hồi về việc xác thực
            
            // Kiểm tra thông báo từ server
            if (verify.equals("valid")) {
                // Nếu xác thực thành công, nhận thông tin kích thước màn hình
                width = dataIn.readUTF();
                height = dataIn.readUTF();
                
                // Tạo cửa sổ điều khiển cho client sau khi xác thực thành công
                CreateFrame abc = new CreateFrame(cSocket, width, height);
            } else if (verify.equals("invalid")) {
                // Nếu mật khẩu sai, thông báo lỗi
                System.out.println("Incorrect password");
                JOptionPane.showMessageDialog(null, "Incorrect password", "Wrong password", JOptionPane.ERROR_MESSAGE);
            } else if (verify.equals("You cannot connect to your own machine.")) {
                // Nếu client cố gắng kết nối vào chính máy của mình
                System.out.println("Cannot connect to your own machine");
                JOptionPane.showMessageDialog(null, "You cannot connect to your own machine.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Thông báo lỗi nếu có thông tin bất ngờ từ server
                System.out.println("Unexpected response from server: " + verify);
            }
        } catch (IOException ex) {
            Logger.getLogger(Authentication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
