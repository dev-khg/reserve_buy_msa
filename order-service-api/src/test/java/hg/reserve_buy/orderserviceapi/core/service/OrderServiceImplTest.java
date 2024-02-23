package hg.reserve_buy.orderserviceapi.core.service;

import com.example.orderserviceevent.event.OrderReserveEvent;
import hg.reserve_buy.commonkafka.constant.KafkaTopic;
import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import hg.reserve_buy.orderserviceapi.infrastructure.kafka.OrderProducerService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Random;

import static hg.reserve_buy.commonkafka.constant.KafkaTopic.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceImplTest {
    @Autowired
    OrderServiceImpl orderService;
    @MockBean
    PayService payService;
    @MockBean
    ItemCacheService itemCacheService;
    @MockBean
    StockCacheService stockCacheService;
    @MockBean
    OrderProducerService orderProducerService;

    Random r = new Random();

    @Test
    @DisplayName("주문 예약시 유저가 지불할 금액이 충분하지 않으면 예외가 반환되어야 한다.")
    void failure_user_have_not_enough_money() {
        // given
        Long userNumber = r.nextLong();
        Long itemNumber = r.nextLong();
        Integer count = r.nextInt(1, 101);
        Integer price = r.nextInt(1000, 100001);

        // when
        when(itemCacheService.getPrice(itemNumber))
                .thenReturn(price);
        when(payService.isPayable(userNumber, count * price))
                .thenReturn(false);

        // then
        assertThatThrownBy(() -> orderService.reserveOrder(userNumber, itemNumber, count))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("선착순 아이템 주문 예약시 오픈시간이 되지 않았으면 예외가 반환되어야 한다.")
    void failure_reserve_order_not_opened_item() {
        // given
        Long userNumber = r.nextLong(1, 10000);
        Long itemNumber = r.nextLong(1, 10000);
        Integer count = r.nextInt(1, 101);
        Integer price = r.nextInt(1000, 100001);

        // when
        when(itemCacheService.getPrice(itemNumber))
                .thenReturn(price);
        when(payService.isPayable(userNumber, count * price))
                .thenReturn(true);
        when(itemCacheService.isTimeDeal(itemNumber))
                .thenReturn(true);
        when(itemCacheService.isOpen(itemNumber))
                .thenReturn(false);

        // then
        assertThatThrownBy(() -> orderService.reserveOrder(userNumber, itemNumber, count))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("선착순 아이템 주문 예약시 오픈시간 넘었다면, 예약 처리가 가능 해야한다.")
    void success_reserve_order_opened_item() {
        // given
        Long userNumber = r.nextLong(1, 10000);
        Long itemNumber = r.nextLong(1, 1000);
        Integer count = r.nextInt(1, 101);
        Integer price = r.nextInt(1000, 1001);

        // when
        when(itemCacheService.getPrice(itemNumber))
                .thenReturn(price);
        when(payService.isPayable(userNumber, count * price))
                .thenReturn(true);
        when(itemCacheService.isTimeDeal(itemNumber))
                .thenReturn(true);
        when(itemCacheService.isOpen(itemNumber))
                .thenReturn(true);
        String orderId = orderService.reserveOrder(userNumber, itemNumber, count);

        // then
        assertNotNull(orderId);

        verify(payService, times(1))
                .isPayable(userNumber, price * count);
        verify(itemCacheService, times(1))
                .isTimeDeal(itemNumber);
        verify(itemCacheService, times(1))
                .isOpen(itemNumber);
        verify(stockCacheService, times(1))
                .reserveStock(orderId, itemNumber, count);
        verify(orderProducerService, times(1))
                .publish(eq(orderId), eq(ORDER_RESERVED), any());
    }

    @Test
    @DisplayName("일반 아이템 주문 예약 시 선착순 판단 로직을 실행하면 안된다.")
    void success_reserve_order_general_item() {
        // given
        Long userNumber = r.nextLong(1, 10000);
        Long itemNumber = r.nextLong(1, 1000);
        Integer count = r.nextInt(1, 101);
        Integer price = r.nextInt(1000, 1001);

        // when
        when(itemCacheService.getPrice(itemNumber))
                .thenReturn(price);
        when(payService.isPayable(userNumber, count * price))
                .thenReturn(true);
        when(itemCacheService.isTimeDeal(itemNumber))
                .thenReturn(false);
        String orderId = orderService.reserveOrder(userNumber, itemNumber, count);

        // then
        assertNotNull(orderId);

        verify(payService, times(1))
                .isPayable(userNumber, price * count);
        verify(itemCacheService, times(1))
                .isTimeDeal(itemNumber);
        verify(itemCacheService, times(0))
                .isOpen(itemNumber);
        verify(stockCacheService, times(0))
                .reserveStock(orderId, itemNumber, count);
        verify(orderProducerService, times(1))
                .publish(eq(orderId), eq(ORDER_RESERVED), any());
    }
}