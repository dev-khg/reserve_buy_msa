package hg.reserve_buy.stockserviceapi.core.service;

import com.example.orderserviceevent.event.OrderCancelEvent;
import com.example.orderserviceevent.event.OrderPayedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static hg.reserve_buy.commonkafka.constant.KafkaTopic.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockConsumerService {
    private final StockService stockService;

    private final static String GROUP_ID = "STOCK-SERVICE";

    @KafkaListener(topics = ORDER_PAYED, groupId = GROUP_ID)
    public void handleOrderPayed(OrderPayedEvent event) {
        log.info("<======= [KAFKA] Topic = [{}] | data = [{}]", ORDER_PAYED, event);
        stockService.decreaseStockByItemNumber(event.getItemNumber(), event.getCount());
    }

    @KafkaListener(topics = ORDER_CANCELED, groupId = GROUP_ID)
    public void handleOrderCanceled(OrderCancelEvent event) {
        log.info("<======= [KAFKA] Topic = [{}] | data = [{}]", ORDER_CANCELED, event);
        stockService.increaseStockByItemNumber(event.getItemNumber(), event.getCount());
    }
}