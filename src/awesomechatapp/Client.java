package awesomechatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;

/**
 *
 * @author ArteneR
 */
public class Client {
	
//	private static final String SERVER = "192.168.0.105";
	private static final String SERVER = "raspberrypi.local";
//	private static final String SERVER = "169.254.113.204";
	private static final int PORT = 5001;
	private static final String CLOSE_CONNECTION = "CLOSE_CONNECTION";
	private static final String MESSAGE = "MESSAGE";
	private static final String RECEIVER = "ReceiverUsernameORConferenceName";
	private Socket clientSocket;
	private static PrintWriter outToServer;
	private static BufferedReader inFromServer;
        private BufferedReader inFromUser;
	private String messageToSend;
	private String messageReceived;
        private static String username;
        private static String userID;
        private static String status;
        private static Image userPhoto;
        private static ArrayList<Friend> friends;
        
	
	
	public Client() {
                try {
                        System.out.println("Creating new Client...");
                        clientSocket = new Socket(SERVER, PORT);

                        inFromUser = new BufferedReader(new InputStreamReader(System.in));
                        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        outToServer = new PrintWriter(clientSocket.getOutputStream(),true);
                        
                        friends = new ArrayList<Friend>();
                } 
                catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
	}
	
        
	/**
	 * messageType: can be: 
	 * 		MESSAGE - regular message
	 * 		CLOSE_CONNECTION - message for closing connection
	 * message: is the actual message to be sent
	 * receiver: the username of the message receiver 	OR 	 the name of the conference
	 * */
	public static void sendMessage(MessageType messageType, String sender, String receiver, String message) {
                System.out.println(messageType + ":" + sender + ":" + receiver + ":" + message);
                outToServer.println(messageType + ":" + sender + ":" + receiver + ":" + message);
                System.out.println("Message sent!");
	}
        
        
        public static void sendQuery(MessageType messageType, Operation operation, String params) throws IOException {
                System.out.println(messageType + ":" + operation + ":" + params);
                outToServer.println(messageType + ":" + operation + ":" + params);
                System.out.println("Query message sent!");
        }
        
        
        public static void sendFriendRequest(MessageType messageType, String friendUsernameOrEmail) {
                System.out.println(messageType + ":" + friendUsernameOrEmail);
                outToServer.println(messageType + ":" + friendUsernameOrEmail);
                System.out.println("Friend request sent!");
        }
        
        
        public static String waitForResponse() throws IOException {
                System.out.println("waiting..");
                String response = inFromServer.readLine();
                System.out.println("Response from server: " + response);
                return response;
        }
        
        
        public static void setUserID(String newUserID) {
                userID = newUserID;
        }
        
        
        public static String getUserID() {
                return userID;
        }
        
        
        public static void setUsername(String newUsername) {
                username = newUsername;
        }
        
        
        public static String getUsername() {
                return username;
        }
        
        public static void setStatus(String newStatus) {
                status = newStatus;
        }
        
        public static String getStatus() {
                return status;
        }
        
        public static void setUserPhoto(Image newPhoto) {
                userPhoto = newPhoto;
        }
        
        public static Image getUserPhoto() {
                return userPhoto;
        }
        
        public static void addFriend(Friend friend) {
                friends.add(friend);
        }
        
        public static ArrayList<Friend> getFriends() {
                return friends;
        }
        
}