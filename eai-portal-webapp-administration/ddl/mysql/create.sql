-- 
-- Achtung: Wegen eines Bugs in Liferay werden VARCHAR-Felder existierender Tabellen auf 1/4tel Länge gekürzt.
-- Daher sollte dieses Script erst nach dem ersten Liferay-Start ausgeführt werden. 
--

-- Tabelle für Konfigurationen
CREATE TABLE ADM_CONFIG
(
	ID INT AUTO_INCREMENT PRIMARY KEY, 
	PORTLET_ID VARCHAR(255) NOT NULL,
	COMMUNITY_ID INT NOT NULL,
	CONFIG_NAME VARCHAR(255) NOT NULL,
	CONFIG_XML BLOB NOT NULL,
	USER_CREATED VARCHAR(255) NOT NULL,
	DATE_CREATED DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	DATE_UPDATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabelle für Berechtigungen
CREATE TABLE RESOURCEID_PRIMKEY 
(
  PRIMKEY BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  RESOURCEID VARCHAR(100) NOT NULL UNIQUE
);
