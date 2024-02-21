SET foreign_key_checks = 0;
drop table if exists item;
drop table if exists item_info;

create table item
(
    price       integer                      not null,
    create_at   datetime(6),
    item_number bigint                       not null auto_increment,
    start_at    datetime(6),
    update_at   datetime(6),
    name        varchar(200)                 not null,
    type        enum ('GENERAL','TIME_DEAL') not null,
    primary key (item_number)
) engine = InnoDB;

create table item_info
(
    item_info_number bigint not null auto_increment,
    item_number      bigint not null,
    content          TEXT   not null,
    primary key (item_info_number)
) engine = InnoDB;

alter table item_info
    add constraint item_info_item_number_unique unique (item_number);

alter table item_info
    add constraint item_info_item_number_fk
        foreign key (item_number)
            references item (item_number);

SET foreign_key_checks = 1;