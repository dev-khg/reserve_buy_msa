package hg.reserve_buy.orderserviceapi.core.service;

import hg.reserve_buy.commonredis.lock.DistributionLock;
import hg.reserve_buy.commonredis.lock.RedisKey;
import hg.reserve_buy.commonredis.timedeal.RedisTimeDealScripts;
import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import hg.reserve_buy.orderserviceapi.external.StockFeignClient;
import hg.reserve_buy.orderserviceapi.infrastructure.redis.RedisExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StockCacheService {
    private final RedisExecutor redisExecutor;

    public void reserveStock(String orderId, Long itemNumber, Integer count) {
        String key = RedisKey.REDIS_STOCK_PREFIX + itemNumber;

        Boolean success
                = redisExecutor.executeTemplate(RedisTimeDealScripts.reserveOrderScript, List.of(key), count);

        if (success != null && !success) {
            throw new BadRequestException("재고가 충분하지 않습니다.");
        }

        String reserveKey = String.format(RedisKey.REDIS_ORDER_RESERVE_FORMAT, orderId, itemNumber, count);
        redisExecutor.putValue(reserveKey, 1, 2, TimeUnit.MINUTES);
    }

    @Component
    @RequiredArgsConstructor
    static class StockAdapter {
        private final KeyValueStorage<String, Integer> keyValueStorage;
        private final StockFeignClient stockFeignClient;

        @DistributionLock(prefix = RedisKey.REDIS_ORDER_STOCK_PREFIX, key = "#itemNumber")
        public Integer refreshCacheAndGet(Long itemNumber) {
            String key = RedisKey.REDIS_STOCK_PREFIX + itemNumber;
            Integer stock = keyValueStorage.getValue(key).orElse(null);

            if (stock == null) {
                stock = stockFeignClient.getStockCache(itemNumber);
            }

            return stock;
        }
    }
}
