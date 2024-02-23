package hg.reserve_buy.commonredis.lock;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisKey {

    public static final String ITEM_JOIN_PREFIX = "ITEM:JOIN:";
    public static final String ORDER_PREFIX = "ORDER:";
    public static final String REDIS_STOCK_PREFIX = "STOCK:";
    public static final String REDIS_ORDER_STOCK_PREFIX = "ORDER:STOCK:";
    public static final String REDIS_ORDER_RESERVE_FORMAT = "ORDER:RESERVE:%s:%s%s"; // orderId, itemNumber, count

}
