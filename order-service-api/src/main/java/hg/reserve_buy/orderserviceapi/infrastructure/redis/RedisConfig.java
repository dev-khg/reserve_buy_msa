package hg.reserve_buy.orderserviceapi.infrastructure.redis;

import hg.reserve_buy.commonredis.lock.RedisLockConfig;
import hg.reserve_buy.commonredis.price.RedisPriceCacheConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {RedisPriceCacheConfig.class, RedisLockConfig.class})
public class RedisConfig {

}
