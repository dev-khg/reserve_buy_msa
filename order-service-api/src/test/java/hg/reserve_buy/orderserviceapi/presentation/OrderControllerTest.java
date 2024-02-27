package hg.reserve_buy.orderserviceapi.presentation;

import com.example.orderserviceevent.event.OrderPayedEvent;
import com.example.orderserviceevent.event.OrderReserveEvent;
import hg.reserve_buy.commonkafka.constant.KafkaTopic;
import hg.reserve_buy.commonservicedata.response.ApiResponse;
import hg.reserve_buy.orderserviceapi.IntegrationTest;
import hg.reserve_buy.orderserviceapi.core.entity.OrderEntity;
import hg.reserve_buy.orderserviceapi.core.entity.OrderStatus;
import hg.reserve_buy.orderserviceapi.core.service.PayService;
import hg.reserve_buy.orderserviceapi.presentation.request.OrderReserveRequest;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static hg.reserve_buy.commonkafka.constant.KafkaTopic.*;
import static hg.reserve_buy.commonredis.lock.RedisKey.REDIS_STOCK_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest extends IntegrationTest {

    @Test
    @DisplayName("주문 예약 시, 재고 보다 많이 주문하면 실패가 반환되어야 한다.")
    void failure_reserve_order_not_enough_stock() throws Exception {
        // given
        OrderReserveRequest orderReserveRequest = new OrderReserveRequest(openedTimeDealItemNumber, 101);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userNumber)
                        .content(objectMapper.writeValueAsString(orderReserveRequest))
                ).andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        // then

    }

    @Test
    @DisplayName("주문 예약 시, 일반 아이템을 주문하면 정상적으로 주문 처리가 되어야한다.")
    void success_reserve_order_enough_stock() throws Exception {
        // given
        OrderReserveRequest orderReserveRequest = new OrderReserveRequest(generalItemNumber, 100);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userNumber)
                        .content(objectMapper.writeValueAsString(orderReserveRequest))
                ).andExpect(status().isOk())
                .andReturn();

        String orderId = readResponseJsonBody(mvcResult.getResponse().getContentAsString(), String.class);
        // then
        assertNotNull(orderId);

        verify(orderProducerService, times(1))
                .publish(eq(orderId), eq(ORDER_RESERVED), any(OrderReserveEvent.class));
    }

    @Test
    @DisplayName("주문 예약 시, 오픈되지 않은 선착순 아이템을 주문하면 예외가 반환되어야한다.")
    void failure_reserve_order_not_opened_item() throws Exception {
        // given
        OrderReserveRequest orderReserveRequest = new OrderReserveRequest(notOpenedTimeDealItemNumber, 1);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userNumber)
                        .content(objectMapper.writeValueAsString(orderReserveRequest))
                ).andExpect(status().isBadRequest())
                .andReturn();

        String message = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiResponse.class)
                .getMessage();

        // then
        assertEquals("현재 판매중인 아이템이 아닙니다.", message);
    }

    @Test
    @DisplayName("주문 예약 시, 오픈된 선착순 아이템을 정상적으로 주문하면 재고 및 주문이 정확히 반영되어야 한다.")
    void success_reserve_order_open_item() throws Exception {
        // given
        int initialStock = 100;
        int buyStock = r.nextInt(1, 100);
        OrderReserveRequest orderReserveRequest = new OrderReserveRequest(openedTimeDealItemNumber, buyStock);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, userNumber)
                        .content(objectMapper.writeValueAsString(orderReserveRequest))
                ).andExpect(status().isOk())
                .andReturn();

        String orderId = readResponseJsonBody(mvcResult.getResponse().getContentAsString(), String.class);

        // then
        assertEquals(initialStock - buyStock,
                stockCacheStorage.getValue(REDIS_STOCK_PREFIX + openedTimeDealItemNumber).get()
        );
        verify(orderProducerService, times(1))
                .publish(eq(orderId), eq(ORDER_RESERVED), any(OrderReserveEvent.class));
    }

    @Test
    @DisplayName("주문 시, 권한 없는 요저가 요청하면 예외가 반환되어야 한다.")
    @Transactional
    void failure_order_not_auth() throws Exception {
        // given
        OrderEntity orderEntity = createOrderEntity(generalItemNumber, userNumber, r.nextInt(10000, 100000));
        String orderId = orderRepository.save(orderEntity).getOrderId();
        flushAndClearPersistence();

        // when
        MvcResult mvcResult = mockMvc.perform(post("/order/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, userNumber + 1)
                ).andExpect(status().isBadRequest())
                .andReturn();

        String message = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiResponse.class).getMessage();

        // then
        assertEquals("don't have authorization.", message);
    }

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    @DisplayName("주문 시, 이미 처리된 주문이면 예외가 반환되어야 한다.")
    @Transactional
    void failure_order_already_handle(OrderStatus status) throws Exception {
        // given
        if (status == OrderStatus.RESERVED) return;

        OrderEntity orderEntity = createOrderEntity(generalItemNumber, userNumber, r.nextInt(10000, 100000));
        orderEntity.changeStatus(status);
        String orderId = orderRepository.save(orderEntity).getOrderId();
        flushAndClearPersistence();

        // when
        MvcResult mvcResult = mockMvc.perform(post("/order/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, userNumber)
                ).andExpect(status().isBadRequest())
                .andReturn();

        String message = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiResponse.class).getMessage();

        // then
        assertEquals("이미 결제되었거나 취소된 주문입니다.", message);
    }

    @Test
    @DisplayName("정상 주문 처리 시, 이벤트를 발행해야 한다..")
    @Transactional
    void success_order() throws Exception {
        // given
        OrderEntity orderEntity = createOrderEntity(generalItemNumber, userNumber, r.nextInt(10000, 100000));
        String orderId = orderRepository.save(orderEntity).getOrderId();
        flushAndClearPersistence();

        // when
        MvcResult mvcResult = mockMvc.perform(post("/order/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, userNumber)
                ).andExpect(status().isOk())
                .andReturn();

        String responseOrderId
                = readResponseJsonBody(mvcResult.getResponse().getContentAsString(), String.class);

        // then
        assertEquals(orderEntity.getOrderId(), responseOrderId);

        verify(orderProducerService, times(1))
                .publish(eq(orderId), eq(ORDER_PAYED), any(OrderPayedEvent.class));
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }
}