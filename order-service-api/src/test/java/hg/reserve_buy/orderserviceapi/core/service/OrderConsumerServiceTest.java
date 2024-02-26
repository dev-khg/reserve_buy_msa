package hg.reserve_buy.orderserviceapi.core.service;

import com.example.orderserviceevent.event.OrderExpireEvent;
import com.example.orderserviceevent.event.OrderPayedEvent;
import com.example.orderserviceevent.event.OrderReserveEvent;
import hg.reserve_buy.orderserviceapi.core.entity.OrderEntity;
import hg.reserve_buy.orderserviceapi.core.entity.OrderStatus;
import hg.reserve_buy.orderserviceapi.core.repository.OrderRepository;
import hg.reserve_buy.orderserviceapi.infrastructure.kafka.OrderConsumerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderConsumerServiceTest {
    @Autowired
    OrderConsumerService orderConsumerService;
    @Autowired
    OrderRepository orderRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("주문 예약 이벤트 발생시 정상적으로 디비에 저장되어야 한다.")
    void success_handle_order_reserved() {
        // given
        OrderReserveEvent event = createMockReserveEvent();

        // when
        orderConsumerService.handleOrderReserve(event);

        // then
        OrderEntity savedEntity = orderRepository.findById(event.getOrderId()).orElseThrow();

        assertEquals(savedEntity.getOrderId(), event.getOrderId());
        assertEquals(savedEntity.getUnitPrice(), event.getPrice());
        assertEquals(savedEntity.getUserNumber(), event.getUserNumber());
        assertEquals(savedEntity.getCount(), event.getCount());
        assertEquals(savedEntity.getItemNumber(), event.getItemNumber());
        assertEquals(savedEntity.getStatus(), OrderStatus.RESERVED);
    }

    @Test
    @DisplayName("주문 결제 성공 이벤트 발생시 정상적으로 디비에 반영되어야 한다.")
    void success_handle_order_payed() {
        // given
        OrderReserveEvent event = createMockReserveEvent();
        orderConsumerService.handleOrderReserve(event);
        OrderPayedEvent orderPayedEvent = new OrderPayedEvent(
                event.getOrderId(),
                event.getItemNumber(),
                event.getCount()
        );

        // when
        orderConsumerService.handleOrderPayed(orderPayedEvent);

        // then
        OrderEntity savedEntity = orderRepository.findById(event.getOrderId()).orElseThrow();
        assertEquals(OrderStatus.PAYED, savedEntity.getStatus());
    }

    @Test
    @DisplayName("주문 취소 이벤트 발생시 정상적으로 디비에 반영되어야 한다.")
    void success_handle_order_canceled() {
        // given
        OrderReserveEvent event = createMockReserveEvent();
        orderConsumerService.handleOrderReserve(event);
        OrderExpireEvent orderExpireEvent = new OrderExpireEvent(
                event.getOrderId(), event.getItemNumber(), event.getCount()
        );

        // when
        orderConsumerService.handleOrderCanceled(orderExpireEvent);

        // then
        OrderEntity savedEntity = orderRepository.findById(event.getOrderId()).orElseThrow();
        assertEquals(OrderStatus.CANCELED, savedEntity.getStatus());
    }

    private OrderReserveEvent createMockReserveEvent() {
        Random r = new Random();
        String orderId = UUID.randomUUID().toString();
        Long userNumber = r.nextLong(1, Long.MAX_VALUE);
        Long itemNumber = r.nextLong(1, Long.MAX_VALUE);
        Integer price = r.nextInt(1, Integer.MAX_VALUE);
        Integer count = r.nextInt(1, Integer.MAX_VALUE);
        OrderReserveEvent event = new OrderReserveEvent(orderId, userNumber, itemNumber, price, count);
        return event;
    }
}