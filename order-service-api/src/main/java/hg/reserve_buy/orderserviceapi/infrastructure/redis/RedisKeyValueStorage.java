package hg.reserve_buy.orderserviceapi.infrastructure.redis;

import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class RedisKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    private final RedisTemplate<K, V> redisTemplate;

    @Override
    public Optional<V> getValue(K key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void putValue(K key, V value, int timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }
}
