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


public class FXMLForgotPasswordWindowController implements Initializable {
    
        @FXML private TextField tfEmailRetrievePassword;
        @FXML private Stage thisStage;
        @FXML private Button btnSendRequest;
        
        
        @Override
        public void initialize(URL location, ResourceBundle resources) {
                System.out.println("Opening Forgot Password Window...");
        }
    
        
        @FXML
        private void clickedSendRequest(ActionEvent event) throws IOException {
                String params = tfEmailRetrievePassword.getText();
                System.out.println("Clicked 'Send Request'...");
                
                Client client = new Client();
                
                Client.sendQuery(MessageType.QUERY, Operation.FORGOT_PASSWORD, params);
                String response = Client.waitForResponse();
                String responseType = response.substring(0, response.indexOf(":"));

                if (responseType.equals(MessageType.RESPONSE_SUCCESS.toString())) {
                        // Close this window
                        Node thisSource = (Node) event.getSource();
                        thisStage = (Stage) thisSource.getScene().getWindow();
                        thisStage.close();
                        
                        FXMLResetPasswordWindowController.setEmailAddress(tfEmailRetrievePassword.getText());
                        // Open ResetPasswordWindow
                        Parent root = FXMLLoader.load(getClass().getResource("FXMLResetPasswordWindow.fxml"));
                        Scene scene = new Scene(root);
                        Stage stage = new Stage();
                        stage.setTitle("AwesomeChatApp Reset Password");
                        stage.setScene(scene);
                        stage.setResizable(false);

                        stage.show();
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
