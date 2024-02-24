package hg.reserve_buy.orderserviceapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hg.reserve_buy.commonredis.lock.RedisKey;
import hg.reserve_buy.commonservicedata.response.ApiResponse;
import hg.reserve_buy.orderserviceapi.core.entity.OrderEntity;
import hg.reserve_buy.orderserviceapi.core.repository.KeyValueStorage;
import hg.reserve_buy.orderserviceapi.core.repository.OrderRepository;
import hg.reserve_buy.orderserviceapi.core.service.ItemCacheService;
import hg.reserve_buy.orderserviceapi.core.service.PayService;
import hg.reserve_buy.orderserviceapi.core.service.StockCacheService;
import hg.reserve_buy.orderserviceapi.infrastructure.kafka.OrderConsumerService;
import hg.reserve_buy.orderserviceapi.infrastructure.kafka.OrderProducerService;
import hg.reserve_order.itemserviceevent.api.ItemCacheResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static hg.reserve_buy.commonredis.lock.RedisKey.*;
import static hg.reserve_buy.orderserviceapi.core.entity.OrderEntity.*;
import static org.mockito.Mockito.*;

@Disabled
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTest {
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected OrderRepository orderRepository;
    @Autowired
    protected StockCacheService stockCacheService;
    @Autowired
    protected ItemCacheService itemCacheService;
    @Autowired
    protected KeyValueStorage<String, Integer> stockCacheStorage;
    @Autowired
    protected MockMvc mockMvc;
    @PersistenceContext
    protected EntityManager em;
    @MockBean
    protected PayService payService;
    @MockBean
    protected OrderProducerService orderProducerService;

    protected Random r = new Random();

    protected LocalDateTime current = LocalDateTime.now();

    protected Long userNumber = r.nextLong(1, 100);
    protected Long openedTimeDealItemNumber = 1L;
    protected Long notOpenedTimeDealItemNumber = 2L;
    protected Long generalItemNumber = 3L;
    protected Integer price = r.nextInt(1000, 10000);
    protected ItemCacheResponse openedTimeDealItemCache = new ItemCacheResponse(
            openedTimeDealItemNumber, price, "TIME_DEAL", current.minusSeconds(1)
    );
    protected ItemCacheResponse notOpenedTimeDealItemCache = new ItemCacheResponse(
            notOpenedTimeDealItemNumber, price, "TIME_DEAL", current.plusMinutes(1)
    );
    protected ItemCacheResponse generalItemCache = new ItemCacheResponse(
            generalItemNumber, price, "GENERAL", null
    );

    @BeforeEach
    void beforeEach() {
        // 아이템 정보 캐싱
        itemCacheService.refreshCache(openedTimeDealItemNumber, openedTimeDealItemCache);
        itemCacheService.refreshCache(notOpenedTimeDealItemNumber, notOpenedTimeDealItemCache);
        itemCacheService.refreshCache(generalItemNumber, generalItemCache);

        // 재고 캐싱
        stockCacheStorage.putValue(REDIS_STOCK_PREFIX + openedTimeDealItemNumber, 100, 1, TimeUnit.MINUTES);
        stockCacheStorage.putValue(REDIS_STOCK_PREFIX + notOpenedTimeDealItemNumber, 100, 1, TimeUnit.MINUTES);

        when(payService.isPayable(any(), any()))
                .thenReturn(true);

    }

    protected OrderEntity createOrderEntity(Long itemNumber, Long userNumber, Integer price) {
        String orderId = UUID.randomUUID().toString();
        return create(orderId, itemNumber, userNumber, price, r.nextInt(1, 100));
    }

    protected <T> T readResponseJsonBody(String content, Class<T> clazz) throws Exception {
        TypeReference<ApiResponse<T>> typeReference = new TypeReference<>() { };
        ApiResponse<T> apiResponse = objectMapper.readValue(content, typeReference);
        T data = objectMapper.convertValue(apiResponse.getData(), clazz);

        return data;
    }
}
