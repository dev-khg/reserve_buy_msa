package hg.reserve_buy.commonkafka.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaTopic {
    // Topic Format = "{Publisher}.{EventName}"

    // Publisher : ORDER
    public final static String ORDER_RESERVED = "ORDER.RESERVED";
    public final static String ORDER_PAYED = "ORDER.PAYED";
    public final static String ORDER_CANCELED = "ORDER.CANCELED";
    public final static String ORDER_EXPIRED = "ORDER.EXPIRED";

    // Publisher : ITEM
    public final static String ITEM_CREATED = "ITEM.CREATED";
    public final static String ITEM_RESERVE_TIME = "ITEM.RESERVED.STOCK";
    public final static String ITEM_CANCELED_TIME = "ITEM.CANCELED.STOCK";

    // Publisher : STOCK
}
