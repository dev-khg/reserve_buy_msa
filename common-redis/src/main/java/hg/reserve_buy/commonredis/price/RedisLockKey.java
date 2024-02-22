package hg.reserve_buy.commonredis.price;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisLockKey {

    public static final String ITEM_PRICE_PREFIX = "ITEM:PRICE:";
    public static final String ITEM_JOIN_PREFIX = "ITEM:JOIN:";
    public static final String ORDER_PREFIX = "ORDER:";
}
