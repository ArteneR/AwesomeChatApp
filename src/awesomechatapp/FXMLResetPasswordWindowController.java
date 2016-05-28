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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class FXMLResetPasswordWindowController implements Initializable {

        @FXML private TextField tfCode;
        @FXML private PasswordField pfNewPassword;
        @FXML private PasswordField pfRepeatNewPassword;
        @FXML private Stage thisStage;
        @FXML private Text txtEmailAddress;
        private static String emailAddress = "";
        
    
        @Override
        public void initialize(URL location, ResourceBundle resources) {
                System.out.println("Opening ResetPasswordWindow...");
                txtEmailAddress.setText(emailAddress);
        }
        
        
        public static void setEmailAddress(String email) {
                emailAddress = email;
        }
        
        
        @FXML
        private void clickedResetPassword(ActionEvent event) throws IOException {
                System.out.println("Clicked ResetPassword...");
                String params = txtEmailAddress.getText() + "," + tfCode.getText() + "," + pfNewPassword.getText() + "," + pfRepeatNewPassword.getText();
                Client.sendQuery(MessageType.QUERY, Operation.RESET_PASSWORD, params);

                String response = Client.waitForResponse();
                String responseType = response.substring(0, response.indexOf(":"));
                
                if (responseType.equals(MessageType.RESPONSE_SUCCESS.toString())) {
                        // Close this window
                        Node thisSource = (Node) event.getSource();
                        thisStage = (Stage) thisSource.getScene().getWindow();
                        thisStage.close();
                    
                        String responseSuccessMessage = response.substring(response.indexOf(":") + 1, response.length());
                        FXMLSuccessPopupController.setSuccessMessage(responseSuccessMessage);
                        // Open ErrorPopup
                        Parent root = FXMLLoader.load(getClass().getResource("FXMLSuccessPopup.fxml"));
                        Scene scene = new Scene(root);
                        Stage stage = new Stage();
                        stage.setTitle("AwesomeChatApp Success");
                        stage.setScene(scene);
                        stage.setResizable(false);

                        stage.showAndWait();
                }
                else {
                        String responseErrorMessage = response.substring(response.indexOf(":") + 1, response.length());
                        FXMLErrorPopupController.setErrorMessage(responseErrorMessage);
                        // Open ErrorPopup
                        Parent root = FXMLLoader.load(getClass().getResource("FXMLErrorPopup.fxml"));
                        Scene scene = new Scene(root);
                        Stage stage = new Stage();
                        stage.setTitle("AwesomeChatApp Error");
                        stage.setScene(scene);
                        stage.setResizable(false);

                        stage.showAndWait();
                }
                
        }
    
}
