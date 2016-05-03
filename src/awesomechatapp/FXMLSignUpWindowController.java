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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 *
 * @author ArteneR
 */
public class FXMLSignUpWindowController implements Initializable {
    
    @FXML private Button btnSignUp;
    @FXML private TextField tfUsername;
    @FXML private TextField tfEmail;
    @FXML private PasswordField pfPassword;
    @FXML private PasswordField pfConfirmPassword;
    private static Stage thisStage;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            System.out.println("Opening SignUpWindow...");
    }
    
    
    @FXML
    private void clickedSignUp(ActionEvent event) throws IOException {
            // create user with the specified information
            String params = tfUsername.getText() + "," + tfEmail.getText() + "," + pfPassword.getText() + "," + pfConfirmPassword.getText();
            System.out.println("params: " + params);
            Client.sendQuery(MessageType.QUERY, Operation.CREATE_USER, params);
            
            String response = Client.waitForResponse();
            String responseType = response.substring(0, response.indexOf(":"));
            
            // if signup successful, open SignUpConfirmationWindow
            if (responseType.equals(MessageType.RESPONSE_SUCCESS.toString())) {
                    String responseUserID = response.substring(response.indexOf("**")+2, response.length());
                    System.out.println("Response UserID: " + responseUserID);
                    Client.setUsername(tfUsername.getText());
                    Client.setUserID(responseUserID);
                    openSignUpConfirmationWindow(event);
            }
            else {
                    // else, display error dialog
                    String responseMessage = response.substring(response.indexOf(":")+1, response.length());
                    openFailedDialog(event, responseMessage);
            }
            
    }
    
    
    @FXML
    /*** 'Sign Up' clicked ***/
    private void openSignUpConfirmationWindow(ActionEvent event) throws IOException {
            // get reference to this window
            Node thisSource = (Node) event.getSource();
            thisStage = (Stage) thisSource.getScene().getWindow();
            
            // Open SignUpConfirmationWindow
            FXMLSignUpConfirmationWindowController.setEmail(tfEmail.getText());
            Parent root = FXMLLoader.load(getClass().getResource("FXMLSignUpConfirmationWindow.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("AwesomeChatApp Email Confirmation Code");
            stage.setResizable(false);
            System.out.println("Setting email...");
            System.out.println("Email set.");
            
            stage.show();
            root.requestFocus();

            thisStage.close();
    }
    
    
    @FXML
    private void openFailedDialog(ActionEvent event, String responseMessage) throws IOException {
            // Open SignUpFailedDialog
            FXMLSignUpFailedDialogController.setResponseMessage(responseMessage);
            Parent root = FXMLLoader.load(getClass().getResource("FXMLSignUpFailedDialog.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("AwesomeChatApp Sign Up Failed");
            stage.setResizable(false);

            stage.show();
            root.requestFocus();
    }
    
    
    
    public static void closeWindow() {
            thisStage.close();
    }
    
}
