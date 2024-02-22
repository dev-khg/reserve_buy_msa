package hg.reserve_buy.orderserviceapi.infrastructure.kafka;

import com.example.orderserviceevent.event.OrderCancelEvent;
import com.example.orderserviceevent.event.OrderPayedEvent;
import com.example.orderserviceevent.event.OrderReserveEvent;
import hg.reserve_buy.commonkafka.constant.KafkaTopic;
import hg.reserve_buy.commonredis.lock.DistributionLock;
import hg.reserve_buy.commonredis.price.RedisLockKey;
import hg.reserve_buy.commonservicedata.exception.InternalServerException;
import hg.reserve_buy.orderserviceapi.core.entity.OrderEntity;
import hg.reserve_buy.orderserviceapi.core.entity.OrderStatus;
import hg.reserve_buy.orderserviceapi.core.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderConsumerService {
    private final static String GROUP_ID = "order-service";
    private final OrderRepository orderRepository;

    @KafkaListener(topics = KafkaTopic.ORDER_RESERVED, groupId = GROUP_ID, containerFactory = "kafkaContainerFactory")
    public void handleOrderReserve(OrderReserveEvent event) {
        OrderEntity orderEntity = createOrderEntity(event);
        orderRepository.save(orderEntity);
    }

    @KafkaListener(topics = KafkaTopic.ORDER_PAYED, groupId = GROUP_ID, containerFactory = "kafkaContainerFactory")
    @DistributionLock(prefix = RedisLockKey.ORDER_PREFIX, key = "#event.orderId")
    public void handleOrderPayed(OrderPayedEvent event) {
        OrderEntity orderEntity = getOrderEntity(event.getOrderId());
        orderEntity.changeStatus(OrderStatus.PAYED);
    }

    @KafkaListener(topics = KafkaTopic.ORDER_CANCELED, groupId = GROUP_ID, containerFactory = "kafkaContainerFactory")
    @DistributionLock(prefix = RedisLockKey.ORDER_PREFIX, key = "#event.orderId")
    public void handleOrderCanceled(OrderCancelEvent event) {
        OrderEntity orderEntity = getOrderEntity(event.getOrderId());
        orderEntity.changeStatus(OrderStatus.CANCELED);
    }

    private OrderEntity createOrderEntity(OrderReserveEvent event) {
        return OrderEntity.create(
                event.getOrderId(), event.getItemNumber(), event.getUserNumber(), event.getPrice(), event.getCount()
        );
    }

    private OrderEntity getOrderEntity(String orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new InternalServerException("Not found order information.")
        );
    }
}
