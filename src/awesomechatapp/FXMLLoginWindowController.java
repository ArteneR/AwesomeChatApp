package awesomechatapp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;


/**
 *
 * @author ArteneR
 */
public class FXMLLoginWindowController implements Initializable {
    
    @FXML private Label myLabel;
    @FXML private Button btnSignIn;
    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private AnchorPane apLoginWindow;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            System.out.println("Opening LoginWindow...");

//             NOT WORKING YET!
//            apLoginWindow.setOnKeyPressed(new EventHandler<KeyEvent>() {
//                    @Override
//                    public void handle(KeyEvent keyEvent)
//                    {
//                            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
////                                    openMainWindow(keyEvent);
//                            }
//                    }
//            });
            
    }    
    
    
    @FXML
    /*** clicked 'Sign In' ***/
    private void clickedSignIn(ActionEvent event) throws IOException, InterruptedException {
            Client client = new Client();
            
            // check user credentials, log in if OK
            String params = tfUsername.getText() + "," + pfPassword.getText();
            Client.sendQuery(MessageType.QUERY, Operation.TRY_LOGIN, params);
            
            String response = Client.waitForResponse();
            String responseType = response.substring(0, response.indexOf(":"));
            
            // if successfully logged in, open MainWindow
            if (responseType.equals(MessageType.RESPONSE_SUCCESS.toString())) {
                    Client.setUsername(tfUsername.getText());
                    
                    // get user avatar
                    String paramsUsername = Client.getUsername();
                    Client.sendQuery(MessageType.QUERY, Operation.GET_USER_AVATAR, paramsUsername);
                    byte[] imageBytes = Client.waitForFileBytes();
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                    Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                    Client.setUserPhoto(image);
                    
                    
                    getFriendsInfo();
                    
                    // this thread will display the friendship requests if there are any
                    Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                    showFriendshipRequests();
                            } 
                    });
                    
//                    Thread messageListener = new Thread(new ListenerThread(Client.getServer(), Client.getPort()));
//                    messageListener.start();
                    
                    openMainWindow(event);
            }
            else {
                    // else, display error dialog
                    openFailedDialog();
            }
            
    }
    
    
    @FXML
    /*** clicked 'Forgot your password' ***/
    private void clickedForgotPassword(ActionEvent event) throws IOException {
            // Open ForgotPasswordWindow
            Parent root = FXMLLoader.load(getClass().getResource("FXMLForgotPasswordWindow.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("AwesomeChatApp Forgot Password");
            stage.setScene(scene);
            stage.setResizable(false);
            
            stage.showAndWait();
    }
    
    
    
    private void showFriendshipRequests() {
            System.out.println("Thread started!");
            try {
                Client.sendQuery(MessageType.QUERY, Operation.CHECK_FRIEND_REQUESTS, null);

                String responseFriendRequests = Client.waitForResponse();
                String responseFriendRequestsType = responseFriendRequests.substring(0, responseFriendRequests.indexOf(":"));
                String responseFriendRequestsData = responseFriendRequests.substring(responseFriendRequests.indexOf(":")+1, responseFriendRequests.length());

                if (responseFriendRequestsType.equals(MessageType.RESPONSE_SUCCESS.toString())) {
                        if (!responseFriendRequestsData.equals(ResponseMessage.NO_FRIEND_REQUESTS.toString())) {
                                // convert data (comma separated values) into array
                                List<String> friendRequestsUsersID = Arrays.asList(responseFriendRequestsData.split("\\,"));

                                // for each request open a separate window
                                for (int i = 0; i < friendRequestsUsersID.size(); i++) {
                                        String user = friendRequestsUsersID.get(i);
                                        String username = user.substring(0, user.indexOf(" "));
                                        String email = user.substring(user.indexOf(" ")+1, user.length());
                                        System.out.println("username: " + username + " email: " + email);

                                        // pass username and email of this user
                                        FXMLFriendRequestWindowController.setUsername(username);
                                        FXMLFriendRequestWindowController.setEmail(email);

                                        openFriendRequestWindow();
                                }//end for
                        }
                }
            } 
            catch (IOException ex) {
                Logger.getLogger(FXMLLoginWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    
    private void getFriendsInfo() {
            System.out.println("Thread2 started!");
            try {
                Client.sendQuery(MessageType.QUERY, Operation.GET_FRIENDS_INFO, null);

                String responseFriendsInfo = Client.waitForResponse();
                String responseFriendsInfoType = responseFriendsInfo.substring(0, responseFriendsInfo.indexOf(":"));
                String responseFriendsInfoData = responseFriendsInfo.substring(responseFriendsInfo.indexOf(":")+1, responseFriendsInfo.length());

                if (responseFriendsInfoType.equals(MessageType.RESPONSE_SUCCESS.toString())) {
                        if (!responseFriendsInfoData.equals(ResponseMessage.NO_FRIENDS.toString())) {
                                // convert data (separated by ; ) into array
                                List<String> friends = Arrays.asList(responseFriendsInfoData.split("\\;"));

                                // for each friend
                                for (int i = 0; i < friends.size(); i++) {
                                        List<String> data = Arrays.asList(friends.get(i).split("\\,"));

                                        String friendUsername = data.get(0);
                                        String friendStatus = data.get(1);
                                        // Convert string of bytes to image from data.get(2)

                                        Image friendPhoto = null;
                                        Friend newFriend = new Friend(friendUsername, friendStatus, friendPhoto);
                                        Client.addFriend(newFriend);
                                }
                                System.out.println("Friends info: " + Client.getFriends());
                        }
                }
            }
            catch (IOException ex) {
                Logger.getLogger(FXMLLoginWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    
    @FXML
    /*** 'Sign In' clicked OR 'Logout' clicked (from MainWindow) ***/
    private void openMainWindow(ActionEvent event) throws IOException {
            
            // Close this window
            Node thisSource = (Node) event.getSource();
            Stage thisStage = (Stage) thisSource.getScene().getWindow();
            thisStage.close();

            // Open MainWindow
            Parent root = FXMLLoader.load(getClass().getResource("FXMLMainWindow.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("AwesomeChatApp");
            stage.setScene(scene);
            stage.setResizable(false);
            
            root.requestFocus();
            stage.showAndWait();
    }
    
    
    @FXML
    /*** 'or Sign Up' clicked ***/
    private void openSignUpWindow(ActionEvent event) throws IOException {
            //Open SignUpWindow
            Parent root = FXMLLoader.load(getClass().getResource("FXMLSignUpWindow.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("AwesomeChatApp SignUp");
            stage.setScene(scene);
            stage.setResizable(false);
            
            Node thisSource = (Node) event.getSource();
            Stage thisStage = (Stage) thisSource.getScene().getWindow();
            double newWindowX = thisStage.getX() + 50;
            double newWindowY = thisStage.getY() + 50;
            stage.setX(newWindowX);
            stage.setY(newWindowY);
            
            root.requestFocus();
            stage.showAndWait();
    }
    
    
    @FXML
    /**** Credentials not matching ****/
    private void openFailedDialog() throws IOException {
            // Open FailedDialog
            Parent root = FXMLLoader.load(getClass().getResource("FXMLLoginFailedDialog.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("AwesomeChatApp Login Failed!");
            stage.setScene(scene);
            stage.setResizable(false);
            
            stage.showAndWait();
    }
    
    
//    @FXML
    private void openFriendRequestWindow() throws IOException {
            // Open FriendRequestWindow
            Parent root = FXMLLoader.load(getClass().getResource("FXMLFriendRequestWindow.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("AwesomeChatApp Friend Request");
            stage.setScene(scene);
            stage.setResizable(false);
            
            root.requestFocus();
            stage.show();
    }
    
}
