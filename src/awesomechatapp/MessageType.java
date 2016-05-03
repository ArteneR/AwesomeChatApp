package awesomechatapp;

public enum MessageType {
        CLOSE_CONNECTION("CLOSE_CONNECTION"), 
        NORMAL_MESSAGE("NORMAL_MESSAGE"), 
        QUERY("QUERY"), 
        RESPONSE_SUCCESS("RESPONSE_SUCCESS"), 
        RESPONSE_FAILED("RESPONSE_FAILED"),
        FRIEND_REQUEST("FRIEND_REQUEST");
        
        private final String text;
        
        
        private MessageType(final String text) {
            this.text = text;
        }
        
         @Override
        public String toString() {
            return text;
        }
}
