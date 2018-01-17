package ChatSoftware;

/**
 *
 * @author Ekansh Gupta
 */
import static ChatSoftware.ClientGui.jLabel5;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Client {

    ClientGui clientguic;
    PrintWriter print = null;
    String uu, pp;
    int flag = 0;
    public Client(ClientGui ui) {
        clientguic = ui;
    }

    public void connectit() throws IOException {
        this.flag = 0;
        runserver1 runit1 = new runserver1();
        Thread thread3 = new Thread(runit1);
        thread3.start();
    }

    class runserver1 implements Runnable {

        public void run() {
            try {
                Socket sock = new Socket("localhost" , 2017);
                SendThread sendThread = new SendThread(sock);
                Thread thread5 = new Thread(sendThread);
                thread5.start();
                RecieveThread recieveThread = new RecieveThread(sock);
                Thread thread6 = new Thread(recieveThread);
                thread6.start();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public int check(String username, char[] password) throws IOException {
        String pass = String.valueOf(password);
        Scanner S = new Scanner(new File("userpass.txt"));
        while(S.hasNext()) {
            String usern = S.next();
            String passw = S.next();
            if(username.equals(usern) && pass.equals(passw)) {
                uu = username;
                clientguic.jLabel1.setText(uu);
                clientguic.jLabel1.setVisible(true);
                return 1;
            }
        }
        return 0;
    }
    
    public int adduser(String username, char[] password) throws IOException {
        String pass = String.valueOf(password);
        FileWriter g = new FileWriter("userpass.txt", true);
        PrintWriter ou = new PrintWriter(g);
        Scanner S = new Scanner(new File("userpass.txt"));
        while(S.hasNext()) {
            String usern = S.next();
            String passw = S.next();
            if(username.equals(usern)) {
                ou.close();
                return 0;
            }
        }
        uu = username;
        clientguic.jLabel1.setText(uu);
        clientguic.jLabel1.setVisible(true);
        ou.append(username + " " + pass + "\n");
        ou.close();
        return 1;
    }
    
    class RecieveThread implements Runnable {

        Socket sock = null;
        BufferedReader recieve = null;

        public RecieveThread(Socket sock) {
            this.sock = sock;
        }//end constructor

        public void run() {
            try {
                recieve = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));//get inputstream
                String msgRecieved = null;
                while ((msgRecieved = recieve.readLine()) != null) {
                   /* if (msgRecieved.length() >= 6 && msgRecieved.charAt(0) == 'A' && msgRecieved.charAt(1) == '&' && msgRecieved.charAt(2) == 'U' && msgRecieved.charAt(3) == '&' && msgRecieved.charAt(4) == 'P') {
                        if (msgRecieved.charAt(5) == '0') {
                            jLabel5.setText("Already Registered User!!!");
                            jLabel5.setVisible(true);
                        } else if (msgRecieved.charAt(5) == '1') {
                            clientguic.setVisible(true);
                        }
                        clientguic.verifyFrame.setVisible(false);
                        continue;
                    }
                    if (msgRecieved.length() >= 6 && msgRecieved.charAt(0) == 'C' && msgRecieved.charAt(1) == '&' && msgRecieved.charAt(2) == 'U' && msgRecieved.charAt(3) == '&' && msgRecieved.charAt(4) == 'P') {
                        if (msgRecieved.charAt(5) == '0') {
                            jLabel5.setText("Invalid Username or password!!!");
                            jLabel5.setVisible(true);
                        } else if (msgRecieved.charAt(5) == '1') {
                            clientguic.setVisible(true);
                        }
                        clientguic.verifyFrame.setVisible(false);
                        continue;
                    }*/
                    String enc = msgRecieved;
                    EncryptDecrypt ed = new EncryptDecrypt();
                    enc = ed.decrypt(enc);
                    clientguic.currentinbox1.append(enc + "\n");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }//end run
    }//end class recievethread

    public void sendtoserver(String mess) {
        try {
            clientguic.currentinbox1.append("Me -> " + mess + "\n");
            String enc = uu + " -> " + mess;
            EncryptDecrypt ed = new EncryptDecrypt();
            enc = ed.encrypt(enc);
            print.println(enc);
            print.flush();
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void discon() {
        this.flag = 1;
    }
    class SendThread implements Runnable {

        Socket sock = null;
        BufferedReader brinput = null;

        public SendThread(Socket sock) {
            this.sock = sock;
        }//end constructor

        public void run() {
            try {
                if (sock.isConnected()) {
                    //System.out.println(uu + " connected to " + sock.getInetAddress() + " on port " + sock.getPort());
                    print = new PrintWriter(sock.getOutputStream(), true);
                    String enc = uu + " joined the Conversation :)";
                    EncryptDecrypt ed = new EncryptDecrypt();
                    enc = ed.encrypt(enc);
                    print.println(enc);
                    print.flush();
                    while (true) {
                        if(flag == 1) break;
                    }//end while
                    enc = uu + " left the Conversation";
                    enc = ed.encrypt(enc);
                    print.println(enc);
                    print.flush();
                    sock.close();
                    System.exit(0);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }//end run method
    }//end class

}
