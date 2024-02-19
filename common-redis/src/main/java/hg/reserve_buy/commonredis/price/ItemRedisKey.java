package hg.reserve_buy.commonredis.price;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRedisKey {

    public static final String ITEM_PRICE_PREFIX = "ITEM:PRICE:";
}
