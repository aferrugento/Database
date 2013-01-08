package bd;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceClientRMI extends Remote {

    public void receive(String msg, byte[] file) throws RemoteException;
}
