package hg.reserve_buy.orderserviceapi.core.service;

import hg.reserve_buy.commonservicedata.response.ApiResponse;
import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import hg.reserve_buy.orderserviceapi.external.ItemFeignClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Random;

import static hg.reserve_buy.commonredis.price.RedisLockKey.*;
import static hg.reserve_buy.orderserviceapi.core.service.ItemPriceService.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemPriceServiceTest {
    @InjectMocks
    ItemPriceService itemPriceService;

    @Mock
    KeyValueStorage keyValueStorage;
    @Mock
    ItemPriceAdapter itemPriceAdapter;
    @Mock
    ItemFeignClient itemFeignClient;

    @Test
    @DisplayName("캐시되지 않은 데이터 조회 시, 아이템 가격정보를 요청해서 캐싱해야한다.")
    void get_price_not_cached_item() {
        // given
        Long itemNumber = 1L;
        int random = new Random().nextInt(10000);
        String key = ITEM_PRICE_PREFIX + itemNumber;

        // when
        when(keyValueStorage.getValue(key))
                .thenReturn(Optional.empty());
        when(itemPriceAdapter.requestPrice(itemNumber))
                .thenReturn(random);
        when(itemFeignClient.getItemPrice(itemNumber))
                .thenReturn(ApiResponse.success(random));

        // then
        assertEquals(random, itemPriceService.getPrice(itemNumber));

        verify(keyValueStorage, times(1))
                .getValue(key);
    }

    @Test
    @DisplayName("캐시된 데이터 조회 시, 정상적으로 반환되어야 한다.")
    void get_price_cached_item() {
        // given
        Long itemNumber = 1L;
        int random = new Random().nextInt(10000);
        String key = ITEM_PRICE_PREFIX + itemNumber;

        // when
        when(keyValueStorage.getValue(key))
                .thenReturn(Optional.ofNullable(random));

        // then
        assertEquals(random, itemPriceService.getPrice(itemNumber));
    }
}