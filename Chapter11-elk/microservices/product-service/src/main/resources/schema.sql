create table if not exists category
(
    id          SERIAL not null primary key,
    version     integer ,
    description varchar(255),
    name        varchar(255)
);

create table if not exists product
(
    id               SERIAL          not null primary key,
    version            integer ,
    available_quantity bigint not null,
    description        varchar(255),
    name               varchar(255),
    price              numeric(38, 2),
    category_id        integer
);


