package hg.reserve_buy.orderserviceapi.infrastructure.redis;

import hg.reserve_buy.commonredis.timedeal.RedisTimeDealScripts;
import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisExecutorProxy {
    private final RedisTemplate<String, Integer> redisTemplate;
    private final KeyValueStorage<String, Integer> keyValueStorage;

    public Optional<Integer> getValue(String key) {
        return keyValueStorage.getValue(key);
    }

    public void putValue(String key, Integer value, int timeout, TimeUnit timeUnit) {
        keyValueStorage.putValue(key, value, timeout, timeUnit);
    }

    public <T> T executeTemplate(RedisScript<T> script, List<String> keys, Object arg) {
        return redisTemplate.execute(script, keys, arg);
    }
}
