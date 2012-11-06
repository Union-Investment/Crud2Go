
-- zu Testzwecken hinsichtlich Datentypen und not null
CREATE TABLE T
(
	CVARCHAR5 		VARCHAR2(5 BYTE),
	CNUMBER5_2		NUMBER(5,2),
	CDATE			DATE,
	CTIMESTAMP		TIMESTAMP,
	CVARCHAR5_NN 		VARCHAR2(5 BYTE) NOT NULL,
	CNUMBER5_2_NN		NUMBER(5,2) NOT NULL,
	CDATE_NN			DATE NOT NULL,
	CTIMESTAMP_NN		TIMESTAMP NOT NULL,
	PRIMARY KEY (CVARCHAR5)
);

CREATE TABLE TEST_CRUD2
(
	id 				number, 
	CNUMBER5_2		NUMBER(5,2),
	CDATE			DATE,
	CTIMESTAMP		TIMESTAMP,
	CVARCHAR5_NN 	VARCHAR2(5 BYTE) NOT NULL,
	CNUMBER5_2_NN	NUMBER(5,2) NOT NULL,
	CDATE_NN		DATE NOT NULL,
	CTIMESTAMP_NN	TIMESTAMP,
	TESTDATA		VARCHAR2(255),
	PRIMARY KEY (ID)
);

create sequence TEST_CRUD2_SEQ start with 1 increment by 1 nomaxvalue;

create trigger TEST_CRUD2_TRIGGER
before insert on TEST_CRUD2
for each row
begin
	select TEST_CRUD2_SEQ.nextval into :new.ID from dual;
end;
/

INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (1, 1, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (2, 2, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (3, 3, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (4, 4, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (5, 5, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (6, 6, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (7, 7, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (8, 8, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (9, 9, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (10, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (11, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (12, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (13, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (14, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (15, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (16, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (17, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (18, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (19, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (20, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (21, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (22, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (23, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (24, 10, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'TEST', 15, TO_DATE ('01/01/2010', 'MM/DD/YYYY'), TO_DATE ('01/01/2010', 'MM/DD/YYYY'), 'Dies ist ein sehr langer Text ...');


CREATE TABLE "TEST_CRUD2_DROP_NUMBER" 
   (	"KEY" NUMBER, 
		"TITLE" VARCHAR2(20 BYTE)
   );
Insert into TEST_CRUD2_DROP_NUMBER (KEY,TITLE) values (1,'Zahl1');
Insert into TEST_CRUD2_DROP_NUMBER (KEY,TITLE) values (2,'Zahl2');
Insert into TEST_CRUD2_DROP_NUMBER (KEY,TITLE) values (3,'Zahl3');
Insert into TEST_CRUD2_DROP_NUMBER (KEY,TITLE) values (4,'Zahl4');
Insert into TEST_CRUD2_DROP_NUMBER (KEY,TITLE) values (5,'Zahl5');
Insert into TEST_CRUD2_DROP_NUMBER (KEY,TITLE) values (6,'Zahl6');
Insert into TEST_CRUD2_DROP_NUMBER (KEY,TITLE) values (7,'Zahl7');
Insert into TEST_CRUD2_DROP_NUMBER (KEY,TITLE) values (8,'Zahl8');
Insert into TEST_CRUD2_DROP_NUMBER (KEY,TITLE) values (9,'Zahl9');
Insert into TEST_CRUD2_DROP_NUMBER (KEY,TITLE) values (10,'Zahl10');

CREATE TABLE "TEST_CRUD2_DROP_DATE" 
   (	"DATECOL" DATE
   );
   
Insert into TEST_CRUD2_DROP_DATE (DATECOL) values (TO_DATE ('01/01/2010', 'MM/DD/YYYY'));
Insert into TEST_CRUD2_DROP_DATE (DATECOL) values (TO_DATE ('01/02/2010', 'MM/DD/YYYY'));
Insert into TEST_CRUD2_DROP_DATE (DATECOL) values (TO_DATE ('01/03/2010', 'MM/DD/YYYY'));
Insert into TEST_CRUD2_DROP_DATE (DATECOL) values (TO_DATE ('01/04/2010', 'MM/DD/YYYY'));
Insert into TEST_CRUD2_DROP_DATE (DATECOL) values (TO_DATE ('01/05/2010', 'MM/DD/YYYY'));

CREATE TABLE "TEST_CRUD2_DROP_TEXT" 
   (	"KEY" VARCHAR2(255), 
		"TITLE" VARCHAR2(255)
   );
   
Insert into TEST_CRUD2_DROP_TEXT (KEY,TITLE) values ('Dies ist ein sehr langer Text ... saogaspodj spadjf sapdfjs üdfpj südfpjs dfpisajdf üaspidgwüiejdfsgdfg', 'Dies ist ein sehr langer Text ...');
Insert into TEST_CRUD2_DROP_TEXT (KEY,TITLE) values ('Dies ist ein kurzer Text ... Hello World!', 'Dies ist ein kurzer Text ...');


INSERT INTO T (
	CVARCHAR5,
	CNUMBER5_2,
	CDATE,
	CTIMESTAMP,
	CVARCHAR5_NN,
	CNUMBER5_2_NN,
	CDATE_NN,
	CTIMESTAMP_NN 
) VALUES (
	's' /*not nullable*/,
	0,
	TO_DATE('2011-02-16', 'yyyy-MM-dd'),
	TO_DATE('2011-02-16 11:16:07', 'yyyy-MM-dd HH24:MI:SS'),
	's', /*not nullable*/
	0, /*not nullable*/
	TO_DATE('2011-02-16', 'yyyy-MM-dd'), /*not nullable*/
	TO_DATE('2011-02-16 11:16:07', 'yyyy-MM-dd HH24:MI:SS') /*not nullable*/
);
INSERT INTO T (
	CVARCHAR5,
	CNUMBER5_2,
	CDATE,
	CTIMESTAMP,
	CVARCHAR5_NN,
	CNUMBER5_2_NN,
	CDATE_NN,
	CTIMESTAMP_NN 
) VALUES (
	's2' /*not nullable*/,
	10,
	TO_DATE('2011-02-16', 'yyyy-MM-dd'),
	TO_DATE('2011-02-16 11:16:07', 'yyyy-MM-dd HH24:MI:SS'),
	's2' /*not nullable*/,
	10 /*not nullable*/,
	TO_DATE('2011-02-16', 'yyyy-MM-dd'), /*not nullable*/
	TO_DATE('2011-02-16 11:16:08', 'yyyy-MM-dd HH24:MI:SS') /*not nullable*/
);

CREATE TABLE TEST_NAME
(
	ID 				number, 
	NAME 	VARCHAR2(255),
	PRIMARY KEY (ID)
);

create sequence TEST_NAME_SEQ start with 1 increment by 1 nomaxvalue;

create trigger TEST_NAME_TRIGGER
before insert on TEST_NAME
for each row
begin
	select TEST_NAME_SEQ.nextval into :new.ID from dual;
end;
/

CREATE TABLE "TEST_PARENT"
  (
    "ID"   NUMBER NOT NULL ENABLE,
    "NAME" VARCHAR2(80 BYTE) NOT NULL ENABLE,
    CONSTRAINT "TEST_PARENT_PK" PRIMARY KEY ("ID") ENABLE
  );

CREATE TABLE "TEST_CHILD"
  (
    "PARENT_ID" NUMBER NOT NULL ENABLE,
    "VALUE"     VARCHAR2(80 BYTE) NOT NULL ENABLE,
    CONSTRAINT "TEST_CHILD_PK" PRIMARY KEY ("PARENT_ID") ENABLE,
    CONSTRAINT "PARENT_FK" FOREIGN KEY ("PARENT_ID") REFERENCES "TEST_PARENT" ("ID") ENABLE
  );
  
  INSERT INTO TEST_PARENT values (1,'Horst');
  INSERT INTO TEST_CHILD values (1,'Mustermann');
  
  
CREATE TABLE "TEST_CLOB"
  (
    "ID" NUMBER NOT NULL ENABLE,
    "TEXT"     VARCHAR2(80 BYTE),
    "DATA"     CLOB,
    PRIMARY KEY (ID)
  );
  
create sequence TEST_CLOB_SEQ start with 1 increment by 1 nomaxvalue;

create trigger TEST_CLOB_TRIGGER
before insert on TEST_CLOB
for each row
begin
	select TEST_CLOB_SEQ.nextval into :new.ID from dual;
end;
/

INSERT INTO TEST_CLOB values (1,'Horst','HORST_CLOB');
INSERT INTO TEST_CLOB values (2,'LangerText','Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.');


CREATE TABLE "TEST_BLOB"
  (
    "ID" NUMBER NOT NULL ENABLE,
    "TEXT"     VARCHAR2(80 BYTE),
    "DATA"     BLOB,
    PRIMARY KEY (ID)
  );
  
create sequence TEST_BLOB_SEQ start with 1 increment by 1 nomaxvalue;

create or replace trigger TEST_BLOB_TRIGGER
before insert on TEST_BLOB
for each row
begin
	select TEST_BLOB_SEQ.nextval into :new.ID from dual;
end;
/

INSERT INTO TEST_BLOB values (1,'Horst',RAWTOHEX('HORST_CLOB'));
INSERT INTO TEST_BLOB values (2,'LangerText',RAWTOHEX('Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.'));

create or replace FUNCTION FUNC_SLEEP 
(
  IN_SECONDS IN NUMBER  
) RETURN NUMBER AS 
BEGIN
  DBMS_LOCK.SLEEP(IN_SECONDS);
  RETURN 42;
END FUNC_SLEEP;
/
