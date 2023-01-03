CREATE TABLE IF NOT EXISTS `AUTHOR`(
	id  bigint not null unique auto_increment,
	username varchar(100) not null,
	password varchar(100) not null,
	email varchar(100) not null unique,
    primary key(id)
);

CREATE TABLE IF NOT EXISTS `PUBLICATION`(
	id bigint not null unique auto_increment,
	author_id bigint not null,
	content varchar(500) not null,
	date timestamp default current_timestamp,
	primary key(id),
	constraint FK_AUTHOR foreign key(author_id) references AUTHOR(id)
);