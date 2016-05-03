package awesomechatapp;

public enum ResponseMessage {
        NO_FRIEND_REQUESTS("NO_FRIEND_REQUESTS"),
        NO_FRIENDS("NO_FRIENDS");
        
        
        private final String text;
        
        private ResponseMessage(final String text) {
            this.text = text;
        }
        
         @Override
        public String toString() {
            return text;
        }
}
