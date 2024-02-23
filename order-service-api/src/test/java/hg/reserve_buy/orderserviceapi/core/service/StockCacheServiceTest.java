package hg.reserve_buy.orderserviceapi.core.service;

import hg.reserve_buy.commonredis.lock.RedisKey;
import hg.reserve_buy.commonredis.timedeal.RedisTimeDealScripts;
import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import hg.reserve_buy.orderserviceapi.infrastructure.redis.RedisExecutor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockCacheServiceTest {
    @Autowired
    StockCacheService stockCacheService;
    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    @BeforeEach
    void beforeEach() {
        clearRedis();
    }

    @Test
    @DisplayName("[주문예약] 재고 정보가 없는 요청이면 예외가 반환되어야 한다.")
    void reserve_order_not_exists_item() {
        // given
        Random r = new Random();
        String orderId = UUID.randomUUID().toString();
        long itemNumber = r.nextLong(1, 1000);

        // when

        // then
        assertThatThrownBy(() -> stockCacheService.reserveStock(orderId, itemNumber, 1))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("[주문예약] 재고 정보가 있는 요청이지만, 개수가 부족하면, 예외를 반환하고, 재고값의 변경이 있으면 안된다.")
    void reserve_order_not_enough_stock_item() {
        // given
        Random r = new Random();
        String orderId = UUID.randomUUID().toString();
        Long itemNumber = 1L;
        int initialStock = r.nextInt(1, 1000);
        String key = RedisKey.REDIS_STOCK_PREFIX + itemNumber;

        putKeyValue(key, initialStock);

        // when

        // then
        assertThatThrownBy(() -> stockCacheService.reserveStock(orderId, itemNumber, initialStock + 1))
                .isInstanceOf(BadRequestException.class);
        assertEquals(initialStock, redisTemplate.opsForValue().get(key));
    }

    @Test
    @DisplayName("[주문예약] 재고 정보가 있는 요청이고, 개수가 충분하면, 예약 내역이 레디스에 저장되고, 재고값이 정상적으로 변경되어야 한다.")
    void reserve_order_enough_stock_item() {
        // given
        Random r = new Random();
        String orderId = UUID.randomUUID().toString();
        Long itemNumber = 1L;
        int initialStock = r.nextInt(1, 1000);
        int buyCount = r.nextInt(0, initialStock);
        String key = RedisKey.REDIS_STOCK_PREFIX + itemNumber;
        String reservedTicket = String.format(RedisKey.REDIS_ORDER_RESERVE_FORMAT, orderId, itemNumber, buyCount);
        putKeyValue(key, initialStock);

        // when
        stockCacheService.reserveStock(orderId, itemNumber, buyCount);

        // then
        assertTrue(redisTemplate.hasKey(reservedTicket));
        assertEquals(initialStock - buyCount, redisTemplate.opsForValue().get(key));
    }

    private void putKeyValue(String key, Integer value) {
        redisTemplate.opsForValue().set(key, value, 10, TimeUnit.SECONDS);
    }

    private void clearRedis() {
        for (String key : redisTemplate.keys("*")) {
            redisTemplate.delete(key);
        }
    }
}