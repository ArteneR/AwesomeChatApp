package awesomechatapp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 *
 * @author ArteneR
 */
public class FXMLLoginFailedDialogController implements Initializable {
    
        @FXML private static Stage thisStage;
        
    
         @Override
        public void initialize(URL location, ResourceBundle resources) {

        }
    
    
        @FXML
        /*** clicked 'OK' ***/
        private void closeFailedDialog(ActionEvent event) throws IOException {
                System.out.println("Closing FailedDialog...");
                Node thisSource = (Node) event.getSource();
                thisStage = (Stage) thisSource.getScene().getWindow();
                thisStage.close();
        }
    
}
