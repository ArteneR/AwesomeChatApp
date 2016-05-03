package awesomechatapp;

import javafx.scene.image.Image;


public class Friend {
        private String username;
        private String status;
        private Image photo;
    
        
        public Friend(String username, String status, Image photo) {
                this.username = username;
                this.status = status;
                this.photo = photo;
        }
        
        public String getUsername() {
                return username;
        }
        
        public String getStatus() {
                return status;
        }

        public Image getPhoto() {
                return photo;
        }
        
        public String toString() {
                return username + " " + status;
        }
    
}
