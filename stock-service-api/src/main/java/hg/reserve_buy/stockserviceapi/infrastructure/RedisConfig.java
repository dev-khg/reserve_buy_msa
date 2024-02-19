package hg.reserve_buy.stockserviceapi.infrastructure;

import hg.reserve_buy.commonredis.lock.RedisLockConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {RedisLockConfig.class})
public class RedisConfig {
}
