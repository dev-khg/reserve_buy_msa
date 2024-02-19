package hg.reserve_buy.orderserviceapi.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GeneralOrderService implements OrderService {
    @Qualifier("orderServiceImpl")
    private final OrderService orderService;
    private final ItemCacheService itemCacheService;

    @Override
    @Transactional
    public String reserveOrder(Long userNumber, Long itemNumber, Integer count) {

        if (isTimeDeal(itemNumber)) {

        }

        return orderService.reserveOrder(userNumber, itemNumber, count);
    }

    @Override
    public String order(Long userNumber, String orderId) {
        return orderService.order(userNumber, orderId);
    }

    private boolean isTimeDeal(Long itemNumber) {
        return false;
    }
}
