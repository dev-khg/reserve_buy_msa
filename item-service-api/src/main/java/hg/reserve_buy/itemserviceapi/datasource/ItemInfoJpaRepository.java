package hg.reserve_buy.itemserviceapi.datasource;

import hg.reserve_buy.itemserviceapi.core.entity.ItemInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemInfoJpaRepository extends JpaRepository<ItemInfoEntity, Long> {

    @Query("select i_f from ItemInfoEntity i_f " +
            "join fetch i_f.itemEntity " +
            "where i_f.itemEntity.itemNumber = :itemNumber")
    Optional<ItemInfoEntity> findByItemInfoFJItem(@Param("itemNumber") Long itemNumber);
}
