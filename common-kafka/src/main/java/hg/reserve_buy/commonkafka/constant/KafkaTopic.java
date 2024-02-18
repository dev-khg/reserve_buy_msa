package hg.reserve_buy.commonkafka.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaTopic {
    public final static String ORDER_CREATED = "ORDER.CREATED";
    public final static String ORDER_PAYED = "ORDER.PAYED";
    public final static String ORDER_CANCELED = "ORDER.CANCELED";
}
