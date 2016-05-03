package awesomechatapp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 *
 * @author ArteneR
 */
public class FXMLSignUpConfirmationWindowController implements Initializable {

    private static String userEmail = "";
    @FXML private static Stage thisStage;
    @FXML private TextField tfConfirmationCode;
    @FXML private Label lblEmailAddress;
            
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
            System.out.println("Opening SignUpConfirmationWindow...");
            lblEmailAddress.setText(userEmail);
            System.out.println("here");
    }
    
    
    public static void setEmail(String email) {
            userEmail = email;
    }

    
    @FXML
    /*** 'Confirm' clicked ***/
    private void checkConfirmationCode(ActionEvent event) throws IOException {
            // If confirmation code ok, close these 2 windows
            // Get reference to this Stage
            Node thisSource = (Node) event.getSource();
            thisStage = (Stage) thisSource.getScene().getWindow();
            thisStage.close();
        
            String params = tfConfirmationCode.getText().toUpperCase() + "," + Client.getUserID();
            Client.sendQuery(MessageType.QUERY, Operation.VERIFY_CODE, params);
            
            String response = Client.waitForResponse();
            String responseType = response.substring(0, response.indexOf(":"));
            
            if (responseType.equals(MessageType.RESPONSE_SUCCESS.toString())) {
                    FXMLSignUpWindowController.closeWindow();

                    // Open SignUpConfirmationSuccessfulWindow
                    Parent rootSuccessful = FXMLLoader.load(getClass().getResource("FXMLSignUpConfirmationSuccessfulDialog.fxml"));
                    Scene sceneSuccessful = new Scene(rootSuccessful);
                    Stage stageSuccessful = new Stage();
                    stageSuccessful.setScene(sceneSuccessful);
                    stageSuccessful.setTitle("AwesomeChatApp Success!");
                    stageSuccessful.setResizable(false);
                    stageSuccessful.show();
            }
            else {
                    // If not ok:
                    // Open SignUpConfirmationFailedWindow
                    Parent rootFailed = FXMLLoader.load(getClass().getResource("FXMLSignUpConfirmationFailedDialog.fxml"));
                    Scene sceneFailed = new Scene(rootFailed);
                    Stage stageFailed = new Stage();
                    stageFailed.setScene(sceneFailed);
                    stageFailed.setTitle("AwesomeChatApp Failed!");
                    stageFailed.setResizable(false);
                    stageFailed.setOnCloseRequest(closeEvent -> {
                            try {
                                    System.out.println("Window Closed by X button");
                                    Parent root = FXMLLoader.load(getClass().getResource("FXMLSignUpConfirmationWindow.fxml"));
                                    Scene scene = new Scene(root);
                                    Stage stage = new Stage();
                                    stage.setScene(scene);
                                    stage.setTitle("AwesomeChatApp Email Confirmation Code");
                                    stage.show();
                                    root.requestFocus();
                            } 
                            catch (IOException ex) {
                                    Logger.getLogger(FXMLSignUpConfirmationWindowController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    });
                    stageFailed.show();
            }
        
    }
    
}
