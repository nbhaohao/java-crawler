create table NEWS
(
    id          bigint primary key auto_increment,
    title       text,
    content     text,
    url         varchar(300),
    created_at  timestamp default now(),
    modified_at timestamp default now()
) default charset = utf8;

create table LINKS_ALREADY_PROCESSED
(
    link varchar(300)
) default charset = utf8;

create table LINKS_TO_BE_PROCESSED
(
    link varchar(300)
) default charset = utf8;