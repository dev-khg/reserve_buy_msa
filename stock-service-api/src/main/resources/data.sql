SET foreign_key_checks = 0;
DROP TABLE IF EXISTS stock;

create table stock
(
    stock_number bigint  not null auto_increment,
    item_number  bigint  not null,
    total        integer not null,
    primary key (stock_number)
) engine = InnoDB;
alter table stock
    add constraint stock_item_number_fk unique (item_number);

SET foreign_key_checks = 1;