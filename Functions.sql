--############################USERS################################################

CREATE OR REPLACE FUNCTION updateUsername (usernme in VARCHAR2, username_temp in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET username = username_temp WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /
  
CREATE OR REPLACE FUNCTION updateActivity (usernme in VARCHAR2, act in SMALLINT) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET active = act  WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /


CREATE OR REPLACE FUNCTION updateName (usernme in VARCHAR2, name_ in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET name = name_  WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /


CREATE OR REPLACE FUNCTION updateNationality (usernme in VARCHAR2, nationality_ in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET nationality = nationality_  WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /
  
CREATE OR REPLACE FUNCTION updateCitizenship (usernme in VARCHAR2, citizenship_ in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET citizenship = citizenship_  WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /
  
  
CREATE OR REPLACE FUNCTION updateInterests (usernme in VARCHAR2, interests_ in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET interests = interests_  WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /  
  
CREATE OR REPLACE FUNCTION updateEmail (usernme in VARCHAR2, email_ in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET email = email_  WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /    
  
  CREATE OR REPLACE FUNCTION updateJob (usernme in VARCHAR2, job_ in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET job = job_  WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /  
  
  CREATE OR REPLACE FUNCTION updateCity (usernme in VARCHAR2, city_ in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET city = city_  WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /  
  
CREATE OR REPLACE FUNCTION updateInterests (usernme in VARCHAR2, interests_ in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET interests = interests_  WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /  
  
CREATE OR REPLACE FUNCTION updateGender (usernme in VARCHAR2, gender_ in SMALLINT) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET gender = gender_  WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /
  
CREATE OR REPLACE FUNCTION updatePrivacy (usernme in VARCHAR2, privacy_ in SMALLINT) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET privacy = privacy_  WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /
  
CREATE OR REPLACE FUNCTION updateBday (usernme in VARCHAR2, bday  in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET birthday = to_date(bday,'dd-mm-yyyy') WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /
  
CREATE OR REPLACE FUNCTION updatePassword (usernme in VARCHAR2, pass_ in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET password = pass_ WHERE username = usernme;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /
  
CREATE OR REPLACE FUNCTION deleteInformation (id_ in SMALLINT) RETURN NUMBER
  IS
  BEGIN
  UPDATE users SET birthday = null,nationality=null,interests=null,email=null,job=null,city=null,gender=null WHERE userid = id_;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /

CREATE OR REPLACE FUNCTION getUserName (id_ in SMALLINT) RETURN VARCHAR
  IS
    usernme users.username%TYPE;
  BEGIN
  SELECT username INTO usernme FROM users WHERE userid = id_;
  RETURN usernme;
  END;
  /  
  
CREATE OR REPLACE FUNCTION getUserPass (id_ in SMALLINT) RETURN VARCHAR
  IS
    pass users.password%TYPE;
  BEGIN
  SELECT password INTO pass FROM users WHERE userid = id_;
  RETURN pass;
  END;
  /

--############################MESSAGES#############################################

CREATE OR REPLACE FUNCTION updateRead (id_ in SMALLINT) RETURN NUMBER
  IS
  BEGIN
  UPDATE privatemessage SET read=1,receivetime=SYSDATE WHERE messageid=id_;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /  
  
CREATE OR REPLACE FUNCTION getMessage (id_ in SMALLINT, send_ in VARCHAR2) RETURN NUMBER
  IS
    id_mess SMALLINT;
  BEGIN
  SELECT messageid INTO id_mess FROM messages WHERE userid=id_ AND sendtime=to_date(send_,'dd-mm-yyyy hh24:mi:ss');
  RETURN id_mess;
  END;
  / 
  
  CREATE OR REPLACE FUNCTION addAttachment (idx in SMALLINT, path_ in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  INSERT INTO attachment VALUES (id_.nextval, idx, path_);
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  / 
  
  
  
--############################CHATROOMS################################################
  
CREATE OR REPLACE FUNCTION createChatroom (theme_ in VARCHAR2, id_ in SMALLINT) RETURN NUMBER
  IS
  BEGIN
  INSERT INTO chatrooms VALUES (chatroom_id.nextval, id_, theme_,1); 
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /
  
  CREATE OR REPLACE FUNCTION modifyChatroom (id_ in SMALLINT, theme_ in VARCHAR2) RETURN NUMBER
  IS
  BEGIN
  UPDATE chatrooms SET theme=theme_ WHERE chatroomid = id_;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /
  
   CREATE OR REPLACE FUNCTION modifyPermission (chatroomid_ in SMALLINT, userid_ in SMALLINT, post in SMALLINT, wat in SMALLINT) RETURN NUMBER
  IS
  BEGIN
  UPDATE connection SET poster=post, watcher=wat WHERE userid = userid_ AND chatroomid=chatroomid_;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /
  
  CREATE OR REPLACE FUNCTION closeChatroom (id_ in SMALLINT) RETURN NUMBER
  IS
  BEGIN
  UPDATE chatrooms SET active=0 WHERE chatroomid = id_;
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  / 
  
CREATE OR REPLACE FUNCTION joinChatroom (chatroomid_ in SMALLINT, userid_ in SMALLINT, post in SMALLINT, wat in SMALLINT) RETURN NUMBER
  IS
  BEGIN
  INSERT INTO connection VALUES (userid_, chatroomid_, wat, post);
  RETURN 1;
  EXCEPTION WHEN OTHERS THEN
  RETURN -1;
  END;
  /

  
  
 
  
  