
/**
 *
 * @author Anurag
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {

    private static final int PORT = 7520;
    private ServerSocket listener = null;
    private static ArrayList<PrintWriter> printWriterList;
    private static ArrayList<String> nameList;
    BufferedReader input;
    PrintWriter output;
    String name;

    Server() throws IOException {
        listener = new ServerSocket(PORT);
        nameList = new ArrayList<String>();
        printWriterList = new ArrayList<PrintWriter>();
    }

    public void run() {
        System.out.println("Server Listening on port : " + listener.getLocalPort());
        while (true) {
            try {
                Socket cliListener = listener.accept();
                System.out.println("Connected to " + cliListener.getRemoteSocketAddress());
                input = new BufferedReader(new InputStreamReader(cliListener.getInputStream()));
                output = new PrintWriter(cliListener.getOutputStream(), true);
                new Read(input, output).start();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Thread t = new Server();
        t.start();
    }

    public void broadcast(String message, PrintWriter out) {
        for (PrintWriter pw : printWriterList) {
            if (pw != out) {
                pw.println(message);
            }
        }
    }

    class Read extends Thread {

        String name;
        BufferedReader inp;
        PrintWriter out;

        Read(BufferedReader inp, PrintWriter out) {
            this.inp = inp;
            this.out = out;
        }

        public void run() {
            try {
                String s;
                name = inp.readLine();
                String password = inp.readLine();
                
                AuthenticationHandler ah = new AuthenticationHandler();
                String authResult = ah.checkUser(name, password);
                if (authResult.equals("added")) {
                    out.println(authResult);
                } else {
                    out.println("present");
                }
                if (!ah.authenticate(name, password)) {
                    out.println("error");
//                        JOptionPane.showMessageDialog(jfr, "An error occured.");
                    return;
                } else {
                    out.println("success");
                }
                
                printWriterList.add(output);
                nameList.add(name);
                System.out.println(name + " joined.");
                broadcast(AESenc.encrypt(name + " joined."), out);
                s = inp.readLine().trim();
                while (!s.equals("8ylVQeGptt/GeeyA0X3UoA==^")) {
                    while (s.equals("")) {
                        s = inp.readLine();
                    }
                    if (s.indexOf("~<N!_/_!D>~") != -1 && s.indexOf(" left") != -1 && s.indexOf(":") == -1) {
                        break;
                    }
//                    System.out.println(name + " : " + s);
                    broadcast(s, out);
                    s = inp.readLine().trim();
                }
                System.out.println(name + " left");
                broadcast(AESenc.encrypt(name + " left"), out);
                for (int i = 0; i != printWriterList.size(); i++) {
                    if (printWriterList.get(i).equals(out)) {
                        printWriterList.remove(i);
                        nameList.remove(i);
                    }
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
}
