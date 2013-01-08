package bd;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.RemoteException;

public class ClientRMIConnection {

    private User user;
    private InterfaceClientRMI remote;

    public ClientRMIConnection(User user, InterfaceClientRMI remote) {
        this.user = user;
        this.remote = remote;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void send(String msg, byte[] file) {
        try {
            this.remote.receive(msg, file);
            return;
        } catch (RemoteException e) {
            System.out.println("Remote exception caught.");
            send(msg, file);
            return;
        }
    }

    public boolean auth(InterfaceClientRMI remote) {
        return this.remote.equals(remote);
    }
}
