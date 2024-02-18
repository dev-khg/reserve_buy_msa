package hg.reserve_buy.stockserviceapi.datasource;

import hg.reserve_buy.stockserviceapi.core.entity.StockEntity;
import hg.reserve_buy.stockserviceapi.core.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {
    private final StockJpaRepository stockJpaRepository;

    @Override
    public StockEntity save(StockEntity entity) {
        return stockJpaRepository.save(entity);
    }

    @Override
    public Optional<StockEntity> findByItemNumber(Long itemNumber) {
        return stockJpaRepository.findByItemNumber(itemNumber);
    }

    @Override
    public Optional<StockEntity> findByItemNumberWithLock(Long itemNumber) {
        return Optional.empty();
    }
}
