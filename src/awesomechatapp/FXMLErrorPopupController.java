package awesomechatapp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class FXMLErrorPopupController implements Initializable {

    private static String errorMessage = "";
    @FXML private static Stage thisStage;
    @FXML private Button btnOk;
    @FXML private Text txtErrorMessage;
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
            System.out.println("Opening ErrorPopup...");
            txtErrorMessage.setText(errorMessage);
    }
    
    public static void setErrorMessage(String error) {
            errorMessage = error;
    }
    
    @FXML
    /*** clicked 'OK' ***/
    private void clickedOk(ActionEvent event) throws IOException {
            // Close this popup
            System.out.println("Closing ErrorPopup...");
            Node thisSource = (Node) event.getSource();
            thisStage = (Stage) thisSource.getScene().getWindow();
            thisStage.close();
    }
    
}
