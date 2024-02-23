package hg.reserve_buy.orderserviceapi.infrastructure.redis;

import hg.reserve_buy.commonredis.lock.RedisLockConfig;
import hg.reserve_buy.commonredis.price.RedisPriceCacheConfig;
import hg.reserve_buy.commonredis.stock.RedisStockCacheConfig;
import hg.reserve_buy.commonredis.timedeal.RedisTimeDealCacheConfig;
import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@ComponentScan(basePackageClasses =
        {RedisPriceCacheConfig.class, RedisLockConfig.class, RedisTimeDealCacheConfig.class}
)
@EnableAutoConfiguration(exclude = {RedisRepositoriesAutoConfiguration.class})
public class RedisConfig {

    @Bean
    public KeyValueStorage<String, Object> priceKeyValueStorage(
            @Qualifier("priceRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        return new RedisKeyValueStorage<>(redisTemplate);
    }

    @Bean
    public KeyValueStorage<String, Integer> timeDealKeyValueStorage(
            @Qualifier("timeDealRedisTemplate") RedisTemplate<String, Integer> redisTemplate) {
        return new RedisKeyValueStorage<>(redisTemplate);
    }
}
