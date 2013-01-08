package bd;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Date;
import java.io.Serializable;

public class PrivateMessage extends Message implements Serializable {

    private String subject;
    private boolean read;
    private Date receivingTime;
    private User dest;

    public PrivateMessage(String content, String subject, User dest, User source) {
        super(content, source);
        this.subject = subject;
        this.read = false;
        this.dest = dest;
    }

    public boolean isRead() {
        return this.read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public User getDest() {
        return this.dest;
    }

    public void setDest(User dest) {
        this.dest = dest;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getReceivingTime() {
        return receivingTime;
    }

    public void setReceivingTime(Date receivingTime) {
        this.receivingTime = receivingTime;
    }
}
