package hg.reserve_buy.orderserviceapi.core.service;

import com.example.orderserviceevent.event.OrderExpireEvent;
import hg.reserve_buy.commonkafka.constant.KafkaTopic;
import hg.reserve_buy.commonredis.lock.DistributionLock;
import hg.reserve_buy.commonredis.lock.RedisKey;
import hg.reserve_buy.commonredis.timedeal.RedisTimeDealScripts;
import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import hg.reserve_buy.orderserviceapi.external.StockFeignClient;
import hg.reserve_buy.orderserviceapi.infrastructure.kafka.OrderProducerService;
import hg.reserve_buy.orderserviceapi.infrastructure.redis.RedisExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StockCacheService {
    private final RedisExecutor redisExecutor;
    private final StockAdapter stockAdapter;

    public void reserveStock(String orderId, Long itemNumber, Integer count) {
        String key = RedisKey.REDIS_STOCK_PREFIX + itemNumber;

        if (!redisExecutor.containsKey(key)) {
            stockAdapter.refreshCacheAndGet(itemNumber);
        }

        Boolean success
                = redisExecutor.executeTemplate(RedisTimeDealScripts.reserveOrderScript, List.of(key), count);

        if (success != null && !success) {
            throw new BadRequestException("재고가 충분하지 않습니다.");
        }

        String reserveKey = String.format(RedisKey.REDIS_ORDER_RESERVE_FORMAT, orderId, itemNumber, count);
        redisExecutor.putValue(reserveKey, 1, 2, TimeUnit.MINUTES);
    }

    public void increaseStock(Long itemNumber, Integer count) {
        String key = RedisKey.REDIS_STOCK_PREFIX + itemNumber;
        redisExecutor.executeTemplate(RedisTimeDealScripts.increaseStockScript, List.of(key), count);
    }

    @Slf4j
    @Component
    static class StockAdapter extends KeyExpirationEventMessageListener {
        private final String EXPIRE_KEY_PREFIX = RedisKey.REDIS_ORDER_RESERVE_FORMAT.split(":")[0] + ":"
                + RedisKey.REDIS_ORDER_RESERVE_FORMAT.split(":")[1];
        private final OrderProducerService orderProducerService;
        private final KeyValueStorage<String, Integer> keyValueStorage;
        private final StockFeignClient stockFeignClient;

        public StockAdapter(RedisMessageListenerContainer listenerContainer,
                            OrderProducerService orderProducerService,
                            KeyValueStorage<String, Integer> keyValueStorage,
                            StockFeignClient stockFeignClient) {
            super(listenerContainer);
            this.orderProducerService = orderProducerService;
            this.keyValueStorage = keyValueStorage;
            this.stockFeignClient = stockFeignClient;
        }

        @DistributionLock(prefix = RedisKey.ORDER_REDIS_STOCK_CACHE_PREFIX, key = "#itemNumber")
        public Integer refreshCacheAndGet(Long itemNumber) {
            String key = RedisKey.REDIS_STOCK_PREFIX + itemNumber;
            Integer stock = keyValueStorage.getValue(key).orElse(null);

            if (stock == null) {
                log.info("refresh {}", itemNumber);
                stock = stockFeignClient.getStockCache(itemNumber);
                keyValueStorage.putValue(key, stock, 5, TimeUnit.MINUTES);
            }

            return stock;
        }

        @Override
        public void onMessage(Message message, byte[] pattern) {
            String expiredKey = message.toString();
            log.info("expired = {}", expiredKey);
            if (expiredKey.startsWith(EXPIRE_KEY_PREFIX)) {
                String[] split = expiredKey.split(":");
                String orderId = split[2];
                long itemNumber = Long.parseLong(split[3]);
                int count = Integer.parseInt(split[4]);

                orderProducerService.publish(
                        orderId,
                        KafkaTopic.ORDER_EXPIRED,
                        new OrderExpireEvent(orderId, itemNumber, count)
                );
            }
        }
    }
}
