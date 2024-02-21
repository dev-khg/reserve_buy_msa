package hg.reserve_buy.stockserviceapi.infrastructure;

import hg.reserve_buy.commonredis.lock.RedisLockConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {RedisLockConfig.class})
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class})
public class RedisConfig {
}
