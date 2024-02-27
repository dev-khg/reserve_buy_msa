package hg.reserve_buy.stockserviceapi.core.service;

import com.example.orderserviceevent.event.OrderExpireEvent;
import com.example.orderserviceevent.event.OrderPayedEvent;
import hg.reserve_buy.stockserviceapi.core.entity.StockEntity;
import hg.reserve_buy.stockserviceapi.core.repository.StockRepository;
import hg.reserve_order.itemserviceevent.event.ItemCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static hg.reserve_buy.commonkafka.constant.KafkaTopic.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockConsumerService {
    private final StockService stockService;
    private final StockRepository stockRepository;
    private final static String GROUP_ID = "STOCK-SERVICE";

    @KafkaListener(topics = ORDER_PAYED, groupId = GROUP_ID, containerFactory = "kafkaContainerFactory")
    public void handleOrderPayed(ConsumerRecord<String, OrderPayedEvent> event) {
        OrderPayedEvent value = event.value();
        stockService.decreaseStockByItemNumber(value.getItemNumber(), value.getCount());
    }

    @KafkaListener(topics = ORDER_CANCELED, groupId = GROUP_ID, containerFactory = "kafkaContainerFactory")
    public void handleOrderCanceled(ConsumerRecord<String, OrderExpireEvent> event) {
        OrderExpireEvent value = event.value();
        stockService.increaseStockByItemNumber(value.getItemNumber(), value.getCount());
    }

    @KafkaListener(topics = ITEM_CREATED, groupId = GROUP_ID, containerFactory = "kafkaContainerFactory")
    @Transactional
    public void handleItemCreated(ItemCreatedEvent event) {
        StockEntity stockEntity = StockEntity.create(event.getItemNumber(), event.getCount());
        stockRepository.save(stockEntity);
    }
}
