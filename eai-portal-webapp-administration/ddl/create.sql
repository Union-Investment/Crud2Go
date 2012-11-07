-- Tabelle für Konfigurationen
CREATE TABLE ADM_CONFIG
(
	ID number, 
	PORTLET_ID VARCHAR2(255) NOT NULL,
	COMMUNITY_ID INT NOT NULL,
	CONFIG_NAME VARCHAR2(255) NOT NULL,
	CONFIG_XML BLOB NOT NULL,
	USER_CREATED VARCHAR2(255) NOT NULL,
	DATE_CREATED TIMESTAMP NOT NULL,
	PRIMARY KEY (ID)
);

CREATE sequence ADM_CONFIG_SEQ start WITH 1 increment BY 1 nomaxvalue;

CREATE OR REPLACE TRIGGER "TRG_ADM_CONFIG_INS" 
BEFORE INSERT
ON ADM_CONFIG FOR EACH ROW
BEGIN
  SELECT ADM_CONFIG_SEQ.nextval INTO :new.ID FROM DUAL;
END; 
/

-- Tabelle für Berechtigungen
CREATE TABLE RESOURCEID_PRIMKEY 
(
  PRIMKEY NUMBER NOT NULL PRIMARY KEY,
  RESOURCEID VARCHAR2(100 BYTE) NOT NULL UNIQUE
);

CREATE SEQUENCE RESOURCEID_SEQ INCREMENT BY 1 MAXVALUE 999999999999999999999999999 MINVALUE 1 CACHE 20;