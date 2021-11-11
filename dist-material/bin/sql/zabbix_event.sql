CREATE OR REPLACE FUNCTION "public"."event_notify"("channel" text, "routing_key" text, "message" text)
  RETURNS "pg_catalog"."void" AS $BODY$
	select	pg_notify(channel, routing_key || '$$' || message);
$BODY$
  LANGUAGE sql STABLE
  COST 100;






  CREATE OR REPLACE FUNCTION "public"."events_problem_trace"()
  RETURNS "pg_catalog"."trigger" AS $BODY$
DECLARE
    v_eventid    INT;
    v_event_json TEXT;
BEGIN
    CASE TG_OP
        WHEN 'INSERT' THEN v_eventid := NEW.eventid;
                           select row_to_json(x)
                           into v_event_json
                           from (SELECT p."eventid",  p."objectid", p."clock", p."r_clock", p."name", p."acknowledged", p."severity",tag.tag,tag."value" tagValue from problem p Inner JOIN (select eventid,tag,value from event_tag where tag ='__alarm__' ) tag on tag.eventid=p.eventid where p.source=0 and p.eventid = v_eventid) x;
				WHEN 'UPDATE' THEN v_eventid := NEW.eventid;
                           select row_to_json(x)
                           into v_event_json
                           from (SELECT p."eventid",  p."objectid", p."clock", p."r_clock", p."name", p."acknowledged", p."severity",tag.tag,tag."value" tagValue from problem p Inner JOIN (select eventid,tag,value from event_tag where tag ='__alarm__' ) tag on tag.eventid=p.eventid where p.source=0 and p.eventid = v_eventid) x;
        ELSE RETURN NULL;		
    END CASE;

    perform event_notify('zabbix_pg_event', 'events', v_event_json);
		
		RETURN NULL;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE STRICT
  COST 100;








  CREATE OR REPLACE FUNCTION "public"."events_tag_trace"()
  RETURNS "pg_catalog"."trigger" AS $BODY$
DECLARE
    v_eventid    INT;
    v_event_json TEXT;
BEGIN
    CASE TG_OP
        WHEN 'INSERT' THEN v_eventid := NEW.eventid;
                           select row_to_json(x)
                           into v_event_json
                           from (select  e.eventid,e.objectid,e.name,tag.tag,tag.value tagValue from events e LEFT JOIN (select eventid,tag,value from event_tag where tag = '__event__' ) tag on tag.eventid=e.eventid where e.source=0 and e.eventid = v_eventid) x;
        ELSE RETURN NULL;
    END CASE;

    perform event_notify('zabbix_pg_event', 'events', v_event_json);
		
		RETURN NULL;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE STRICT
  COST 100;






  CREATE OR REPLACE FUNCTION "public"."events_trace"()
  RETURNS "pg_catalog"."trigger" AS $BODY$
DECLARE
    v_eventid    INT;
    v_event_json TEXT;
BEGIN
    CASE TG_OP
        WHEN 'INSERT' THEN v_eventid := NEW.eventid;
                           select row_to_json(x)
                           into v_event_json
                           from (select  e.eventid,e.objectid,e.name,tag.tag,tag.value tagValue from events e Inner JOIN (select triggerid,tag,value from trigger_tag where tag in ('__online__','__offline__','__execute__','__scene__') ) tag on tag.triggerid=e.objectid where e.source=0 and e.eventid = v_eventid) x;
        ELSE RETURN NULL;
    END CASE;

    perform event_notify('zabbix_pg_event', 'events', v_event_json);
		
		RETURN NULL;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE STRICT
  COST 100;



  CREATE TRIGGER "events_trigger" AFTER INSERT ON "public"."events"
FOR EACH ROW
EXECUTE PROCEDURE "public"."events_trace"();


CREATE TRIGGER "events_problem_trigger" AFTER INSERT OR UPDATE OF "r_clock", "acknowledged" ON "public"."problem"
FOR EACH ROW
EXECUTE PROCEDURE "public"."events_problem_trace"();
