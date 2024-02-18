package hg.reserve_buy.stockserviceapi.datasource;

import hg.reserve_buy.stockserviceapi.core.entity.StockEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StockJpaRepository extends JpaRepository<StockEntity, Long> {
    Optional<StockEntity> findByItemNumber(Long itemNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StockEntity s " +
            "where s.itemNumber = :itemNumber")
    Optional<StockEntity> findByItemNumberWithLock(@Param("itemNumber") Long itemNumber);
}
