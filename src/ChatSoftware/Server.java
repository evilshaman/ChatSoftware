package ChatSoftware;

/**
 *
 * @author Ekansh Gupta
 */
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Server {

    ServerGui serverguic;
    PrintWriter pwPrintWriter;
    int flag = 1;
    ArrayList<Socket> list = new ArrayList<>();
    
    Map<String,String> map=new HashMap<>();
    
    public Server(ServerGui ui) {
        serverguic = ui;
    }

    public void connectit() throws IOException {
        runserver runit = new runserver();
        Thread thread3 = new Thread(runit);
        thread3.start();
        JOptionPane.showMessageDialog(serverguic, "Server Started!!!");
    }

    class runserver implements Runnable {

        public void run() {
            try {
                final int port = 2017;
                //System.out.println("Server waiting for connection on port " + port);
                ServerSocket ss = new ServerSocket(port);
                while (true) {
                    Socket clientSocket = ss.accept();
                    list.add(clientSocket);
                    //System.out.println("Recieved connection from " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort());
                    //create two threads to send and recieve from client
                    Runmulti runmulti = new Runmulti(clientSocket);
                    Thread threadx = new Thread(runmulti);
                    threadx.start();
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class Runmulti implements Runnable {

        Socket clientSocket = null;

        public Runmulti(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            RecieveFromClientThread recieve = new RecieveFromClientThread(clientSocket);
            Thread thread = new Thread(recieve);
            thread.start();
            SendToClientThread send = new SendToClientThread(clientSocket);
            Thread thread2 = new Thread(send);
            thread2.start();
        }
    }

    class RecieveFromClientThread implements Runnable {

        Socket clientSocket = null;
        BufferedReader brBufferedReader = null;

        public RecieveFromClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }//end constructor

        public void run() {
            try {
                brBufferedReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                String messageString;
                while (true) {
                    while ((messageString = brBufferedReader.readLine()) != null) {//assign message from client to messageString
                        /*if(messageString.length() >= 5 && messageString.charAt(0) == 'C' && messageString.charAt(0) == '&' && messageString.charAt(0) == 'U' && messageString.charAt(0) == '&' && messageString.charAt(0) == 'P') {
                            StringTokenizer st = new StringTokenizer(messageString," ");
                            String tmp = st.nextToken();
                            String usern = st.nextToken();
                            String pass = st.nextToken();
                            flag = 0;
                            if(map.containsKey(usern)) {
                                if(map.get(usern).equals(pass)) {
                                    flag = 1;
                                }
                            }
                            if(flag == 1) {
                                pwPrintWriter = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
                                pwPrintWriter.println("C&U&P1");
                                pwPrintWriter.flush();
                            } else {
                                pwPrintWriter = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
                                pwPrintWriter.println("C&U&P0");
                                pwPrintWriter.flush();
                            }
                            continue;
                        }
                        if(messageString.length() >= 5 && messageString.charAt(0) == 'A' && messageString.charAt(1) == '&' && messageString.charAt(2) == 'U' && messageString.charAt(3) == '&' && messageString.charAt(4) == 'P') {
                            StringTokenizer st = new StringTokenizer(messageString," ");
                            String tmp = st.nextToken();
                            String usern = st.nextToken();
                            String pass = st.nextToken();
                            flag = 0;
                            if(map.containsKey(usern)) {
                                flag = 0;
                            } else {
                                map.put(usern, pass);
                                flag = 1;
                            }
                            if(flag == 1) {
                                pwPrintWriter = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
                                pwPrintWriter.println("A&U&P1");
                                pwPrintWriter.flush();
                            } else {
                                pwPrintWriter = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
                                pwPrintWriter.println("A&U&P0");
                                pwPrintWriter.flush();
                            }
                            continue;
                        }*/
                        String enc = messageString;
                        EncryptDecrypt ed = new EncryptDecrypt();
                        enc = ed.decrypt(enc);
                        serverguic.currentinbox.append(enc + "\n");
                        enc = ed.encrypt(enc);
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i) != clientSocket) {
                                pwPrintWriter = new PrintWriter(new OutputStreamWriter(list.get(i).getOutputStream()));
                                pwPrintWriter.println(enc);
                                pwPrintWriter.flush();
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }//end class RecieveFromClientThread

    void sendtoclient(String msgToClientString) throws IOException, Exception {
        serverguic.currentinbox.append("Server -> " + msgToClientString + "\n");
        for (int i = 0; i < list.size(); i++) {
            pwPrintWriter = new PrintWriter(new OutputStreamWriter(list.get(i).getOutputStream()));
            String enc = "Server -> " + msgToClientString;
            EncryptDecrypt ed = new EncryptDecrypt();
            enc = ed.encrypt(enc);
            pwPrintWriter.println(enc);//send message to client with PrintWriter
            pwPrintWriter.flush();//flush the PrintWriter
        }
    }

    class SendToClientThread implements Runnable {

        Socket clientSock = null;

        public SendToClientThread(Socket clientSock) {
            this.clientSock = clientSock;
        }

        public void run() {
            try {
                while (true) {
                }//end while
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }//end run
    }//end class SendToClientThread

}
