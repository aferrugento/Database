package bd;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.registry.LocateRegistry;
import java.io.*;
import java.util.Calendar;
import java.util.Scanner;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Vector;

public class ClientRMI {

    private DataInputStream console = null;
    private InterfaceServerRMI serverRemote;
    private RMIReceiver rmiReceiver;
    private String hostname;
    private int port;
    private User user;
    private boolean login;
    private Encryption encrypt;
    private Vector<PrivateMessage> mess;

    public ClientRMI() {
    }

    public ClientRMI(String hostname) {
        encrypt = new Encryption();
        this.hostname = hostname;
        this.port = 7000;
        this.login = false;
        console = new DataInputStream(System.in);
        try {
            this.rmiReceiver = new RMIReceiver();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (!this.Connect()) {
            System.exit(1);
        }
    }

    public boolean Connect() {
        try {
            this.serverRemote = (InterfaceServerRMI) LocateRegistry.getRegistry(this.hostname, this.port).lookup("SayMore");
            if (this.login) {
                this.serverRemote.loginRequest(user, rmiReceiver);
            }
            // iniciate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void chatrooms() {
    }

    public void sendFile() {
    }

    //#######################################MESSAGES###########################################################
    public int message(String subject, String mess, String dest) {
        User chosenUsr = null;
        User sourceUser = this.user;
        try {
            chosenUsr = this.serverRemote.searchUser(dest);
            if (chosenUsr == null || chosenUsr.getUsername() == null) {
                return -1;
            }
        } catch (IOException ioe) {
            System.out.println("IOException");
            return -1;
        }

        try {
            int id =  this.serverRemote.handleMessages(new PrivateMessage(mess, subject, chosenUsr, sourceUser));
            return id;
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
    }
    
    public int publicMessages(Chatroom chat, String content){
        PublicMessage mess = new PublicMessage(content,this.user);
        mess.setChatroomID(chat.getChatroomID());
        int id = -1;
        try {
            id = this.serverRemote.insertPublicMessage(mess);
            return id;
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public void addAttachment(String path, int messid){
        Attachment attach = new Attachment(path);
        attach.setMess(messid);
        try {
            this.serverRemote.addAttachment(attach);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Vector<Attachment> listAttachs(){
        Vector<Attachment> attachs = new Vector<Attachment>();
        try {
            attachs = this.serverRemote.listAttachs();
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return attachs;
    }
    
    public Vector<PrivateMessage> listMessagesReceived() {
        mess = new Vector<PrivateMessage>();
        try {
            mess = this.serverRemote.listMessagesReceived(this.user);
        } catch (RemoteException e) {
        }
        if (mess.size() == 0) {
            return null;
        }
        return mess;
    }

    public Vector<PrivateMessage> listMessagesSent() {
        mess = new Vector<PrivateMessage>();
        try {
            mess = this.serverRemote.listMessagesSent(this.user);
        } catch (RemoteException e) {
        }
        int y = 1;
        if (mess.size() == 0) {
            return null;
        }
        return mess;
    }

    public void read(int id, Vector<PrivateMessage> mess) {
        int pos = searchMsg(id);
        if (!mess.get(pos).isRead()) {
            try {
                this.serverRemote.readMessage(mess.get(pos));
            } catch (RemoteException e) {
            }
        }
    }

    public int searchMsg(int id) {
        for (int i = 0; i < mess.size(); i++) {
            if (id == mess.get(i).getMessageID()) {
                return i;
            }
        }
        return -1;
    }

    public int searchDelayed(int id) throws RemoteException {
        return this.serverRemote.searchDelayedMsg(id);
    }

    public void print() throws RemoteException {
        for (int i = 0; i < this.serverRemote.getMessages().size(); i++) {
            System.out.println(this.serverRemote.getMessages().get(i).getContent());
        }
    }

    public void delayedMessages(int day, int mouth, int year, int hour, int min, String dest, String subject, String mess,String path) throws RemoteException {
        Date date = new Date();
        date.setHours(hour);
        date.setMinutes(min);
        date.setSeconds(0);
        DelayedMessage pm = new DelayedMessage(mess, subject, this.user, this.serverRemote.searchUser(dest), date);
        pm.setPath(path);
        this.serverRemote.putDelayedMsg(pm);
    }

    public void cancelDelayed(int id) throws RemoteException {
        this.serverRemote.cancelDelayed(id);
    }

    public Vector<DelayedMessage> getDelayedMsgs() throws RemoteException {
        return this.serverRemote.getDelayedMessages();
    }

    public Vector<PrivateMessage> conversation(String username) {
        Vector<PrivateMessage> all = new Vector<PrivateMessage>();
        try {
            all = this.serverRemote.listMessages(username, this.user);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    public Vector<PrivateMessage> order(Vector<PrivateMessage> all) {
        PrivateMessage aux;
        for (int i = 0; i < all.size() - 1; i++) {
            if (all.get(i + 1).getReceivingTime().compareTo(all.get(i).getReceivingTime()) < 0) {
                aux = all.get(i);
                all.set(i, all.get(i + 1));
                all.set(i + 1, aux);
            }
        }
        return all;
    }

    public void deleteMessagesUnread(int pos) throws RemoteException {
        this.serverRemote.deleteMsgsUnread(pos);
    }

    public Vector<String> getUsersMessages() {
        Vector<String> users = new Vector<String>();
        try {
            users = this.serverRemote.getUsersMessages(this.user);
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).equals(this.user.getUsername())) {
                    users.remove(i);
                }
            }
            return users;
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //###############################################SETTINGS####################################################
    public Vector<User> searchUserBy(Object[] obj) {
        Vector<User> users = new Vector<User>();
        try {
            users = this.serverRemote.searchUserBy(obj);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(this.user.getUsername())) {
                users.remove(i);
            }
        }
        return users;
    }

    public int modifyInformation(Object change, int choiceNumber) {

        String tempName = null, name, nationality, citizenship, interests, email, job, city;
        boolean gender, privacy;
        Date bday;
        if (choiceNumber == 1) {
            try {
                tempName = (String) change;
                if (this.serverRemote.searchUser(tempName) != null && this.serverRemote.searchUser(tempName).getUsername() != null) {
                    return -1;
                }
                this.serverRemote.modifyInformation(this.user, tempName, 1);
                this.user.setUsername(tempName);
            } catch (IOException i) {
            }
        } else if (choiceNumber == 2) {
            name = (String) change;
            try {
                this.serverRemote.modifyInformation(this.user, name, 2);
                this.user.setName(name);
            } catch (RemoteException e) {
            }
        } else if (choiceNumber == 3) {
            nationality = (String) change;
            try {
                this.serverRemote.modifyInformation(this.user, nationality, 3);
                this.user.setNationality(nationality);
            } catch (RemoteException e) {
            }
        } else if (choiceNumber == 4) {
            citizenship = (String) change;
            try {
                this.serverRemote.modifyInformation(this.user, citizenship, 4);
                this.user.setCitizenship(citizenship);
            } catch (RemoteException e) {
            }
        } else if (choiceNumber == 5) {
            interests = (String) change;
            try {
                this.serverRemote.modifyInformation(this.user, interests, 5);
                this.user.setInterests(interests);
            } catch (RemoteException e) {
            }
        } else if (choiceNumber == 6) {
            email = Protection.insertEmail((String) change);
            try {
                this.serverRemote.modifyInformation(this.user, email, 6);
                this.user.setEmail(email);
            } catch (RemoteException e) {
            }
        } else if (choiceNumber == 7) {
            job = (String) change;
            try {
                this.serverRemote.modifyInformation(this.user, job, 7);
                this.user.setJob(job);
            } catch (RemoteException e) {
            }
        } else if (choiceNumber == 8) {
            city = (String) change;
            try {
                this.serverRemote.modifyInformation(this.user, city, 8);
                this.user.setCity(city);
            } catch (RemoteException e) {
            }
        } else if (choiceNumber == 9) {

            gender = (Boolean) change;
            try {
                this.serverRemote.modifyInformation(this.user, gender, 9);
                this.user.setGender(gender);
            } catch (RemoteException e) {
            }
        } else if (choiceNumber == 10) {
            privacy = (Boolean) change;
            try {
                this.serverRemote.modifyInformation(this.user, privacy, 10);
                this.user.setPrivacy(privacy);
            } catch (RemoteException e) {
            }
        } else if (choiceNumber == 11) {
            int[] date = (int[]) change;
            date = Protection.cria_data(date[0], date[1], date[2]);
            if (date[0] == -1) {
                return -1;
            }
            bday = new Date(date[2], date[1], date[0]);
            try {
                this.serverRemote.modifyInformation(this.user, bday, 11);
                this.user.setBirthday(bday);
            } catch (RemoteException e) {
            }
        }
        return 0;
    }

    public int modifyPassword(String previousPass, String newPass) {
        String pass = null;
        try {
            try {
                pass = encrypt.MD5(previousPass);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException e) {
        }
        if (!((pass.substring(0, 32)).equals(this.user.getPass().substring(0, 32)))) {
            return -1;
        }
        try {
            newPass = encrypt.MD5(newPass);
            this.serverRemote.modifyPassword(this.user, newPass);
            this.user.setPass(newPass);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
        }
        return 0;
    }

    public void removeAccount() {
        try {
            this.serverRemote.removeAccount(this.user);
            logout();

        } catch (IOException ioe) {
            System.out.println("Error reading from console.");
        }
    }

    public void deleteInformation() {
        try {
            this.serverRemote.deleteInformation(this.user);
            return;
        } catch (IOException ioe) {
            System.out.println("Error reading from console.");

        }
    }
    //##############################################USERS#################################################

    public void showUsers() {
        Vector<String> users = new Vector<String>();
        try {
            users = this.serverRemote.showUsers();
        } catch (RemoteException r) {
        }
        System.out.println("USERS");
        for (int i = 0; i < users.size(); i++) {
            System.out.println(users.get(i));
        }

    }

    public void logout() {
        try {
            this.serverRemote.logout(this.user);
        } catch (RemoteException e) {
        }
    }

    @SuppressWarnings("deprecation")
    public int[] register(String tempName, String tempPass, String name, String nationality, String citizenship, String interests, String email, String job, String city, int gender, Boolean privacy, int day, int month, int year) throws InvalidKeySpecException {
        Date bday = null;
        int id;
        int[] response = new int[2];
        try {
            if (this.serverRemote.searchUser(tempName) != null && this.serverRemote.searchUser(tempName).getUsername() != null) {
                response[0] = 0;
                response[1] = 1;
                return response;
            }
        } catch (IOException i) {
        }
        try {
            String pass = null;
            try {
                pass = encrypt.MD5(tempPass);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!email.equals("")) {
                email = Protection.insertEmail(email);
            }
            int[] date = new int[3];
            if (day == 0 && month == 0 && year == 0) {
                bday = null;
            } else {
                if (day == 0 || month == 0 || year == 0) {
                    date[0] = -1;
                } else {
                    date = Protection.cria_data(day, month, year);
                    bday = new Date(date[2], date[1], date[0]);
                }
            }
            if (email.equals("-1") || date[0] == -1) {
                response[0] = 0;
                response[1] = 0;
                return response;
            }
            if (gender == 2) {
                this.user = new User(name, tempName, pass, email, job, nationality, citizenship, city, interests, privacy);
            } else {
                if (gender == 0) {
                    this.user = new User(name, tempName, pass, false, email, job, nationality, citizenship, city, interests, privacy);
                } else {
                    this.user = new User(name, tempName, pass, true, email, job, nationality, citizenship, city, interests, privacy);
                }
            }
            this.user.setBirthday(bday);
            id = this.serverRemote.register(this.user, rmiReceiver);
            this.user.setUserID(id);

        } catch (IOException e) {
        }
        this.login = true;
        response[0] = 1;
        return response;

    }

    public boolean loginRequest(String username, String password) throws InvalidKeySpecException {
        User temp = null;
        try {
            temp = this.serverRemote.searchUser(username);

        } catch (IOException ioe) {
        }

        try {
            if (temp.getUsername() == null || temp == null) {
                return false;

            } else {
                try {
                    password = encrypt.MD5(password);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                if (!encrypt.authenticate(temp, password)) {
                    System.out.println("Wrong password or username. ");
                    return false;
                }
                this.user = temp;

                this.serverRemote.loginRequest(temp, rmiReceiver);

                this.login = true;
            }
        } catch (IOException e) {
        }
        return true;
    }

    //##############################################CHATROOMS#################################################
    public Vector<Chatroom> listChatrooms() {
        Vector<Chatroom> chat = new Vector<Chatroom>();
        try {
            chat = this.serverRemote.listChatrooms();
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return chat;
    }

    public Vector<Chatroom> listChatroomsOwned() {
        Vector<Chatroom> chat = new Vector<Chatroom>();
        try {
            chat = this.serverRemote.listChatroomsOwned(this.user);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return chat;
    }
    
    public Vector<Chatroom> listChatroomsJoined(){
        Vector<Chatroom> chat = new Vector<Chatroom>();
        try {
            chat = this.serverRemote.listChatroomsJoined(this.user);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return chat;
    }
    
    public Vector<PublicMessage> listMessagesChatrooms(Chatroom chat){
        Vector<PublicMessage> mess = new Vector<PublicMessage>();
        try {
            mess = this.serverRemote.listMessagesChatrooms(chat);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mess;
    }

    public Chatroom createChatroom(String theme) {
        Chatroom chat = null;
        try {
            chat = this.serverRemote.searchChatroom(theme);
            if (chat != null) {
                return null;
            } else {
                try {
                    chat = new Chatroom(theme);
                    chat.setActive(true);
                    chat.setUser(user);
                    int id = this.serverRemote.createChatroom(theme, this.user);
                    chat.setChatroomID(id);
                    return chat;
                } catch (RemoteException ex) {
                    Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean modifyChatroom(Chatroom chat, String theme) {
        Chatroom temp = null;
        try {
            temp = this.serverRemote.searchChatroom(theme);
            if (temp != null) {
                return false;
            } else {
                this.serverRemote.modifyChatroom(chat, theme);
                return true;
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public void closeChatroom(Chatroom chat) {
        try {
            this.serverRemote.closeChatroom(chat);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void joinChatroom(Chatroom chat, Boolean poster, Boolean watcher) {
        try {
            this.serverRemote.joinChatroom(chat, this.user, poster, watcher);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void modifyPermission(int chatroomid, int userid, boolean poster, boolean watcher){
        try {
            this.serverRemote.modifyPermission(chatroomid, userid, poster, watcher);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Vector<User> usersChatrooms(Chatroom chat){
        Vector<User> users = new Vector<User>();
        try {
            users = this.serverRemote.usersChatrooms(chat);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }
    
    public void banUser(Chatroom chat, User user){
        try {
            this.serverRemote.banUser(chat, user);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int [] permissions(Chatroom chat){
        int [] perm = new int [2];
        try {
            perm = this.serverRemote.permissions(chat, this.user);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return perm;
    }
    
    public int checkVotes(Chatroom chat){
        int perm = -1;
        try {
            perm = this.serverRemote.checkVotes(chat);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return perm;
    }
    
    public int howManyVotes(Chatroom chat){
        int perm = -1;
        try {
            perm = this.serverRemote.howManyVotes(chat);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return perm;
    }
    
    public void newVote(Chatroom chat, int vote){
        try {
            this.serverRemote.newVote(this.user, chat, vote);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Boolean hasVoted(Chatroom chat){
        Boolean perm = null;
        try {
            perm = this.serverRemote.hasVoted(chat,this.user);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return perm;
    }
    
    public Boolean isInChatroom(Chatroom chat){
        Boolean perm = null;
        try {
            perm = this.serverRemote.isInChatroom(chat,this.user);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return perm;
    }
    
    public Vector<Chatroom> searchChatroomBy(String username, String theme){
        Vector<Chatroom> chats = new Vector<Chatroom>();
        try {
            chats = this.serverRemote.searchChatroomBy(username,theme);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return chats;
    }
}
