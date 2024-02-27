package hg.reserve_buy.orderserviceapi.infrastructure.redis;

import hg.reserve_buy.commonredis.timedeal.RedisTimeDealScripts;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisExecutorTest {

    @Autowired
    RedisExecutor redisExecutor;
    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    @BeforeEach
    void beforeEach() {
        clearRedis();
    }

    @Test
    @DisplayName("[주문예약] 재고 정보가 없는 요청이면 false가 반환되어야 한다.")
    void reserve_order_not_exists_item() {
        // given
        RedisScript<Boolean> script = RedisTimeDealScripts.reserveOrderScript;
        String key = UUID.randomUUID().toString();

        // when
        Boolean result
                = redisExecutor.executeTemplate(script, List.of(key), 1);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("[주문예약] 재고 정보가 있는 요청이지만, 개수가 부족하면, False를 반환하고, 재고값의 변경이 있으면 안된다.")
    void reserve_order_not_enough_stock_item() {
        // given
        RedisScript<Boolean> script = RedisTimeDealScripts.reserveOrderScript;
        String key = UUID.randomUUID().toString();
        int initialStock = 1;

        putKeyValue(key, initialStock);

        // when
        Boolean result
                = redisExecutor.executeTemplate(script, List.of(key), initialStock + 1);

        // then
        assertFalse(result);
        assertEquals(redisTemplate.opsForValue().get(key), initialStock);
    }

    @Test
    @DisplayName("[주문예약] 재고 정보가 있는 요청이고, 개수가 충분하면, True를 반환하고, 재고값이 정상적으로 변경되어야 한다.")
    void reserve_order_enough_stock_item() {
        // given
        Random r = new Random();
        RedisScript<Boolean> script = RedisTimeDealScripts.reserveOrderScript;
        String key = UUID.randomUUID().toString();
        int initialStock = r.nextInt(10, 100);
        int buyStock = r.nextInt(0, initialStock);
        putKeyValue(key, initialStock);


        // when
        Boolean result
                = redisExecutor.executeTemplate(script, List.of(key), buyStock);

        // then
        assertTrue(result);
        assertEquals(redisTemplate.opsForValue().get(key), initialStock - buyStock);
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