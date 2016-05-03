package awesomechatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ListenerThread implements Runnable {

//    private static final String SERVER = "192.168.0.105";
    private static final String SERVER = "raspberrypi.local";
//	private static final String SERVER = "169.254.113.204";
    private static final int PORT = 5001;
    private Socket clientSocket;
    private static BufferedReader inFromServer;
    
    
    public ListenerThread() {
                try{
                        System.out.println("Creating new Listener...");
                        clientSocket = new Socket(SERVER, PORT);
                        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                } 
                catch (IOException ex) {
                        Logger.getLogger(ListenerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
        }



        @Override
        public void run() {
                // receive messages from friend 
                String response = "";
                while(true) {
                        try {    
                                System.out.println("Waiting for server message..");
                                response = inFromServer.readLine();
                                System.out.println("Message from server: " + response);
                                Thread.sleep(5000);

//                                FXMLChatWindowController.writeMessageFromFriend(receivedMessage);
                        } 
                        catch (IOException | InterruptedException ex) {
                            Logger.getLogger(ListenerThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
        }
    
}
