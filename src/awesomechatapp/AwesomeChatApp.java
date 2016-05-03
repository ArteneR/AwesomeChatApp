package awesomechatapp;

import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 *
 * @author ArteneR
 */
public class AwesomeChatApp extends Application {
    
    
    @Override
    public void start(Stage stage) throws Exception {
            // Open LoginWindow
            Parent root = FXMLLoader.load(getClass().getResource("FXMLLoginWindow.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("AwesomeChatApp Login");
            stage.setResizable(false);
            stage.show();
    }

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
