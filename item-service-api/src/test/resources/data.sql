drop table if exists item cascade;
drop table if exists item_info cascade;
create table item
(
    price       integer      not null,
    create_at   timestamp(6),
    item_number bigint generated by default as identity,
    start_at    timestamp(6),
    update_at   timestamp(6),
    name        varchar(200) not null,
    type        varchar(50) not null check (type in ('GENERAL', 'TIME_DEAL')),
    primary key (item_number)
);
create table item_info
(
    item_info_number bigint generated by default as identity,
    item_number      bigint not null unique,
    content          TEXT   not null,
    primary key (item_info_number)
);

alter table if exists item_info
    add constraint item_iem_info_fk
    foreign key (item_number)
    references item;
