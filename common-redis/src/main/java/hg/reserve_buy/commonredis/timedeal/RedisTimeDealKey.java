package hg.reserve_buy.commonredis.timedeal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisTimeDealKey {

    public static final String TIME_DEAL_PREFIX = "TIMEDEAL:";
}
