
/**
 *
 * @author Anurag
 */
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class Client extends Thread {

    final JTextArea jtx = new JTextArea();
    final JTextArea jtxm = new JTextArea();
    private static String serverName;
    private static int PORT;
    private static String name;
    BufferedReader input;
    BufferedReader br;
    PrintWriter output;
    Socket server;
    boolean readContinue;

    public Client() {
        this.readContinue = true;
        this.serverName = "localhost";
        this.PORT = 7520;
        this.name = "";

        Font font = new Font("Times New Roman", Font.PLAIN, 16);

        final JFrame jfr = new JFrame("Anurag's Chat");
        jfr.getContentPane().setLayout(null);
        jfr.setSize(700, 500);
        jfr.setResizable(false);
        jfr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jtx.setBounds(25, 25, 750, 350);
        jtx.setEditable(false);
        jtx.setFont(font);
        jtx.setMargin(new Insets(6, 6, 6, 6));
        jtx.setWrapStyleWord(true);
        JScrollPane jsp = new JScrollPane(jtx);
        jsp.setBounds(25, 25, 650, 250);

        jtxm.setBounds(0, 300, 400, 200);
        jtxm.setFont(font);
        jtxm.setMargin(new Insets(6, 6, 6, 6));
        jtxm.setWrapStyleWord(true);
        final JScrollPane jspm = new JScrollPane(jtxm);
        jspm.setBounds(25, 300, 650, 100);
        final JButton jsbtn = new JButton("SEND");
        jsbtn.setBounds(575, 410, 100, 35);

        jtxm.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                    jtx.setCaretPosition(jtx.getDocument().getLength());
                }
            }
        });

        jsbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                sendMessage();
            }
        });

        final JTextField jtfName = new JTextField("NAME");
        final JTextField jtfPass = new JPasswordField("PASS");
        final JTextField jtfAddr = new JTextField("localhost");
        final JButton jcbtn = new JButton("Connect");
        jtfAddr.setBounds(25, 300, 135, 40);
        jtfName.setBounds(200, 300, 135, 40);
        jtfPass.setBounds(375, 300, 135, 40);
        jcbtn.setBounds(575, 300, 100, 40);

        jcbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    name = jtfName.getText();
                    String pass = jtfPass.getText();
                    serverName = jtfAddr.getText();
                    if (name.equals("")) {
                        JOptionPane.showMessageDialog(jfr, "Please enter name.");
                        return;
                    }
                    if (name.length() > 8) {
                        JOptionPane.showMessageDialog(jfr, "Max length of name is 8 characters.");
                        return;
                    }
                    if (pass.equals("")) {
                        JOptionPane.showMessageDialog(jfr, "Please enter password.");
                        return;
                    }
                    if (serverName.equals("")) {
                        JOptionPane.showMessageDialog(jfr, "Please enter servername.");
                        return;
                    }
                    jtx.append("Connecting to " + serverName + " on port " + PORT + "...");
                    server = new Socket(serverName, PORT);
                    jtx.append("\nConnected to " + server.getRemoteSocketAddress());
                    jtx.append("\nAuthenticating User...\n");
                    input = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    output = new PrintWriter(server.getOutputStream(), true);
                    br = new BufferedReader(new InputStreamReader(System.in));
                    output.println(name);
                    output.println(pass);
                    String authStr = input.readLine();
//                    System.out.println(authStr);
                    if (authStr.equals("added")) {
                        JOptionPane.showMessageDialog(jfr, "User Added.");
                    }
                    authStr = input.readLine();
//                    System.out.println(authStr);
                    if (authStr.equals("error")) {
                        JOptionPane.showMessageDialog(jfr, "Incorrect username/password.");
                        return;
                    }
                    jtx.append("\nWelcome " + name);
                    jfr.addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                            try {
                                output.println("~<N!_/_!D>~" + name + " left.");
                                System.exit(0);
//                                server.close();
                            } catch (Exception ex) {
                                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                    Thread r = new Read();
                    r.start();
                    jfr.remove(jtfName);
                    jfr.remove(jtfPass);
                    jfr.remove(jtfAddr);
                    jfr.remove(jcbtn);
                    jfr.add(jsbtn);
                    jfr.add(jspm);
                    jfr.revalidate();
                    jfr.repaint();
                } catch (Exception ex) {
                    jtx.append("\nCould not connect to Server");
                    JOptionPane.showMessageDialog(jfr, ex.getMessage());
                }
            }
        });

        jfr.add(jcbtn);
        jfr.add(jsp);
        jfr.add(jtfName);
        jfr.add(jtfPass);
        jfr.add(jtfAddr);
        jfr.setVisible(true);
    }

    public void sendMessage() {
        try {
            String message = jtxm.getText().trim();
            if (message.equals("")) {
                return;
            }
            jtx.append("\n" + name + " : " + message);
            if (message.length() < 30) {
                output.println(AESenc.encrypt(name + " : " + message));
            } else {
                output.println("~<N!_/_!D>~" + name + " : " + message);
            }
            jtxm.requestFocus();
            jtxm.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }

    public static void main(String[] args) throws Exception {
//        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 
        Client client = new Client();
    }

    class Read extends Thread {

        public void run() {
            String s;
            String message;
            while (true) {
                try {
                    s = input.readLine();
                    if (s.indexOf("~<N!_/_!D>~") == -1) {
                        jtx.append("\n" + AESenc.decrypt(s));
                    } else {
                        jtx.append("\n" + s.substring("~<N!_/_!D>~".length()));
                    }
                } catch (Exception ex) {
//                    System.err.println(ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Connection closed by remote host.");
                    System.exit(0);
                }
            }
        }
    }
}
