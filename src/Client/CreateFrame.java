package Client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.beans.PropertyVetoException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import java.net.Socket;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;

class CreateFrame extends Thread {
    String width = "", height = "";
    private JFrame frame = new JFrame();
    private JDesktopPane desktop = new JDesktopPane();
    private Socket cSocket = null;
    private JInternalFrame interFrame = new JInternalFrame("Server Screen", true, true, true);
    private JPanel cPanel = new JPanel();
    InputStream in = null;
    ReceivingScreen receivingScreen;


    private void createCustomMenuBar() {
        JPanel menuBarPanel = new JPanel();
        menuBarPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Add custom buttons to simulate menu items
        JButton scButton = new JButton("Capture Screen");
        JButton ftpButton = new JButton("File Transfer");
        JButton cButton = new JButton("Chat Box");

        ftpButton.addActionListener(e -> {
            new FTPClient(cSocket.getInetAddress());
        });

        scButton.addActionListener(e -> {
            receivingScreen.saveImageWithFileChooser();
        });

        cButton.addActionListener(e -> {
            try {
                /*ClientChat clientChat = */new ClientChat(cSocket.getInetAddress().getHostAddress(), 54321);
            } catch (IOException ex) {
                Logger.getLogger(CreateFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        menuBarPanel.add(ftpButton);
        menuBarPanel.add(scButton);
        menuBarPanel.add(cButton);

        frame.add(menuBarPanel, BorderLayout.NORTH);
    }

    public CreateFrame(Socket cSocket, String width, String height) {
        this.width = width;
        this.height = height;
        this.cSocket = cSocket;
        start();
    }

    public void drawGUI() {
        frame.add(desktop, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createCustomMenuBar();

        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        interFrame.setLayout(new BorderLayout());
        interFrame.getContentPane().add(cPanel, BorderLayout.CENTER);
        interFrame.setSize(800, 600);
        desktop.add(interFrame);

        try {
            interFrame.setMaximum(true);
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }

        cPanel.setFocusable(true);
        interFrame.setVisible(true);
        URL iconURL = CreateFrame.class.getResource("/Main/icon.png");
        ImageIcon icon = new ImageIcon(iconURL);
        frame.setIconImage(icon.getImage());
    }

    @Override
    public void run() {
        drawGUI();

        try {
            in = cSocket.getInputStream(); // Lệnh này ném IOException
        } catch (IOException e) {
            e.printStackTrace();
        }

        receivingScreen = new ReceivingScreen(in, cPanel);
        new SendEvents(cSocket, cPanel, width, height);

        
    }


}
