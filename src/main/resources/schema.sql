DROP TABLE IF EXISTS photo cascade;

CREATE table photo (
id varchar(255) not null,
author varchar(255),
camera varchar(255),
cropped_picture varchar(255),
full_picture varchar(255),
tags varchar(255),
primary key (id));
