package hg.reserve_buy.orderserviceapi.infrastructure.redis;

import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisKeyValueStorage implements KeyValueStorage {
    private final RedisTemplate<String, Integer> redisTemplate;

    @Override
    public Optional<Integer> getValue(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    // TODO : 분산락 고려 필요.
    @Override
    public void putValue(String key, Integer value, int timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }
}
