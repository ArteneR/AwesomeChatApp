package awesomechatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ListenerThread implements Runnable {

    private Socket clientSocket;
    private static BufferedReader inFromServer;
    
    
        public ListenerThread(String server, int port) {
                try{
                        System.out.println("Creating new Listener...");
                        clientSocket = new Socket(server, port);
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
                                System.out.println("--Waiting for server message..");
                                response = inFromServer.readLine();
                                
                                // respone format: MessageType.NORMAL_MESSAGE:sender:message
                                System.out.println("--Message from server: " + response);
                                String responseType = response.substring(0, response.indexOf(":"));
                                
                                if (responseType.equals(MessageType.NORMAL_MESSAGE.toString())) {
                                        String responseData = response.substring(response.indexOf(":")+1, response.length());
                                        String messageSender = responseData.substring(0, responseData.indexOf(":"));
                                        String message = responseData.substring(responseData.indexOf(":")+1, responseData.length());
                                        
                                        System.out.println("Sender: " + messageSender + " message: " + message);
                                        
                                        // TO DO:
//                                        FXMLChatWindowController.writeMessageFromFriend(receivedMessage);
                                }
                        } 
                        catch (IOException ex) {
                                Logger.getLogger(ListenerThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
        }
    
}
