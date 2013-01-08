package bd;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adriana
 */
public class ServerRMI extends UnicastRemoteObject implements InterfaceServerRMI {

    public ArrayList<ClientRMIConnection> clients;
    public int countID;
    private DataBase db;
    private Vector<DataBase> connections;
    private Vector<DelayedMessage> delayedMessages;
    private Vector<PrivateMessage> mess;

    public ServerRMI() throws RemoteException {
        super();
        this.clients = new ArrayList<ClientRMIConnection>();
        this.delayedMessages = new Vector<DelayedMessage>();
        this.mess = new Vector<PrivateMessage>();
        try {
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("SayMore", this);
            System.out.println("Starting RMIServer.");
        } catch (RemoteException re) {
            System.out.println("Remote exception: " + re);
        }
        connections = new Vector<DataBase>();
        for (int i = 0; i < 10; i++) {
            connections.add(new DataBase());
        }
    }
    //########################################USER###############################################################

    public void logout(User user) {
        this.remove(user);
    }

    public synchronized void remove(User user) {
        for (int i = 0; i < this.clients.size(); i++) {
            if (this.clients.get(i).getUser().getUsername().equals(user.getUsername())) {
                clients.remove(i);
                break;
            }
        }
    }

    public ClientRMIConnection getClient(User u) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getUser().getUsername().equals(u.getUsername())) {
                return clients.get(i);
            }
        }
        return null;
    }

    public User searchUser(String name) {
        User temp;
        DataBase aux;
        aux = getConnection();
        temp = aux.searchUser(name);
        setConnection(aux);
        return temp;
    }

    public Vector<String> showUsers() {
        Vector<String> users = new Vector<String>();
        DataBase aux;
        aux = getConnection();
        users = aux.showUsers();
        setConnection(aux);
        return users;
    }

    public void loginRequest(User user, InterfaceClientRMI remote) {
        ClientRMIConnection client = new ClientRMIConnection(user, remote);
        this.clients.add(client);
    }

    public int register(User user, InterfaceClientRMI remote) {
        ClientRMIConnection client;
        DataBase aux;
        int id;
        aux = getConnection();
        id = aux.insertUser(user);
        setConnection(aux);
        user.setUserID(id);
        client = new ClientRMIConnection(user, remote);
        this.clients.add(client);
        return id;
    }

    //#######################################SETTINGS##################################################
    public void modifyInformation(User user, Object x, int type) {
        DataBase aux;
        String temp;
        aux = getConnection();
        try {
            if (type == 1) {
                temp = (String) x;
                aux.updateUsername(user.getUsername(), temp);

            } else if (type == 2) {
                temp = (String) x;
                aux.updateName(user.getUsername(), temp);
            } else if (type == 3) {
                temp = (String) x;
                aux.updateNationality(user.getUsername(), temp);
            } else if (type == 4) {
                temp = (String) x;
                aux.updateCitizenship(user.getUsername(), temp);
            } else if (type == 5) {
                temp = (String) x;
                aux.updateInterests(user.getUsername(), temp);
            } else if (type == 6) {
                temp = (String) x;
                aux.updateEmail(user.getUsername(), temp);
            } else if (type == 7) {
                temp = (String) x;
                aux.updateJob(user.getUsername(), temp);
            } else if (type == 8) {
                temp = (String) x;

                aux.updateCity(user.getUsername(), temp);
            } else if (type == 9) {
                Boolean gen = (Boolean) x;
                int gender = 0;
                if (gen) {
                    gender = 1;
                }
                aux.updateGender(user.getUsername(), gender);
            } else if (type == 10) {
                Boolean pri = (Boolean) x;
                int privacy = 0;
                if (pri) {
                    privacy = 1;
                }
                aux.updatePrivacy(user.getUsername(), privacy);
            } else if (type == 11) {
                Date bday = (Date) x;
                String bday_temp = String.format("%d-%d-%d", bday.getDate(), bday.getMonth(), bday.getYear());
                aux.updateBday(user.getUsername(), bday_temp);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        setConnection(aux);
    }

    public void modifyPassword(User user, String pass) {
        DataBase aux;
        aux = getConnection();
        aux.updatePassword(user.getUsername(), pass);
        setConnection(aux);
    }

    public void deleteInformation(User user) {
        DataBase aux;
        aux = getConnection();
        aux.deleteInformation(user.getUserID());
        setConnection(aux);
    }

    public void removeAccount(User user) {
        DataBase aux;
        aux = getConnection();
        aux.updateActivity(user.getUsername(), 0);
        setConnection(aux);
    }

    //########################################MESSAGES################################################################
    public Vector<User> searchUserBy(Object[] obj) {
        Vector<User> users = new Vector<User>();
        DataBase aux;
        aux = getConnection();
        users = aux.searchUserBy(obj);
        setConnection(aux);

        return users;
    }

    public void putDelayedMsg(DelayedMessage dm) {
        this.delayedMessages.add(dm);
        int id = this.delayedMessages.indexOf(dm);
        this.delayedMessages.get(id).setMessageID(id);
        new Timer().schedule(new DelayTask(searchDelayedMsg(dm.getMessageID()), this), dm.getReadDate());
    }

    public int searchDelayedMsg(int id) {
        for (int i = 0; i < delayedMessages.size(); i++) {
            if (id == delayedMessages.get(i).getMessageID()) {
                return i;
            }
        }
        return -1;
    }

    public void cancelDelayed(int id) {
        this.delayedMessages.removeElementAt(id);
    }

    public Vector<PrivateMessage> listMessagesSent(User user) {
        DataBase aux;
        aux = getConnection();
        mess = aux.listMessagesSent(user);
        setConnection(aux);
        return mess;
    }

    public Vector<PrivateMessage> listMessages(String user, User sender) {
        DataBase aux;
        aux = getConnection();
        mess = aux.listMessages(user, sender);
        setConnection(aux);
        return mess;
    }

    public void deleteMsgsUnread(int pos) {
        DataBase aux;
        aux = getConnection();
        aux.removeMessage(mess.elementAt(pos));
        mess.remove(pos);
    }

    public Vector<PrivateMessage> getMessages() {
        return mess;
    }

    public void setMessages(Vector<PrivateMessage> mess) {
        this.mess = mess;
    }

    public Vector<DelayedMessage> getDelayedMessages() {
        return delayedMessages;
    }

    public void setDelayedMessages(Vector<PrivateMessage> mess) {
        this.mess = mess;
    }

    public int handleMessages(PrivateMessage mess) {
        DataBase aux;
        aux = getConnection();
        int id = aux.insertMessage(mess);
        setConnection(aux);
        return id;
    }

    public String newMessages(User user) {
        int count = 0;
        Vector<PrivateMessage> mess = new Vector<PrivateMessage>();
        mess = listMessagesReceived(user);
        for (int i = 0; i < mess.size(); i++) {
            if (mess.get(i).getDest().getUsername().equals(user.getUsername()) && (!(mess.get(i).isRead()))) {
                count++;
            }
        }
        return "You have " + count + " messages uread!";
    }

    public Vector<PrivateMessage> listMessagesReceived(User user) {
        DataBase aux;
        Vector<PrivateMessage> mess = new Vector<PrivateMessage>();
        aux = getConnection();
        mess = aux.listMessagesReceived(user);
        setConnection(aux);
        return mess;
    }

    public void readMessage(PrivateMessage mess) {
        DataBase aux;
        aux = getConnection();
        aux.updateRead(mess.getMessageID());
        setConnection(aux);
    }

    public Vector<String> getUsersMessages(User user) {
        Vector<String> users = new Vector<String>();
        DataBase aux;
        aux = getConnection();
        users = aux.getUsersMessages(user);
        setConnection(aux);
        return users;
    }

    public int insertPublicMessage(PublicMessage mess) {
        DataBase aux;
        int id;
        aux = getConnection();
        id = aux.insertPublicMessage(mess);
        setConnection(aux);
        return id;
    }

    public void addAttachment(Attachment attach) {
        DataBase aux;
        aux = getConnection();
        aux.addAttachment(attach.getMess(),attach.getPath());
        setConnection(aux);
    }

    //###################################CONNECTIONS######################################################
    public DataBase getConnection() {
        DataBase aux;
        if (connections.size() != 0) {
            aux = connections.remove(0);
        } else {
            for (int i = 0; i < 10; i++) {
                connections.add(new DataBase());
            }
            aux = connections.remove(0);
        }
        return aux;
    }

    public void setConnection(DataBase conn) {
        connections.add(conn);
    }

    //###################################CHATROOMS######################################################
    public Vector<Chatroom> listChatrooms() {
        Vector<Chatroom> chat = new Vector<Chatroom>();
        DataBase aux;
        aux = getConnection();
        chat = aux.listChatrooms();
        setConnection(aux);
        return chat;
    }

    public int createChatroom(String theme, User user) {
        int id;
        DataBase aux;
        aux = getConnection();
        id = aux.createChatroom(theme, user.getUserID());
        setConnection(aux);
        return id;
    }

    public Chatroom searchChatroom(String theme) {
        DataBase aux;
        Chatroom temp;
        aux = getConnection();
        temp = aux.searchChatroom(theme);
        setConnection(aux);
        return temp;
    }

    public Vector<Chatroom> listChatroomsOwned(User user) {
        Vector<Chatroom> chat = new Vector<Chatroom>();
        DataBase aux;
        aux = getConnection();
        chat = aux.listChatroomsOwned(user);
        setConnection(aux);

        return chat;
    }

    public void modifyChatroom(Chatroom chat, String theme) {
        DataBase aux;
        aux = getConnection();
        aux.modifyChatroom(chat.getChatroomID(), theme);
        setConnection(aux);
    }

    public void closeChatroom(Chatroom chat) {
        DataBase aux;
        aux = getConnection();
        aux.closeChatroom(chat.getChatroomID());
        setConnection(aux);
    }

    public void joinChatroom(Chatroom chat, User user, Boolean poster, Boolean watcher) {
        DataBase aux;
        aux = getConnection();
        aux.joinChatroom(chat.getChatroomID(), user.getUserID(), poster, watcher);
        setConnection(aux);
    }

    public void modifyPermission(int chatroomid, int userid, boolean poster, boolean watcher) {
        DataBase aux;
        aux = getConnection();
        aux.modifyPermission(chatroomid, userid, poster, watcher);
        setConnection(aux);
    }

    public Vector<Chatroom> listChatroomsJoined(User user) {
        Vector<Chatroom> chat = new Vector<Chatroom>();
        DataBase aux;
        aux = getConnection();
        chat = aux.listChatroomsJoined(user);
        setConnection(aux);
        return chat;
    }

    public Vector<PublicMessage> listMessagesChatrooms(Chatroom chat) {
        Vector<PublicMessage> mess = new Vector<PublicMessage>();
        DataBase aux;
        aux = getConnection();
        mess = aux.listMessagesChatrooms(chat.getChatroomID());
        setConnection(aux);
        return mess;
    }

    public Vector<Attachment> listAttachs() {
        Vector<Attachment> attachs = new Vector<Attachment>();
        DataBase aux;
        aux = getConnection();
        attachs = aux.listAttachs();
        setConnection(aux);
        return attachs;
    }

    public Vector<User> usersChatrooms(Chatroom chat) {
        Vector<User> users = new Vector<User>();
        DataBase aux;
        aux = getConnection();
        users = aux.usersChatrooms(chat);
        setConnection(aux);
        return users;
    }

    public void banUser(Chatroom chat, User user){
        DataBase aux;
        aux = getConnection();
        aux.banUser(chat,user);
        setConnection(aux);
    }
    
    public int [] permissions(Chatroom chat, User user){
        int [] perm = new int [2];
        DataBase aux;
        aux = getConnection();
        perm = aux.permissions(chat,user);
        setConnection(aux);
        return perm;
    }
    
    public int howManyVotes(Chatroom chat){
        int perm;
        DataBase aux;
        aux = getConnection();
        perm = aux.howManyVotes(chat);
        setConnection(aux);
        return perm;
    }
    
    public int checkVotes(Chatroom chat){
        int perm;
        DataBase aux;
        aux = getConnection();
        perm = aux.checkVotes(chat);
        setConnection(aux);
        return perm;
    }
    
    public Boolean hasVoted(Chatroom chat, User user){
        Boolean perm;
        DataBase aux;
        aux = getConnection();
        perm = aux.hasVoted(chat,user);
        setConnection(aux);
        return perm;
    }
    
    public void newVote(User user,Chatroom chat,int vote){
        DataBase aux;
        aux = getConnection();
        aux.newVote(user,chat,vote);
        setConnection(aux);
    }
    
    public Boolean isInChatroom(Chatroom chat, User user){
        Boolean isIn;
        DataBase aux;
        aux = getConnection();
        isIn = aux.isInChatroom(chat,user);
        setConnection(aux);
        return isIn;
    }
    
    public Vector<Chatroom> searchChatroomBy(String username, String theme){
        Vector<Chatroom> chats;
        DataBase aux;
        aux = getConnection();
        chats = aux.searchChatroomBy(username,theme);
        setConnection(aux);
        return chats;
    }
    
    public static void main(String args[]) {
        try {
            new ServerRMI();
        } catch (RemoteException ex) {
            Logger.getLogger(ServerRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class DelayTask extends TimerTask {

    private int dpID;
    private ServerRMI server;

    public DelayTask(int dpID, ServerRMI sv) {
        this.dpID = dpID;
        this.server = sv;
    }

    public void run() {
        DelayedMessage dm = server.getDelayedMessages().get(dpID);
        server.getDelayedMessages().remove(dpID);
        dm.setSendTime(new Date());
        int id = server.handleMessages(dm);
        if (!dm.getPath().trim().equals("")) {
            Attachment attach = new Attachment(dm.getPath());
            attach.setMess(id);
            server.addAttachment(attach);
        }
        //BD e notificação
    }
}
