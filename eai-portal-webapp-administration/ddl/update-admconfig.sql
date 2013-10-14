alter table ADM_CONFIG add DATE_UPDATED TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
update ADM_CONFIG set DATE_UPDATED = CURRENT_TIMESTAMP;
alter table ADM_CONFIG modify DATE_UPDATED DATE NOT NULL;

create or replace trigger TRG_ADM_CONFIG_UPD
before update
on ADM_CONFIG for each row
begin
	select CURRENT_TIMESTAMP into :new.DATE_UPDATED from dual;
end;
/


-- alter table ADM_CONFIG drop column DATE_UPDATED;

