package awesomechatapp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class FXMLFriendRequestWindowController implements Initializable {

    private static String userUsername = "";
    private static String userEmail = "";
    @FXML private Text txtFriendUsernameEmail;
    @FXML private static Stage thisStage;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
            System.out.println("Opening FriendRequestWindow...");
            txtFriendUsernameEmail.setText("Would you like to confirm " + userUsername + " (" + userEmail + ") as your friend?");
    }
    
    public static void setUsername(String username) {
            userUsername = username;
    }
    
    public static void setEmail(String email) {
            userEmail = email;
    }
    
    @FXML
    private void clickedConfirm(ActionEvent event) throws IOException {
            System.out.println("clicked Confirm...");
            String params = userUsername;
            Client.sendQuery(MessageType.QUERY, Operation.FRIENDSHIP_CONFIRMED, params);
            
            System.out.println("Closing FriendRequestWindow...");
            Node thisSource = (Node) event.getSource();
            thisStage = (Stage) thisSource.getScene().getWindow();
            thisStage.close();
    }
    
     @FXML
     /*** clicked 'Ignore' ***/
    private void clickedIgnore(ActionEvent event) throws IOException {
            // Remove entry from db
            String params = userUsername;
            Client.sendQuery(MessageType.QUERY, Operation.FRIENDSHIP_IGNORED, params);
        
            System.out.println("Closing FriendRequestWindow...");
            Node thisSource = (Node) event.getSource();
            thisStage = (Stage) thisSource.getScene().getWindow();
            thisStage.close();
            
    }
    
}
