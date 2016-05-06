package awesomechatapp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;


public class FXMLMainWindowController implements Initializable {

    @FXML private Stage thisStage;
    @FXML private AnchorPane anchorPane;
    @FXML private ImageView newPhoto;
    @FXML private ImageView ivMyPhoto;
    @FXML private Button btnAddFriend;
    @FXML private Button btnLogout;
    @FXML private TextField tfEnterStatus;
    @FXML private Label lblMyUsername;
    @FXML private Label lblMyStatus;
    private Tooltip tooltipLabel2;
    private ArrayList<Label> usernames; 
    private ArrayList<Label> statuses;
    private ArrayList<Tooltip> tooltips;
    private static final double FRIEND_PANE_HEIGHT = 70.0;
    private static final double FRIEND_PANE_WIDTH = 330.0;
    private static final String FRIEND_PANE_BACKGROUND_COLOR = "#EBF4FA";
    private static final String FRIEND_PANE_BACKGROUND_COLOR_HIGHLIGHTED = "#DBE4EA";
    private static final double FRIEND_PHOTO_HEIGHT = 60.0;
    private static final double FRIEND_PHOTO_WIDTH = 60.0;
    private static final double FRIEND_PHOTO_LAYOUT_X = 5.0;
    private static final double FRIEND_PHOTO_LAYOUT_Y = 5.0;
    private static final double FRIEND_USERNAME_HEIGHT = 17.0;
    private static final double FRIEND_USERNAME_WIDTH = 135.0;
    private static final double FRIEND_USERNAME_LAYOUT_X = 70.0;
    private static final double FRIEND_USERNAME_LAYOUT_Y = 14.0;
    private static final double FRIEND_STATUS_HEIGHT = 17.0;
    private static final double FRIEND_STATUS_WIDTH = 135.0;
    private static final double FRIEND_STATUS_LAYOUT_X = 70.0;
    private static final double FRIEND_STATUS_LAYOUT_Y = 35.0;
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            System.out.println("Opening MainWindow...");
            
            lblMyUsername.setText(Client.getUsername());
            ivMyPhoto.setImage(Client.getUserPhoto());
            ivMyPhoto.setCursor(Cursor.HAND);
            
            ArrayList<Friend> friends = Client.getFriends();
            
            // set friends list container
            anchorPane.setPrefHeight(FRIEND_PANE_HEIGHT + friends.size()*FRIEND_PANE_HEIGHT);

            // Add info for each user in the friends list (username, status and image)
            for (int i = 0; i < friends.size(); i++) {
                    System.out.println("adding Pane...");
                    Friend newFriend = friends.get(i);
                    
                    // set friend container
                    Pane newPane = new Pane();
                    newPane.setStyle("-fx-border-color: transparent transparent #98AFC7 transparent; -fx-background-color: " + FRIEND_PANE_BACKGROUND_COLOR + ";");
                    newPane.setPrefSize(FRIEND_PANE_WIDTH, FRIEND_PANE_HEIGHT);
                    newPane.setLayoutY(i*FRIEND_PANE_HEIGHT);
                    newPane.setCursor(Cursor.HAND);
                    newPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                                            if(mouseEvent.getClickCount() == 2){
                                                    try {
                                                        openChatWindow(mouseEvent, newFriend);
                                                    } 
                                                    catch (IOException ex) {
                                                        Logger.getLogger(FXMLMainWindowController.class.getName()).log(Level.SEVERE, null, ex);
                                                    }
                                            }
                                    }
                            }
                    });
                    newPane.setOnMouseEntered((Event event) -> {
                            newPane.setStyle("-fx-border-color: transparent transparent #98AFC7 transparent; -fx-background-color: " + FRIEND_PANE_BACKGROUND_COLOR_HIGHLIGHTED + ";");
                    });
                    newPane.setOnMouseExited((Event event) -> {
                            newPane.setStyle("-fx-border-color: transparent transparent #98AFC7 transparent; -fx-background-color: " + FRIEND_PANE_BACKGROUND_COLOR + ";");
                    });

                    // set the photo
                    newPhoto = new ImageView();
                    newPhoto.setFitHeight(FRIEND_PHOTO_HEIGHT);
                    newPhoto.setFitWidth(FRIEND_PHOTO_WIDTH);
                    newPhoto.setLayoutX(FRIEND_PHOTO_LAYOUT_X);
                    newPhoto.setLayoutY(FRIEND_PHOTO_LAYOUT_Y);
                    newPhoto.setPickOnBounds(true);
                    newPhoto.setPreserveRatio(true);
                    File file = new File("images/user2.png");
                    Image newImage = new Image(file.toURI().toString());
                    newPhoto.setImage(newImage);
                    
                    // set the username
                    Label newUsername = new Label(friends.get(i).getUsername());
                    newUsername.setLayoutX(FRIEND_USERNAME_LAYOUT_X);
                    newUsername.setLayoutY(FRIEND_USERNAME_LAYOUT_Y);
                    newUsername.setPrefSize(FRIEND_USERNAME_WIDTH, FRIEND_USERNAME_HEIGHT);
                    
                    // set the status
                    Label newStatus = new Label(friends.get(i).getStatus());
                    newStatus.setLayoutX(FRIEND_STATUS_LAYOUT_X);
                    newStatus.setLayoutY(FRIEND_STATUS_LAYOUT_Y);
                    newStatus.setPrefSize(FRIEND_STATUS_WIDTH, FRIEND_STATUS_HEIGHT);
                    
                    // add information inside the friend pane
                    newPane.getChildren().add(newPhoto);
                    newPane.getChildren().add(newUsername);
                    newPane.getChildren().add(newStatus);
                    
                    // add friend pane to the friends list pane
                    anchorPane.getChildren().add(newPane);
            }
            
            tfEnterStatus.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent keyEvent) {
                            if (keyEvent.getCode() == KeyCode.ENTER)  {
                                    String newStatus = tfEnterStatus.getText();
                                    lblMyStatus.setText(newStatus);
                                    Client.setStatus(newStatus);
                                    tfEnterStatus.setVisible(false);
                            }
                    }
            });
             
            String statusTooltip = "Click here to change the status";
            Tooltip lblMyStatusTooltip = new Tooltip(statusTooltip);
            lblMyStatus.setTooltip(lblMyStatusTooltip);
            
            String statusTextFieldStatus = "Press Enter to confirm status change";
            Tooltip tfEnterStatusTooltip = new Tooltip(statusTextFieldStatus);
            tfEnterStatus.setTooltip(tfEnterStatusTooltip);
        
    }    
    
    
    @FXML
    /*** 'Logout' clicked ***/
    private void clickedLogout(ActionEvent event) throws IOException {
            Client.sendQuery(MessageType.QUERY, Operation.LOGOUT, null);
            
            openLoginWindow(event);
            System.out.println("Repsonsee");
            String response = Client.waitForResponse();
            String responseType = response.substring(0, response.indexOf(":"));
            System.out.println("Repsonsee");
    }
    
    
    @FXML
    /*** clicked 'Logout' ***/
    private void openLoginWindow(ActionEvent event) throws IOException {
            // Close this window
            Node thisSource = (Node) event.getSource();
            Stage thisStage = (Stage) thisSource.getScene().getWindow();
            thisStage.close();
        
            // Open LoginWindow
            Parent root = FXMLLoader.load(getClass().getResource("FXMLLoginWindow.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("AwesomeChatApp Login");
            stage.setResizable(false);
            
            stage.show();
            
    }
    
    
    @FXML
    /*** One of the friends double clicked ***/
    private void openChatWindow(MouseEvent event, Friend friend) throws IOException {
            FXMLChatWindowController.setFriend(friend);
        
//            Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                            while(true) {
//                                    // NOT WORKING !
//                                    FXMLChatWindowController.writeMessageFromFriend("sdfsfdsdfdsf");
//                                    System.out.println("sdfsdfsfd");
//                            }
//                    } 
//            });
            
            
            // create listener thread
//            Thread listenerThread = new Thread(new ListenerThread());
//            listenerThread.start();
            
            
            // Open ChatWindow
            Parent root = FXMLLoader.load(getClass().getResource("FXMLChatWindow.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("AwesomeChatApp Chat");
            stage.setResizable(false);
            
            Node thisSource = (Node) event.getSource();
            thisStage = (Stage) thisSource.getScene().getWindow();
            double newWindowX = thisStage.getX() + 50;
            double newWindowY = thisStage.getY() + 50;
            stage.setX(newWindowX);
            stage.setY(newWindowY);
            
            stage.showAndWait();
    }
    
    
    @FXML
    /*** clicked 'Add Friend' ***/
    private void clickedAddFriend(ActionEvent event) throws IOException {
            // Open ChatWindow
            Parent root = FXMLLoader.load(getClass().getResource("FXMLAddFriendWindow.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("AwesomeChatApp Add Friend");
            stage.setResizable(false);
        
            stage.showAndWait();
    }
    
    
    @FXML
    /*** clicked 'My Photo' ***/
    private void clickedMyPhoto(MouseEvent event) throws IOException {
            FileChooser fileChooser = new FileChooser();

            //Set extension filter
            List<String> allExtensions = new ArrayList<String>();
            allExtensions.add("*.JPG");
            allExtensions.add("*.PNG");
            
            FileChooser.ExtensionFilter extFilterALL = new FileChooser.ExtensionFilter("All files (*.*)", allExtensions);
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            fileChooser.getExtensionFilters().addAll(extFilterALL, extFilterJPG, extFilterPNG);

            //Show open file dialog
            File file = fileChooser.showOpenDialog(null);
                       
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                ivMyPhoto.setImage(image);
                Client.setUserPhoto(image);
            }
            catch (IOException ex) {
                Logger.getLogger(FXMLMainWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    
    @FXML
    private void clickedStatus(MouseEvent event) throws IOException {
            tfEnterStatus.setVisible(true);
    }
    
    
}
