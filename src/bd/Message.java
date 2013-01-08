package bd;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Date;
import java.io.Serializable;

/**
 *
 * @author Adriana
 */
public class Message implements Serializable {

    private String content;
    private Date sendTime;
    private User source;
    private int messageID;

    public Message(String content, User source) {
        this.source = source;
        this.content = content;
        sendTime = new Date();
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public User getSource() {
        return this.source;
    }

    public void setSource(User source) {
        this.source = source;
    }
}
