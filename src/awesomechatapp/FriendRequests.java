package awesomechatapp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class FriendRequests implements Runnable {

    @Override
    public void run() {
           System.out.println("Thread started!");
        try {
            // check if any friend requests are found in the database
            Client.sendQuery(MessageType.QUERY, Operation.CHECK_FRIEND_REQUESTS, null);
            
            String responseFriendRequests = Client.waitForResponse();
            String responseFriendRequestsType = responseFriendRequests.substring(0, responseFriendRequests.indexOf(":"));
            String responseFriendRequestsData = responseFriendRequests.substring(responseFriendRequests.indexOf(":"), responseFriendRequests.length());
            
            if (responseFriendRequestsType.equals(MessageType.RESPONSE_SUCCESS.toString())) {
                if (!responseFriendRequestsData.equals(ResponseMessage.NO_FRIEND_REQUESTS.toString())) {
                    // convert data (comma separated values) into array
                    List<String> friendRequestsUsersID = Arrays.asList(responseFriendRequestsData.split("\\s*,\\s*"));
                    
                    // for each request open a separate window
                    for (int i = 0; i < friendRequestsUsersID.size(); i++) {
                        String user = responseFriendRequestsData;
                        String username = user.substring(0, user.indexOf(" "));
                        String email = user.substring(user.indexOf(" ")+1, user.length());
                        System.out.println("username: " + username + " email: " + email);
                        
                        // pass username and email of this user
                        FXMLFriendRequestWindowController.setUsername(username);
                        FXMLFriendRequestWindowController.setUsername(email);
                        
                        // open friend request window
                        openFriendRequestWindow();
                    }
                }
            }
        } 
        catch (IOException ex) {
            Logger.getLogger(FriendRequests.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    
//    @FXML
    private void openFriendRequestWindow() throws IOException {
            // Open FriendRequestWindow
            Parent root = FXMLLoader.load(getClass().getResource("FXMLFriendRequestWindow.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("AwesomeChatApp Friend Request");
            stage.setScene(scene);
            stage.setResizable(false);
            
            root.requestFocus();
            stage.showAndWait();
    }
    
}
