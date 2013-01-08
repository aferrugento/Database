package bd;

import java.util.Date;
import java.io.Serializable;

public class PublicMessage extends Message implements Serializable {

    public int chatroomID;
    
    public PublicMessage(String content, User source) {
        super(content, source);
    }

    public int getChatroomID() {
        return chatroomID;
    }

    public void setChatroomID(int chatroomID) {
        this.chatroomID = chatroomID;
    }
}
