package hg.reserve_buy.stockserviceapi.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hg.reserve_buy.commonservicedata.response.ApiResponse;
import hg.reserve_buy.stockserviceapi.core.entity.StockEntity;
import hg.reserve_buy.stockserviceapi.core.repository.StockRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Disabled
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    StockRepository stockRepository;
    @Autowired
    ObjectMapper objectMapper;
    @PersistenceContext
    EntityManager em;

    protected StockEntity savedEntity;

    @BeforeEach
    void beforeEach() {
        StockEntity stockEntity = StockEntity.create(1L, 100);
        savedEntity = stockRepository.save(stockEntity);
        flushAndClearPersistence();
    }

    protected void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    protected <T> T readResponseJsonBody(String content, Class<T> clazz) throws Exception {
        TypeReference<ApiResponse<T>> typeReference = new TypeReference<>() {
        };
        ApiResponse<T> apiResponse = objectMapper.readValue(content, typeReference);
        T data = objectMapper.convertValue(apiResponse.getData(), clazz);

        return data;
    }
}
