ALTER  table product_event_time_interval ADD COLUMN day_of_weeks varchar(16) DEFAULT null;
ALTER  table product_event_time_interval ALTER COLUMN start_time type int4;
ALTER  table product_event_time_interval ALTER COLUMN end_time type int4;