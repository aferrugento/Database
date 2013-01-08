/*==============================================================*/
/* Table : ATTACHMENT                                           */
/*==============================================================*/
create table ATTACHMENT 
(
   ID                   INTEGER              not null,
   MESSAGEID            INTEGER              not null,
   ATTACH               CHAR(256)           not null,
   constraint PK_ATTACHMENT primary key (ID)
);

/*==============================================================*/
/* Index : CONTAIN_FK                                           */
/*==============================================================*/
create index CONTAIN_FK on ATTACHMENT (
   MESSAGEID ASC
);

/*==============================================================*/
/* Table : CHATROOMS                                            */
/*==============================================================*/
create table CHATROOMS 
(
   CHATROOMID           INTEGER              not null,
   USERID               INTEGER              not null,
   THEME                CHAR(256)            not null,
   ACTIVE               SMALLINT             not null,
   constraint PK_CHATROOMS primary key (CHATROOMID)
);

/*==============================================================*/
/* Index : OWNS_FK                                              */
/*==============================================================*/
create index OWNS_FK on CHATROOMS (
   USERID ASC
);

/*==============================================================*/
/* Table : CONNECTION                                           */
/*==============================================================*/
create table CONNECTION 
(
   USERID               INTEGER              not null,
   CHATROOMID           INTEGER              not null,
   WATCHER              SMALLINT             not null,
   POSTER               SMALLINT             not null,
   constraint PK_CONNECTION primary key (USERID, CHATROOMID)
);

/*==============================================================*/
/* Index : HAS_FK                                               */
/*==============================================================*/
create index HAS_FK on CONNECTION (
   USERID ASC
);

/*==============================================================*/
/* Index : CONNECTS_FK                                          */
/*==============================================================*/
create index CONNECTS_FK on CONNECTION (
   CHATROOMID ASC
);

/*==============================================================*/
/* Table : MESSAGES                                             */
/*==============================================================*/
create table MESSAGES 
(
   MESSAGEID            INTEGER              not null,
   USERID               INTEGER              not null,
   CONTENT              CHAR(256)            not null,
   SENDTIME             DATE                 not null,
   constraint PK_MESSAGES primary key (MESSAGEID)
);

/*==============================================================*/
/* Index : SENDS_FK                                             */
/*==============================================================*/
create index SENDS_FK on MESSAGES (
   USERID ASC
);

/*==============================================================*/
/* Table : PRIVATEMESSAGE                                       */
/*==============================================================*/
create table PRIVATEMESSAGE 
(
   MESSAGEID            INTEGER              not null,
   USERID               INTEGER              not null,
   USE_USERID           INTEGER,
   CONTENT              CHAR(256)            not null,
   SENDTIME             DATE                 not null,
   SUBJECT              CHAR(256)            not null,
   READ                 SMALLINT             not null,
   RECEIVETIME          DATE,
   constraint PK_PRIVATEMESSAGE primary key (MESSAGEID)
);

/*==============================================================*/
/* Index : RELATIONSHIP_6_FK                                    */
/*==============================================================*/
create index RELATIONSHIP_6_FK on PRIVATEMESSAGE (
   USERID ASC
);

/*==============================================================*/
/* Table : PUBLICMESSAGE                                        */
/*==============================================================*/
create table PUBLICMESSAGE 
(
   MESSAGEID            INTEGER              not null,
   CHATROOMID           INTEGER              not null,
   USERID               INTEGER,
   CONTENT              CHAR(256)            not null,
   SENDTIME             DATE                 not null,
   constraint PK_PUBLICMESSAGE primary key (MESSAGEID)
);

/*==============================================================*/
/* Index : RELATIONSHIP_5_FK                                    */
/*==============================================================*/
create index RELATIONSHIP_5_FK on PUBLICMESSAGE (
   CHATROOMID ASC
);

/*==============================================================*/
/* Table : USERS                                                */
/*==============================================================*/
create table USERS 
(
   USERID               INTEGER              not null,
   NAME                 CHAR(256)            not null,
   USERNAME             CHAR(256)            not null,
   PASSWORD             CHAR(256)            not null,
   BIRTHDAY             DATE,
   GENDER               SMALLINT,
   EMAIL                CHAR(256),
   JOB                  CHAR(256),
   NATIONALITY          CHAR(256),
   CITIZENSHIP          CHAR(256),
   CITY                 CHAR(256),
   INTERESTS            CHAR(256),
   PRIVACY              SMALLINT             not null,
   ACTIVE               SMALLINT             not null,
   constraint PK_USERS primary key (USERID)
);

/*==============================================================*/
/* Table : VOTE                                                 */
/*==============================================================*/
create table VOTE 
(
   USERID               INTEGER              not null,
   CHATROOMID           INTEGER              not null,
   VOTE                 INTEGER,
   constraint PK_VOTE primary key (USERID, CHATROOMID)
);

/*==============================================================*/
/* Index : GIVES_FK                                             */
/*==============================================================*/
create index GIVES_FK on VOTE (
   USERID ASC
);

/*==============================================================*/
/* Index : TO_FK                                                */
/*==============================================================*/
create index TO_FK on VOTE (
   CHATROOMID ASC
);

alter table ATTACHMENT
   add constraint FK_ATTACHME_CONTAIN_MESSAGES foreign key (MESSAGEID)
      references MESSAGES (MESSAGEID);

alter table CHATROOMS
   add constraint FK_CHATROOM_OWNS_USERS foreign key (USERID)
      references USERS (USERID);

alter table CONNECTION
   add constraint FK_CONNECTI_CONNECTS_CHATROOM foreign key (CHATROOMID)
      references CHATROOMS (CHATROOMID);

alter table CONNECTION
   add constraint FK_CONNECTI_HAS_USERS foreign key (USERID)
      references USERS (USERID);

alter table MESSAGES
   add constraint FK_MESSAGES_SENDS_USERS foreign key (USERID)
      references USERS (USERID);

alter table PRIVATEMESSAGE
   add constraint FK_PRIVATEM_INHERITAN_MESSAGES foreign key (MESSAGEID)
      references MESSAGES (MESSAGEID);

alter table PRIVATEMESSAGE
   add constraint FK_PRIVATEM_RELATIONS_USERS foreign key (USERID)
      references USERS (USERID);

alter table PUBLICMESSAGE
   add constraint FK_PUBLICME_INHERITAN_MESSAGES foreign key (MESSAGEID)
      references MESSAGES (MESSAGEID);

alter table PUBLICMESSAGE
   add constraint FK_PUBLICME_RELATIONS_CHATROOM foreign key (CHATROOMID)
      references CHATROOMS (CHATROOMID);

alter table VOTE
   add constraint FK_VOTE_GIVES_USERS foreign key (USERID)
      references USERS (USERID);

alter table VOTE
   add constraint FK_VOTE_TO_CHATROOM foreign key (CHATROOMID)
      references CHATROOMS (CHATROOMID);
