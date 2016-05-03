package awesomechatapp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class FXMLAddFriendWindowController implements Initializable {

    @FXML private Button btnSendRequest;
    @FXML private Stage thisStage;
    @FXML private TextField tfFriendUsernameOrEmail;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    
    
    @FXML
    /*** clicked 'Send request' ***/
    private void clickedSendRequest(ActionEvent event) throws IOException {
            System.out.println("Sending friend request...");
            String friendUsernameOrEmail = tfFriendUsernameOrEmail.getText();
            
            // check if field is empty
            if (friendUsernameOrEmail.isEmpty()) {
                    // Open error dialog
                    String errorMessage = "It appears that the field is empty.\n"
                                    + "Please enter the username or email address of the person you want to add as friend.";
                    FXMLErrorPopupController.setErrorMessage(errorMessage);
                    openErrorPopup(event);
                    return ;
            }
            
            // Send friend request
            Client.sendFriendRequest(MessageType.FRIEND_REQUEST, friendUsernameOrEmail);

            String response = Client.waitForResponse();
            System.out.println("Server response: " + response);

            String responseType = response.substring(0, response.indexOf(":"));
            String responseMessage = response.substring(response.indexOf(":")+1, response.length());

            // if successful, close window, show success popup
            if (responseType.equals(MessageType.RESPONSE_SUCCESS.toString())) {
                    // Close this window
                    Node thisSource = (Node) event.getSource();
                    thisStage = (Stage) thisSource.getScene().getWindow();
                    thisStage.close();

                    FXMLSuccessPopupController.setSuccessMessage(responseMessage);
                    openSuccessPopup(event);
            }
            else {
                    // show error popup
                    FXMLErrorPopupController.setErrorMessage(responseMessage);
                    openErrorPopup(event);
            }
                
    }
    
    
    private void openErrorPopup(ActionEvent event) throws IOException {
            // Open ErrorPopup
            Parent root = FXMLLoader.load(getClass().getResource("FXMLErrorPopup.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("AwesomeChatApp Error Message");
            stage.setScene(scene);
            stage.setResizable(false);
            
            stage.showAndWait();
    }
    
    
    private void openSuccessPopup(ActionEvent event) throws IOException {
            // Open SuccessPopup
            Parent root = FXMLLoader.load(getClass().getResource("FXMLSuccessPopup.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("AwesomeChatApp Success Message");
            stage.setScene(scene);
            stage.setResizable(false);
            
            stage.showAndWait();
    }
    
    
}
