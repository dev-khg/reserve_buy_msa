package hg.reserve_buy.orderserviceapi.core.service;

import com.example.orderserviceevent.event.OrderPayedEvent;
import com.example.orderserviceevent.event.OrderReserveEvent;
import hg.reserve_buy.commonkafka.constant.KafkaTopic;
import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import hg.reserve_buy.orderserviceapi.core.entity.OrderEntity;
import hg.reserve_buy.orderserviceapi.core.entity.OrderStatus;
import hg.reserve_buy.orderserviceapi.core.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static hg.reserve_buy.commonkafka.constant.KafkaTopic.*;
import static hg.reserve_buy.orderserviceapi.core.entity.OrderStatus.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderProducerService orderProducerService;
    private final ItemPriceService itemPriceService;
    private final PayService payService;

    @Override
    public String reserveOrder(Long userNumber, Long itemNumber, Integer count) {
        Integer unitPrice = itemPriceService.getPrice(itemNumber);
        int totalPrice = unitPrice * count;

        if (!payService.isPayable(userNumber, totalPrice)) {
            throw new BadRequestException("Not enough money.");
        }

        String orderId = UUID.randomUUID().toString();

        OrderReserveEvent orderReserveEvent = new OrderReserveEvent(
                orderId, userNumber, itemNumber, unitPrice, count
        );
        orderProducerService.publish(orderId, ORDER_CREATED, orderReserveEvent);

        return orderId;
    }

    @Override
    public String order(Long userNumber, String orderId) {
        OrderEntity orderEntity = getOrderEntity(orderId);

        if (!orderEntity.getUserNumber().equals(userNumber)) {
            throw new BadRequestException("don't have authorization.");
        } else if (orderEntity.getStatus() == RESERVED) {
            throw new BadRequestException("이미 결제되었거나 취소된 주문입니다.");
        }

        OrderPayedEvent orderPayedEvent = new OrderPayedEvent(orderId);
        orderProducerService.publish(orderId, ORDER_PAYED, orderPayedEvent);

        return orderId;
    }

    private OrderEntity getOrderEntity(String orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new BadRequestException("Not found order info.")
        );
    }
}
