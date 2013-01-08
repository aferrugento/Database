package bd;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIReceiver extends UnicastRemoteObject implements InterfaceClientRMI {

    protected RMIReceiver() throws RemoteException {
        super();
    }

    public void receive(String msg, byte[] file) {
        System.out.println(msg);
    }
}
