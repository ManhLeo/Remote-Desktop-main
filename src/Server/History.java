/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class History {

    public static void LogIP(String clientIP) {
        // Sử dụng đường dẫn tương đối .\connection_logs.txt
        String logFilePath = ".\\src\\Server\\connection_logs.txt";  // Lưu file vào thư mục hiện tại

        // Lấy thời gian hiện tại theo định dạng ngày giờ dễ đọc
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());

        // Tạo đối tượng BufferedWriter để ghi file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            // Ghi IP của client cùng với thời gian
            writer.write("Client IP: " + clientIP + " - Date_Time: " + timestamp + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
