package awesomechatapp;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ArteneR
 */
public class Client {
	
        private static final String JSON_FILES_PATH = "json/";
        private static int PORT;
        private static String SERVER;
	private Socket clientSocket;
	private static PrintWriter outToServer;
	private static BufferedReader inFromServer;
        private static InputStream is;
        private static DataOutputStream os;
        private BufferedReader inFromUser;
	private String messageToSend;
	private String messageReceived;
        private static String username;
        private static String userID;
        private static String status;
        private static Image userPhoto;
        private static ArrayList<Friend> friends;
        private static final int BUFFER_SIZE = 8192;    // !!! MUST be the same with the buffer size on server !!!
        
	
        
	public Client() {
                JSONParser parser = new JSONParser();
                try {
                        Object obj = parser.parse(new FileReader(JSON_FILES_PATH + "config.json"));
                        JSONObject jsonObject = (JSONObject) obj;
                        PORT = Integer.parseInt((String) jsonObject.get("port")); 
                        SERVER = (String) jsonObject.get("server");
                        System.out.println("Port: " + PORT + "\nServer: " + SERVER);
                        
                        System.out.println("Creating new Client...");
                        clientSocket = new Socket(SERVER, PORT);

                        inFromUser = new BufferedReader(new InputStreamReader(System.in));
                        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        outToServer = new PrintWriter(clientSocket.getOutputStream(),true);
                        is = clientSocket.getInputStream();
                        os = new DataOutputStream(clientSocket.getOutputStream());
                        
                        friends = new ArrayList<Friend>();
                } 
                catch (IOException | ParseException ex) {
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
        
        public static byte[] waitForFileBytes() throws IOException, InterruptedException {
                byte[] allReceivedBytes = new byte[0];
                byte[] readBytes = new byte[BUFFER_SIZE];
                byte[] enlargedAllReceivedBytes;
                
                FileOutputStream fos = new FileOutputStream("avatar-" + username + ".png");
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                
                int bytesRead;
                int totalBytes = 0;
                while ((bytesRead = is.read(readBytes, 0, readBytes.length)) > 0) {
                        String message = new String(readBytes, "UTF-8");
                        if (message.contains(":")) {
                                String messageType = message.substring(0, message.indexOf(":"));
                                if (messageType.equals(MessageType.RESPONSE_SUCCESS.toString())) {
                                        break;
                                }
                        }
                        
                        enlargedAllReceivedBytes = new byte[allReceivedBytes.length + BUFFER_SIZE];
                        for (int i = 0; i < allReceivedBytes.length; i++) {
                                enlargedAllReceivedBytes[i] = allReceivedBytes[i];
                        }
                        for (int i = 0, j = allReceivedBytes.length; j < enlargedAllReceivedBytes.length; i++, j++) {
                                enlargedAllReceivedBytes[j] = readBytes[i];
                        }
                        allReceivedBytes = new byte[allReceivedBytes.length + BUFFER_SIZE];
                        System.arraycopy(enlargedAllReceivedBytes, 0, allReceivedBytes, 0, enlargedAllReceivedBytes.length);
                        
                        totalBytes += bytesRead;
                        readBytes = new byte[BUFFER_SIZE];
                        outToServer.println(MessageType.ACK + ":" + Status.OK);
                }
                bos.write(allReceivedBytes, 0, totalBytes);
                bos.flush();
                fos.close();
                
                return allReceivedBytes;
        }
        
        
        public static void saveImageOnServer(BufferedImage bufferedImage) throws IOException {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", baos);
                byte[] imageBytes = baos.toByteArray();
                baos.close();
                
                sendQuery(MessageType.QUERY, Operation.SAVE_USER_AVATAR, null);
                System.out.println("Saving image on server...");
                
                String response = waitForResponse();
                String responseType = response.substring(0, response.indexOf(":"));
                String responseStatus = response.substring(response.indexOf(":")+1, response.length());
                System.out.println("Response Type: " + responseType);
                
                // check if server is ready to accept the image
                if (responseType.equals(MessageType.ACK.toString()) && responseStatus.equals(Status.READY_TO_ACCEPT.toString())) {
                        byte[] bytesBlock = new byte[BUFFER_SIZE];
                        
                        /* Split the array of bytes into byte-arrays of
                           BUFFER_SIZE bytes and send each buffer to server */
                        
                        for (int i = 0, m = 0; i < imageBytes.length; i += BUFFER_SIZE, m++) {
                                
                                for (int j = i, k = 0; j < (m * BUFFER_SIZE + BUFFER_SIZE); j++, k++) {
                                        if (j >= imageBytes.length) {
                                                break;
                                        }
                                        bytesBlock[k] = imageBytes[j];
                                }
                                
                                System.out.println("Sending bytes...");
                                os.write(bytesBlock);
                                os.flush();
                                bytesBlock = new byte[BUFFER_SIZE];
                        }

                        System.out.println("Done sending the image");
                        
                        String responseSentImage = Client.waitForResponse();
                        String responseSentImageType = responseSentImage.substring(0, responseSentImage.indexOf(":"));
                        
                        if (responseSentImageType.equals(MessageType.RESPONSE_FAILED.toString())) {
                                // TO DO:  Handle error -> show popup
                                System.out.println("Error saving image on server!");
                            
                                
                                
                        }
                        System.out.println("Done");
                }
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
        
        public static String getServer() {
                return SERVER;
        }
        
        public static int getPort() {
                return PORT;
        }
        
}