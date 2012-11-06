-- to be executed as SYS user with SYSDBA privileges

alter system set processes=200 scope=spfile;

call dbms_xdb.cfg_update(updateXML( dbms_xdb.cfg_get() , '/xdbconfig/sysconfig/protocolconfig/httpconfig/http-port/text()', 6060))

create user test identified by test DEFAULT TABLESPACE users ACCOUNT unlock;
grant connect,resource to test;
grant execute on DBMS_LOCK to test;

create user liferay identified by liferay DEFAULT TABLESPACE users ACCOUNT unlock;
grant connect,resource to liferay;

create user eai identified by eai DEFAULT TABLESPACE users ACCOUNT unlock;
grant connect,resource to eai;

