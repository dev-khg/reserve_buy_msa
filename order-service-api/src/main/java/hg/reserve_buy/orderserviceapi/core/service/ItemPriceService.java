package hg.reserve_buy.orderserviceapi.core.service;

import hg.reserve_buy.commonredis.lock.DistributionLock;
import hg.reserve_buy.commonredis.price.ItemRedisKey;
import hg.reserve_buy.commonservicedata.response.ApiResponse;
import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import hg.reserve_buy.orderserviceapi.external.ItemFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static hg.reserve_buy.commonredis.price.ItemRedisKey.*;

@Service
@RequiredArgsConstructor
public class ItemPriceService {
    private final KeyValueStorage keyValueStorage;
    private final ItemPriceAdapter itemPriceAdapter;


    public Integer getPrice(Long itemNumber) {
        String key = ITEM_PRICE_PREFIX + itemNumber;
        Integer price = keyValueStorage.getValue(key).orElse(null);

        if (price == null) {
            price = itemPriceAdapter.requestPrice(itemNumber);
        }

        return price;
    }

    @Component
    @RequiredArgsConstructor
    private static class ItemPriceAdapter {
        private final ItemFeignClient itemFeignClient;
        private final KeyValueStorage keyValueStorage;

        @DistributionLock(prefix = ITEM_PRICE_PREFIX, key = "#itemNumber")
        private Integer requestPrice(Long itemNumber) {
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
