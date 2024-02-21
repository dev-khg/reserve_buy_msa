SET foreign_key_checks = 0;

drop table if exists _order;
create table _order
(
    sell_count  integer                              not null,
    unit_price  integer                              not null,
    item_number bigint                               not null,
    user_number bigint                               not null,
    order_id    varchar(255)                         not null,
    status      enum ('CANCELED','PAYED','RESERVED') not null,
    primary key (order_id)
) engine = InnoDB;

SET foreign_key_checks = 1;