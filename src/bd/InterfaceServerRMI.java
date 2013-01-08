package bd;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.*;
import java.util.concurrent.*;
import java.util.Vector;

public interface InterfaceServerRMI extends Remote {
    
    public Vector<Chatroom> searchChatroomBy(String username, String theme) throws RemoteException;
    
    public Boolean isInChatroom(Chatroom chat, User user) throws RemoteException;
    
    public Boolean hasVoted(Chatroom chat, User user) throws RemoteException;
    
    public void newVote(User user,Chatroom chat,int vote) throws RemoteException;
    
    public int checkVotes(Chatroom chat) throws RemoteException;
    
    public int howManyVotes(Chatroom chat) throws RemoteException;
    
    public int [] permissions(Chatroom chat, User user) throws RemoteException;
    
    public void banUser(Chatroom chat, User user) throws RemoteException;
    
    public Vector<User> usersChatrooms(Chatroom chat) throws RemoteException;
    
    public Vector<Attachment> listAttachs() throws RemoteException;
    
    public void addAttachment(Attachment attach) throws RemoteException;
    
    public int insertPublicMessage(PublicMessage mess) throws RemoteException;
    
    public Vector<PublicMessage> listMessagesChatrooms(Chatroom chat) throws RemoteException;
    
    public Vector<Chatroom> listChatroomsJoined(User user) throws RemoteException;
    
    public void modifyPermission(int chatroomid, int userid, boolean poster, boolean watcher) throws RemoteException;
    
    public void joinChatroom(Chatroom chat, User user, Boolean poster, Boolean watcher) throws RemoteException;
    
    public void closeChatroom(Chatroom chat) throws RemoteException;
    
    public void modifyChatroom(Chatroom chat, String theme) throws RemoteException;
    
    public Vector<Chatroom> listChatroomsOwned(User user) throws RemoteException;
    
    public Chatroom searchChatroom(String theme) throws RemoteException;
    
    public int createChatroom(String theme, User user) throws RemoteException;
    
    public Vector<Chatroom> listChatrooms() throws RemoteException;
    
    public Vector<PrivateMessage> listMessages(String user, User sender) throws RemoteException;

    public Vector<String> getUsersMessages(User user) throws RemoteException;

    public Vector<User> searchUserBy(Object[] obj) throws RemoteException;

    public String newMessages(User user) throws RemoteException;

    public void modifyInformation(User user, Object x, int type) throws RemoteException;

    public void modifyPassword(User user, String pass) throws RemoteException;

    public void removeAccount(User user) throws RemoteException;

    public void deleteInformation(User user) throws RemoteException;

    public int handleMessages(PrivateMessage mess) throws RemoteException;

    public void loginRequest(User user, InterfaceClientRMI remote) throws RemoteException;

    public int register(User user, InterfaceClientRMI remote) throws RemoteException;

    public User searchUser(String name) throws RemoteException;

    public Vector<PrivateMessage> listMessagesReceived(User user) throws RemoteException;

    public void readMessage(PrivateMessage mess) throws RemoteException;

    public void logout(User user) throws RemoteException;

    public Vector<String> showUsers() throws RemoteException;

    public Vector<PrivateMessage> listMessagesSent(User user) throws RemoteException;

    public Vector<PrivateMessage> getMessages() throws RemoteException;

    public void setMessages(Vector<PrivateMessage> mess) throws RemoteException;

    public Vector<DelayedMessage> getDelayedMessages() throws RemoteException;

    public void setDelayedMessages(Vector<PrivateMessage> mess) throws RemoteException;

    public void putDelayedMsg(DelayedMessage dm) throws RemoteException;

    public void cancelDelayed(int id) throws RemoteException;

    public int searchDelayedMsg(int id) throws RemoteException;

    public void deleteMsgsUnread(int pos) throws RemoteException;
}
