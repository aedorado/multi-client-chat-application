
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthenticationHandler {

    private BufferedReader br;
    private BufferedWriter bw;
    String filename;

    public AuthenticationHandler() throws Exception {
        filename = "passwords.txt";
        checkFile(filename);
        br = new BufferedReader(new FileReader(filename));
        bw = new BufferedWriter(new FileWriter(filename, true));
    }
    
    void refreshVariables() throws IOException {
        try {
            br = new BufferedReader(new FileReader(filename));
            bw = new BufferedWriter(new FileWriter(filename, true));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AuthenticationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void checkFile(String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            try {
                System.out.println("Creating file");
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(AuthenticationHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean authenticate(String username, String password) throws IOException {
        refreshVariables();
        if (username.equals("") && password.equals("")) {
            return false;
        }
        try {
            String sn, sp;
            while ((sn = br.readLine()) != null) {
                sp = br.readLine();
                if (sn.equals(username) && sp.equals(password)) {
                    return true;
                }
            }
        } catch (IOException ex) {

        }
        return false;
    }

    public String checkUser(String username, String password) {
        try {
            String sn, sp;
            while ((sn = br.readLine()) != null) {
                sp = br.readLine();
                if (sn.equals(username)) {
                    return "exists";
                }
            }
            bw.write(username);
            bw.newLine();
            bw.write(password);
            bw.newLine();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(AuthenticationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "added";
    }

    public String addUser(String username, String password, String repassword) {
        try {
            System.out.println("Adding user");
            if (!password.equals(repassword)) {
                return "mismatch";
            }
            String sn, sp;
            while ((sn = br.readLine()) != null) {
                sp = br.readLine();
                if (sn.equals(username)) {
                    return "exists";
                }
            }
            bw.write(username);
            bw.newLine();
            bw.write(password);
            bw.newLine();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(AuthenticationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "added";
    }
}
