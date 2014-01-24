
-- zu Testzwecken hinsichtlich Datentypen und not null
CREATE TABLE T (
    CVARCHAR5 VARCHAR(5),
    CNUMBER5_2 NUMERIC(5 , 2 ),
    CDATE DATE,
    CTIMESTAMP DATETIME,
    CVARCHAR5_NN VARCHAR(5) NOT NULL,
    CNUMBER5_2_NN NUMERIC(5 , 2 ) NOT NULL,
    CDATE_NN DATE NOT NULL,
    CTIMESTAMP_NN TIMESTAMP NOT NULL,
    PRIMARY KEY (CVARCHAR5)
);

CREATE TABLE TEST_CRUD2 (
    id INT auto_increment,
    CNUMBER5_2 NUMERIC(5 , 2 ),
    CDATE DATE,
    CTIMESTAMP DATETIME,
    CVARCHAR5_NN VARCHAR(5) NOT NULL,
    CNUMBER5_2_NN NUMERIC(5 , 2 ) NOT NULL,
    CDATE_NN DATE NOT NULL,
    CTIMESTAMP_NN TIMESTAMP,
    TESTDATA VARCHAR(255),
    PRIMARY KEY (ID)
);

INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (1, 1, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (2, 2, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (3, 3, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (4, 4, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (5, 5, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (6, 6, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (7, 7, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (8, 8, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (9, 9, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (10, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (11, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (12, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (13, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (14, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (15, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (16, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (17, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (18, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (19, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (20, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (21, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (22, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (23, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');
INSERT INTO TEST_CRUD2 (ID, CNUMBER5_2, CDATE, CTIMESTAMP, CVARCHAR5_NN, CNUMBER5_2_NN, CDATE_NN, CTIMESTAMP_NN, TESTDATA) VALUES (24, 10, '2010-01-01', '2010-01-01', 'TEST', 15, '2010-01-01', '2010-01-01', 'Dies ist ein sehr langer Text ...');


CREATE TABLE TEST_CRUD2_DROP_NUMBER (
    `KEY` INT,
    TITLE VARCHAR(20)
);
Insert into TEST_CRUD2_DROP_NUMBER (`KEY`,TITLE) values (1,'Zahl1');
Insert into TEST_CRUD2_DROP_NUMBER (`KEY`,TITLE) values (2,'Zahl2');
Insert into TEST_CRUD2_DROP_NUMBER (`KEY`,TITLE) values (3,'Zahl3');
Insert into TEST_CRUD2_DROP_NUMBER (`KEY`,TITLE) values (4,'Zahl4');
Insert into TEST_CRUD2_DROP_NUMBER (`KEY`,TITLE) values (5,'Zahl5');
Insert into TEST_CRUD2_DROP_NUMBER (`KEY`,TITLE) values (6,'Zahl6');
Insert into TEST_CRUD2_DROP_NUMBER (`KEY`,TITLE) values (7,'Zahl7');
Insert into TEST_CRUD2_DROP_NUMBER (`KEY`,TITLE) values (8,'Zahl8');
Insert into TEST_CRUD2_DROP_NUMBER (`KEY`,TITLE) values (9,'Zahl9');
Insert into TEST_CRUD2_DROP_NUMBER (`KEY`,TITLE) values (10,'Zahl10');

CREATE TABLE TEST_CRUD2_DROP_DATE (
    DATECOL DATE
);
   
Insert into TEST_CRUD2_DROP_DATE (DATECOL) values ('2010-01-01');
Insert into TEST_CRUD2_DROP_DATE (DATECOL) values ('2010-02-01');
Insert into TEST_CRUD2_DROP_DATE (DATECOL) values ('2010-03-01');
Insert into TEST_CRUD2_DROP_DATE (DATECOL) values ('2010-04-01');
Insert into TEST_CRUD2_DROP_DATE (DATECOL) values ('2010-05-01');

CREATE TABLE TEST_CRUD2_DROP_TEXT (
    `KEY` VARCHAR(255),
    TITLE VARCHAR(255)
);
   
Insert into TEST_CRUD2_DROP_TEXT (`KEY`,TITLE) values ('Dies ist ein sehr langer Text ... saogaspodj spadjf sapdfjs üdfpj südfpjs dfpisajdf üaspidgwüiejdfsgdfg', 'Dies ist ein sehr langer Text ...');
Insert into TEST_CRUD2_DROP_TEXT (`KEY`,TITLE) values ('Dies ist ein kurzer Text ... Hello World!', 'Dies ist ein kurzer Text ...');


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
	'2011-02-16',
	'2011-02-16 11:16:07',
	's', /*not nullable*/
	0, /*not nullable*/
	'2011-02-16', /*not nullable*/
	'2011-02-16 11:16:07' /*not nullable*/
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
	'2011-02-16',
	'2011-02-16 11:16:07',
	's', /*not nullable*/
	0, /*not nullable*/
	'2011-02-16', /*not nullable*/
	'2011-02-16 11:16:07' /*not nullable*/
);

CREATE TABLE TEST_NAME (
    ID INT auto_increment,
    NAME VARCHAR(255),
    PRIMARY KEY (ID)
);

CREATE TABLE TEST_PARENT (
    ID INT NOT NULL,
    NAME VARCHAR(80) NOT NULL,
    CONSTRAINT TEST_PARENT_PK PRIMARY KEY (ID)
);

CREATE TABLE TEST_CHILD (
    PARENT_ID INT NOT NULL,
    VALUE VARCHAR(80) NOT NULL,
    CONSTRAINT TEST_CHILD_PK PRIMARY KEY (PARENT_ID),
    CONSTRAINT PARENT_FK FOREIGN KEY (PARENT_ID)
        REFERENCES TEST_PARENT (ID)
);
  
  INSERT INTO TEST_PARENT values (1,'Horst');
  INSERT INTO TEST_CHILD values (1,'Mustermann');
  
  
CREATE TABLE TEST_CLOB (
    ID INT NOT NULL auto_increment,
    TEXT VARCHAR(80),
    DATA TEXT,
    PRIMARY KEY (ID)
);

INSERT INTO TEST_CLOB values (1,'Horst','HORST_CLOB');
INSERT INTO TEST_CLOB values (2,'LangerText','Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.');


CREATE TABLE TEST_BLOB (
    ID INT NOT NULL auto_increment,
    TEXT VARCHAR(80),
    DATA BLOB,
    PRIMARY KEY (ID)
);

INSERT INTO TEST_BLOB values (1,'Horst',BINARY('HORST_CLOB'));
INSERT INTO TEST_BLOB values (2,'LangerText',BINARY('Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.'));


create function FUNC_SLEEP (IN_SECONDS INT) RETURNS INT
return SLEEP(IN_SECONDS);

