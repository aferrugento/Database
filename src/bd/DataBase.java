package bd;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Vector;

public class DataBase {

    final String username = "bd01";
    final String password = "bd01";
    final String url = "jdbc:oracle:thin:@localhost:1521:xe";
    private Connection conn;

    public DataBase() {
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            conn = DriverManager.getConnection(url, username, password);
            conn.setAutoCommit(false);
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int createChatroom(String theme, int id) {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := createChatroom(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, theme);
            ct.setInt(3, id);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return getChatroom(theme);
    }

    public int modifyChatroom(int id, String theme) {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := modifyChatroom(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setInt(2, id);
            ct.setString(3, theme);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int modifyPermission(int chatroomid, int userid, boolean poster, boolean watcher) {
        int reply = -1;
        int pos, wat;
        if (poster) {
            pos = 1;
        } else {
            pos = 0;
        }
        if (watcher) {
            wat = 1;
        } else {
            wat = 0;
        }
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := modifyPermission(?,?,?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setInt(2, chatroomid);
            ct.setInt(3, userid);
            ct.setInt(4, pos);
            ct.setInt(5, wat);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int getChatroom(String theme) {
        int id = 0;
        try {
            Statement st = this.conn.createStatement();
            ResultSet rset = st.executeQuery("SELECT * FROM chatrooms WHERE theme='" + theme + "'");
            while (rset.next()) {
                id = rset.getInt("chatroomid");

            }
            st.close();
            return id;


        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public Vector<Chatroom> listChatroomsOwned(User user) {
        Vector<Chatroom> chat = new Vector<Chatroom>();

        try {
            Statement st = this.conn.createStatement();
            String sql = "SELECT * FROM chatrooms WHERE userid=?";
            //ResultSet rset = st.executeQuery("SELECT * FROM chatrooms WHERE userid=" + user.getUserID());
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, user.getUserID());
            ResultSet rset = pst.executeQuery();
            while (rset.next()) {
                Chatroom temp = new Chatroom(rset.getString("theme"));
                temp.setChatroomID(rset.getInt("chatroomid"));
                temp.setUser(user);
                temp.setActive(rset.getBoolean("active"));
                temp.setRate(countVotes(temp));
                chat.add(temp);
            }
            st.close();
            return chat;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Vector<Chatroom> listChatroomsJoined(User user) {
        Vector<Chatroom> chat = new Vector<Chatroom>();
        try {
            Statement st = this.conn.createStatement();
            String sql = "SELECT * FROM connection co, chatrooms ch WHERE co.userid = ? AND co.chatroomid =ch.chatroomid";
            //ResultSet rset = st.executeQuery("SELECT * FROM connection co, chatrooms ch WHERE co.userid =" + user.getUserID() + " AND co.chatroomid =ch.chatroomid");
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, user.getUserID());
            ResultSet rset = pst.executeQuery();
            while (rset.next()) {
                Chatroom temp = new Chatroom(rset.getString("theme"));
                temp.setChatroomID(rset.getInt("chatroomid"));
                temp.setUser(user);
                temp.setActive(rset.getBoolean("active"));
                temp.setRate(countVotes(temp));
                chat.add(temp);
            }
            st.close();
            return chat;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Vector<Chatroom> listChatrooms() {
        Vector<Chatroom> chat = new Vector<Chatroom>();

        try {
            Statement st = this.conn.createStatement();
            ResultSet rset = st.executeQuery("SELECT * FROM chatrooms ORDER BY chatroomid");
            while (rset.next()) {
                Chatroom temp = new Chatroom(rset.getString("theme"));
                temp.setChatroomID(rset.getInt("chatroomid"));
                temp.setUser(getUser(rset.getInt("userid")));
                temp.setActive(rset.getBoolean("active"));
                temp.setRate(countVotes(temp));
                chat.add(temp);
            }
            st.close();
            return chat;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public int closeChatroom(int id) {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := closeChatroom(?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setInt(2, id);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public Vector<PublicMessage> listMessagesChatrooms(int chatroomid) {
        Vector<PublicMessage> publicMess = new Vector<PublicMessage>();

        try {
            Statement st = this.conn.createStatement();
            String sql = "SELECT * FROM publicmessage WHERE chatroomid= ? ";
            //ResultSet rset = st.executeQuery("SELECT * FROM publicmessage WHERE chatroomid=" + chatroomid);
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, chatroomid);
            ResultSet rset = pst.executeQuery();
            while (rset.next()) {
                PublicMessage temp = new PublicMessage(rset.getString("content"), getUser(rset.getInt("userid")));
                temp.setMessageID(rset.getInt("messageid"));
                temp.setChatroomID(rset.getInt("chatroomid"));
                temp.setSendTime(rset.getTimestamp("sendtime"));
                publicMess.add(temp);
            }
            st.close();
            return publicMess;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Chatroom searchChatroom(String theme) {
        Chatroom temp = null;
        try {
            Statement st = this.conn.createStatement();
            String sql = "SELECT * FROM chatrooms WHERE theme= ? ";
            //ResultSet rset = st.executeQuery("SELECT * FROM chatrooms WHERE theme='" + theme + "'");
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, theme);
            ResultSet rset = pst.executeQuery();
            while (rset.next()) {
                temp = new Chatroom(rset.getString("theme"));
                temp.setChatroomID(rset.getInt("chatroomid"));
                temp.setUser(getUser(rset.getInt("userid")));
                temp.setActive(rset.getBoolean("active"));
            }
            st.close();
            return temp;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public int insertPublicMessage(PublicMessage mess) {
        int source;
        String send = String.format("%d-%d-%d %d:%d", mess.getSendTime().getDate(), mess.getSendTime().getMonth() + 1, mess.getSendTime().getYear() + 1900, mess.getSendTime().getHours(), mess.getSendTime().getMinutes());

        source = getUserId(mess.getSource().getUsername());
        try {
            Statement s = this.conn.createStatement();
            //s.executeUpdate("INSERT INTO messages VALUES (message_id.nextval," + source + ",'" + mess.getContent() + "'," + "to_date('" + send + "','dd-mm-yyyy hh24:mi'))");
            String sql = "INSERT INTO messages VALUES (message_id.nextval,?,?,to_date(?,'dd-mm-yyyy hh24:mi:ss'))";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, source);
            pst.setString(2, mess.getContent());
            pst.setString(3, send);
            pst.executeUpdate();
            this.conn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        int x = getMessage(source, send);
        try {
            Statement s = this.conn.createStatement();
            //s.executeUpdate("INSERT INTO publicmessage VALUES (" + x + "," + mess.getChatroomID() + "," + source + ",'" + mess.getContent() + "'," + "to_date('" + send + "','dd-mm-yyyy hh24:mi'))");
            String sql = "INSERT INTO publicmessage VALUES (?,?,?,?,to_date(?,'dd-mm-yyyy hh24:mi:ss'))";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, x);
            pst.setInt(2, mess.getChatroomID());
            pst.setInt(3, source);
            pst.setString(4, mess.getContent());
            pst.setString(5, send);
            pst.executeUpdate();
            this.conn.commit();

        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return x;
    }

    public int addAttachment(int id, String path) {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := addAttachment(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setInt(2, id);
            ct.setString(3, path);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int joinChatroom(int chatroomid, int userid, Boolean poster, Boolean watcher) {
        int pos, wat;
        if (poster) {
            pos = 1;
        } else {
            pos = 0;
        }
        if (watcher) {
            wat = 1;
        } else {
            wat = 0;
        }
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := joinChatroom(?,?,?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setInt(2, chatroomid);
            ct.setInt(3, userid);
            ct.setInt(4, pos);
            ct.setInt(5, wat);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public void banUser(Chatroom chat, User user) {
        try {
            Statement st = this.conn.createStatement();
            st.executeUpdate("DELETE FROM connection WHERE userid=" + user.getUserID() + " AND chatroomid=" + chat.getChatroomID());
            this.conn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int[] permissions(Chatroom chat, User user) {
        int[] perm = new int[2];
        try {
            Statement st = this.conn.createStatement();
            ResultSet rset = st.executeQuery("SELECT * FROM connection co, chatrooms ch WHERE ch.chatroomid=" + chat.getChatroomID() + " AND co.userid=" + user.getUserID());
            while (rset.next()) {
                if (rset.getBoolean("poster")) {
                    perm[0] = 1;
                } else {
                    perm[0] = 0;
                }
                if (rset.getBoolean("watcher")) {
                    perm[1] = 1;
                } else {
                    perm[1] = 0;
                }
            }
            st.close();
            return perm;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return perm;
        }
    }

    public Vector<User> usersChatrooms(Chatroom chat) {
        Vector<User> users = new Vector<User>();
        try {
            Statement st = this.conn.createStatement();
            ResultSet rset = st.executeQuery("SELECT u.username, u.userid FROM connection co, users u WHERE co.chatroomid=" + chat.getChatroomID() + " AND co.userid=u.userid");
            while (rset.next()) {
                User u = new User();
                u.setUsername(rset.getString("username"));
                u.setUserID(rset.getInt("userid"));
                users.add(u);
            }
            st.close();
            return users;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public int insertUser(User user) {
        @SuppressWarnings("deprecation")
        String bday;
        if (user.getBirthday() == null) {
            bday = null;
        } else {
            bday = String.format("%d-%d-%d", user.getBirthday().getDate(), user.getBirthday().getMonth(), user.getBirthday().getYear());
        }
        int gender = 2, privacy, active;
        if (user.isGender() != null) {
            if (user.isGender()) {
                gender = 1;
            } else {
                gender = 0;
            }
        }
        if (user.isPrivacy()) {
            privacy = 1;
        } else {
            privacy = 0;
        }

        try {
            Statement s = this.conn.createStatement();
            String sql = "";
            PreparedStatement pst;
            if (bday == null && gender == 2) {
                sql = "INSERT INTO users VALUES (user_id.nextval,?,?,?,null, null,?,?,?,?,?,?,?,?)";
                pst = conn.prepareStatement(sql);
                pst.setString(1, user.getName());
                pst.setString(2, user.getUsername());
                pst.setString(3, user.getPass());
                pst.setString(4, user.getEmail());
                pst.setString(5, user.getJob());
                pst.setString(6, user.getNationality());
                pst.setString(7, user.getCitizenship());
                pst.setString(8, user.getCity());
                pst.setString(9, user.getInterests());
                pst.setInt(10, privacy);
                pst.setInt(11, 1);
                //s.executeUpdate("INSERT INTO users VALUES (user_id.nextval,'" + user.getName() + "','" + user.getUsername() + "','" + user.getPass() + "'," + "null, null,'" + user.getEmail() + "','" + user.getJob() + "','" + user.getNationality() + "','" + user.getCitizenship() + "','" + user.getCity() + "','" + user.getInterests() + "'," + privacy + "," + active + ")");
            } else if (bday == null) {
                //s.executeUpdate("INSERT INTO users VALUES (user_id.nextval,'" + user.getName() + "','" + user.getUsername() + "','" + user.getPass() + "'," + "null," + gender + ",'" + user.getEmail() + "','" + user.getJob() + "','" + user.getNationality() + "','" + user.getCitizenship() + "','" + user.getCity() + "','" + user.getInterests() + "'," + privacy + "," + active + ")");
                sql = "INSERT INTO users VALUES (user_id.nextval,?,?,?,null,?,?,?,?,?,?,?,?,?)";
                pst = conn.prepareStatement(sql);
                pst.setString(1, user.getName());
                pst.setString(2, user.getUsername());
                pst.setString(3, user.getPass());
                pst.setInt(4, gender);
                pst.setString(5, user.getEmail());
                pst.setString(6, user.getJob());
                pst.setString(7, user.getNationality());
                pst.setString(8, user.getCitizenship());
                pst.setString(9, user.getCity());
                pst.setString(10, user.getInterests());
                pst.setInt(11, privacy);
                pst.setInt(12, 1);
            } else if (gender == 2) {
                //s.executeUpdate("INSERT INTO users VALUES (user_id.nextval,'" + user.getName() + "','" + user.getUsername() + "','" + user.getPass() + "'," + "to_date('" + bday + "','dd-mm-yyyy')," + "null,'" + user.getEmail() + "','" + user.getJob() + "','" + user.getNationality() + "','" + user.getCitizenship() + "','" + user.getCity() + "','" + user.getInterests() + "'," + privacy + "," + active + ")");
                sql = "INSERT INTO users VALUES (user_id.nextval,?,?,?,to_date(?, 'dd-mm-yyyy'),null,?,?,?,?,?,?,?,?)";
                pst = conn.prepareStatement(sql);
                pst.setString(1, user.getName());
                pst.setString(2, user.getUsername());
                pst.setString(3, user.getPass());
                pst.setString(4, bday);
                pst.setString(5, user.getEmail());
                pst.setString(6, user.getJob());
                pst.setString(7, user.getNationality());
                pst.setString(8, user.getCitizenship());
                pst.setString(9, user.getCity());
                pst.setString(10, user.getInterests());
                pst.setInt(11, privacy);
                pst.setInt(12, 1);
            } else {
                s.executeUpdate("INSERT INTO users VALUES (user_id.nextval,'" + user.getName() + "','" + user.getUsername() + "','" + user.getPass() + "'," + "to_date('" + bday + "','dd-mm-yyyy')," + gender + ",'" + user.getEmail() + "','" + user.getJob() + "','" + user.getNationality() + "','" + user.getCitizenship() + "','" + user.getCity() + "','" + user.getInterests() + "'," + privacy + ",1)");
                sql = "INSERT INTO users VALUES (user_id.nextval,?,?,?,to_date(?, 'dd-mm-yyyy'),?,?,?,?,?,?,?,?,?)";
                pst = conn.prepareStatement(sql);
                pst.setString(1, user.getName());
                pst.setString(2, user.getUsername());
                pst.setString(3, user.getPass());
                pst.setString(4, bday);
                pst.setInt(5, gender);
                pst.setString(6, user.getEmail());
                pst.setString(7, user.getJob());
                pst.setString(8, user.getNationality());
                pst.setString(9, user.getCitizenship());
                pst.setString(10, user.getCity());
                pst.setString(11, user.getInterests());
                pst.setInt(12, privacy);
                pst.setInt(13, 1);
            }
            pst.executeUpdate();
            this.conn.commit();
            return getUserId(user.getUsername());
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public int checkVotes(Chatroom chat) {
        int votes = 0;
        try {
            Statement st = this.conn.createStatement();
            ResultSet rset = st.executeQuery("SELECT count(*) FROM vote WHERE chatroomid=" + chat.getChatroomID());
            while (rset.next()) {
                votes = rset.getInt(1);
            }
            st.close();
            return votes;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public int howManyVotes(Chatroom chat) {
        int votes = 0;
        try {
            Statement st = this.conn.createStatement();
            ResultSet rset = st.executeQuery("SELECT count(DISTINCT userid) FROM publicmessage WHERE chatroomid=" + chat.getChatroomID());
            while (rset.next()) {
                votes = rset.getInt(1);
            }
            st.close();
            return votes;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public void newVote(User user, Chatroom chat, int vote) {
        try {
            Statement s = this.conn.createStatement();
            s.executeUpdate("INSERT INTO vote VALUES (" + user.getUserID() + "," + chat.getChatroomID() + "," + vote + ")");
            this.conn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Boolean hasVoted(Chatroom chat, User user) {
        try {
            Statement st = this.conn.createStatement();
            ResultSet rset = st.executeQuery("SELECT * FROM vote WHERE chatroomid=" + chat.getChatroomID() + " AND userid=" + user.getUserID());
            while (rset.next()) {
                return true;
            }
            st.close();
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public float countVotes(Chatroom chat) {
        float avg = 0;
        try {
            Statement st = this.conn.createStatement();
            ResultSet rset = st.executeQuery("SELECT avg(vote) FROM vote WHERE chatroomid=" + chat.getChatroomID());
            while (rset.next()) {
                avg = rset.getFloat(1);
                return avg;
            }
            st.close();
            return 0;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public Boolean isInChatroom(Chatroom chat, User user) {
        try {
            Statement st = this.conn.createStatement();
            ResultSet rset = st.executeQuery("SELECT * FROM connection WHERE chatroomid=" + chat.getChatroomID() + " and userid=" + user.getUserID());
            while (rset.next()) {
                return true;
            }
            st.close();
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public Vector<Chatroom> searchChatroomBy(String username, String theme) {
        Vector<Chatroom> chats = new Vector<Chatroom>();
        Chatroom temp = null;
        try {
            Statement st = this.conn.createStatement();
            ResultSet rset = st.executeQuery("SELECT * FROM chatrooms WHERE " + (username.equals("") ? "" : "userid=" + getUserId(username) + " and ") + ((theme.equals("") ? "" : "theme='" + theme + "' and ") + "1=1"));
            while (rset.next()) {
                temp = new Chatroom(rset.getString("theme"));
                temp.setChatroomID(rset.getInt("chatroomid"));
                temp.setUser(getUser(rset.getInt("userid")));
                temp.setActive(rset.getBoolean("active"));
                temp.setRate(countVotes(temp));
                chats.add(temp);
            }
            st.close();
            return chats;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public int insertMessage(PrivateMessage mess) {
        @SuppressWarnings("deprecation")
        int dest, source, read;
        String send = String.format("%d-%d-%d %d:%d:%d", mess.getSendTime().getDate(), mess.getSendTime().getMonth() + 1, mess.getSendTime().getYear() + 1900, mess.getSendTime().getHours(), mess.getSendTime().getMinutes(), mess.getSendTime().getSeconds());

        if (mess.isRead()) {
            read = 1;
        } else {
            read = 0;
        }

        dest = getUserId(mess.getDest().getUsername());
        source = getUserId(mess.getSource().getUsername());
        try {
            Statement s = this.conn.createStatement();
            //s.executeUpdate("INSERT INTO messages VALUES (message_id.nextval," + source + ",'" + mess.getContent() + "'," + "to_date('" + send + "','dd-mm-yyyy hh24:mi'))");
            String sql = "INSERT INTO messages VALUES (message_id.nextval,?,?,to_date(?,'dd-mm-yyyy hh24:mi:ss'))";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, source);
            pst.setString(2, mess.getContent());
            pst.setString(3, send);
            pst.executeUpdate();
            this.conn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        int x = getMessage(source, send);
        try {
            Statement s = this.conn.createStatement();
            //s.executeUpdate("INSERT INTO privatemessage VALUES (" + x + "," + source + "," + dest + ",'" + mess.getContent() + "'," + "to_date('" + send + "','dd-mm-yyyy hh24:mi'),'" + mess.getSubject() + "'," + read + "," + null + ")");
            String sql = "INSERT INTO privatemessage VALUES (?,?,?,?,to_date(?,'dd-mm-yyyy hh24:mi:ss'),?,?,null)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, x);
            pst.setInt(2, source);
            pst.setInt(3, dest);
            pst.setString(4, mess.getContent());
            pst.setString(5, send);
            pst.setString(6, mess.getSubject());
            pst.setInt(7, read);
            pst.executeUpdate();
            this.conn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return x;
    }

    public int getMessage(int source, String send) {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := getMessage(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setInt(2, source);
            ct.setString(3, send);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public Vector<String> getUsersMessages(User user) {
        Vector<String> users = new Vector<String>();
        try {
            Statement st = this.conn.createStatement();
            String sql = "SELECT DISTINCT u.username FROM users u, privatemessage p WHERE (u.userid = p.userid or u.userid = p.use_userid) and (p.userid = ? or p.use_userid = ?)";
            //ResultSet rset = st.executeQuery("SELECT DISTINCT u.username FROM users u, privatemessage p WHERE (u.userid = p.userid or u.userid = p.use_userid) and (p.userid = " + getUserId(user.getUsername()) + "or p.use_userid = " + getUserId(user.getUsername()) + ")");
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, getUserId(user.getUsername()));
            pst.setInt(2, getUserId(user.getUsername()));
            ResultSet rset = pst.executeQuery();
            while (rset.next()) {
                users.add(rset.getString("username"));
            }
            st.close();
            return users;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public int updateActivity(String username, int active) {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updateActivity(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setInt(3, active);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int updateUsername(String username, String username_temp) throws InterruptedException {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updateUsername(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setString(3, username_temp);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int updateName(String username, String name) throws InterruptedException {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updateName(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setString(3, name);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int updateNationality(String username, String nationality) throws InterruptedException {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updateNationality(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setString(3, nationality);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int updateCitizenship(String username, String citizenship) throws InterruptedException {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updateCitizenship(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setString(3, citizenship);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int updateInterests(String username, String interests) throws InterruptedException {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updateInterests(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setString(3, interests);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int updateEmail(String username, String email) throws InterruptedException {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updateEmail(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setString(3, email);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int updateJob(String username, String job) throws InterruptedException {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updateJob(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setString(3, job);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int updateCity(String username, String city) throws InterruptedException {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updateCity(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setString(3, city);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int updateGender(String username, int gender) {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updateGender(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setInt(3, gender);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int updatePrivacy(String username, int privacy) {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updatePrivacy(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setInt(3, privacy);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int updateBday(String username, String bday) {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updateBday(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setString(3, bday);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int updatePassword(String username, String pass) {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updatePassword(?,?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setString(2, username);
            ct.setString(3, pass);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int deleteInformation(int id) {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := deleteInformation(?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setInt(2, id);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public int getUserId(String user) {
        int id = 0;
        try {
            Statement st = this.conn.createStatement();

            ResultSet rset = st.executeQuery("SELECT userid FROM users WHERE username='" + user + "'");
            while (rset.next()) {
                id = rset.getInt("userid");
            }
            st.close();
            return id;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public User getUser(int id) {
        User temp = new User();
        temp.setUsername(getUserName(id));
        temp.setPass(getUserPass(id));
        temp.setUserID(id);
        return temp;
    }

    public String getUserName(int id) {
        String username = "";
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := getUserName(?)}");
            ct.registerOutParameter(1, java.sql.Types.VARCHAR);
            ct.setInt(2, id);
            ct.execute();
            username = ct.getString(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return username;
    }

    public String getUserPass(int id) {
        String pass = "";
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := getUserPass(?)}");
            ct.registerOutParameter(1, java.sql.Types.VARCHAR);
            ct.setInt(2, id);
            ct.execute();
            pass = ct.getString(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return username;
    }

    public int updateRead(int id) {
        int reply = -1;
        try {
            CallableStatement ct = conn.prepareCall("{CALL ? := updateRead(?)}");
            ct.registerOutParameter(1, java.sql.Types.INTEGER);
            ct.setInt(2, id);
            ct.execute();
            reply = ct.getInt(1);
            conn.commit();
        } catch (SQLException e) {
            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
        }
        return reply;
    }

    public Vector<Attachment> listAttachs() {
        Vector<Attachment> attachs = new Vector<Attachment>();
        try {
            Statement st = this.conn.createStatement();

            ResultSet rset = st.executeQuery("SELECT * FROM attachment");
            while (rset.next()) {
                Attachment temp = new Attachment(rset.getString("attach"));
                temp.setMess(rset.getInt("messageid"));
                temp.setID(rset.getInt("id"));
                attachs.add(temp);
            }
            st.close();
            return attachs;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Vector<PrivateMessage> listMessagesReceived(User user) {
        Vector<PrivateMessage> mess = new Vector<PrivateMessage>();

        try {
            Statement st = this.conn.createStatement();
            String sql = "SELECT * FROM privatemessage WHERE use_userid=?";
            //ResultSet rset = st.executeQuery("SELECT * FROM privatemessage WHERE use_userid=" + getUserId(user.getUsername()));
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, getUserId(user.getUsername()));
            ResultSet rset = pst.executeQuery();
            while (rset.next()) {
                PrivateMessage temp = new PrivateMessage(rset.getString("content"), rset.getString("subject"), getUser(rset.getInt("use_userid")), getUser(rset.getInt("userid")));
                temp.setMessageID(rset.getInt("messageid"));
                temp.setRead(rset.getBoolean("read"));
                temp.setSendTime(rset.getTimestamp("sendtime"));
                temp.setReceivingTime(rset.getTimestamp("sendtime"));
                mess.add(temp);
            }
            st.close();
            return mess;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Vector<PrivateMessage> listMessagesSent(User user) {
        Vector<PrivateMessage> mess = new Vector<PrivateMessage>();

        int id = 0;
        try {
            Statement st = this.conn.createStatement();
            String sql = "SELECT * FROM privatemessage WHERE userid=?";
            //ResultSet rset = st.executeQuery("SELECT * FROM privatemessage WHERE userid=" + getUserId(user.getUsername()));
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, getUserId(user.getUsername()));
            ResultSet rset = pst.executeQuery();
            while (rset.next()) {
                PrivateMessage temp = new PrivateMessage(rset.getString("content"), rset.getString("subject"), getUser(rset.getInt("use_userid")), getUser(rset.getInt("userid")));
                temp.setMessageID(rset.getInt("messageid"));
                temp.setRead(rset.getBoolean("read"));
                temp.setSendTime(rset.getTimestamp("sendtime"));
                temp.setReceivingTime(rset.getTimestamp("sendtime"));
                mess.add(temp);
            }
            st.close();
            return mess;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Vector<PrivateMessage> listMessages(String user, User sender) {
        Vector<PrivateMessage> mess = new Vector<PrivateMessage>();

        int id = getUserId(user);
        try {
            Statement st = this.conn.createStatement();
            String sql = "SELECT * FROM privatemessage WHERE use_userid= ? AND userid= ? OR userid= ? AND use_userid= ? ";
            //ResultSet rset = st.executeQuery("SELECT * FROM privatemessage WHERE use_userid=" + id + " AND userid=" + sender.getUserID() + " OR userid=" + id + " AND use_userid=" + sender.getUserID() + "");
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setInt(2, sender.getUserID());
            pst.setInt(3, id);
            pst.setInt(4, sender.getUserID());
            ResultSet rset = pst.executeQuery();
            while (rset.next()) {
                PrivateMessage temp = new PrivateMessage(rset.getString("content"), rset.getString("subject"), getUser(rset.getInt("use_userid")), getUser(rset.getInt("userid")));
                temp.setMessageID(rset.getInt("messageid"));
                temp.setRead(rset.getBoolean("read"));
                temp.setSendTime(rset.getTimestamp("sendtime"));
                temp.setReceivingTime(rset.getTimestamp("sendtime"));
                mess.add(temp);
            }
            st.close();
            return mess;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public Vector<String> showUsers() {
        Vector<String> temp = new Vector<String>();
        try {

            Statement st = this.conn.createStatement();

            ResultSet rset = st.executeQuery("SELECT * FROM users ");
            while (rset.next()) {
                temp.add(rset.getString("username"));
            }
            st.close();
            return temp;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public User searchUser(String username) {

        User temp = new User();
        String tempPass;
        try {
            Statement st = this.conn.createStatement();
            String sql = "SELECT * FROM users WHERE username = ? ";
            ResultSet rset = st.executeQuery("SELECT * FROM users " + "WHERE username='" + username + "'");
            PreparedStatement pst = this.conn.prepareStatement(sql);
            pst.setString(1, (String) username);
            //ResultSet rset = pst.executeQuery();
            while (rset.next()) {
                if (rset.getBoolean("active")) {
                    tempPass = rset.getString("password");
                    temp.setPass(tempPass);
                    temp.setUsername(rset.getString("username"));
                    temp.setUserID(rset.getInt("userid"));
                }
            }
            pst.close();
            return temp;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

//    public int removeMessage(int id) {
//        int reply = -1;
//        try {
//            CallableStatement ct = conn.prepareCall("{CALL ? := removeMessage(?)}");
//            ct.registerOutParameter(1, java.sql.Types.INTEGER);
//            ct.setInt(2, id);
//            ct.execute();
//            reply = ct.getInt(1);
//            conn.commit();
//        } catch (SQLException e) {
//            System.out.println("\n ERRO SQL: " + e.getMessage() + " !!!\n");
//        }
//        return reply;
//    }
    
    public void removeMessage(PrivateMessage pm) {
        try {
            Statement st = conn.createStatement();
            st.executeUpdate("DELETE FROM privatemessage WHERE messageid='" + pm.getMessageID() + "'");
            this.conn.commit();//commit, end transaction
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public Vector<User> searchUserBy(Object[] obj) {
        Vector<User> users = new Vector<User>();

        int gender = -1;
        if (!((String) obj[5]).equals("")) {
            if (((String) obj[5]).equals("f")) {
                gender = 1;
            } else {
                gender = 0;
            }
        }
        String a = (String) obj[0];
        int age, year = 0;
        if (a.equals("")) {
            age = 0;
        } else {
            age = Integer.parseInt(a);
            year = 2012 - age;
        }

        if (gender == -1) {

            try {
                Statement st = this.conn.createStatement();

                ResultSet rset = st.executeQuery("SELECT * FROM users WHERE " + (((String) obj[0]).equals("") ? "" : "to_char(birthday,'yyyy')=" + year + "and ") + " " + (((String) obj[1]).equals("") ? "" : "nationality='" + (String) obj[1] + "' and ") + " " + (((String) obj[2]).equals("") ? "" : "email='" + (String) obj[2] + "' and ") + " " + (((String) obj[3]).equals("") ? "" : "job='" + (String) obj[3] + "' and ") + " " + (((String) obj[4]).equals("") ? "" : "city='" + (String) obj[4] + "' and ") + " " + (((String) obj[5]).equals("") ? "" : " gender=" + null + " and ") + " " + (((String) obj[6]).equals("") ? "" : "interests LIKE '%" + obj[6] + "%' and ") + "1=1");

                while (rset.next()) {
                    if (rset.getBoolean("privacy")) {
                        User temp = new User();
                        temp.setUserID(rset.getInt("userid"));
                        temp.setName(rset.getString("name"));
                        temp.setUsername(rset.getString("username"));
                        temp.setPass(rset.getString("password"));
                        temp.setGender(rset.getBoolean("gender"));
                        temp.setEmail(rset.getString("email"));
                        temp.setJob(rset.getString("job"));
                        temp.setName(rset.getString("name"));
                        temp.setNationality(rset.getString("nationality"));
                        temp.setCitizenship(rset.getString("citizenship"));
                        temp.setCity(rset.getString("city"));
                        temp.setInterests(rset.getString("interests"));
                        temp.setActive(rset.getBoolean("active"));
                        temp.setBirthday(rset.getDate("birthday"));
                        users.add(temp);
                    }
                }
                st.close();
                return users;
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            try {
                Statement st = this.conn.createStatement();
                ResultSet rset = st.executeQuery("SELECT * FROM users WHERE " + (((String) obj[0]).equals("") ? "" : "to_char(birthday,'yyyy')=" + year + "and ") + " " + (((String) obj[1]).equals("") ? "" : "nationality='" + (String) obj[1] + "' and ") + " " + (((String) obj[2]).equals("") ? "" : "email='" + (String) obj[2] + "' and ") + " " + (((String) obj[3]).equals("") ? "" : "job='" + (String) obj[3] + "' and ") + " " + (((String) obj[4]).equals("") ? "" : "city='" + (String) obj[4] + "' and ") + " " + (((String) obj[5]).equals("") ? "" : " gender=" + gender + " and ") + " " + (((String) obj[6]).equals("") ? "" : "interests LIKE '%" + obj[6] + "%' and ") + "1=1");
                while (rset.next()) {
                    if (rset.getBoolean("privacy")) {
                        User temp = new User();
                        temp.setUserID(rset.getInt("userid"));
                        temp.setName(rset.getString("name"));
                        temp.setUsername(rset.getString("username"));
                        temp.setPass(rset.getString("password"));
                        temp.setGender(rset.getBoolean("gender"));
                        temp.setEmail(rset.getString("email"));
                        temp.setJob(rset.getString("job"));
                        temp.setName(rset.getString("name"));
                        temp.setNationality(rset.getString("nationality"));
                        temp.setCitizenship(rset.getString("citizenship"));
                        temp.setCity(rset.getString("city"));
                        temp.setInterests(rset.getString("interests"));
                        temp.setActive(rset.getBoolean("active"));
                        temp.setBirthday(rset.getDate("birthday"));
                        users.add(temp);
                    }
                }
                st.close();
                return users;
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }
}
