package hg.reserve_buy.commonredis.timedeal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisTimeDealScripts {
    private final static String timeDealOrderScript  = "local storedValue = redis.call('GET', KEYS[1]) " +
            "if storedValue and tonumber(storedValue) >= tonumber(ARGV[1]) then " +
            "    redis.call('SET', KEYS[1], tonumber(storedValue) - tonumber(ARGV[1])) " +
            "    return true " +
            "else " +
            "    return false " +
            "end";

    public static RedisScript<Boolean> reserveOrderScript
            = new DefaultRedisScript<>(timeDealOrderScript, Boolean.class);
}
