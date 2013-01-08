CREATE OR REPLACE TRIGGER updatePrivateMessages
BEFORE DELETE ON privatemessage 
FOR EACH ROW 
BEGIN
	DELETE FROM messages WHERE messageid=:OLD.messageid;
END;
/
SHOW ERRORS;

CREATE OR REPLACE TRIGGER updateMessages
BEFORE DELETE ON messages --antes de se apagar algo na tabela autores
FOR EACH ROW --faz várias vezes
BEGIN
	DELETE FROM attachment WHERE messageid=:OLD.messageid; --:old. é guardado o registo que ele tinha antes de um apagamento ou uptdate// :nem. row depois update ou insert
END;
/
SHOW ERRORS;

CREATE OR REPLACE TRIGGER updateConnection
AFTER UPDATE OF active ON users --antes de se apagar algo na tabela autores
FOR EACH ROW --faz várias vezes
BEGIN
	DELETE FROM connection WHERE userid=:OLD.userid; 
END;
/
SHOW ERRORS;

CREATE OR REPLACE TRIGGER updateVote
AFTER DELETE ON connection --antes de se apagar algo na tabela autores
FOR EACH ROW --faz várias vezes
BEGIN
	DELETE FROM vote WHERE userid=:OLD.userid AND chatroomid=:OLD.chatroomid; --:old. é guardado o registo que ele tinha antes de um apagamento ou uptdate// :nem. row depois update ou insert
END;
/
SHOW ERRORS;

CREATE OR REPLACE TRIGGER updateChatrooms
AFTER UPDATE OF active ON users --antes de se apagar algo na tabela autores
FOR EACH ROW --faz várias vezes
BEGIN
	UPDATE chatrooms SET active=0 WHERE userid=:OLD.userid; --:old. é guardado o registo que ele tinha antes de um apagamento ou uptdate// :nem. row depois update ou insert
END;
/
SHOW ERRORS;

CREATE OR REPLACE TRIGGER updatePermissions
AFTER UPDATE OF active ON chatrooms --antes de se apagar algo na tabela autores
FOR EACH ROW --faz várias vezes
BEGIN
	UPDATE connection SET watcher=1,poster=0 WHERE chatroomid=:OLD.chatroomid; --:old. é guardado o registo que ele tinha antes de um apagamento ou uptdate// :nem. row depois update ou insert
END;
/
SHOW ERRORS;
