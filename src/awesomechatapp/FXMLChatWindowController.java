package awesomechatapp;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

/**
 *
 * @author ArteneR
 */
public class FXMLChatWindowController implements Initializable {

    @FXML private TextArea taTypedMessage;
    @FXML private TextArea taAllMessages;
    @FXML private TextFlow tfAllMessages;
    @FXML private ImageView ivMyPhoto;
    @FXML private ImageView ivFriendPhoto;
    @FXML private Label lblFriendUsername;
    @FXML private Label lblFriendStatus;
    @FXML private ScrollPane spAllMessages;
    private static Friend friend;
    private static final String CHAT_FONT_NAME = "Verdana";
    private static final int CHAT_FONT_SIZE = 13;
    private static final Font CHAT_FONT = Font.font(CHAT_FONT_NAME, CHAT_FONT_SIZE);
    private static final Color OTHER_USERNAME_COLOR = Color.web("0xB30000", 1.0);
    private static final Color OTHER_USER_TEXT = Color.BLACK;
    private static final Color THIS_USERNAME_COLOR = Color.web("0x8cb56b", 1.0);
    private static final Color THIS_USER_TEXT = Color.GREY;
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
            ivMyPhoto.setImage(Client.getUserPhoto());
            
            lblFriendUsername.setText(friend.getUsername());
            lblFriendStatus.setText(friend.getStatus());
            ivFriendPhoto.setImage(friend.getPhoto());
            
            taTypedMessage.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent keyEvent) {
                            if (keyEvent.getCode() == KeyCode.ENTER)  {
                                    keyEvent.consume();
                                    try {
                                        sendMessage();
                                    } 
                                    catch (IOException ex) {
                                        Logger.getLogger(FXMLChatWindowController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                            }
                    }
            });
            
//            Thread messageListener = new Thread(new ListenerThread(Client.getServer(), Client.getPort()));
//            messageListener.start();
            
            Task <Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                            // send message to server that a new socket for receiving messages will be created
                            Client.sendQuery(MessageType.QUERY, Operation.MAKE_RECEIVER_SOCKET, null);
                            
                            String responseMakeSocket = Client.waitForResponse();
                            String responseMakeSocketType = responseMakeSocket.substring(0, responseMakeSocket.indexOf(":"));

                            System.out.println("------" + responseMakeSocketType);
                            if (responseMakeSocketType.equals(MessageType.ACK.toString())) {
                                    Socket clientSocket = new Socket(Client.getServer(), Client.getPort());
                                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                                    System.out.println("Creating new Listener...");

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

                                                            System.out.println("1Sender: " + messageSender + " message: " + message);

                                                            Platform.runLater(new Runnable() {
                                                                    public void run() {
                                                                            Text otherUsername = new Text(lblFriendUsername.getText() + ": ");
                                                                            otherUsername.setFill(OTHER_USERNAME_COLOR);
                                                                            otherUsername.setFont(CHAT_FONT);

                                                                            Text otherUserText = new Text(message + "\n");
                                                                            otherUserText.setFill(OTHER_USER_TEXT);
                                                                            otherUserText.setFont(CHAT_FONT);
                                                                            tfAllMessages.getChildren().addAll(otherUsername, otherUserText);
                                                                            spAllMessages.setVvalue(1.0);
                                                                    }
                                                            });
                                                    }
                                            } 
                                            catch (IOException ex) {
                                                    Logger.getLogger(ListenerThread.class.getName()).log(Level.SEVERE, null, ex);
                                                    return null;
                                            }
                                    }
                            }
                            return null;
                    }
            }; 

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

            
            
            
            
//            Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                            while(true) {
//                                    // NOT WORKING !
//                                    FXMLChatWindowController.writeMessageFromFriend("sdfsfdsdfdsf");
//                            }
//                    } 
//            });
    }
    
    public static void setFriend(Friend newFriend) {
            friend = newFriend;
    }
    
    
    @FXML
    private void clickedSend(ActionEvent event) throws IOException {
            sendMessage();
    }

    
    private void sendMessage() throws IOException {
            String message = taTypedMessage.getText();
            taTypedMessage.clear();
            
            Text thisUsername = new Text(Client.getUsername() + ": ");
            thisUsername.setFill(THIS_USERNAME_COLOR);
            thisUsername.setFont(CHAT_FONT);
            
            Text thisUserText = new Text(message + "\n");
            thisUserText.setFill(THIS_USER_TEXT);
            thisUserText.setFont(CHAT_FONT);
            tfAllMessages.getChildren().addAll(thisUsername, thisUserText);
            spAllMessages.setVvalue(1.0);
            
            Client.sendMessage(MessageType.NORMAL_MESSAGE, Client.getUsername(), friend.getUsername(), message);
            /*
            String response = Client.waitForResponse();
            String responseType = response.substring(0, response.indexOf(":"));
            
            if (responseType.equals(MessageType.RESPONSE_FAILED.toString())) {
                    // Error, message sending failed, maybe other user disconnected
            }
            */
    }
    
    
    public static void writeMessageFromFriend(String receivedMessage) {
//            taAllMessages.appendText(friend.getUsername() + ": " + receivedMessage);
    }
    
    
    @FXML
    /*** clicked 'My Photo' ***/
    private void clickedMyPhoto(MouseEvent event) throws IOException {
            FileChooser fileChooser = new FileChooser();

            //Set extension filter
            List<String> allExtensions = new ArrayList<String>();
            allExtensions.add("*.JPG");
            allExtensions.add("*.PNG");
            
            FileChooser.ExtensionFilter extFilterALL = new FileChooser.ExtensionFilter("All files (*.*)", allExtensions);
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            fileChooser.getExtensionFilters().addAll(extFilterALL, extFilterJPG, extFilterPNG);

            //Show open file dialog
            File file = fileChooser.showOpenDialog(null);
                       
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                ivMyPhoto.setImage(image);
                Client.setUserPhoto(image);
            }
            catch (IOException ex) {
                Logger.getLogger(FXMLMainWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
}
