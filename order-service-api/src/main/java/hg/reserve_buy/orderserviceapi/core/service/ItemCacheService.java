package hg.reserve_buy.orderserviceapi.core.service;

import hg.reserve_buy.commonredis.lock.DistributionLock;
import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import hg.reserve_buy.orderserviceapi.external.ItemFeignClient;
import hg.reserve_order.itemserviceevent.api.ItemCacheResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static hg.reserve_buy.commonredis.lock.RedisKey.*;

@Service
@RequiredArgsConstructor
public class ItemCacheService {
    private final KeyValueStorage<String, Object> keyValueStorage;
    private final ItemPriceAdapter itemPriceAdapter;

    public Integer getPrice(Long itemNumber) {
        ItemCacheResponse itemCache = getItemCacheResponse(itemNumber);
        return itemCache.getPrice();
    }

    public boolean isTimeDeal(Long itemNumber) {
        ItemCacheResponse itemCacheResponse = getItemCacheResponse(itemNumber);

        return itemCacheResponse.getType().equals("TIME_DEAL");
    }

    public boolean isOpen(Long itemNumber) {
        ItemCacheResponse itemCacheResponse = getItemCacheResponse(itemNumber);
        return itemCacheResponse.getStartAt().isAfter(LocalDateTime.now());
    }

    public void refreshCache(Long itemNumber, ItemCacheResponse itemCache) {
        String key = ITEM_PRICE_PREFIX + itemNumber;
        keyValueStorage.putValue(key, itemCache, 10, TimeUnit.MINUTES);
    }

    private ItemCacheResponse getItemCacheResponse(Long itemNumber) {
        ItemCacheResponse itemCache = itemPriceAdapter.getItemCache(itemNumber);

        if (itemCache == null) {
            itemCache = itemPriceAdapter.refreshCacheAndGet(itemNumber);
        }

        return itemCache;
    }

    @Component
    @RequiredArgsConstructor
    static class ItemPriceAdapter {
        private final ItemFeignClient itemFeignClient;
        private final KeyValueStorage<String, Object> keyValueStorage;

        @DistributionLock(prefix = ITEM_JOIN_PREFIX, key = "#itemNumber")
        public ItemCacheResponse refreshCacheAndGet(Long itemNumber) {
            String key = ITEM_JOIN_PREFIX + itemNumber;
            ItemCacheResponse itemCache = (ItemCacheResponse) keyValueStorage.getValue(key).orElse(null);

            if (itemCache == null) {
                itemCache = itemFeignClient.getItemCache(itemNumber);
                keyValueStorage.putValue(key, itemCache, 10, TimeUnit.MINUTES);
            }

            return itemCache;
        }

        public ItemCacheResponse getItemCache(Long itemNumber) {
            String key = ITEM_JOIN_PREFIX + itemNumber;
            ItemCacheResponse itemCache = (ItemCacheResponse) keyValueStorage.getValue(key).orElse(null);

            return itemCache;
        }
    }
}
