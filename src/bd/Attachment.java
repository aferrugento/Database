/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bd;

import java.io.Serializable;

/**
 *
 * @author Adriana
 */
public class Attachment implements Serializable{
    private String path;
    private int ID;
    private int mess;

    public Attachment(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getMess() {
        return mess;
    }

    public void setMess(int mess) {
        this.mess = mess;
    }

}
