package hg.reserve_buy.orderserviceapi.core.service;

import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ItemPriceService {
    private final KeyValueStorage keyValueStorage;

    public Integer getPrice(Long itemNumber) {
        // TODO : Key Custom 필요
        String key = String.valueOf(itemNumber);
        Integer price = keyValueStorage.getValue(key).orElse(null);

        if(price == null) {
            price = mockPrice(itemNumber);
            keyValueStorage.putValue(
                    String.valueOf(itemNumber), price, 1, TimeUnit.MINUTES
            );
        }

        return price;
    }

    private int mockPrice(Long itemNumber) {
        return 10000;
    }
}
