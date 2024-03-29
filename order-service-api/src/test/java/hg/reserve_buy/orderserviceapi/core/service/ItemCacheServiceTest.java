package hg.reserve_buy.orderserviceapi.core.service;

import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import hg.reserve_buy.orderserviceapi.external.ItemFeignClient;
import hg.reserve_order.itemserviceevent.api.ItemCacheResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static hg.reserve_buy.commonredis.lock.RedisKey.*;
import static hg.reserve_buy.orderserviceapi.core.service.ItemCacheService.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemCacheServiceTest {
    @InjectMocks
    ItemCacheService itemCacheService;

    @Mock
    KeyValueStorage<String, Object> keyValueStorage;
    @Mock
    ItemPriceAdapter itemPriceAdapter;
    @Mock
    ItemFeignClient itemFeignClient;

    @Test
    @DisplayName("캐시되지 않은 데이터 가격 조회 시, 아이템 가격정보를 요청해서 캐싱해야한다.")
    void get_price_not_cached_item() {
        // given
        Long itemNumber = 1L;
        int random = new Random().nextInt(10000);
        String key = ITEM_JOIN_PREFIX + itemNumber;
        ItemCacheResponse cacheResponse
                = new ItemCacheResponse(itemNumber, random, "TIME_DEAL", LocalDateTime.now());

        // when
        when(keyValueStorage.getValue(key))
                .thenReturn(Optional.empty());
        when(itemPriceAdapter.getItemCache(itemNumber))
                .thenReturn(cacheResponse);

        // then
        Integer price = itemCacheService.getPrice(itemNumber);
        assertEquals(random, price);
    }

    @Test
    @DisplayName("캐시된 데이터 가격 조회 시, 정상적으로 반환되어야 한다.")
    void get_price_cached_item() {
        // given
        Long itemNumber = 1L;
        int random = new Random().nextInt(10000);
        String key = ITEM_JOIN_PREFIX + itemNumber;
        ItemCacheResponse cacheResponse
                = new ItemCacheResponse(itemNumber, random, "TIME_DEAL", LocalDateTime.now());

        // when
        when(keyValueStorage.getValue(key))
                .thenReturn(Optional.ofNullable(random));
        when(itemPriceAdapter.getItemCache(itemNumber))
                .thenReturn(cacheResponse);

        // then
        assertEquals(random, itemCacheService.getPrice(itemNumber));
    }

    @Test
    @DisplayName("아이템 오픈 여부 조회 시, 현재시간 이전에 오픈되어야 한다면, True를 반환해야한다.")
    void is_open_cached_item() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long itemNumber = 1L;
        int random = new Random().nextInt(10000);
        String key = ITEM_JOIN_PREFIX + itemNumber;
        ItemCacheResponse cacheResponse
                = new ItemCacheResponse(itemNumber, random, "TIME_DEAL", now.minusSeconds(1));

        // when
        when(keyValueStorage.getValue(key))
                .thenReturn(Optional.ofNullable(random));
        when(itemPriceAdapter.getItemCache(itemNumber))
                .thenReturn(cacheResponse);

        // then
        assertTrue(itemCacheService.isOpen(itemNumber));
    }

    @Test
    @DisplayName("아이템 오픈 여부 조회 시, 현재시간 이후에 오픈되어야 한다면, False를 반환해야한다.")
    void is_not_open_cached_item() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long itemNumber = 1L;
        int random = new Random().nextInt(10000);
        String key = ITEM_JOIN_PREFIX + itemNumber;
        ItemCacheResponse cacheResponse
                = new ItemCacheResponse(itemNumber, random, "TIME_DEAL", now.plusMinutes(1));

        // when
        when(keyValueStorage.getValue(key))
                .thenReturn(Optional.ofNullable(random));
        when(itemPriceAdapter.getItemCache(itemNumber))
                .thenReturn(cacheResponse);

        // then
        assertFalse(itemCacheService.isOpen(itemNumber));
    }
}