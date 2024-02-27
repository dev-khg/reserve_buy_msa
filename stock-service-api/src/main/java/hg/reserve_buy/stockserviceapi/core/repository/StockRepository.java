package hg.reserve_buy.stockserviceapi.core.repository;

import hg.reserve_buy.stockserviceapi.core.entity.StockEntity;

import java.util.Optional;

public interface StockRepository {
    StockEntity save(StockEntity entity);

    Optional<StockEntity> findByItemNumber(Long itemNumber);

    Optional<StockEntity> findByItemNumberWithLock(Long itemNumber);
}
