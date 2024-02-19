package hg.reserve_buy.orderserviceapi.core.service;

import hg.reserve_buy.commonredis.lock.DistributionLock;
import hg.reserve_buy.commonredis.timedeal.RedisTimeDealKey;
import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import hg.reserve_buy.orderserviceapi.external.ItemFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static hg.reserve_buy.commonredis.price.RedisLockKey.*;

@Service
@RequiredArgsConstructor
public class ItemCacheService {
    private final KeyValueStorage<String, Integer> keyValueStorage;
    private final ItemPriceAdapter itemPriceAdapter;

    public Integer getPrice(Long itemNumber) {
        String key = ITEM_PRICE_PREFIX + itemNumber;
        Integer price = keyValueStorage.getValue(key).orElse(null);

        if (price == null) {
            price = itemPriceAdapter.requestPrice(itemNumber);
        }

        return price;
    }

    public boolean isTimeDeal(Long itemNumber) {
        String key = RedisTimeDealKey.TIME_DEAL_PREFIX + itemNumber;

        return false;
    }

    @Component
    @RequiredArgsConstructor
    static class ItemPriceAdapter {
        private final ItemFeignClient itemFeignClient;
        private final KeyValueStorage<String, Integer> keyValueStorage;

        @DistributionLock(prefix = ITEM_PRICE_PREFIX, key = "#itemNumber")
        public Integer requestPrice(Long itemNumber) {
            String key = ITEM_PRICE_PREFIX + itemNumber;
            Integer price = keyValueStorage.getValue(key).orElse(null);

            if (price == null) {
                price = itemFeignClient.getItemPrice(itemNumber).getData();
                keyValueStorage.putValue(key, price, 1, TimeUnit.MINUTES);
            }

            return price;
        }
    }
}
