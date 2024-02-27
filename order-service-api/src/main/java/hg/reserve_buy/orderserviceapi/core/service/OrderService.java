package hg.reserve_buy.orderserviceapi.core.service;

public interface OrderService {
    String reserveOrder(Long userNumber, Long itemNumber, Integer count);

    String order(Long userNumber, String orderId);
}
