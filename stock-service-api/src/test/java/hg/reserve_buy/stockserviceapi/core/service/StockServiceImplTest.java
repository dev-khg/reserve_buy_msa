package hg.reserve_buy.stockserviceapi.core.service;

import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import hg.reserve_buy.stockserviceapi.core.entity.StockEntity;
import hg.reserve_buy.stockserviceapi.core.repository.StockRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static hg.reserve_buy.stockserviceapi.core.entity.StockEntity.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StockServiceImplTest {
    @Autowired
    StockRepository stockRepository;
    @Autowired
    StockServiceImpl stockService;
    @PersistenceContext
    EntityManager em;

    List<StockEntity> stockEntities;

    Random r = new Random();

    private static final ExecutorService service = Executors.newFixedThreadPool(100);

    @BeforeEach
    void beforeEach() {
        stockEntities = new ArrayList<>();
        int loopCount = 10;

        for (int i = 1; i <= loopCount; i++) {
            stockEntities.add(generateStockEntity((long) i, r.nextInt(100, 200)));
        }
    }

    @Test
    @DisplayName("존재하지 않는 아이템 재고 조회시 예외가 반환되어야 한다")
    void failure_get_stock_info_not_exists_item() {
        // given
        long notExistsItemNumber1 = stockEntities.size() + 1;
        long notExistsItemNumber2 = 0;

        // when

        // then
        assertThatThrownBy(() ->
                stockService.getStockByItemNumber(notExistsItemNumber1)
        ).isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() ->
                stockService.getStockByItemNumber(notExistsItemNumber2)
        ).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("존재하는 아이템 조회시 정상적으로 반환되어야 한다.")
    void success_get_stock_info() {
        for (StockEntity savedEntity : stockEntities) {
            // given

            // when
            Integer stock = stockService.getStockByItemNumber(savedEntity.getItemNumber());

            // then
            assertEquals(stock, savedEntity.getTotal());
            flushAndClearPersistence();
        }
    }

    private StockEntity generateStockEntity(Long itemNumber, Integer stock) {
        return stockRepository.save(create(itemNumber, stock));
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    @SpringBootTest
    static class StockConcurrencyTest {

        @Autowired
        StockRepository stockRepository;
        @Autowired
        StockServiceImpl stockService;

        @Test
        @DisplayName("재고 증가: 동시성 요청시 정상적으로 재고가 반영되어야 한다.")
        void success_get_stock_increase() throws InterruptedException {
            // given
            StockEntity stockEntity = generateStockEntity(1L, 100);

            int numberOfThreads = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch latch = new CountDownLatch(numberOfThreads);

            // when
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.execute(() -> {
                    stockService.increaseStockByItemNumber(stockEntity.getItemNumber(), 1);
                    latch.countDown();
                });
            }
            latch.await();

            // then
            StockEntity afterStock = stockRepository.findByItemNumber(stockEntity.getItemNumber()).orElseThrow();
            assertEquals(stockEntity.getTotal() + numberOfThreads, afterStock.getTotal());
        }

        @Test
        @DisplayName("재고 감소: 동시성 요청시 정상적으로 재고가 반영되어야 한다.")
        void success_get_stock_decrease() throws InterruptedException {
            // given
            StockEntity stockEntity = generateStockEntity(2L, 100);

            int numberOfThreads = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch latch = new CountDownLatch(numberOfThreads);

            // when
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.execute(() -> {
                    stockService.decreaseStockByItemNumber(stockEntity.getItemNumber(), 1);
                    latch.countDown();
                });
            }
            latch.await();

            // then
            StockEntity afterStock = stockRepository.findByItemNumber(stockEntity.getItemNumber()).orElseThrow();
            assertEquals(stockEntity.getTotal() - numberOfThreads, afterStock.getTotal());
        }

        private StockEntity generateStockEntity(Long itemNumber, Integer stock) {
            return stockRepository.save(create(itemNumber, stock));
        }
    }
}