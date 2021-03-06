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
import javafx.stage.Stage;


/**
 *
 * @author ArteneR
 */
public class FXMLSignUpConfirmationFailedDialogController implements Initializable {

        @FXML private static Stage thisStage;
        
    
        @Override
        public void initialize(URL location, ResourceBundle resources) {
                System.out.println("Opening SignUpConfirmationFailedDialog...");
        }
    
        
        @FXML
        /*** 'OK' clicked ***/
        private void closeFailedDialog(ActionEvent event) throws IOException {
                System.out.println("Closing FailedDialog...");
                Node thisSource = (Node) event.getSource();
                thisStage = (Stage) thisSource.getScene().getWindow();
                thisStage.close();
                
                Parent root = FXMLLoader.load(getClass().getResource("FXMLSignUpConfirmationWindow.fxml"));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("AwesomeChatApp Email Confirmation Code");
                stage.setResizable(false);
                stage.show();
                root.requestFocus();
            
        }
        
}
