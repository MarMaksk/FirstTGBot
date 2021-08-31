DROP TABLE IF EXISTS tb_monday;
DROP TABLE IF EXISTS tb_tuesday;
DROP TABLE IF EXISTS tb_wednesday;
DROP TABLE IF EXISTS tb_thursday;
DROP TABLE IF EXISTS tb_saturday;
DROP TABLE IF EXISTS tb_friday;
DROP TABLE IF EXISTS tb_sunday;
DROP TABLE IF EXISTS tb_user_schedule_time;


CREATE TABLE tb_monday
(
    tb_user_id integer,
    tb_name    varchar(200) not null,
    tb_one     varchar(300),
    tb_two     varchar(300),
    tb_three   varchar(300),
    tb_four    varchar(300),
    tb_five    varchar(300),
    tb_six     varchar(300),
    tb_seven   varchar(300),
    tb_eight   varchar(300),
    tb_nine    varchar(300),
    tb_public  boolean      not null,
    PRIMARY KEY (tb_name)

);
CREATE TABLE tb_tuesday
(
    tb_user_id integer,
    tb_name    varchar(200) not null,
    tb_one     varchar(300),
    tb_two     varchar(300),
    tb_three   varchar(300),
    tb_four    varchar(300),
    tb_five    varchar(300),
    tb_six     varchar(300),
    tb_seven   varchar(300),
    tb_eight   varchar(300),
    tb_nine    varchar(300),
    tb_public  boolean      not null,
    PRIMARY KEY (tb_name)

);
CREATE TABLE tb_wednesday
(
    tb_user_id integer,
    tb_name    varchar(200) not null,
    tb_one     varchar(300),
    tb_two     varchar(300),
    tb_three   varchar(300),
    tb_four    varchar(300),
    tb_five    varchar(300),
    tb_six     varchar(300),
    tb_seven   varchar(300),
    tb_eight   varchar(300),
    tb_nine    varchar(300),
    tb_public  boolean      not null,
    PRIMARY KEY (tb_name)

);
CREATE TABLE tb_thursday
(
    tb_user_id integer,
    tb_name    varchar(200) not null,
    tb_one     varchar(300),
    tb_two     varchar(300),
    tb_three   varchar(300),
    tb_four    varchar(300),
    tb_five    varchar(300),
    tb_six     varchar(300),
    tb_seven   varchar(300),
    tb_eight   varchar(300),
    tb_nine    varchar(300),
    tb_public  boolean      not null,
    PRIMARY KEY (tb_name)

);
CREATE TABLE tb_friday
(
    tb_user_id integer,
    tb_name    varchar(200) not null,
    tb_one     varchar(300),
    tb_two     varchar(300),
    tb_three   varchar(300),
    tb_four    varchar(300),
    tb_five    varchar(300),
    tb_six     varchar(300),
    tb_seven   varchar(300),
    tb_eight   varchar(300),
    tb_nine    varchar(300),
    tb_public  boolean      not null,
    PRIMARY KEY (tb_name)

);
CREATE TABLE tb_saturday
(
    tb_user_id integer,
    tb_name    varchar(200) not null,
    tb_one     varchar(300),
    tb_two     varchar(300),
    tb_three   varchar(300),
    tb_four    varchar(300),
    tb_five    varchar(300),
    tb_six     varchar(300),
    tb_seven   varchar(300),
    tb_eight   varchar(300),
    tb_nine    varchar(300),
    tb_public  boolean      not null,
    PRIMARY KEY (tb_name)

);
CREATE TABLE tb_sunday
(
    tb_user_id integer,
    tb_name    varchar(200) not null,
    tb_one     varchar(300),
    tb_two     varchar(300),
    tb_three   varchar(300),
    tb_four    varchar(300),
    tb_five    varchar(300),
    tb_six     varchar(300),
    tb_seven   varchar(300),
    tb_eight   varchar(300),
    tb_nine    varchar(300),
    tb_public  boolean      not null,
    PRIMARY KEY (tb_name)

);
CREATE TABLE tb_users_tablename
(
    tb_user_id   integer,
    tb_tablename varchar(200) not null,
    PRIMARY KEY (tb_tablename)
);
CREATE TABLE tb_users_actual_table_name
(
    tb_user_id   integer,
    tb_tablename varchar(200) not null,
    PRIMARY KEY (tb_user_id)
);
CREATE TABLE tb_user_schedule_time
(
    tb_user_id                 integer,
    tb_pair_length             varchar(5),
    tb_time_start              varchar(5),
    tb_time_lunch              varchar(5),
    tb_lunch_after             varchar(5),
    tb_time_change             varchar(5),
    tb_time_change_fourth_pair varchar(5),
    PRIMARY KEY (tb_user_id)
)