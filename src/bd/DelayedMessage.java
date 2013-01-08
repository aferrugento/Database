/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bd;

/**
 *
 * @author Mateus
 */
import java.util.ArrayList;
import java.util.Date;

public class DelayedMessage extends PrivateMessage {

    //private static final long serialVersionU = 1L;
    // private int parent;
    // private int replyLevel; 
    private Date readDate;
    private String Path;

    public DelayedMessage(String content, String subject, User source, User dest, Date readDate) {
        super(content, subject, dest, source);
        this.readDate = readDate;        
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String Path) {
        this.Path = Path;
    }
    
    public Date getReadDate() {
        return readDate;
    }    
    
    
}